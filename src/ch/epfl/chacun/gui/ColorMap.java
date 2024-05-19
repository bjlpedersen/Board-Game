package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;

/**
 * This class provides utility methods for mapping player colors to JavaFX colors.
 * It cannot be instantiated.
 *
 * @author Bjork Pedersen (376143)
 */
public class ColorMap {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws AssertionError always
     */
    private ColorMap() {
        throw new AssertionError("ColorMap class cannot be instantiated");
    }

    /**
     * Returns the fill color for a given player.
     *
     * @param player the player for whom the fill color is being retrieved
     * @return a JavaFX Color representing the fill color for the player
     */
    public static javafx.scene.paint.Color fillColor(PlayerColor player) {
        switch (player) {
            case RED -> {
                return Color.RED;
            }
            case BLUE -> {
                return Color.BLUE;
            }
            case GREEN -> {
                return Color.LIME;
            }
            case YELLOW -> {
                return Color.YELLOW;
            }
            case PURPLE -> {
                return Color.PURPLE;
            }
            default -> {
                return Color.BLACK; // Default color, only reached if player is null;
            }
        }
    }

    /**
     * Returns the stroke color for a given player.
     *
     * @param player the player for whom the stroke color is being retrieved
     * @return a JavaFX Color representing the stroke color for the player
     */
    public static Color strokeColor(PlayerColor player) {
        if (player == PlayerColor.RED || player == PlayerColor.PURPLE || player == PlayerColor.BLUE) {
            return Color.WHITE;
        }
        return fillColor(player).deriveColor(0, 1, 0.6, 1);
    }
}