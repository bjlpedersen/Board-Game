package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class Icon {
    private Icon() {
        throw new AssertionError("ColorMap class cannot be instantiated");
    }

    public static Node newFor(Color player, Occupant.Kind kind) {
        SVGPath svg = new SVGPath();
        if (kind == Occupant.Kind.PAWN) {
            svg.setContent("M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6\n" +
                    "L 6 -10 L 0 -10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z");
            svg.setFill(player);
            svg.setStroke(player);
        } else {
            svg.setContent("M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z");
            svg.setFill(player);
            svg.setStroke(player);
        }
        return svg;
    }
}
