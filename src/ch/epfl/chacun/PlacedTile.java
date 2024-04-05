package ch.epfl.chacun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This record represents a placed tile in the game.
 * A placed tile has a tile, a placer, a rotation, a position, and an occupant.
 * @author Bjork Pedersen (376143)
 */
public record PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos, Occupant occupant) {

    /**
     * Constructor for the PlacedTile record.
     * Checks that the tile, rotation, and position are not null.
     * @throws IllegalArgumentException if the tile, rotation, or position is null
     * @throws NullPointerException if the tile, rotation, or position is null
     */
    public PlacedTile {
        if (tile == null || rotation == null || pos == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Overloaded constructor for the PlacedTile record.
     * Sets the occupant to null.
     */
    public PlacedTile(Tile tile, PlayerColor placer, Rotation rotation, Pos pos) {
        this(tile, placer, rotation, pos, null);
    }

    /**
     * Returns the id of the tile.
     *
     * @return the id of the tile
     */
    public int id() {
        return tile.id();
    }

    /**
     * Returns the kind of the tile.
     *
     * @return the kind of the tile
     */
    public Tile.Kind kind() {
        return tile.kind();
    }

    /**
     * Returns the side of the tile in the given direction.
     *
     * @param direction the direction of the side to return
     * @return the side of the tile in the given direction
     */
    public TileSide side(Direction direction) {
        Direction finalDirection = direction.rotated(rotation.negated());
        int finalDirectionOrdinal = finalDirection.ordinal();
        List<TileSide> sideZones = this.tile.sides();
        return sideZones.get(finalDirectionOrdinal);
    }

    /**
     * Returns the zone with the given id.
     *
     * @param id the id of the zone to return
     * @return the zone with the given id
     * @throws IllegalArgumentException if no zone with the given id is found
     */
    public Zone zoneWithId(int id) {
        Set<Zone> allZones = tile.zones();
        for (Zone zone : allZones) {
            if (zone.id() == id) {
                return zone;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the zone with a special power, if any.
     *
     * @return the zone with a special power, or null if no such zone exists
     */
    public Zone specialPowerZone() {
        Set<Zone> allZones = tile.zones();
        for (Zone zone : allZones) {
            if (zone.specialPower() != null) {
                return zone;
            }
        }
        return null;
    }

    /**
     * Returns a set of all forest zones.
     *
     * @return a set of all forest zones
     */
    public Set<Zone.Forest> forestZones() {
        Set<Zone> allZones = tile.zones();
        Set<Zone.Forest> result = new HashSet<>();
        for (Zone zone : allZones) {
            if (zone instanceof Zone.Forest forest) {
                result.add(forest);
            }
        }
        return result;
    }

    /**
     * Returns a set of all meadow zones.
     *
     * @return a set of all meadow zones
     */
    public Set<Zone.Meadow> meadowZones() {
        Set<Zone> allZones = tile.zones();
        Set<Zone.Meadow> result = new HashSet<>();
        for (Zone zone : allZones) {
            if (zone instanceof Zone.Meadow meadow) {
                result.add(meadow);
            }
        }
        return result;
    }

    /**
     * Returns a set of all river zones.
     *
     * @return a set of all river zones
     */
    public Set<Zone.River> riverZones() {
        Set<Zone> allZones = tile.zones();
        Set<Zone.River> result = new HashSet<>();
        for (Zone zone : allZones) {
            if (zone instanceof Zone.River river) {
                result.add(river);
            }
        }
        return result;
    }

    /**
     * Returns a set of all potential occupants.
     *
     * @return a set of all potential occupants
     */
    public Set<Occupant> potentialOccupants() {
        Set <Occupant> result = new HashSet<>();
        if (this.placer == null) {
            return result;
        } else {
            for (Zone sideZone : tile.sideZones()) {
                result.add(new Occupant(Occupant.Kind.PAWN, sideZone.id()));
                if (sideZone instanceof Zone.River) {
                    if (((Zone.River) sideZone).hasLake()) {
                        result.add(new Occupant(Occupant.Kind.HUT, ((Zone.River) sideZone).lake().id()));
                    } else {
                        result.add(new Occupant(Occupant.Kind.HUT, sideZone.id()));
                    }
                }
            }
            return result;
        }
    }

    /**
     * Returns a new PlacedTile with the given occupant.
     *
     * @param occupant the occupant to add
     * @return a new PlacedTile with the given occupant
     * @throws IllegalArgumentException if the current occupant is not null
     */
    public PlacedTile withOccupant(Occupant occupant) {
        Preconditions.checkArgument(this.occupant == null);
        return new PlacedTile(this.tile, this.placer, this.rotation, this.pos, occupant);
    }

    /**
     * Returns a new PlacedTile with no occupant.
     *
     * @return a new PlacedTile with no occupant
     */
    public PlacedTile withNoOccupant() {
        return new PlacedTile(this.tile, this.placer, this.rotation, this.pos);
    }

    /**
     * Returns the id of the zone occupied by the given occupant kind.
     *
     * @param occupantKind the kind of occupant to check
     * @return the id of the zone occupied by the given occupant kind, or -1 if no such occupant exists
     */
    public int idOfZoneOccupiedBy(Occupant.Kind occupantKind) {
        if (occupant == null || occupant.kind() != occupantKind) {
            return -1;
        }
        return occupant.zoneId();
    }
}