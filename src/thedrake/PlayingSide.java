package thedrake;

import java.io.PrintWriter;

public enum PlayingSide implements JSONSerializable {
    ORANGE, BLUE;

    public void toJSON(PrintWriter writer) {
        writer.printf("\"" + this.toString() + "\"");
    }
}
