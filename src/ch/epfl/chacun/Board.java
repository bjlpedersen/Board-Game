package ch.epfl.chacun;

import java.util.*;

/**
 * Represents a game board with various methods to manipulate and query the state of the board.
 * @author Bjork Pedersen (376143)
 */
public final class Board {
    private final PlacedTile[] placedTilesInArray;
    private final int[] placedTilesOrder;
    private final ZonePartitions zonePartitions;
    private final Set<Animal> deletedAnimals;
    public final static int REACH = 12;
    private final static int MAX_TILES = (REACH * 2 + 1) * (REACH * 2 + 1);
    public final static Board EMPTY = new Board(new PlacedTile[MAX_TILES], new int[0], ZonePartitions.EMPTY, Set.of());

    /**
     * Constructs a new Board with the given placed tiles, order of placed tiles, zone partitions, and deleted animals.
     *
     * @param placedTiles the placed tiles on the board
     * @param placedTilesOrder the order of the placed tiles
     * @param zonePartitions the partitions of the zones on the board
     * @param deletedAnimals the set of deleted animals on the board
     */
    private Board(PlacedTile[] placedTiles, int[] placedTilesOrder, ZonePartitions zonePartitions, Set<Animal> deletedAnimals) {
        this.placedTilesInArray = placedTiles;
        this.placedTilesOrder = placedTilesOrder;
        this.zonePartitions = zonePartitions;
        this.deletedAnimals = deletedAnimals;
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
        return placedTilesInArray[pos.x() + REACH + (pos.y() + REACH) * (REACH * 2 + 1)];
    }

    /**
     * Returns the tile with the given ID.
     *
     * @param tileId the ID of the tile
     * @return the tile with the given ID
     * @throws IllegalArgumentException if the tile with the given ID is not found
     */
    public PlacedTile tileWithId(int tileId) {
        for (PlacedTile placed : placedTilesInArray) {
            if (placed != null && placed.id() == tileId) {
                return placed;
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
        return Collections.unmodifiableSet(deletedAnimals);
    }

    /**
     * Returns the set of occupants of the board.
     *
     * @return the set of occupants
     */
    public Set<Occupant> occupants() {
        Set<Occupant> occupants = new HashSet<>();
        for (int orderId : placedTilesOrder) {
            if (tileWithId(orderId) == null) {
                continue;
            }
            PlacedTile placedTile = tileWithId(orderId);
            if (placedTile.occupant() != null) {
                occupants.add(placedTile.occupant());
            }
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
        return zonePartitions.forests().areaContaining(forest);
    }

    /**
     * Returns the area of the given meadow zone.
     *
     * @param meadow the meadow zone
     * @return the area of the given meadow zone
     */
    public Area<Zone.Meadow> meadowArea(Zone.Meadow meadow) {
        return zonePartitions.meadows().areaContaining(meadow);
    }

    /**
     * Returns the area of the given river zone.
     *
     * @param riverZone the river zone
     * @return the area of the given river zone
     */
    public Area<Zone.River> riverArea(Zone.River riverZone) {
        return zonePartitions.rivers().areaContaining(riverZone);
    }

    /**
     * Returns the area of the given water zone.
     *
     * @param water the water zone
     * @return the area of the given water zone
     */
    public Area<Zone.Water> riverSystemArea(Zone.Water water) {
        return zonePartitions.riverSystems().areaContaining(water);
    }

    /**
     * Returns the set of meadow areas.
     *
     * @return the set of meadow areas
     */
    public Set<Area<Zone.Meadow>> meadowAreas() {
        return zonePartitions.meadows().areas();
    }

    /**
     * Returns the set of water areas.
     *
     * @return the set of water areas
     */
    public Set<Area<Zone.Water>> riverSystemAreas() {
        return zonePartitions.riverSystems().areas();
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
        zones.add(meadowZone);
        List<PlayerColor> occupants = meadowArea(meadowZone).occupants();
        int x = pos.x();
        int y = pos.y();
        Set<Pos> possiblePositions = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                possiblePositions.add(new Pos(x + i, y + j));
            }
        }
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
        for (int orderId : placedTilesOrder) {
            if (tileWithId(orderId) == null) {
                continue;
            }
            PlacedTile placedTile = tileWithId(orderId);
            if (placedTile.placer() == player && placedTile.occupant() != null && placedTile.occupant().kind() == occupantKind) {
                count++;
            }
        }
        return count;
    }

    /**
     * finds the set of all directions minus the ones that would be out of bounds if it moved by one more tile.
     * @param placedTile the tile that has been placed
     * @return the set of all directions minus the ones that would be out of bounds if it moved by one more tile.
     */
    private static Set<Direction> removeEdgesThatTileIsOn(PlacedTile placedTile) {
        Set<Direction> possibleDirections = new HashSet<>(Direction.ALL);
        possibleDirections.removeIf(direction -> !isValidPosition(placedTile, direction));
        return possibleDirections;
    }

    /**
     * Checks if the given placed tile's position is valid based on the provided direction.
     *
     * @param placedTile The tile whose position is to be checked.
     * @param direction The direction in which the tile's position is to be validated.
     * @return true if the tile's position is valid in the given direction, false otherwise.
     * @throws IllegalArgumentException if an invalid direction is provided.
     */
    private static boolean isValidPosition(PlacedTile placedTile, Direction direction) {
        switch (direction) {
            case N:
                return placedTile.pos().y() != -REACH;
            case S:
                return placedTile.pos().y() != REACH;
            case E:
                return placedTile.pos().x() != REACH;
            case W:
                return placedTile.pos().x() != -REACH;
            default:
                throw new IllegalArgumentException("Invalid direction");
        }
    }

    /**
     * Returns the set of possible insertion positions.
     *
     * @return the set of possible insertion positions
     */
    public Set<Pos> insertionPositions() {
        Set<Pos> insertionPositions = new HashSet<>();
        for (PlacedTile placed : placedTilesInArray) {
            if (placed != null) {
                Set<Direction> possibleDirections = removeEdgesThatTileIsOn(placed);
                for (Direction d : possibleDirections) {
                    if (tileAt(placed.pos().neighbor(d)) == null) {
                        insertionPositions.add(placed.pos().neighbor(d));
                    }
                }
            }
        }
        return  insertionPositions;
    }

    /**
     * Returns the last placed tile.
     *
     * @return the last placed tile
     */
    public PlacedTile lastPlacedTile() {
        Integer lastPlacedId = null;
        for (int i : placedTilesOrder) {
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
        if (placedTilesOrder.length > 0) {
            PlacedTile lastPlaced = lastPlacedTile();
            for (Zone.Forest forest : lastPlaced.forestZones()) {
                if (forestArea(forest).isClosed()) {
                    closedForests.add(forestArea(forest));
                }
            }
        } else {
            return Set.of();
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
        if (placedTilesOrder.length > 0) {
            PlacedTile lastPlaced = lastPlacedTile();
            for (Zone.River river : lastPlaced.riverZones()) {
                if (riverArea(river).isClosed()) {
                    closedRivers.add(riverArea(river));
                }
            }
        } else {
            return Set.of();
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
        boolean contained = insertionPositions().contains(tile.pos());
        if (!contained) return false;

        Direction[] directions = Direction.ALL.toArray(Direction[]::new);
        for (Direction direction : directions) {
            PlacedTile neighbour = tileAt(tile.pos().neighbor(direction));
            if (neighbour != null && !areTilesCompatible(neighbour, tile, direction)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the given tiles are compatible in the given direction: if their two sides are compatible
     *
     * @param tile1 the first tile
     * @param tile2 the second tile
     * @param direction the direction
     * @return true if the tiles are compatible, false otherwise
     */
    private static boolean areTilesCompatible(PlacedTile tile1, PlacedTile tile2, Direction direction) {
        return tile1.side(direction.opposite()).isSameKindAs(tile2.side(direction));
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
            for (Rotation rotation : Rotation.ALL) {
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
     * @throws IllegalArgumentException if the tile cannot be added
     */
    public Board withNewTile(PlacedTile tile) {
        Preconditions.checkArgument(canAddTile(tile) || placedTilesOrder.length == 0);
        PlacedTile[] newPlacedTiles = Arrays.copyOf(placedTilesInArray, placedTilesInArray.length);
        int indexInPlacedTiles = indexOfTileInPLacedTiles(tile);
        newPlacedTiles[indexInPlacedTiles] = tile;
        int[] newPlacedTilesOrder = new int[placedTilesOrder.length + 1];
        System.arraycopy(placedTilesOrder, 0, newPlacedTilesOrder, 0, placedTilesOrder.length);
        newPlacedTilesOrder[placedTilesOrder.length] = tile.id();
        ZonePartitions.Builder newPartitionsBuilder = new ZonePartitions.Builder(zonePartitions);
        newPartitionsBuilder.addTile(tile.tile());
        for (Direction direction : Direction.ALL) {
            if (tileAt(tile.pos().neighbor(direction)) != null) {
                newPartitionsBuilder.connectSides(tile.side(direction), tileAt(tile.pos().neighbor(direction)).side(direction.opposite()));
            }
        }
        return new Board(newPlacedTiles, newPlacedTilesOrder, newPartitionsBuilder.build(), deletedAnimals);
    }

    /**
     * Returns a new board with the given occupant added.
     *
     * @param occupant the occupant to be added
     * @return a new board with the given occupant added
     * @throws IllegalArgumentException if the occupant cannot be added
     */
    public Board withOccupant(Occupant occupant) {
        int zoneId = occupant.zoneId();
        PlacedTile tile = tileWithId(Zone.tileId(zoneId));
        Preconditions.checkArgument(tile.occupant() == null);
        PlacedTile[] newPlacedTiles = placedTilesInArray.clone();
        int indexInPlacedTiles = indexOfTileInPLacedTiles(tile);
        newPlacedTiles[indexInPlacedTiles] = tile.withOccupant(occupant);
        ZonePartitions.Builder zonePartitions = new ZonePartitions.Builder(this.zonePartitions);
        zonePartitions.addInitialOccupant(tile.placer() ,occupant.kind(), tile.zoneWithId(zoneId));
        return new Board(newPlacedTiles, placedTilesOrder, zonePartitions.build(), deletedAnimals);
    }

    /**
     * Returns a new board with the given occupant removed.
     *
     * @param occupant the occupant to be removed
     * @return a new board with the given occupant removed
     */
    public Board withoutOccupant(Occupant occupant) {
        int zoneId = occupant.zoneId();
        PlacedTile tile = tileWithId(Zone.tileId(zoneId));
        PlacedTile[] newPlacedTiles = placedTilesInArray.clone();
        int indexInPlacedTiles = indexOfTileInPLacedTiles(tile);
        newPlacedTiles[indexInPlacedTiles] = tile.withNoOccupant();
        ZonePartitions.Builder zonePartitions = new ZonePartitions.Builder(this.zonePartitions);
        zonePartitions.removePawn(tile.placer(), tile.zoneWithId(zoneId));
        return new Board(newPlacedTiles, placedTilesOrder, zonePartitions.build(), deletedAnimals);
    }

/**
     * Returns a new board with the given tile removed.
     *
     * @param tile the tile to be removed
     * @return a new board with the given tile removed
     */
    private int indexOfTileInPLacedTiles(PlacedTile tile) {
        return tile.pos().x() + REACH + (tile.pos().y() + REACH) * (REACH * 2 + 1);
    }

    /**
     * Returns a new board without gatherers or fishers in the given forests and rivers.
     *
     * @param forests the forests
     * @param rivers the rivers
     * @return a new board without gatherers or fishers in the given forests and rivers
     */
    public Board withoutGatherersOrFishersIn(Set<Area<Zone.Forest>> forests, Set<Area<Zone.River>> rivers) {
        ZonePartitions.Builder partitionsBuilder = new ZonePartitions.Builder(zonePartitions);
        for (Area<Zone.Forest> forest : forests) {
            partitionsBuilder.clearGatherers(forest);
        }
        for (Area<Zone.River> river : rivers) {
            partitionsBuilder.clearFishers(river);
        }
        PlacedTile[] newPlacedTiles = removeOccupantsInZonePartitionsForest(forests, placedTilesInArray.clone());
        newPlacedTiles = removeOccupantsInZonePartitionsRiver(rivers, newPlacedTiles);
        return new Board(newPlacedTiles,
                placedTilesOrder,
                partitionsBuilder.build(),
                deletedAnimals);
    }

    /**
     * Removes occupants in the given forests
     * @param forests the Set of all forest areas
     * @param placedTiles the list of placed tiles it has to remove occupants from
     * @return new board with no occupants in forests
     */
    private PlacedTile[] removeOccupantsInZonePartitionsForest(Set<Area<Zone.Forest>> forests, PlacedTile[] placedTiles) {
        // Clone the array of placed tiles
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        for (int orderId : placedTilesOrder) {
            // Get the placed tile with the current order ID
            PlacedTile placedTile = tileWithId(orderId);

            // Iterate over the forest zones in the placed tile
            for (Zone.Forest forest : placedTile.forestZones()) {
                // Get the area containing the current forest zone
                Area<Zone.Forest> area = zonePartitions.forests().areaContaining(forest);

                // If the area is in the set of forests, is occupied, and the occupant's zone ID matches the forest's ID
                if (forests.contains(area) && area.isOccupied() && placedTile.occupant() != null && placedTile.occupant().zoneId() == forest.id()) {
                    // Create a new tile with the same properties as the placed tile, but with no occupant
                    placedTile = placedTile.withNoOccupant();

                    // Calculate the index in the array of placed tiles
                    int indexInPlacedTiles = indexOfTileInPLacedTiles(placedTile);

                    // Replace the tile at the calculated index with the new tile
                    newPlacedTiles[indexInPlacedTiles] = placedTile;
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
        // Clone the array of placed tiles
        PlacedTile[] newPlacedTiles = placedTiles.clone();
        for (int orderId : placedTilesOrder) {
            // Get the placed tile with the current order ID
            PlacedTile placedTile = tileWithId(orderId);

            // If the placed tile is null, skip to the next iteration
            if (placedTile == null) {
                continue;
            }

            // Iterate over the river zones in the placed tile
            for (Zone.River river : placedTile.riverZones()) {
                // Get the area containing the current river zone
                Area<Zone.River> area = zonePartitions.rivers().areaContaining(river);

                // If the area is in the set of rivers, is occupied, and the occupant is not a hut
                if (rivers.contains(area) &&
                        area.isOccupied() &&
                        placedTile.occupant() != null &&
                        placedTile.occupant().kind() != Occupant.Kind.HUT) {

                    // Create a new tile with the same properties as the placed tile, but with no occupant
                    placedTile = placedTile.withNoOccupant();

                    // Calculate the index in the array of placed tiles
                    int indexInPlacedTiles = indexOfTileInPLacedTiles(placedTile);

                    // Replace the tile at the calculated index with the new tile
                    newPlacedTiles[indexInPlacedTiles] = placedTile;
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
        Set<Animal> newCancelledAnimals = new HashSet<>(deletedAnimals);
        newCancelledAnimals.addAll(newlyCancelledAnimals);
        return new Board(placedTilesInArray, placedTilesOrder, zonePartitions, newCancelledAnimals);
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
        return Arrays.equals(placedTilesInArray, other.placedTilesInArray) &&
                Arrays.equals(placedTilesOrder, other.placedTilesOrder) &&
                Objects.equals(zonePartitions, other.zonePartitions) &&
                Objects.equals(deletedAnimals, other.deletedAnimals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(placedTilesInArray), Arrays.hashCode(placedTilesOrder), zonePartitions, deletedAnimals);
    }


}






















