package ch.epfl.chacun;

/**
 * This class represents a position on the board with x and y coordinates.
 * @author Bjork Pedersen (376143)
 */
public record Pos(int x, int y) {
    /**
     * A constant representing the origin (0,0) in the 2D space.
     */
    public static final Pos ORIGIN = new Pos(0, 0);

    /**
     * Returns a new Pos that is a translation of the current Pos.
     *
     * @param dX The amount to translate in the x direction.
     * @param dY The amount to translate in the y direction.
     * @return A new Pos that is the translation of the current Pos.
     */
    public Pos translated(int dX, int dY) {
        return new Pos(x + dX, y + dY);
    }

    /**
     * Returns a new Pos that is the neighbor of the current Pos in the given direction.
     *
     * @param direction The direction of the neighbor.
     * @return A new Pos that is the neighbor of the current Pos in the given direction.
     */
    public Pos neighbor(Direction direction) {
        return switch (direction) {
            case N -> translated(0, -1);
            case E -> translated(1, 0);
            case S -> translated(0, 1);
            case W -> translated(-1, 0);
        };
    }
}