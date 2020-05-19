package thedrake.ui;

import thedrake.BoardPos;

public interface StackViewContext {

    TileView getTileViewAt (BoardPos target);

    void clearStackMoves();

    void setSelected(TileView tv);
}
