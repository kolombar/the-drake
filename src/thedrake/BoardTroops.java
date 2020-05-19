package thedrake;

import java.io.PrintWriter;
import java.util.*;

public class BoardTroops implements JSONSerializable {
	private final PlayingSide playingSide;
	private final Map<BoardPos, TroopTile> troopMap;
	private final TilePos leaderPosition;
	private final int guards;
	
	public BoardTroops(PlayingSide playingSide) { 
		this.playingSide = playingSide;
		this.troopMap = Collections.emptyMap();
		this.leaderPosition = TilePos.OFF_BOARD;
		this.guards = 0;
	}
	
	public BoardTroops(
			PlayingSide playingSide,
			Map<BoardPos, TroopTile> troopMap,
			TilePos leaderPosition, 
			int guards) {
		this.playingSide = playingSide;
		this.troopMap = troopMap;
		this.leaderPosition = leaderPosition;
		this.guards = guards;
	}

	public Optional<TroopTile> at(TilePos pos) {
		if (troopMap.containsKey(pos)) {
			return Optional.of(troopMap.get(pos));
		} else {
			return Optional.empty();
		}
	}
	
	public PlayingSide playingSide() {
		return playingSide;
	}
	
	public TilePos leaderPosition() {
		return leaderPosition;
	}

	public int guards() {
		return guards;
	}
	
	public boolean isLeaderPlaced() {
		if (leaderPosition == TilePos.OFF_BOARD) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isPlacingGuards() {
		if (isLeaderPlaced() && guards < 2) {
			return true;
		} else {
			return false;
		}
	}	
	
	public Set<BoardPos> troopPositions() {
		return troopMap.keySet();
	}

	public BoardTroops placeTroop(Troop troop, BoardPos target) {
		TroopTile newTile = new TroopTile(troop, playingSide, TroopFace.AVERS);
		Map<BoardPos, TroopTile> newTroopMap = new HashMap<>(troopMap);
		if (newTroopMap.keySet().contains(target)) {
			throw new IllegalArgumentException();
		} else {
			newTroopMap.put(target, newTile);
		}
		TilePos newLeaderPosition = isLeaderPlaced() ? leaderPosition : target;
		int newGuards = isPlacingGuards() ? guards + 1 : guards;
		return new BoardTroops(playingSide, newTroopMap, newLeaderPosition, newGuards);
	}
	
	public BoardTroops troopStep(BoardPos origin, BoardPos target) {
		Map<BoardPos, TroopTile> newTroopMap = new HashMap<>(troopMap);
		if (!isLeaderPlaced() || isPlacingGuards()) {
			throw new IllegalStateException();
		} else if (newTroopMap.keySet().contains(target) || !newTroopMap.keySet().contains(origin)) {
			throw new IllegalArgumentException();
		} else {
			newTroopMap.put(target, newTroopMap.get(origin).flipped());
			newTroopMap.remove(origin);
		}
		TilePos newLeaderPosition = leaderPosition().equals(origin) ? target : leaderPosition();
		return new BoardTroops(playingSide, newTroopMap, newLeaderPosition, guards);
	}
	
	public BoardTroops troopFlip(BoardPos origin) {
		if(!isLeaderPlaced()) {
			throw new IllegalStateException(
					"Cannot move troops before the leader is placed.");			
		}
		
		if(isPlacingGuards()) {
			throw new IllegalStateException(
					"Cannot move troops before guards are placed.");			
		}
		
		if(!at(origin).isPresent())
			throw new IllegalArgumentException();
		
		Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
		TroopTile tile = newTroops.remove(origin);
		newTroops.put(origin, tile.flipped());

		return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
	}
	
	public BoardTroops removeTroop(BoardPos target) {
		if (!isLeaderPlaced() || isPlacingGuards()) {
			throw new IllegalStateException();
		}
		Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
		if (!newTroops.keySet().contains(target)) {
			throw new IllegalArgumentException();
		}
		newTroops.remove(target);
		TilePos newLeaderPosition = leaderPosition().equals(target) ? TilePos.OFF_BOARD : leaderPosition();
		return new BoardTroops(playingSide, newTroops, newLeaderPosition, guards);
	}

	@Override
	public void toJSON(PrintWriter writer) {
		writer.printf("{\"side\":");
		playingSide.toJSON(writer);
		writer.printf(",\"leaderPosition\":" + "\"" + leaderPosition.toString() + "\",\"guards\":" + guards);
		writer.printf(",\"troopMap\":{");
		List<BoardPos> keyList = new ArrayList<BoardPos>(troopMap.keySet());
		Collections.sort(keyList, new TroopsPosComparator());
		for (int i = 0; i < keyList.size(); i++) {
			BoardPos key = keyList.get(i);
			writer.printf("\"" + key.toString() + "\"" + ":");
			troopMap.get(key).toJSON(writer);
			if (i != keyList.size()-1) {
				writer.printf(",");
			}
		}
		writer.printf("}}");
	}
}
