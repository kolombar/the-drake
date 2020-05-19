package thedrake;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GameState implements JSONSerializable {
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if (side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if (sideOnTurn == PlayingSide.BLUE)
            return orangeArmy;

        return blueArmy;
    }

    public Tile tileAt(TilePos pos) {
        if (pos == TilePos.OFF_BOARD) {
            return BoardTile.EMPTY;
        }
        if (armyOnTurn().boardTroops().at(pos).isPresent()) {
            return armyOnTurn().boardTroops().at(pos).get();
        } else if (armyNotOnTurn().boardTroops().at(pos).isPresent()) {
            return armyNotOnTurn().boardTroops().at(pos).get();
        } else {
            return board.at(pos);
        }
        /*Tile tileAtPos = (Tile) board.at(pos);
        if (tileAtPos.hasTroop()) {
            return BoardTile.EMPTY;
        }
        return tileAtPos;*/
    }

    private boolean canStepFrom(TilePos origin) {
        if (origin == TilePos.OFF_BOARD) {
            return false;
        }
        if (!armyOnTurn().boardTroops().isLeaderPlaced() || armyOnTurn().boardTroops().isPlacingGuards()) {
            return false;
        }
        if (result != GameResult.IN_PLAY) {
            return false;
        } else if (armyNotOnTurn().boardTroops().at(origin).isPresent()) {
        	return false;
		} else if (!armyOnTurn().boardTroops().at(origin).isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean canStepTo(TilePos target) {
        if (target == TilePos.OFF_BOARD) {
            return false;
        }
        if (result != GameResult.IN_PLAY) {
            return false;
        }
        if (armyOnTurn().boardTroops().at(target).isPresent() || armyNotOnTurn().boardTroops().at(target).isPresent()) {
        	return false;
		}
        return tileAt(target).canStepOn();
    }

    private boolean canCaptureOn(TilePos target) {
        if (target == TilePos.OFF_BOARD) {
            return false;
        }
        if (result != GameResult.IN_PLAY) {
            return false;
        } else if (!armyNotOnTurn().boardTroops().at(target).isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canStep(TilePos origin, TilePos target) {
    	if (origin.equals(target)) {
    		return false;
		}
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        if (target == TilePos.OFF_BOARD) {
            return false;
        }
        if (result != GameResult.IN_PLAY) {
            return false;
        } else if (armyOnTurn().stack().isEmpty()) {
            return false;
        } else if (!canStepTo(target)) {
            return false;
        }
        //if (!armyOnTurn().boardTroops().isLeaderPlaced() || armyOnTurn().boardTroops().isPlacingGuards()) {
        if (!armyOnTurn().boardTroops().isLeaderPlaced()) {
			if (sideOnTurn == PlayingSide.BLUE) {
				if (target.row() > 1) {
					return false;
				}
			} else {
				if (target.row() < board.dimension()) {
					return false;
				}
			}
		}
        if (armyOnTurn().boardTroops().at(target).isPresent() || armyNotOnTurn().boardTroops().at(target).isPresent()) {
            return false;
        }
        if (armyOnTurn().boardTroops().isPlacingGuards()) {
        	if (armyOnTurn().boardTroops().leaderPosition().neighbours().contains(target)) {
        		return true;
			} else {
        		return false;
			}
		}
        if (armyOnTurn().boardTroops().isLeaderPlaced() && !armyOnTurn().boardTroops().isPlacingGuards()) {
        	Set<BoardPos> troopPositions = armyOnTurn().boardTroops().troopPositions();
        	List<BoardPos> resultList = new ArrayList<BoardPos>();
        	for (BoardPos pos : troopPositions) {
        		resultList.addAll(pos.neighbours());
			}
        	return resultList.contains(target) && canStepTo(target);
		}
        return true;
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"result\":");
        result.toJSON(writer);
        writer.printf(",\"board\":");
        board.toJSON(writer);
        writer.printf(",\"blueArmy\":");
        blueArmy.toJSON(writer);
        writer.printf(",\"orangeArmy\":");
        orangeArmy.toJSON(writer);
        writer.printf("}");
    }
}
