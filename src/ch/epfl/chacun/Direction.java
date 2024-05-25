package ch.epfl.chacun;

import java.util.List;

/**
 * This enum represents a Direction in the game.
 * A Direction can be NORTH (N), EAST (E), SOUTH (S), or WEST (W).
 * @author Bjork Pedersen (376143)
 */
public enum Direction {
    N,
    E,
    S,
    W;

    /**
     * A list of all possible directions.
     */
    public static final List<Direction> ALL = List.of(values());

    /**
     * The count of all possible directions.
     */
    public static final int COUNT = ALL.size();

    /**
     * This method returns the direction after a given rotation.
     *
     * @param rotation The rotation to be applied.
     * @return The direction after the rotation.
     */
    public Direction rotated(Rotation rotation) {
        return values()[(this.ordinal() + rotation.ordinal()) % COUNT];
    }

    /**
     * This method returns the opposite direction.
     *
     * @return The opposite direction.
     */
    public Direction opposite() {
        return rotated(Rotation.HALF_TURN);
    }
}