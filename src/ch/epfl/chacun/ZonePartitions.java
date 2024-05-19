package ch.epfl.chacun;



/**
 * Represents the partitions of different types of zones in a game.
 * @author Bjork Pedersen (376143)
 */
public record ZonePartitions(ZonePartition<Zone.Forest> forests, ZonePartition<Zone.Meadow> meadows,
                             ZonePartition<Zone.River> rivers, ZonePartition<Zone.Water> riverSystems) {


    /**
     * An empty ZonePartitions instance.
     */
    public static final ZonePartitions EMPTY = new ZonePartitions(new ZonePartition<Zone.Forest>(),
            new ZonePartition<Zone.Meadow>(),
            new ZonePartition<Zone.River>(),
            new ZonePartition<Zone.Water>());

    /**
     * Builder class for ZonePartitions.
     * @author Bjork Pedersen (376143)
     */
    public static final class Builder {
        private final ZonePartition.Builder<Zone.Meadow> meadowBuilder;
        private final ZonePartition.Builder<Zone.Forest> forestBuilder;
        private final ZonePartition.Builder<Zone.River> riverBuilder;
        private final ZonePartition.Builder<Zone.Water> waterBuilder;

        /**
         * Initializes the builder with an initial ZonePartitions instance.
         * @param initial The initial ZonePartitions instance.
         */
        public Builder(ZonePartitions initial) {
            meadowBuilder = new ZonePartition.Builder<>(initial.meadows());
            forestBuilder = new ZonePartition.Builder<>(initial.forests());
            riverBuilder = new ZonePartition.Builder<>(initial.rivers());
            waterBuilder = new ZonePartition.Builder<>(initial.riverSystems());

        }

        /**
         * Adds a tile to the zone partitions.
         * @param tile The tile to add.
         * @throws IllegalArgumentException if the tile cannot be added
         */
        public void addTile(Tile tile) {
            int[] openConnectionForZones = new int[10];
            for (Zone z : tile.sideZones()) {
                for (TileSide side : tile.sides()) {
                    if (side.zones().contains(z)) {
                        openConnectionForZones[z.localId()]++;
                    }
                }
                if (z instanceof Zone.River river && river.hasLake()) {
                    openConnectionForZones[river.lake().localId()]++;
                    openConnectionForZones[river.localId()]++;
                }
            }
            for (Zone z : tile.zones()) {
                switch (z) {
                    case Zone.Forest forest -> forestBuilder.addSingleton(forest, openConnectionForZones[z.localId()]);
                    case Zone.Meadow meadow -> meadowBuilder.addSingleton(meadow, openConnectionForZones[z.localId()]);
                    case Zone.River river -> {
                        int openConnectionsRiver;
                        if (((Zone.River) z).hasLake()) {
                            openConnectionsRiver = openConnectionForZones[z.localId()] - 1;
                            waterBuilder.addSingleton(river, openConnectionsRiver + 1);
                            riverBuilder.addSingleton(river, openConnectionsRiver);
                        } else {
                            openConnectionsRiver = openConnectionForZones[z.localId()];
                            waterBuilder.addSingleton(river, openConnectionsRiver);
                            riverBuilder.addSingleton(river, openConnectionsRiver);
                        }
                    }
                    case Zone.Lake lake -> waterBuilder.addSingleton(lake, openConnectionForZones[z.localId()]);
                }
            }
            for (Zone z : tile.zones()) {
                if (z instanceof Zone.River river && river.hasLake()) {
                    waterBuilder.union(river, river.lake());
                }
            }
        }

        /**
         * Connects two sides of different tiles.
         * @param s1 The first side to connect.
         * @param s2 The second side to connect.
         * @throws IllegalArgumentException if the sides cannot be connected
         */
        public void connectSides(TileSide s1, TileSide s2) {
            switch (s1) {
                case TileSide.Meadow(Zone.Meadow m1)
                    when s2 instanceof TileSide.Meadow(Zone.Meadow m2) ->
                        meadowBuilder.union(m1, m2);
                case TileSide.Forest(Zone.Forest f1)
                    when s2 instanceof TileSide.Forest(Zone.Forest f2) ->
                        forestBuilder.union(f1, f2);
                case TileSide.River(Zone.Meadow m3, Zone.River r1, Zone.Meadow m4)
                    when s2 instanceof TileSide.River(Zone.Meadow m5, Zone.River r2, Zone.Meadow m6)  -> {
                        riverBuilder.union(r1, r2);
                        meadowBuilder.union(m3, m6);
                        meadowBuilder.union(m4, m5);
                        waterBuilder.union(r1, r2);
                }
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Adds an initial occupant to a zone.
         * @param player The player color of the occupant.
         * @param occupantKind The kind of the occupant.
         * @param occupiedZone The zone to add the occupant to.
         * @throws IllegalArgumentException if the occupant cannot be added
         */
        public void addInitialOccupant(PlayerColor player, Occupant.Kind occupantKind, Zone occupiedZone) {
            switch(occupiedZone) {
                case Zone.Meadow ignored -> {
                    Preconditions.checkArgument(occupantKind == Occupant.Kind.PAWN);
                    meadowBuilder.addInitialOccupant((Zone.Meadow) occupiedZone, player);
                }
                case Zone.Forest ignored -> {
                    Preconditions.checkArgument(occupantKind == Occupant.Kind.PAWN);
                    forestBuilder.addInitialOccupant((Zone.Forest) occupiedZone, player);
                }
                case Zone.River ignored -> {
                    if (((Zone.River) occupiedZone).hasLake()) {
                        Preconditions.checkArgument(occupantKind == Occupant.Kind.PAWN);
                    }
                    if (occupantKind == Occupant.Kind.PAWN) {
                        riverBuilder.addInitialOccupant((Zone.River) occupiedZone, player);
                    } else {
                        waterBuilder.addInitialOccupant((Zone.Water) occupiedZone, player);
                    }
                }
                case Zone.Lake ignored -> {
                    Preconditions.checkArgument(occupantKind == Occupant.Kind.HUT);
                    waterBuilder.addInitialOccupant((Zone.Lake) occupiedZone, player);
                }
            }
        }

        /**
         * Removes a pawn from a zone.
         * @param player The player color of the pawn.
         * @param occupiedZone The zone to remove the pawn from.
         * @throws IllegalArgumentException if the pawn cannot be removed
         */
        public void removePawn(PlayerColor player, Zone occupiedZone) {
            switch(occupiedZone) {
                case Zone.Meadow ignored -> meadowBuilder.removeOccupant((Zone.Meadow) occupiedZone, player);
                case Zone.Forest ignored -> forestBuilder.removeOccupant((Zone.Forest) occupiedZone, player);
                case Zone.River ignored -> riverBuilder.removeOccupant((Zone.River) occupiedZone, player);
                case Zone.Lake ignored -> throw new IllegalArgumentException();
            }
        }

        /**
         * Clears all gatherers from a forest area.
         * @param forest The forest area to clear the gatherers from.
         * @throws IllegalArgumentException if the gatherers cannot be cleared
         */
        public void clearGatherers(Area<Zone.Forest> forest) {
            forestBuilder.removeAllOccupantsOf(forest);
        }

        /**
         * Clears all fishers from a river area.
         * @param river The river area to clear the fishers from.
         * @throws IllegalArgumentException if the fishers cannot be cleared
         */
        public void clearFishers(Area<Zone.River> river) {
            riverBuilder.removeAllOccupantsOf(river);
        }

        /**
         * Builds a ZonePartitions instance.
         * @return The built ZonePartitions instance.
         */
        public ZonePartitions build() {
            return new ZonePartitions(
                    forestBuilder.build(),
                    meadowBuilder.build(),
                    riverBuilder.build(),
                    waterBuilder.build());
        }
    }
}