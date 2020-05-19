package thedrake.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import thedrake.*;

import java.util.List;

import static javafx.geometry.Pos.CENTER;

public class CaptureView extends GridPane implements TileViewContext, CaptureViewContext {
    private GameState gameState;

    public CaptureView(GameState gameState) {
        this.gameState = gameState;

        Integer row = 0;
        Integer col = 0;

        for (int i = 0; i < 7; i++) {
            PositionFactory pf = new PositionFactory(gameState.board().dimension());
            TileView pane = new TileView(pf.pos(col, row), BoardTile.EMPTY, this);
            pane.prefWidthProperty().bind(Bindings.divide(widthProperty(), 3.5));
            pane.prefHeightProperty().bind(Bindings.divide(widthProperty(), 3.5));
            if (i % 2 == 0 && i != 0) {
                row++;
                col = 0;
            }
            if (i == 6) {
                add(pane, 1, row);
            } else {
                add(pane, col, row);
            }
            col++;
        }

        setAlignment(CENTER);
        setHgap(5);
        setVgap(5);
        setPadding(new Insets(30));
    }

    @Override
    public void tileViewSelected(TileView tileView) {

    }

    @Override
    public void executeMove(Move move) {

    }

    @Override
    public PlayingSide getPlayingSide() {
        return null;
    }

    @Override
    public void update(GameState gs) {
        gameState = gs;
        List<Troop> captured = gameState.armyNotOnTurn().captured();
        int capturedSize = captured.size();
        int i = 0;
        for(Node node : getChildren()) {
            if (i < capturedSize) {
                TileView tileView = (TileView) node;
                tileView.setTile(new TroopTile(captured.get(i), gameState.armyOnTurn().side(), TroopFace.AVERS));
                tileView.update();
            }
            i++;
        }
    }
}
