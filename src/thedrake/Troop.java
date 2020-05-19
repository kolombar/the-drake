package thedrake;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.IntStream;

import static thedrake.TroopFace.AVERS;

public class Troop implements JSONSerializable {

    private final String name;
    private final Offset2D aversPivot;
    private final Offset2D reversPivot;
    private final List<TroopAction> aversActions;
    private final List<TroopAction> reversActions;

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = aversPivot;
        this.reversPivot = reversPivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public Troop(String name, Offset2D pivot, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = pivot;
        this.reversPivot = pivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public Troop(String name, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = new Offset2D(1, 1);
        this.reversPivot = new Offset2D(1, 1);
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public String name() {
        return this.name;
    }

    public Offset2D pivot(TroopFace face) {
       if (face == AVERS) {
           return aversPivot;
       } else {
           return reversPivot;
       }
    }

    public List<TroopAction> actions(TroopFace face) {
        if (face == AVERS) {
            return aversActions;
        } else {
            return reversActions;
        }
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("\"" + name + "\"");
        /*writer.printf("{\"name\":" + name + ",\"aversPivot\":" + aversPivot.toString() + ",\"reversPivot\":" + reversPivot.toString() + ",\"aversActions\":[");
        IntStream
                .range(0, aversActions.size())
                .forEach(ind -> {
            writer.printf(aversActions.get(ind).toString());
            if (ind != aversActions.size()-1) {
                writer.printf(",");
            }
        });
        writer.printf("],\"reversActions\":[");
        IntStream
                .range(0, reversActions.size())
                .forEach(ind -> {
                    writer.printf(reversActions.get(ind).toString());
                    if (ind != reversActions.size()-1) {
                        writer.printf(",");
                    }
                });
        writer.printf("]}");*/
    }
}
