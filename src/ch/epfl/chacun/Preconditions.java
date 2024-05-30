package ch.epfl.chacun;

/**
 * This class provides utility methods for checking conditions.
 * It is a final class and cannot be instantiated.
 *
 * @author Bjork Pedersen (376143)
 */
public final class Preconditions {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Preconditions() {
    }

    /**
     * Checks the truth of the given argument.
     *
     * @param shouldBeTrue The condition to be checked.
     * @throws IllegalArgumentException if the condition is false.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}