package thedrake.ui;

import java.util.List;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import thedrake.*;

public class BoardView extends GridPane implements TileViewContext, StackViewContext {

    private GameState gameState;

    private ValidMoves validMoves;

    private TileView selected;

    private PlaceFromStackContext placeFromBlueStackContext;
    private PlaceFromStackContext placeFromOrangeStackContext;

    private CaptureViewContext captureContextBlue;
    private CaptureViewContext captureContextOrange;

    public BoardView(GameState gameState) {
        this.gameState = gameState;
        this.validMoves = new ValidMoves(gameState);

        PositionFactory positionFactory = gameState.board().positionFactory();
        for (int y = 0; y < gameState.board().dimension(); y++) {
            for (int x = 0; x < gameState.board().dimension(); x++) {
                BoardPos boardPos = positionFactory.pos(x, gameState.board().dimension() - 1 - y);
                TileView tv = new TileView(boardPos, gameState.tileAt(boardPos), this);
                tv.prefWidthProperty().bind(Bindings.divide(this.heightProperty(), 5));
                tv.prefHeightProperty().bind(Bindings.divide(this.heightProperty(), 5));
                add(tv, x, y);
            }
        }

        setHgap(5);
        setVgap(5);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selected != null && selected != tileView)
            selected.unselect();

        if (gameState.sideOnTurn() == PlayingSide.BLUE) {
            placeFromBlueStackContext.setSelected(null);
        } else {
            placeFromOrangeStackContext.setSelected(null);
        }

        selected = tileView;

        clearMoves();
        showMoves(validMoves.boardMoves(tileView.position()));
    }

    @Override
    public void executeMove(Move move) {
        if (selected == null) {
            clearMoves();
            gameState = gameState.placeFromStack(move.target());
        } else {
            selected.unselect();
            selected = null;
            clearMoves();
            gameState = move.execute(gameState);
        }
        validMoves = new ValidMoves(gameState);
        updateTiles();
        updateContexts();

        if (validMoves.allMoves().size() == 0 && gameState.result() != GameResult.VICTORY) {
            gameState = gameState.draw();
        } else if (gameState.armyOnTurn().captured().size() == 6 && gameState.armyNotOnTurn().captured().size() == 6 && gameState.result() != GameResult.VICTORY) {
            gameState = gameState.draw();
        }

        if (gameState.result() == GameResult.VICTORY || gameState.result() == GameResult.DRAW) {
            showFinalScreen(gameState);
        }
    }

    private void showFinalScreen(GameState gameState) {
        Stage dialog = (Stage) getChildren().get(0).getScene().getWindow();
        VBox dialogVbox = new FinalView(gameState, dialog);
        Scene dialogScene = new Scene(dialogVbox, dialog.getWidth(), dialog.getHeight());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void updateContexts() {
        placeFromOrangeStackContext.updateStack(gameState);
        placeFromBlueStackContext.updateStack(gameState);
        if (gameState.armyNotOnTurn().side() == PlayingSide.BLUE) {
            captureContextBlue.update(gameState);
        } else {
            captureContextOrange.update(gameState);
        }
    }

    private void updateTiles() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.setTile(gameState.tileAt(tileView.position()));
            tileView.update();
        }
    }

    private void clearMoves() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.clearMove();
        }
    }

    private void showMoves(List<Move> moveList) {
        for (Move move : moveList)
            tileViewAt(move.target()).setMove(move);
    }

    private TileView tileViewAt(BoardPos target) {
        int index = (3 - target.j()) * 4 + target.i();
        return (TileView) getChildren().get(index);
    }

    @Override
    public TileView getTileViewAt(BoardPos target) {
        return tileViewAt(target);
    }

    @Override
    public void clearStackMoves() {
        clearMoves();
    }

    @Override
    public PlayingSide getPlayingSide() {
        return gameState.sideOnTurn();
    }

    public void setPlaceFromStackContexts(PlaceFromStackContext blueContext, PlaceFromStackContext orangeContext) {
        placeFromBlueStackContext = blueContext;
        placeFromOrangeStackContext = orangeContext;
    }

    public void setCaptureViewContexts(CaptureViewContext blueContext, CaptureViewContext orangeContext) {
        captureContextBlue = blueContext;
        captureContextOrange = orangeContext;
    }

    @Override
    public void setSelected(TileView tv) {
        if (selected != null) selected.unselect();
        selected = tv;
    }
}
