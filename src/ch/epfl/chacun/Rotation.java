package ch.epfl.chacun;

import java.util.List;

/**
 * This enum represents a Rotation in the game.
 * A Rotation can be NONE, RIGHT, HALF_TURN, LEFT.
 * It also provides a list of all possible rotations and their count.
 *
 * @author Bjork Pedersen (376143)
 */
public enum Rotation {
    NONE,
    RIGHT,
    HALF_TURN,
    LEFT;

    /**
     * A list of all possible rotations.
     */
    public static final List<Rotation> ALL = List.of(values());

    /**
     * The count of all possible rotations.
     */
    public static final int COUNT = ALL.size();

    /**
     * This method adds the given rotation to the current rotation.
     *
     * @param that The rotation to be added.
     * @return The rotation after the addition.
     */
    public Rotation add(Rotation that) {
        int addedPosition = this.ordinal() + that.ordinal();
        return Rotation.values()[addedPosition % 4];
    }

    /**
     * This method returns the negated rotation of the current rotation.
     *
     * @return The negated rotation.
     */
    public Rotation negated() {
        int position = this.ordinal();
        for (int i = 0; i <= 3; i++) {
            if ((position + i) % 4 == 0) {
                return Rotation.values()[i];
            }
        }
        return null;
    }

    /**
     * This method returns the number of quarter turns clockwise for the current rotation.
     *
     * @return The number of quarter turns clockwise.
     */
    public int quarterTurnsCW() {
        return this.ordinal();
    }

    /**
     * This method returns the degrees clockwise for the current rotation.
     *
     * @return The degrees clockwise.
     */
    public int degreesCW() {
        return quarterTurnsCW() * 90;
    }
}