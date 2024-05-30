package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.PlayerColor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * This class provides utility methods for creating icons for the game.
 * It cannot be instantiated.
 *
 * @author Bjork Pedersen (376143)
 */
public class Icon {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws AssertionError always
     */
    private Icon() {
        throw new AssertionError("ColorMap class cannot be instantiated");
    }

    /**
     * Creates a new icon for a given player and occupant kind.
     * The icon is represented as a SVGPath node, which can be added to a JavaFX scene graph.
     *
     * @param player the player for whom the icon is being created
     * @param kind   the kind of occupant for which the icon is being created
     * @return a Node representing the icon
     */
    public static Node newFor(PlayerColor player, Occupant.Kind kind) {
        SVGPath svg = new SVGPath();
        Color col = ColorMap.fillColor(player);
        if (kind == Occupant.Kind.PAWN) {
            svg.setContent("M -10 10 H -4 L 0 2 L 6 10 H 12 L 5 0 L 12 -2 L 12 -4 L 6 -6\n" +
                    "L 6 -10 L 0 -10 L -2 -4 L -6 -2 L -8 -10 L -12 -10 L -8 6 Z");
            svg.setFill(col);
            svg.setStroke(ColorMap.strokeColor(player));
        } else {
            svg.setContent("M -8 10 H 8 V 2 H 12 L 0 -10 L -12 2 H -8 Z");
            svg.setFill(col);
            svg.setStroke(ColorMap.strokeColor(player));
        }
        return svg;
    }
}