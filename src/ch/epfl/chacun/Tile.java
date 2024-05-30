package ch.epfl.chacun;

import java.util.*;

/**
 * This record represents a tile in the game.
 * A tile has an id, a kind, and four sides (north, east, south, west).
 *
 * @author Bjork Pedersen (376143)
 */
public record Tile(int id, Kind kind, TileSide n, TileSide e, TileSide s, TileSide w) {

    /**
     * This enum represents the kind of a tile.
     * A tile can be of type START, NORMAL, or MENHIR.
     */
    public enum Kind {
        START,
        NORMAL,
        MENHIR;
    }

    /**
     * Returns a list of all sides of this tile.
     *
     * @return a list of tile sides
     */
    public List<TileSide> sides() {
        return List.of(n, e, s, w);
    }

    /**
     * Returns a set of all zones that are part of the sides of this tile.
     *
     * @return a set of zones
     */
    public Set<Zone> sideZones() {
        Set<Zone> sideZones = new HashSet<Zone>();
        for (TileSide sides : this.sides()) {
            sideZones.addAll(sides.zones());
        }
        return sideZones;
    }

    /**
     * Returns a set of all zones that are part of this tile.
     * This includes zones that are part of the sides of this tile,
     * as well as any lake zones that are part of river zones.
     *
     * @return a set of zones
     */
    public Set<Zone> zones() {
        Set<Zone> allZones = this.sideZones();
        Set<Zone> result = new HashSet<>(allZones);
        for (Zone zone : allZones) {
            if (zone instanceof Zone.River river && river.hasLake()) {
                result.add(river.lake());
            }
        }
        return result;
    }
}