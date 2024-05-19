package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents a partition of zones in the game.
 * @param <Z> The type of Zone this partition can contain.
 * @author Bjork Pedersen (376143)
 */
public record ZonePartition<Z extends Zone>(Set<Area<Z>> areas) {

    /**
     * Constructor for the ZonePartition record.
     * It makes a copy of the areas set.
     */
    public ZonePartition {
        areas = Set.copyOf(areas);
    }

    /**
     * Default constructor for the ZonePartition record.
     * It initializes the areas set as an empty set.
     */
    public ZonePartition() {
        this(new HashSet<>(Set.of()));
    }

    /**
     * Returns the area that contains the given zone.
     * @param zone The zone to look for.
     * @return The area that contains the zone.
     */
    public Area<Z> areaContaining(Z zone) {
        for (Area<Z> area : areas) {
            if (area.zones().contains(zone)) {
                return area;
            }
        }
        Preconditions.checkArgument(false);
        return null;
    }

    /**
     * This class is a builder for the ZonePartition class.
     * @param <Z> The type of Zone this builder can contain.
     */
    public static final class Builder<Z extends Zone> {
        private final Set<Area<Z>> areas = new HashSet<>(Set.of());

        /**
         * Constructor for the Builder class.
         * It initializes the areas set with the areas from the given partition.
         * @param partition The partition to copy the areas from.
         */
        public Builder(ZonePartition<Z> partition) {
            areas.addAll(partition.areas);
        }

        /**
         * Adds a new singleton area with the given zone and open connections to the areas set.
         * @param zone The zone to add.
         * @param openConnections The number of open connections.
         */
        public void addSingleton(Z zone, int openConnections) {
            areas.add(new Area<>(Set.of(zone), null,openConnections));
        }

        /**
         * Adds an initial occupant to the area that contains the given zone.
         * @param zone The zone to look for.
         * @throws IllegalArgumentException if the occupant cannot be added
         */
        private Area<Z> findAreaContaining(Z zone) {
            for (Area<Z> area : areas) {
                if (area.zones().contains(zone)) {
                    return area;
                }
            }
            return null;
        }

        /**
         * Adds an initial occupant to the area that contains the given zone.
         *
         * @param zone The zone to look for.
         * @param color The color of the player to be added as an occupant.
         * @throws IllegalArgumentException if the area is null or already has occupants.
         */
        public void addInitialOccupant(Z zone, PlayerColor color) {
            Area<Z> area = findAreaContaining(zone);
            Preconditions.checkArgument(area != null && area.occupants().isEmpty());
            Area<Z> newArea = area.withInitialOccupant(color);
            areas.remove(area);
            areas.add(newArea);
        }

        /**
         * Removes an occupant from the area that contains the given zone.
         *
         * @param zone The zone to look for.
         * @param color The color of the player to be removed as an occupant.
         * @throws IllegalArgumentException if the area is null or does not contain the occupant.
         */
        public void removeOccupant(Z zone, PlayerColor color) {
            Area<Z> area = findAreaContaining(zone);
            Preconditions.checkArgument(area != null && area.occupants().contains(color));
            List<PlayerColor> newOccupants = new ArrayList<>(area.occupants());
            newOccupants.remove(color);
            Area<Z> newArea = area.withoutOccupant(color);
            areas.remove(area);
            areas.add(newArea);
        }

        /**
         * Unions two areas that contain the given zones.
         *
         * @param zone1 The first zone to look for.
         * @param zone2 The second zone to look for.
         * @throws IllegalArgumentException if either of the areas is null.
         */
        public void union(Z zone1, Z zone2) {
            Area<Z> area1 = findAreaContaining(zone1);
            Area<Z> area2 = findAreaContaining(zone2);
            Preconditions.checkArgument(area1 != null && area2 != null);
            Area<Z> newArea = area1.connectTo(area2);
            areas.remove(area1);
            areas.remove(area2);
            areas.add(newArea);
        }

        /**
         * Removes all occupants from the given area.
         * @param area The area to remove the occupants from.
         */
        public void removeAllOccupantsOf(Area<Z> area) {
            Preconditions.checkArgument(areas.contains(area));
            Area<Z> newArea = area.withoutOccupants();
            areas.remove(area);
            areas.add(newArea);
        }

        /**
         * Builds a new ZonePartition with the current areas set.
         * @return The new ZonePartition.
         */
        public ZonePartition<Z>  build() {
            return new ZonePartition<>(areas);
        }

    }
}