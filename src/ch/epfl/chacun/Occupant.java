package ch.epfl.chacun;

import java.util.Objects;

/**
 * This class represents an Occupant in the game.
 * An Occupant can be of different kinds like PAWN, HUT etc.
 *
 * @author Bjork Pedersen (376143)
 */
public record Occupant(Kind kind, int zoneId) {

    /**
     * This enum represents the kind of an Occupant.
     *
     * @author Bjork Pedersen (376143)
     */
    public enum Kind{
        PAWN,
        HUT;
    }

    /**
     * This constructor creates a new Occupant with the given kind and zoneId.
     * It checks if the kind is not null and if the zoneId is not negative.
     *
     * @param kind The kind of the occupant.
     * @param zoneId The id of the zone that the occupant is part of.
     * @throws NullPointerException if the kind is null.
     * @throws IllegalArgumentException if the zoneId is negative.
     */
    public Occupant {
        Objects.requireNonNull(kind, "The kind must not be null");
        Preconditions.checkArgument(zoneId >= 0);
    }

    /**
     * This method returns the count of occupants for the given kind.
     *
     * @param kind The kind of the occupant.
     * @return The count of occupants.
     * @throws IllegalArgumentException if the kind is not PAWN or HUT.
     */
    public static int occupantsCount(Kind kind) {
        int result;
        return switch (kind) {
            case PAWN -> {
                result = 5;
                yield result;
            }
            case HUT -> {
                result = 3;
                yield result;
            }
            default -> throw new IllegalArgumentException("The kind can't be: " + kind);
        };
    }
}