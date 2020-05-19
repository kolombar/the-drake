package thedrake;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SlideAction extends TroopAction {

    public SlideAction(Offset2D offset) {
        super(offset);
    }

    public SlideAction(int offsetX, int offsetY) {
        super(offsetX, offsetY);
    }

    public List<TilePos> findSlides(Offset2D offset, BoardPos origin, GameState state) {
        List<TilePos> result = new ArrayList<TilePos>();
        if (offset.x == 1 && offset.y == 0) {
            int interval = state.board().dimension() - origin.i() - 1;
            for (int i = 1; i <= interval; i++) {
                result.add(origin.stepByPlayingSide(new Offset2D(i, offset.y), state.sideOnTurn()));
            }
        }
        if (offset.x == -1 && offset.y == 0) {
            int interval = 0 - origin.i();
            for (int i = -1; i >= interval; i--) {
                BoardPos newPos = (BoardPos) origin.stepByPlayingSide(new Offset2D(i, offset.y), state.sideOnTurn());
                result.add(newPos);
            }
        }
        if (offset.x == 1 && offset.y == 1) {
            int intervalCol = state.board().dimension() - origin.i() - 1;
            int intervalRow = state.board().dimension() - origin.j() - 1;
            int interval = Math.min(intervalCol, intervalRow);
            for (int i = 1; i <= interval; i++) {
                result.add(origin.stepByPlayingSide(new Offset2D(i, i), PlayingSide.BLUE));
            }
        }
        if (offset.x == 1 && offset.y == -1) {
            int intervalCol = state.board().dimension() - origin.i() - 1;
            int intervalRow = origin.j();
            int interval = Math.min(intervalCol, intervalRow);
            for (int i = 1; i <= interval; i++) {
                result.add(origin.stepByPlayingSide(new Offset2D(i, -i), PlayingSide.BLUE));
            }
        }
        if (offset.x == -1 && offset.y == -1) {
            int intervalCol = origin.i();
            int intervalRow = origin.j();
            int interval = Math.min(intervalCol, intervalRow);
            for (int i = 1; i <= interval; i++) {
                result.add(origin.stepByPlayingSide(new Offset2D(-i, -i), PlayingSide.BLUE));
            }
        }
        if (offset.x == -1 && offset.y == 1) {
            int intervalCol = origin.i();
            int intervalRow = state.board().dimension() - origin.j() - 1;
            int interval = Math.min(intervalCol, intervalRow);
            for (int i = 1; i <= interval; i++) {
                result.add(origin.stepByPlayingSide(new Offset2D(-i, i), PlayingSide.BLUE));
            }
        }
        if (offset.x == 0 && offset.y == 1) {
            int interval = state.board().dimension() - origin.j() - 1;
            for (int i = 1; i <= interval; i++) {
                result.add(origin.stepByPlayingSide(new Offset2D(offset.x, i), PlayingSide.BLUE));
            }
        }
        if (offset.x == 0 && offset.y == -1) {
            int interval = 0 - origin.j();
            for (int i = -1; i >= interval; i--) {
                result.add(origin.stepByPlayingSide(new Offset2D(offset.x, i), PlayingSide.BLUE));
            }
        }
        return result;
    }

    @Override
    public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
        List<Move> result = new ArrayList<>();
        List<TilePos> targets = findSlides(offset(), origin, state);

        for (TilePos target : targets) {
            if (target == TilePos.OFF_BOARD) {
                break;
            }
            BoardPos targetPos = new BoardPos(state.board().dimension(), target.i(), target.j());
            if (state.canStep(origin, targetPos)) {
                result.add(new StepOnly(origin, targetPos));
            } else if (state.canCapture(origin, targetPos)) {
                result.add(new StepAndCapture(origin, targetPos));
                break;
            } else {
                break;
            }
        }

        return result;
    }
}
