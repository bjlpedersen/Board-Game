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
        private Set<Area<Z>> areas = new HashSet<>(Set.of());

        /**
         * Constructor for the Builder class.
         * It initializes the areas set with the areas from the given partition.
         * @param partition The partition to copy the areas from.
         */
        public Builder(ZonePartition<Z> partition) {
            for (Area<Z> area : partition.areas) {
                areas.add(area);
            }
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
         * @param color The color of the occupant to add.
         */
        public void addInitialOccupant(Z zone, PlayerColor color) {
            boolean added = false;
            for (Area<Z> area : areas) {
                if (area.zones().contains(zone)) {
                    Preconditions.checkArgument(area.occupants().isEmpty());
                    Area<Z> newArea = new Area<Z>(area.zones(), List.of(color), area.openConnections());
                    areas.remove(area);
                    areas.add(newArea);
                    added = true;
                    break;
                }
            }
            if (!added) {
                Preconditions.checkArgument(false);
            }
        }

        /**
         * Removes an occupant from the area that contains the given zone.
         * @param zone The zone to look for.
         * @param color The color of the occupant to remove.
         */
        public void removeOccupant(Z zone, PlayerColor color) {
            boolean removed = false;
            for (Area<Z> area : areas) {
                if (area.zones().contains(zone)) {
                    Preconditions.checkArgument(area.occupants().contains(color));
                    List<PlayerColor> newOccupants = new ArrayList<>(area.occupants());
                    newOccupants.remove(color);
                    Area<Z> newArea = new Area<Z>(area.zones(), newOccupants, area.openConnections());
                    areas.remove(area);
                    areas.add(newArea);
                    removed = true;
                    break;
                }
            }
            if (!removed) {
                Preconditions.checkArgument(false);
            }
        }

        /**
         * Removes all occupants from the given area.
         * @param area The area to remove the occupants from.
         */
        public void removeAllOccupantsOf(Area<Z> area) {
            Preconditions.checkArgument(areas.contains(area));
            Area<Z> newArea = new Area<Z>(area.zones(), List.of(), area.openConnections());
            areas.remove(area);
            areas.add(newArea);
        }

        /**
         * Unions the areas that contain the given zones.
         * @param zone1 The first zone to look for.
         * @param zone2 The second zone to look for.
         */
        public void union(Z zone1, Z zone2) {
            boolean connected = false;
            Set<Area<Z>> areasCopy = Set.copyOf(areas);
            for (Area<Z> area1 : areasCopy) {
                for (Area<Z> area2 : areas) {
                    if (area1.zones().contains(zone1) && area2.zones().contains(zone2)) {
                        Area<Z> newArea = area1.connectTo(area2);
                        areas.remove(area1);
                        areas.remove(area2);
                        areas.add(newArea);
                        connected = true;
                        break;
                    }
                }
            }
            if (!connected) {
                Preconditions.checkArgument(false);
            }
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