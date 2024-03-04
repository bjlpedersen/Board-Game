package ch.epfl.chacun;

import java.util.List;

/**
 * This enum represents the color of a player in the game.
 * A player can be of different colors like RED, BLUE, GREEN, YELLOW, PURPLE.
 * It also provides a list of all possible player colors.
 *
 * @author Bjork Pedersen (376143)
 */
public enum PlayerColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE;

    /**
     * A list of all possible player colors.
     */
    public static final List<PlayerColor> ALL = List.of(values());
}