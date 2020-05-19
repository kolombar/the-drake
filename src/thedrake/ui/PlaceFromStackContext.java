package thedrake.ui;

import thedrake.GameState;

public interface PlaceFromStackContext {

    void updateStack(GameState gamestate);

    void setSelected(TileView tv);
}
