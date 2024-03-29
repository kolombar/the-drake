package thedrake;

import java.io.PrintWriter;
import java.util.*;

import static thedrake.TroopFace.*;

public class TroopTile implements Tile, JSONSerializable {

    private final Troop troop;
    private final PlayingSide side;
    private final TroopFace face;

    // Konstruktor
    public TroopTile(Troop troop, PlayingSide side, TroopFace face) {
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    // Vrací barvu, za kterou hraje jednotka na této dlaždici
    public PlayingSide side() {
        return side;
    }

    // Vrací stranu, na kterou je jednotka otočena
    public TroopFace face() {
        return face;
    }

    // Jednotka, která stojí na této dlaždici
    public Troop troop() {
        return troop;
    }

    // Vrací False, protože na dlaždici s jednotkou se nedá vstoupit
    public boolean canStepOn() {
        return false;
    }

    // Vrací True
    public boolean hasTroop() {
        return true;
    }

    // Vytvoří novou dlaždici, s jednotkou otočenou na opačnou stranu
// (z rubu na líc nebo z líce na rub)
    public TroopTile flipped() {
        if (face == AVERS) {
            return new TroopTile(troop, side, REVERS);
        } else {
            return new TroopTile(troop, side, AVERS);
        }
    }

    public List<Move> movesFrom(BoardPos pos, GameState state) {
        List<Move> result = new ArrayList<Move>();
        List<TroopAction> troopActions = troop().actions(face());
        for (TroopAction action : troopActions) {
            result.addAll(action.movesFrom(pos, state.sideOnTurn(), state));
        }
        return result;
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"troop\":");
        troop.toJSON(writer);
        writer.printf(",\"side\":");
        side.toJSON(writer);
        writer.printf(",\"face\":");
        face.toJSON(writer);
        writer.printf("}");
    }
}
