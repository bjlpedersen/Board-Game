package ch.epfl.chacun.gui;

import ch.epfl.chacun.PlayerColor;
import javafx.scene.paint.Color;

public class ColorMap {
    private ColorMap() {
        throw new AssertionError("ColorMap class cannot be instantiated");
    }

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

    public static Color strokeColor(PlayerColor player) {
        if (player == PlayerColor.RED || player == PlayerColor.PURPLE || player == PlayerColor.BLUE) {
            return Color.WHITE;
        }
        return fillColor(player).deriveColor(0, 1, 0.6, 1);
    }
}
