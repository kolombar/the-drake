package thedrake;

import java.io.PrintWriter;
import java.util.*;

import static thedrake.BoardTile.EMPTY;

public class Board implements JSONSerializable {

	private final int dimension;
	private final Map<BoardPos, BoardTile> board;

	// Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
	public Board(int dimension) {
		this.dimension = dimension;
		PositionFactory factory = new PositionFactory(dimension);
		this.board = new HashMap<BoardPos, BoardTile>();
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				board.put(factory.pos(i,j), EMPTY);
			}
		}
	}

	// Rozměr hrací desky
	public int dimension() {
		return this.dimension;
	}

	// Vrací dlaždici na zvolené pozici.
	public BoardTile at(TilePos pos) {
		return board.get(pos);
	}

	// Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
	public Board withTiles(TileAt ...ats) {
		Board newBoard = new Board(dimension);
		for (BoardPos key : newBoard.board.keySet()) {
			newBoard.board.put(key, this.board.get(key));
		}
		for (TileAt tile : ats) {
			newBoard.board.put(tile.pos, tile.tile);
		}
		return newBoard;
	}

	// Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
	public PositionFactory positionFactory() {
		return new PositionFactory(dimension);
	}
	
	public static class TileAt {
		public final BoardPos pos;
		public final BoardTile tile;
		
		public TileAt(BoardPos pos, BoardTile tile) {
			this.pos = pos;
			this.tile = tile;
		}
	}

	@Override
	public void toJSON(PrintWriter writer) {
		writer.printf("{\"dimension\":" + dimension + ",\"tiles\":[");
		List<BoardPos> keyList = new ArrayList<BoardPos>(board.keySet());
		Collections.sort(keyList, new BoardPosComparator());
		for (int i = 0; i < keyList.size(); i++) {
			BoardPos key = keyList.get(i);
			board.get(key).toJSON(writer);
			if (i != keyList.size()-1) {
				writer.printf(",");
			}
		}
		writer.printf("]}");
	}
}

class BoardPosComparator implements Comparator<BoardPos> {

	public BoardPosComparator() { }

	public int compare(BoardPos p1, BoardPos p2) {
		return p1.row() == p2.row() ? p1.column() - p2.column() : p1.row() - p2.row();
	}

}

class TroopsPosComparator implements Comparator<BoardPos> {

	public TroopsPosComparator() { }

	public int compare(BoardPos p1, BoardPos p2) {
		return p1.column() == p2.column() ? p1.row() - p2.row() : p1.column() - p2.column();
	}

}
