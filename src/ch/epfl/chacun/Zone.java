package ch.epfl.chacun;

import java.util.List;

/**
 * This interface represents a Zone in the game.
 * A Zone can be of different types like Forest, Meadow, Lake, River etc.
 * @author Bjork Pedersen (376143)
 */
public sealed interface Zone {

    /**
     * This enum represents the special powers that a Zone can have.
     * @author Bjork Pedersen (376143)
     */
    enum  SpecialPower {
        SHAMAN,
        LOGBOAT,
        HUNTING_TRAP,
        PIT_TRAP,
        WILD_FIRE,
        RAFT;
    }

    /**
     * This method calculates the tileId from the given zoneId.
     *
     * @param zoneId The id of the zone.
     * @return The id of the tile.
     */
    public static int tileId(int zoneId) {
        return (zoneId - zoneId % 10) / 10;
    }

    /**
     * This method calculates the localId from the given zoneId.
     *
     * @param zoneId The id of the zone.
     * @return The local id within the tile.
     */
    public static int localId(int zoneId) {
        return zoneId % 10;
    }

    /**
     * This method returns the id of the zone.
     *
     * @return The id of the zone.
     */
    public abstract int id();

    /**
     * This method returns the id of the tile that the zone is part of.
     *
     * @return The id of the tile.
     */
    public abstract int tileId();

    /**
     * This method returns the local id of the zone within its tile.
     *
     * @return The local id within the tile.
     */
    public abstract int localId();

    /**
     * This method returns the special power of the zone.
     *
     * @return The special power of the zone.
     */
    public abstract SpecialPower specialPower();

    /**
     * This record represents a Forest zone in the game.
     * @author Bjork Pedersen (376143)
     */
    public record Forest(int id, Kind kind) implements Zone {

        /**
         * This enum represents the kind of a Forest.
         * @author Bjork Pedersen (376143)
         */
        public enum Kind {
            PLAIN,
            WITH_MENHIR,
            WITH_MUSHROOMS;
        }

        /**
         * This method returns the id of the tile that the forest zone is part of.
         *
         * @return The id of the tile.
         */
        @Override
        public int tileId() {
            return (id - id % 10) / 10;
        }

        /**
         * This method returns the local id of the forest zone within its tile.
         *
         * @return The local id within the tile.
         */
        @Override
        public int localId() {
            return id % 10;
        }

        /**
         * This method returns the special power of the forest zone.
         *
         * @return The special power of the forest zone.
         */
        @Override
        public SpecialPower specialPower() {
            return null;
        }

    }

    /**
     * This record represents a Meadow zone in the game.
     * @author Bjork Pedersen (376143)
     */
    public record Meadow(int id, List<Animal> animals, Zone.SpecialPower specialPower) implements Zone {

        /**
         * This constructor creates a new Meadow zone with the given id, list of animals, and special power.
         * The list of animals is copied to ensure immutability.
         *
         * @param id The id of the meadow zone.
         * @param animals The list of animals in the meadow zone.
         * @param specialPower The special power of the meadow zone.
         */
        public Meadow {
            if (animals != null) {
                animals = List.copyOf(animals);
            }
        }

        /**
         * This method returns the id of the tile that the meadow zone is part of.
         *
         * @return The id of the tile.
         */
        public int tileId() {
            return (id - id % 10) / 10;
        }

        /**
         * This method returns the local id of the meadow zone within its tile.
         *
         * @return The local id within the tile.
         */
        public int localId() {
            return id % 10;
        }
    }

    /**
     * This interface represents a Water zone in the game.
     * @author Bjork Pedersen (376143)
     */
    public sealed interface Water extends Zone {

        /**
         * This method returns the count of fish in the water zone.
         *
         * @return The count of fish.
         */
        public int fishCount();
    }

    /**
     * This record represents a Lake zone in the game.
     * @author Bjork Pedersen (376143)
     */
    public record Lake(int id, int fishCount, SpecialPower specialPower) implements ch.epfl.chacun.Zone.Water {
        /**
         * This method returns the count of fish in the lake zone.
         *
         * @return The count of fish.
         */
        public int fishCount() {
            return fishCount;
        }

        /**
         * This method returns the id of the tile that the lake zone is part of.
         *
         * @return The id of the tile.
         */
        @Override
        public int tileId() {
            return (id - id % 10) /10 ;
        }

        /**
         * This method returns the local id of the lake zone within its tile.
         *
         * @return The local id within the tile.
         */
        @Override
        public int localId() {
            return id % 10;
        }
    }

    /**
     * This record represents a River zone in the game.
     * @author Bjork Pedersen (376143)
     */
    public record River(int id, int fishCount, Lake lake) implements ch.epfl.chacun.Zone.Water {

        /**
         * This method returns the id of the tile that the river zone is part of.
         *
         * @return The id of the tile.
         */
        @Override
        public int tileId() {
            return (id - id % 10) / 10;
        }

        /**
         * This method returns the local id of the river zone within its tile.
         *
         * @return The local id within the tile.
         */
        @Override
        public int localId() {
            return id % 10;
        }

        /**
         * This method returns the special power of the river zone.
         *
         * @return The special power of the river zone.
         */
        @Override
        public SpecialPower specialPower() {
            return null;
        }

        /**
         * This method checks if the river zone has a lake.
         *
         * @return true if the river has a lake, false otherwise.
         */
        public boolean hasLake() {
            return lake != null;
        }
    }

}