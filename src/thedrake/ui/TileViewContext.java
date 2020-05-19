package thedrake.ui;

import thedrake.Move;
import thedrake.PlayingSide;

public interface TileViewContext {

    void tileViewSelected(TileView tileView);

    void executeMove(Move move);

    PlayingSide getPlayingSide();

}
