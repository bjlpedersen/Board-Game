package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface represents a side of a tile in the game.
 * It is a sealed interface, meaning that all implementations of this interface
 * are known and are nested within this interface.
 * @author Bjork Pedersen (376143)
 */
public sealed interface TileSide {

    /**
     * Returns a list of zones that are part of this tile side.
     *
     * @return a list of zones
     */
    public abstract List<Zone> zones();

    /**
     * Checks if this tile side is of the same kind as the provided tile side.
     *
     * @param that the tile side to compare with
     * @return true if the tile sides are of the same kind, false otherwise
     */
    public abstract boolean isSameKindAs(TileSide that);

    /**
     * This record represents a forest side of a tile.
     * @author Bjork Pedersen (376143)
     */
    public record Forest(Zone.Forest forest) implements TileSide {

        /**
         * Returns a list of zones that are part of this forest tile side.
         *
         * @return a list of zones
         */
        @Override
        public List<Zone> zones() {
            List<Zone> zones = List.of(this.forest);
            return zones;
        }

        /**
         * Checks if this forest tile side is of the same kind as the provided tile side.
         *
         * @param that the tile side to compare with
         * @return true if the tile sides are of the same kind, false otherwise
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            for (Zone zone : that.zones()) {
                if (!(zone instanceof Zone.Forest)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * This record represents a meadow side of a tile.
     * @author Bjork Pedersen (376143)
     */
    public record Meadow(Zone.Meadow meadow) implements TileSide {

        /**
         * Returns a list of zones that are part of this meadow tile side.
         *
         * @return a list of zones
         */
        @Override
        public List<Zone> zones() {
            List<Zone> zones = List.of(this.meadow);
            return zones;
        }

        /**
         * Checks if this meadow tile side is of the same kind as the provided tile side.
         *
         * @param that the tile side to compare with
         * @return true if the tile sides are of the same kind, false otherwise
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            for (Zone zone : that.zones()) {
                if (!(zone instanceof Zone.Meadow)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * This record represents a river side of a tile.
     * @author Bjork Pedersen (376143)
     */
    public record River(Zone.Meadow meadow1, Zone.River river, Zone.Meadow meadow2) implements TileSide {

        /**
         * Returns a list of zones that are part of this river tile side.
         *
         * @return a list of zones
         */
        @Override
        public List<Zone> zones() {
            List<Zone> zones = List.of(this.meadow1, this.river, this.meadow2);
            return zones;
        }

        /**
         * Checks if this river tile side is of the same kind as the provided tile side.
         *
         * @param that the tile side to compare with
         * @return true if the tile sides are of the same kind, false otherwise
         */
        @Override
        public boolean isSameKindAs(TileSide that) {
            for (Zone zone : that.zones()) {
                if (zone instanceof Zone.River) {
                    return  true;
                }
            }
            return false;
        }
    }

}