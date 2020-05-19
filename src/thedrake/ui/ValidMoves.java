package thedrake.ui;

import thedrake.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidMoves {
    private final GameState state;

    public ValidMoves(GameState state) {
        this.state = state;
    }

    public List<Move> boardMoves(BoardPos position) {
        if (state.armyOnTurn().boardTroops().isPlacingGuards())
            return Collections.emptyList();

        Tile tile = state.tileAt(position);
        if (tile.hasTroop()) {
            if (((TroopTile) tile).side() != state.sideOnTurn()) {
                return Collections.emptyList();
            }

            return ((TroopTile) tile).movesFrom(position, state);
        }

        return Collections.emptyList();
    }

    public List<Move> movesFromStack(TileView tileView) {
        TroopTile troopTile = (TroopTile) tileView.getTile();
        Troop troop = troopTile.troop();
        List<Move> moves = new ArrayList<Move>();
        PositionFactory pf = state.board().positionFactory();
        Army armyOnTurn = state.armyOnTurn();

        if (state.armyOnTurn().stack().get(0) == troop) {
            if (!armyOnTurn.boardTroops().isLeaderPlaced() && troop.name() == "Drake") {
                int j = 0;
                if (state.sideOnTurn() == PlayingSide.ORANGE)
                    j = state.board().dimension() - 1;

                for (int i = 0; i < state.board().dimension(); i++) {
                    moves.add(new PlaceFromStack(pf.pos(i, j)));
                }
            } else if (armyOnTurn.boardTroops().isPlacingGuards() && troop.name() == "Clubman") {
                if ((armyOnTurn.boardTroops().guards() == 0 && tileView.position().i() == 1) || (armyOnTurn.boardTroops().guards() == 1 && tileView.position().i() == 2)) {

                    TilePos leader = armyOnTurn.boardTroops().leaderPosition();
                    TilePos target = leader.step(0, 1);
                    if (state.canPlaceFromStack(target)) {
                        moves.add(new PlaceFromStack((BoardPos) target));
                    }

                    target = leader.step(0, -1);
                    if (state.canPlaceFromStack(target)) {
                        moves.add(new PlaceFromStack((BoardPos) target));
                    }

                    target = leader.step(1, 0);
                    if (state.canPlaceFromStack(target)) {
                        moves.add(new PlaceFromStack((BoardPos) target));
                    }

                    target = leader.step(-1, 0);
                    if (state.canPlaceFromStack(target)) {
                        moves.add(new PlaceFromStack((BoardPos) target));
                    }
                }
            } else {
                for (BoardPos pos : armyOnTurn.boardTroops().troopPositions()) {
                    List<BoardPos> neighbours = pos.neighbours();
                    for (BoardPos target : neighbours) {
                        if (state.canPlaceFromStack(target)) {
                            moves.add(new PlaceFromStack(target));
                        }
                    }
                }
            }
        }

        return moves;
    }

    public List<Move> allMoves() {
        List<Move> moves = new ArrayList<>();
        for (BoardPos pos : state.armyOnTurn().boardTroops().troopPositions()) {
            moves.addAll(boardMoves(pos));
        }

        List<Troop> stack = new ArrayList<>(state.armyOnTurn().stack());
        if (state.armyOnTurn().stack().size() < 7) {
            for (int i = 0; i < 7 - state.armyOnTurn().stack().size(); i++) {
                stack.add(i, null);
            }
        }

        PositionFactory pf = new PositionFactory(state.board().dimension());

        for (int i = 0; i < stack.size(); i++) {
            if (stack.get(i) == null) continue;

            Troop troop = stack.get(i);
            TroopTile tile = new TroopTile(troop, state.sideOnTurn(), TroopFace.AVERS);
            TileView pane = new TileView(pf.pos(i, 1), tile, null);
            moves.addAll(movesFromStack(pane));
        }

        return moves;
    }
}
