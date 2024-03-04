package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class MyPlacedTileTest {

    @Test
    void myPlacedTileThrowsExceptionIfTileRotPosNull() {
        Tile nullTile = null;
        Rotation nullRotation = null;
        Pos nullPos = null;
        Tile tile = new Tile(1, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> new PlacedTile(nullTile, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN));
        assertThrows(IllegalArgumentException.class, () -> new PlacedTile(tile, PlayerColor.GREEN, nullRotation, Pos.ORIGIN));
        assertThrows(IllegalArgumentException.class, () -> new PlacedTile(tile, PlayerColor.GREEN, Rotation.RIGHT, nullPos));
    }

    @Test
    void sideWorksOnAllRotations() {
        TileSide n = new TileSide.Meadow(new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE));
        TileSide e = new TileSide.Forest(new Zone.Forest(1, Zone.Forest.Kind.WITH_MUSHROOMS));
        TileSide s = new TileSide.Meadow(new Zone.Meadow(2, null, Zone.SpecialPower.SHAMAN));
        TileSide w = new TileSide.River(new Zone.Meadow(3, null, null),
                    new Zone.River(4, 1, null),
                    new Zone.Meadow(5, null, null));
        List<TileSide> tileSides = List.of(n, e, s, w);
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n, e, s, w);
        PlacedTile placed1 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        PlacedTile placed2 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.HALF_TURN, Pos.ORIGIN);
        PlacedTile placed3 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.LEFT, Pos.ORIGIN);
        PlacedTile placed4 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.NONE, Pos.ORIGIN);
        List<Direction> directions = Direction.ALL;
        for (Direction dir : directions) {
            assertEquals(tileSides.get(dir.rotated(placed1.rotation()).ordinal()), placed1.side(dir));
            assertEquals(tileSides.get(dir.rotated(placed2.rotation()).ordinal()), placed2.side(dir));
            assertEquals(tileSides.get(dir.rotated(placed3.rotation()).ordinal()), placed3.side(dir));
            assertEquals(tileSides.get(dir.rotated(placed4.rotation()).ordinal()), placed4.side(dir));
        }
    }

    @Test
    void zoneWithIdWorksOnTrivialArrayAndException() {
        List<Zone> zones = List.of(new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE),
                new Zone.Forest(1, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Meadow(2, null, Zone.SpecialPower.SHAMAN),
                new Zone.Meadow(3, null, null),
                new Zone.River(4, 1, null),
                new Zone.Meadow(5, null, null));

        TileSide n = new TileSide.Meadow((Zone.Meadow) zones.get(0));
        TileSide e = new TileSide.Forest((Zone.Forest) zones.get(1));
        TileSide s = new TileSide.Meadow((Zone.Meadow) zones.get(2));
        TileSide w = new TileSide.River((Zone.Meadow) zones.get(3), (Zone.River) zones.get(4), (Zone.Meadow) zones.get(5));
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n, e, s, w);
        PlacedTile placed = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        for (int i = 0; i < 6; i++) {
            assertEquals(zones.get(i), placed.zoneWithId(i));
        }
        assertThrows(IllegalArgumentException.class, () -> placed.zoneWithId(7));
    }

    @Test
    void specialPowerZoneOnTrivialArray() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE);
        Zone.Forest forest1 = new Zone.Forest(1, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, null, null);
        Zone.River river1 = new Zone.River(3, 1, null);
        TileSide n1 = new TileSide.Meadow(meadow1);
        TileSide e1 = new TileSide.Forest(forest1);
        TileSide s1 = new TileSide.Meadow(meadow2);
        TileSide w1 = new TileSide.River(meadow1, river1, meadow2);
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n1, e1, s1, w1);
        PlacedTile placed1 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        assertEquals(meadow1, placed1.specialPowerZone());

        Zone.Meadow meadow3 = new Zone.Meadow(0, null, null);
        Zone.Forest forest2 = new Zone.Forest(1, null);
        Zone.Meadow meadow4 = new Zone.Meadow(2, null, null);
        Zone.River river2 = new Zone.River(3, 1, null);
        TileSide n2 = new TileSide.Meadow(meadow3);
        TileSide e2 = new TileSide.Forest(forest2);
        TileSide s2 = new TileSide.Meadow(meadow4);
        TileSide w2 = new TileSide.River(meadow2, river2, meadow4);
        Tile tile2 = new Tile(43, Tile.Kind.NORMAL, n2, e2, s2, w2);
        PlacedTile placed2 = new PlacedTile(tile2, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        assertNull(placed2.specialPowerZone());
    }

    @Test
    void forestZonesOnTrivialArray() {
        Zone.Forest forest0 = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest1 = new Zone.Forest(1, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, null, null);
        Zone.River river1 = new Zone.River(3, 1, null);
        TileSide n1 = new TileSide.Forest(forest0);
        TileSide e1 = new TileSide.Forest(forest1);
        TileSide s1 = new TileSide.Meadow(meadow2);
        TileSide w1 = new TileSide.River(meadow2, river1, meadow2);
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n1, e1, s1, w1);
        PlacedTile placed1 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Set<Zone.Forest> expected = Set.of(forest0, forest1);
        assertEquals(expected, placed1.forestZones());

        Zone.Meadow meadow4 = new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE);
        Zone.Meadow meadow5 = new Zone.Meadow(1, null, null);
        Zone.Meadow meadow6 = new Zone.Meadow(2, null, null);
        Zone.River river2 = new Zone.River(3, 1, null);
        TileSide n2 = new TileSide.Meadow(meadow4);
        TileSide e2 = new TileSide.Meadow(meadow5);
        TileSide s2 = new TileSide.Meadow(meadow6);
        TileSide w2 = new TileSide.River(meadow4, river2, meadow6);
        Tile tile2 = new Tile(43, Tile.Kind.NORMAL, n2, e2, s2, w2);
        PlacedTile placed2 = new PlacedTile(tile2, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Set<Zone.Forest> expected2 = new HashSet<>();
        assertEquals(expected2, placed2.forestZones());
    }

    @Test
    void meadowZonesOnTrivialArray() {
        Zone.Forest forest0 = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest1 = new Zone.Forest(1, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, null, null);
        Zone.River river1 = new Zone.River(3, 1, null);
        TileSide n1 = new TileSide.Forest(forest0);
        TileSide e1 = new TileSide.Forest(forest1);
        TileSide s1 = new TileSide.Meadow(meadow2);
        TileSide w1 = new TileSide.River(meadow2, river1, meadow2);
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n1, e1, s1, w1);
        PlacedTile placed1 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Set<Zone.Meadow> expected = Set.of(meadow2);
        assertEquals(expected, placed1.meadowZones());

        Zone.Forest forest2 = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest3 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest4 = new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest5 = new Zone.Forest(3, Zone.Forest.Kind.WITH_MENHIR);

        TileSide n2 = new TileSide.Forest(forest2);
        TileSide e2 = new TileSide.Forest(forest3);
        TileSide s2 = new TileSide.Forest(forest4);
        TileSide w2 = new TileSide.Forest(forest5);
        Tile tile2 = new Tile(43, Tile.Kind.NORMAL, n2, e2, s2, w2);
        PlacedTile placed2 = new PlacedTile(tile2, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Set<Zone.Forest> expected2 = new HashSet<>();
        assertEquals(expected2, placed2.meadowZones());
    }

    @Test
    void riverZonesOnTrivialArray() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE);
        Zone.Forest forest1 = new Zone.Forest(1, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, null, null);
        Zone.River river1 = new Zone.River(3, 1, null);
        TileSide n1 = new TileSide.Meadow(meadow1);
        TileSide e1 = new TileSide.Forest(forest1);
        TileSide s1 = new TileSide.Meadow(meadow2);
        TileSide w1 = new TileSide.River(meadow1, river1, meadow2);
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n1, e1, s1, w1);
        PlacedTile placed1 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Set<Zone.River> expected = Set.of(river1);
        assertEquals(expected, placed1.riverZones());

        Zone.Forest forest2 = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest3 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest4 = new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest5 = new Zone.Forest(3, Zone.Forest.Kind.WITH_MENHIR);
        TileSide n2 = new TileSide.Forest(forest2);
        TileSide e2 = new TileSide.Forest(forest3);
        TileSide s2 = new TileSide.Forest(forest4);
        TileSide w2 = new TileSide.Forest(forest5);
        Tile tile2 = new Tile(43, Tile.Kind.NORMAL, n2, e2, s2, w2);
        PlacedTile placed2 = new PlacedTile(tile2, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Set<Zone.Forest> expected2 = new HashSet<>();
        assertEquals(expected2, placed2.riverZones());
    }

    @Test
    void potentialOccupantsWorksOnTrivialArray() {
        List<Zone> zones = List.of(new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE),
                new Zone.Forest(1, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Meadow(2, null, Zone.SpecialPower.SHAMAN),
                new Zone.Meadow(3, null, null),
                new Zone.River(4, 1, null),
                new Zone.Meadow(5, null, null));

        TileSide n = new TileSide.Meadow((Zone.Meadow) zones.get(0));
        TileSide e = new TileSide.Forest((Zone.Forest) zones.get(1));
        TileSide s = new TileSide.Meadow((Zone.Meadow) zones.get(2));
        TileSide w = new TileSide.River((Zone.Meadow) zones.get(3), (Zone.River) zones.get(4), (Zone.Meadow) zones.get(5));
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n, e, s, w);
        PlacedTile placed = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Set<Occupant> expected = Set.of(new Occupant(Occupant.Kind.PAWN, 0),
                                        new Occupant(Occupant.Kind.PAWN, 1),
                                        new Occupant(Occupant.Kind.PAWN, 2),
                                        new Occupant(Occupant.Kind.PAWN, 3),
                                        new Occupant(Occupant.Kind.HUT, 4),
                                        new Occupant(Occupant.Kind.PAWN, 4),
                                        new Occupant(Occupant.Kind.PAWN, 5));
        assertEquals(expected, placed.potentialOccupants());

        PlacedTile placed2 = new PlacedTile(tile1, null, Rotation.LEFT, Pos.ORIGIN);
        assertEquals(Set.of(), placed2.potentialOccupants());

        Zone.River riverWithLake= new Zone.River(4, 1, new Zone.Lake(8, 2, null));
        TileSide w2 = new TileSide.River((Zone.Meadow) zones.get(3), riverWithLake, (Zone.Meadow) zones.get(5));
        Tile tile2 = new Tile(43, Tile.Kind.NORMAL, n, e, s, w2);
        PlacedTile placed3 = new PlacedTile(tile2, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Set<Occupant> expected3 = Set.of(new Occupant(Occupant.Kind.PAWN, 0),
                                        new Occupant(Occupant.Kind.PAWN, 1),
                                        new Occupant(Occupant.Kind.PAWN, 2),
                                        new Occupant(Occupant.Kind.PAWN, 3),
                                        new Occupant(Occupant.Kind.PAWN, 4),
                                        new Occupant(Occupant.Kind.HUT, 8),
                                        new Occupant(Occupant.Kind.PAWN, 5));
        assertEquals(expected3, placed3.potentialOccupants());
    }

    @Test
    void withOccupantWorksOnTrivialArrayAndException() {
        Zone.Forest forest0 = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest1 = new Zone.Forest(1, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, null, null);
        Zone.River river1 = new Zone.River(3, 1, null);
        TileSide n1 = new TileSide.Forest(forest0);
        TileSide e1 = new TileSide.Forest(forest1);
        TileSide s1 = new TileSide.Meadow(meadow2);
        TileSide w1 = new TileSide.River(meadow2, river1, meadow2);
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n1, e1, s1, w1);
        PlacedTile placed1 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        Occupant occupant = new Occupant(Occupant.Kind.PAWN, 0);
        PlacedTile expected = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN, occupant);
        assertEquals(expected, placed1.withOccupant(occupant));
        assertThrows(IllegalArgumentException.class, () -> expected.withOccupant(new Occupant(Occupant.Kind.PAWN, 0)));
    }

    @Test
    void withNoOccupantTest() {
        Zone.Forest forest0 = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest1 = new Zone.Forest(1, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, null, null);
        Zone.River river1 = new Zone.River(3, 1, null);
        TileSide n1 = new TileSide.Forest(forest0);
        TileSide e1 = new TileSide.Forest(forest1);
        TileSide s1 = new TileSide.Meadow(meadow2);
        TileSide w1 = new TileSide.River(meadow2, river1, meadow2);
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n1, e1, s1, w1);
        PlacedTile placed1 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN, new Occupant(Occupant.Kind.HUT, 1));
        PlacedTile expected = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        assertEquals(expected, placed1.withNoOccupant());
    }

    @Test
    void idOfZoneOccupiedByWorksOnTrivialArray() {
        Zone.Forest forest0 = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest1 = new Zone.Forest(1, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, null, null);
        Zone.River river1 = new Zone.River(3, 1, null);
        TileSide n1 = new TileSide.Forest(forest0);
        TileSide e1 = new TileSide.Forest(forest1);
        TileSide s1 = new TileSide.Meadow(meadow2);
        TileSide w1 = new TileSide.River(meadow2, river1, meadow2);
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n1, e1, s1, w1);
        PlacedTile placed1 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN, new Occupant(Occupant.Kind.HUT, 1));
        assertEquals(1, placed1.idOfZoneOccupiedBy(Occupant.Kind.HUT));

        PlacedTile placed2 = new PlacedTile(tile1, PlayerColor.GREEN, Rotation.RIGHT, Pos.ORIGIN);
        assertEquals(-1, placed2.idOfZoneOccupiedBy(Occupant.Kind.HUT));
        assertEquals(-1, placed2.idOfZoneOccupiedBy(Occupant.Kind.PAWN));
    }
}
