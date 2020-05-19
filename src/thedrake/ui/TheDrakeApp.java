package thedrake.ui;

import thedrake.*;

public class TheDrakeApp{

    public static GameState createNewGame() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1,1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board);
    }

}
