package ch.epfl.chacun;

import java.util.*;

/**
 * Represents a game board with various methods to manipulate and query the state of the board.
 * @author Bjork Pedersen (376143)
 */
public class Board {
    private final PlacedTile[] PLACED_TILES;
    private final int[] PLACED_TILES_ORDER;
    private final ZonePartitions ZONE_PARTITIONS;
    private final Set<Animal> DELETED_ANIMALS;
    public final static int REACH = 12;
    public final static Board EMPTY = new Board(new PlacedTile[625], new int[95], ZonePartitions.EMPTY, Set.of());

    /**
     * Constructs a new Board with the given placed tiles, order of placed tiles, zone partitions, and deleted animals.
     *
     * @param placedTiles the placed tiles on the board
     * @param placedTilesOrder the order of the placed tiles
     * @param zonePartitions the partitions of the zones on the board
     * @param deletedAnimals the set of deleted animals on the board
     */
    private Board(PlacedTile[] placedTiles, int[] placedTilesOrder, ZonePartitions zonePartitions, Set<Animal> deletedAnimals) {
        this.PLACED_TILES = placedTiles;
        this.PLACED_TILES_ORDER = placedTilesOrder;
        this.ZONE_PARTITIONS = zonePartitions;
        this.DELETED_ANIMALS = deletedAnimals;
    }

    /**
     * Returns the tile at the given position.
     *
     * @param pos the position of the tile
     * @return the tile at the given position
     */
    public PlacedTile tileAt(Pos pos) {
        if (pos.x() < -REACH || pos.x() > REACH || pos.y() < -REACH || pos.y() > REACH) {
            return null;
        }
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            if (placedTile != null && placedTile.pos().equals(pos)) {
                return placedTile;
            }
        }
        return null;
    }

    /**
     * Returns the tile with the given ID.
     *
     * @param tileId the ID of the tile
     * @return the tile with the given ID
     */
    public PlacedTile tileWithId(int tileId) {
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            if (placedTile != null && placedTile.tile().id() == tileId) {
                return placedTile;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the set of cancelled animals.
     *
     * @return the set of cancelled animals
     */
    public Set<Animal> cancelledAnimals() {
        return Collections.unmodifiableSet(DELETED_ANIMALS);
    }

    /**
     * Returns the set of occupants.
     *
     * @return the set of occupants
     */
    public Set<Occupant> occupants() {
        Set<Occupant> occupants = new HashSet<>();
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            occupants.add(placedTile.occupant());
        }
        return occupants;
    }

    /**
     * Returns the area of the given forest zone.
     *
     * @param forest the forest zone
     * @return the area of the given forest zone
     */
    public Area<Zone.Forest> forestArea(Zone.Forest forest) {
        return ZONE_PARTITIONS.forests().areaContaining(forest);
    }

    /**
     * Returns the area of the given meadow zone.
     *
     * @param meadow the meadow zone
     * @return the area of the given meadow zone
     */
    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        return ZONE_PARTITIONS.meadows().areaContaining(meadow);
    }

    /**
     * Returns the area of the given river zone.
     *
     * @param riverZone the river zone
     * @return the area of the given river zone
     */
    public Area<Zone.River> riverArea(Zone.River riverZone) {
        return ZONE_PARTITIONS.rivers().areaContaining(riverZone);
    }

    /**
     * Returns the area of the given water zone.
     *
     * @param water the water zone
     * @return the area of the given water zone
     */
    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        return ZONE_PARTITIONS.riverSystems().areaContaining(water);
    }

    /**
     * Returns the set of meadow areas.
     *
     * @return the set of meadow areas
     */
    public Set<Area<Zone.Meadow>> meadowAreas() {
        Set<Area<Zone.Meadow>> meadowAreas = new HashSet<>();
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            for (Zone.Meadow meadowZone : placedTile.meadowZones()) {
                meadowAreas.add(meadowArea(meadowZone));
            }
        }
        return meadowAreas;
    }

    /**
     * Returns the set of water areas.
     *
     * @return the set of water areas
     */
    public Set<Area<Zone.Water>> riverSystemAreas() {
        Set<Area<Zone.Water>> waterAreas = new HashSet<>();
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            for (Zone.Water riverZone : placedTile.riverZones()) {
                waterAreas.add(riverSystemArea(riverZone));
            }
        }
        return waterAreas;
    }

    /**
     * Returns the adjacent meadow of the given position and meadow zone.
     *
     * @param pos the position
     * @param meadowZone the meadow zone
     * @return the adjacent meadow of the given position and meadow zone
     */
    public Area<Zone.Meadow> adjacentMeadow(Pos pos, Zone.Meadow meadowZone) {
        Set<Zone.Meadow> zones = new HashSet<>();
        List<PlayerColor> occupants = meadowArea(meadowZone).occupants();
        int x = pos.x();
        int y = pos.y();
        Set<Pos> possiblePositions = Set.of(
                new Pos(x - 1, y - 1),
                new Pos(x + 1, y - 1),
                new Pos(x - 1, y + 1),
                new Pos(x + 1, y + 1),
                pos.neighbor(Direction.N),
                pos.neighbor(Direction.E),
                pos.neighbor(Direction.S),
                pos.neighbor(Direction.W));
        for (Zone.Meadow meadow : meadowArea(meadowZone).zones()) {
            if (possiblePositions.contains(tileWithId(meadow.tileId()).pos())) {
                zones.add(meadow);
            }
        }
        return new Area<>(zones, occupants, 0);
    }

    /**
     * Returns the count of the given occupant kind for the given player.
     *
     * @param player the player
     * @param occupantKind the occupant kind
     * @return the count of the given occupant kind for the given player
     */
    public int occupantCount(PlayerColor player, Occupant.Kind occupantKind) {
        int count = 0;
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            if (placedTile.placer() == player && placedTile.occupant().kind() == occupantKind) {
                count++;
            }
        }
        return count;
    }

    /**
     * finds the set of all direcitons minus the ones that are would be out of bounds if it moved by one more tile.
     * @param placedTile the tile that has been placed
     * @return the set of all direcitons minus the ones that are would be out of bounds if it moved by one more tile.
     */
    private static Set<Direction> tileOnEdge(PlacedTile placedTile) {
        Set<Direction> possibleDirections = new HashSet<>(Set.of(Direction.N, Direction.W, Direction.E, Direction.S));
        if (placedTile.pos().x() == -REACH) {
            possibleDirections.remove(Direction.W);
        } else if (placedTile.pos().x() == REACH) {
            possibleDirections.remove(Direction.E);
        }
        if (placedTile.pos().y() == -REACH) {
            possibleDirections.remove(Direction.N);
        } else if (placedTile.pos().y() == REACH) {
            possibleDirections.remove(Direction.S);
        }
        return possibleDirections;
    }

    /**
     * Returns the set of possible insertion positions.
     *
     * @return the set of possible insertion positions
     */
    public Set<Pos> insertionPositions() {
        Set<Pos> insertionPositions = new HashSet<>();
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            Set<Direction> possibleDirections = tileOnEdge(placedTile);
            for (Direction d : possibleDirections) {
                if (tileAt(placedTile.pos().neighbor(d)) == null) {
                    insertionPositions.add(placedTile.pos().neighbor(d));
                }
            }
        }
        return insertionPositions;
    }

    /**
     * Returns the last placed tile.
     *
     * @return the last placed tile
     */
    public PlacedTile lastPlacedTile() {
        Integer lastPlacedId = null;
        for (int i : PLACED_TILES_ORDER) {
            if (tileWithId(i) != null) {
                lastPlacedId = i;
            } else {
                break;
            }
        }
        if (lastPlacedId == null) {
            return null;
        }
        return tileWithId(lastPlacedId);
    }

    /**
     * Returns the set of forests closed by the last tile.
     *
     * @return the set of forests closed by the last tile
     */
    public Set<Area<Zone.Forest>> forestsClosedByLastTile() {
        Set<Area<Zone.Forest>> closedForests = new HashSet<>();
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            for (Zone.Forest forestZone : placedTile.forestZones()) {
                if (forestArea(forestZone).openConnections() == 0) {
                    closedForests.add(forestArea(forestZone));
                }
            }
        }
        return closedForests;
    }

    /**
     * Returns the set of rivers closed by the last tile.
     *
     * @return the set of rivers closed by the last tile
     */
    public Set<Area<Zone.River>> riversClosedByLastTile() {
        Set<Area<Zone.River>> closedRivers = new HashSet<>();
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            for (Zone.River riverZone : placedTile.riverZones()) {
                if (riverArea(riverZone).openConnections() == 0) {
                    closedRivers.add(riverArea(riverZone));
                }
            }
        }
        return closedRivers;
    }

    /**
     * Returns whether the given tile can be added to the board.
     *
     * @param tile the tile to be added
     * @return true if the tile can be added, false otherwise
     */
    public boolean canAddTile(PlacedTile tile) {
        if (!insertionPositions().contains(tile.pos())) {
            return false;
        }

        Direction[] directions = {Direction.N, Direction.E, Direction.S, Direction.W};
        for (Direction direction : directions) {
            PlacedTile neighbour = tileAt(tile.pos().neighbor(direction));
            if (neighbour != null && !areTilesCompatible(neighbour, tile, direction)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the given tiles are compatible in the given direction.
     *
     * @param tile1 the first tile
     * @param tile2 the second tile
     * @param direction the direction
     * @return true if the tiles are compatible, false otherwise
     */
    private static boolean areTilesCompatible(PlacedTile tile1, PlacedTile tile2, Direction direction) {
        return switch (direction) {
            case N -> tile1.tile().s().isSameKindAs(tile2.tile().n());
            case E -> tile1.tile().w().isSameKindAs(tile2.tile().e());
            case S -> tile1.tile().n().isSameKindAs(tile2.tile().s());
            case W -> tile1.tile().e().isSameKindAs(tile2.tile().w());
        };
    }

    /**
     * Returns whether the given tile can be placed on the board.
     *
     * @param tile the tile to be placed
     * @return true if the tile can be placed, false otherwise
     */
    public boolean couldPlaceTile(Tile tile) {
        Set<Pos> possibleInsertionsPositions = insertionPositions();
        for (Pos pos : possibleInsertionsPositions) {
            for (Rotation rotation : Rotation.values()) {
                if (canAddTile(new PlacedTile(tile, PlayerColor.GREEN, rotation, pos, null ))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a new board with the given tile added.
     *
     * @param tile the tile to be added
     * @return a new board with the given tile added
     */
    public Board withNewTile(PlacedTile tile) {
        Preconditions.checkArgument(canAddTile(tile) && PLACED_TILES_ORDER[0] == 0);
        PlacedTile[] newPlacedTiles = PLACED_TILES.clone();
        int indexInPlacedTiles = tile.pos().x() + REACH + (tile.pos().y() + REACH) * (REACH * 2 + 1);
        newPlacedTiles[indexInPlacedTiles] = tile;
        int[] newPlacedTilesOrder = PLACED_TILES_ORDER.clone();
        newPlacedTilesOrder[0] = tile.id();
        return new Board(newPlacedTiles, newPlacedTilesOrder, ZONE_PARTITIONS, DELETED_ANIMALS);
    }

    /**
     * Returns a new board with the given occupant added.
     *
     * @param occupant the occupant to be added
     * @return a new board with the given occupant added
     */
    public Board withOccupant(Occupant occupant) {
        int zoneId = occupant.zoneId();
        Preconditions.checkArgument(tileWithId((zoneId - (zoneId % 10)) / 10).occupant() == null);
        PlacedTile tile = tileWithId((zoneId - (zoneId % 10)) / 10);
        PlacedTile[] newPlacedTiles = PLACED_TILES.clone();
        int indexInPlacedTiles = tile.pos().x() + REACH + (tile.pos().y() + REACH) * (REACH * 2 + 1);
        newPlacedTiles[indexInPlacedTiles] = new PlacedTile(tile.tile(), tile.placer(), tile.rotation(), tile.pos(), occupant);
        ZonePartitions.Builder zonePartitions = new ZonePartitions.Builder(ZONE_PARTITIONS);
        zonePartitions.addInitialOccupant(tile.placer() ,occupant.kind(), tile.zoneWithId(zoneId));
        return new Board(newPlacedTiles, PLACED_TILES_ORDER, zonePartitions.build(), DELETED_ANIMALS);
    }

    /**
     * Returns a new board with the given occupant removed.
     *
     * @param occupant the occupant to be removed
     * @return a new board with the given occupant removed
     */
    public Board withoutOccupant(Occupant occupant) {
        int zoneId = occupant.zoneId();
        PlacedTile tile = tileWithId((zoneId - (zoneId % 10)) / 10);
        PlacedTile[] newPlacedTiles = PLACED_TILES.clone();
        int indexInPlacedTiles = tile.pos().x() + REACH + (tile.pos().y() + REACH) * (REACH * 2 + 1);
        newPlacedTiles[indexInPlacedTiles] = new PlacedTile(tile.tile(), tile.placer(), tile.rotation(), tile.pos(), null);
        ZonePartitions.Builder zonePartitions = new ZonePartitions.Builder(ZONE_PARTITIONS);
        zonePartitions.removePawn(tile.placer(), tile.zoneWithId(zoneId));
        return new Board(newPlacedTiles, PLACED_TILES_ORDER, zonePartitions.build(), DELETED_ANIMALS);
    }

    /**
     * Returns a new board without gatherers or fishers in the given forests and rivers.
     *
     * @param forests the forests
     * @param rivers the rivers
     * @return a new board without gatherers or fishers in the given forests and rivers
     */
    public Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        ZonePartition.Builder<Zone.Forest> forestBuilder = new ZonePartition.Builder<>(ZONE_PARTITIONS.forests());
        for (Area<Zone.Forest> forest : forests) {
            forestBuilder.removeAllOccupantsOf(forest);
        }
        ZonePartition.Builder<Zone.River> riverBuilder = new ZonePartition.Builder<>(ZONE_PARTITIONS.rivers());
        for (Area<Zone.River> river : rivers) {
            riverBuilder.removeAllOccupantsOf(river);
        }
        //Might need to do the same with riverSystems on top of just rivers
        //
        //
        //
        //
        ZonePartition<Zone.Forest> forestPartition = forestBuilder.build();
        ZonePartition<Zone.River> riverPartition = riverBuilder.build();
        PlacedTile[] newPlacedTiles = removeOccupantsInZonePartitionsForest(forests, PLACED_TILES.clone());
        newPlacedTiles = removeOccupantsInZonePartitionsRiver(rivers, newPlacedTiles);

        return new Board(newPlacedTiles, PLACED_TILES_ORDER, new ZonePartitions(forestPartition, ZONE_PARTITIONS.meadows(), riverPartition, ZONE_PARTITIONS.riverSystems()), DELETED_ANIMALS);
    }

    /**
     * Removes occupants in forests
     * @param forests the Set of allforest areas
     * @param placedTiles the list of placed tiles it has to remove occupants from
     * @return new board with no occupants in forests
     */
    private PlacedTile[] removeOccupantsInZonePartitionsForest(Set<Area<Zone.Forest>> forests, PlacedTile[] placedTiles) {
        PlacedTile[] newPlacedTiles = PLACED_TILES.clone();
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            for (Zone.Forest forest : placedTile.forestZones()) {
                Area<Zone.Forest> area = ZONE_PARTITIONS.forests().areaContaining(forest);
                if (forests.contains(area) && area.isOccupied()) {
                    PlacedTile newTile = new PlacedTile(placedTile.tile(), placedTile.placer(), placedTile.rotation(), placedTile.pos(), null);
                    int indexInPlacedTiles = placedTile.pos().x() + REACH + (placedTile.pos().y() + REACH) * (REACH * 2 + 1);
                    newPlacedTiles[indexInPlacedTiles] = newTile;
                }
            }
        }
        return newPlacedTiles;
    }

    /**
     * Removes occupants in rivers
     * @param rivers the Set of all river areas
     * @param placedTiles the list of placed tiles it has to remove occupants from
     * @return a new board with no occupants in rivers
     */
    private PlacedTile[] removeOccupantsInZonePartitionsRiver(Set<Area<Zone.River>> rivers, PlacedTile[] placedTiles) {
        PlacedTile[] newPlacedTiles = PLACED_TILES.clone();
        for (int orderId : PLACED_TILES_ORDER) {
            PlacedTile placedTile = tileWithId(orderId);
            for (Zone.River river : placedTile.riverZones()) {
                Area<Zone.River> area = ZONE_PARTITIONS.rivers().areaContaining(river);
                if (rivers.contains(area) && area.isOccupied()) {
                    PlacedTile newTile = new PlacedTile(placedTile.tile(), placedTile.placer(), placedTile.rotation(), placedTile.pos(), null);
                    int indexInPlacedTiles = placedTile.pos().x() + REACH + (placedTile.pos().y() + REACH) * (REACH * 2 + 1);
                    newPlacedTiles[indexInPlacedTiles] = newTile;
                }
            }
        }
        return newPlacedTiles;
    }

    /**
     * Returns a new board with more cancelled animals.
     *
     * @param newlyCancelledAnimals the newly cancelled animals
     * @return a new board with more cancelled animals
     */
    public Board withMoreCancelledAnimals(Set<Animal> newlyCancelledAnimals) {
        Set<Animal> newCancelledAnimals = new HashSet<>(DELETED_ANIMALS);
        newCancelledAnimals.addAll(newlyCancelledAnimals);
        return new Board(PLACED_TILES, PLACED_TILES_ORDER, ZONE_PARTITIONS, newCancelledAnimals);
    }

    /**
     * Returns whether the given object is equal to this board.
     * @param obj the object we wish to compare
     * @return true of equal false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Board other)) {
            return false;
        }
        return Arrays.equals(PLACED_TILES, other.PLACED_TILES) &&
                Arrays.equals(PLACED_TILES_ORDER, other.PLACED_TILES_ORDER) &&
                Objects.equals(ZONE_PARTITIONS, other.ZONE_PARTITIONS) &&
                Objects.equals(DELETED_ANIMALS, other.DELETED_ANIMALS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(PLACED_TILES), Arrays.hashCode(PLACED_TILES_ORDER), ZONE_PARTITIONS, DELETED_ANIMALS);
    }
}






















