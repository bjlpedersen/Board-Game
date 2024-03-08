package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ZonePartition<Z extends Zone>(Set<Area<Z>> areas) {

    public ZonePartition {
        areas = Set.copyOf(areas);
    }

    public ZonePartition() {
        this(new HashSet<>(Set.of()));
    }

    public Area<Z> areaContaining(Z zone) {
        for (Area<Z> area : areas) {
            if (area.zones().contains(zone)) {
                return area;
            }
        }
        Preconditions.checkArgument(false);
        return null;
    }

    public static final class Builder<Z extends Zone> {
        private Set<Area<Z>> areas = new HashSet<>(Set.of());

        public Builder(ZonePartition<Z> partition) {
            for (Area<Z> area : partition.areas) {
                areas.add(area);
            }
        }

        public void addSingleton(Z zone, int openConnections) {
            areas.add(new Area<>(Set.of(zone), null,openConnections));
        }

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

        public void removeAllOccupantsOf(Area<Z> area) {
            Preconditions.checkArgument(areas.contains(area));
            Area<Z> newArea = new Area<Z>(area.zones(), List.of(), area.openConnections());
            areas.remove(area);
            areas.add(newArea);
        }

        public void union(Z zone1, Z zone2) {
            boolean connected = false;
            for (Area<Z> area1 : areas) {
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

        public ZonePartition<Z>  build() {
            return new ZonePartition<>(areas);
        }

    }
}
