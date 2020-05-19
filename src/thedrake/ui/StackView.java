package thedrake.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import thedrake.*;

import java.util.*;

import static javafx.geometry.Pos.CENTER;

public class StackView extends GridPane implements TileViewContext, PlaceFromStackContext {

    private GameState gameState;

    private ValidMoves validMoves;

    private TileView selected;

    private StackViewContext stackViewContext;

    private PlayingSide initSide;

    public StackView(GameState gameState, PlayingSide initSide, StackViewContext stackViewContext) {
        this.gameState = gameState;
        this.validMoves = new ValidMoves(gameState);
        this.stackViewContext = stackViewContext;
        this.initSide = initSide;

        Army army = initSide == PlayingSide.BLUE ? gameState.army(PlayingSide.BLUE) : gameState.army(PlayingSide.ORANGE);

        int i = 0;
        for (Troop troop : army.stack()) {
            PositionFactory pf = new PositionFactory(gameState.board().dimension());
            TroopTile tile = new TroopTile(troop, initSide, TroopFace.AVERS);
            TileView pane = new TileView(pf.pos(i, 1), tile, this);
            pane.prefWidthProperty().bind(Bindings.divide(widthProperty(), 20));
            pane.prefHeightProperty().bind(Bindings.divide(widthProperty(), 20));
            add(pane, i, 1);
            i++;
        }

        setAlignment(CENTER);
        setHgap(5);
        setVgap(5);
        setPadding(new Insets(30));
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selected != null && selected != tileView)
            selected.unselect();

        stackViewContext.setSelected(null);
        selected = tileView;

        clearMoves();
        showMoves(validMoves.movesFromStack(tileView));
    }

    private void clearMoves() {
        stackViewContext.clearStackMoves();
    }

    private void showMoves(List<Move> moveList) {
        for (Move move : moveList)
            tileViewAt(move.target()).setMove(move);
    }

    private TileView tileViewAt(BoardPos target) {
        return stackViewContext.getTileViewAt(target);
    }

    @Override
    public void executeMove(Move move) {
        selected.unselect();
        selected = null;
        clearMoves();
        gameState = gameState.placeFromStack(move.target());
        validMoves = new ValidMoves(gameState);
        updateTiles();
    }
    
    private void updateTiles() {
        List<Troop> stack = new ArrayList<>();
        for (Troop troop : gameState.armyNotOnTurn().stack()) {
         stack.add(troop);
        }
        if (stack.size() < getChildren().size()) {
            int diff = getChildren().size() - stack.size();
            for (int j = 0; j < diff; j++) {
                stack.add(j, null);
            }
        }
        int i = 0;
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.unselect();
            if (tileView.getTile().hasTroop()) {
                TroopTile troopTile = (TroopTile) tileView.getTile();
                if (stack.get(i) == null) {
                    tileView.setTile(BoardTile.EMPTY);
                    tileView.update();
                    i++;
                } else if (troopTile.troop().name() != stack.get(i).name()) {
                    tileView.setTile(BoardTile.EMPTY);
                    tileView.update();
                    i++;
                } else i++;
            } else {
                i++;
            }
        }
    }

    @Override
    public PlayingSide getPlayingSide() {
        return gameState.sideOnTurn();
    }

    @Override
    public void updateStack(GameState newGamestate) {
        gameState = newGamestate;
        validMoves = new ValidMoves(gameState);
        if (gameState.sideOnTurn() != initSide) updateTiles();
    }

    @Override
    public void setSelected(TileView tv) {
        if (selected != null) selected.unselect();
        selected = tv;
    }
}
