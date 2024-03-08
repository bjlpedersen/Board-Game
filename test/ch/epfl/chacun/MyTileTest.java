package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MyTileTest {

    @Test
    void sidesWorksOnNonTrivialTile() {
        TileSide n = new TileSide.Meadow(new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE));
        TileSide e = new TileSide.Forest(new Zone.Forest(1, Zone.Forest.Kind.WITH_MUSHROOMS));
        TileSide s = new TileSide.Meadow(new Zone.Meadow(2, null, Zone.SpecialPower.SHAMAN));
        TileSide w = new TileSide.River(new Zone.Meadow(3, null, null),
                                        new Zone.River(4, 1, new Zone.Lake(8, 1, null)),
                                        new Zone.Meadow(5, null, null));
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n, e, s, w);
        assertEquals(tile1.sides().get(0), n);
        assertEquals(tile1.sides().get(1), e);
        assertEquals(tile1.sides().get(2), s);
        assertEquals(tile1.sides().get(3), w);
        assertEquals(4, tile1.sides().size());
    }

    @Test
    void sidesWorksOnTrivialTile() {
        TileSide n = null;
        TileSide e = null;
        TileSide s = null;
        TileSide w = null;

        Tile tile = new Tile(60, Tile.Kind.MENHIR, n, e, s, w);
        assertNull(tile.sides().get(0));
        assertNull(tile.sides().get(1));
        assertNull(tile.sides().get(2));
        assertNull(tile.sides().get(3));
    }

    @Test
    void sideZonesWorksOnNonTrivialTile() {
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

        Set<Zone> expected = Set.of(zones.get(0), zones.get(1), zones.get(2), zones.get(3), zones.get(4), zones.get(5));
        assertEquals(expected, tile1.sideZones());
    }

    @Test
    void sideZonesWorksOnTrivialTile() {
        TileSide n = null;
        TileSide e = null;
        TileSide s = null;
        TileSide w = null;

        Tile tile = new Tile(60, Tile.Kind.MENHIR, n, e, s, w);
        assertThrows(NullPointerException.class, tile::sideZones);
    }

    @Test
    void zonesWorksOnNonTrivialTile() {
        Zone.Lake lake = new Zone.Lake(8, 2, Zone.SpecialPower.LOGBOAT);
        List<Zone> zones = List.of(new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE),
                new Zone.Forest(1, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Meadow(2, null, Zone.SpecialPower.SHAMAN),
                new Zone.Meadow(3, null, null),
                new Zone.River(4, 1, lake),
                new Zone.Meadow(5, null, null));

        TileSide n = new TileSide.Meadow((Zone.Meadow) zones.get(0));
        TileSide e = new TileSide.Forest((Zone.Forest) zones.get(1));
        TileSide s = new TileSide.Meadow((Zone.Meadow) zones.get(2));
        TileSide w = new TileSide.River((Zone.Meadow) zones.get(3), (Zone.River) zones.get(4), (Zone.Meadow) zones.get(5));
        Tile tile1 = new Tile(43, Tile.Kind.NORMAL, n, e, s, w);

        Set<Zone> expected = Set.of(zones.get(0), zones.get(1), zones.get(2), zones.get(3), zones.get(4), lake, zones.get(5));
        assertEquals(expected, tile1.zones());
    }

    @Test
    void zonesWorksOnTrivialTile() {
        TileSide n = null;
        TileSide e = null;
        TileSide s = null;
        TileSide w = null;

        Tile tile = new Tile(60, Tile.Kind.MENHIR, n, e, s, w);
        assertThrows(NullPointerException.class, tile::zones);
    }

}


