package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MyAreaTest {

    Area<Zone.Forest> forestAreaMaker(int openConnections) {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MUSHROOMS);
        Zone.Forest forest3 = new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR);

        PlayerColor color1 = PlayerColor.RED;
        PlayerColor color2 = PlayerColor.BLUE;

        return new Area<>(Set.of(forest1, forest2, forest3), List.of(color2, color1), openConnections);
    }

    @Test
    void constructorTest() {
        assertThrows(IllegalArgumentException.class, () -> forestAreaMaker(-1));
        Area<Zone.Forest> area1 = forestAreaMaker(0);
        assertEquals(List.of(PlayerColor.RED, PlayerColor.BLUE), area1.occupants());
        List<Zone.Forest> zones = new ArrayList<>(area1.zones());
        zones.clear();
        assertEquals(forestAreaMaker(0).zones(), area1.zones());
    }

    @Test
    void hasMenhirWorksOnNonTrivialAreas() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR);
        Area<Zone.Forest> area = new Area<>(Set.of(forest1, forest2), List.of(), 0);
        assertFalse(Area.hasMenhir(area));
        assertTrue(Area.hasMenhir(new Area<>(Set.of(forest1, forest2, forest3), List.of(), 0)));
    }

    @Test
    void hasMenhirWorksOnTrivialArea() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> area = new Area<>(Set.of(forest1), List.of(), 0);
        assertFalse(Area.hasMenhir(area));
        Zone.Forest forest2 = new Zone.Forest(1, null);
        Area<Zone.Forest> area2 = new Area<>(Set.of(forest2), List.of(), 0);
        assertFalse(Area.hasMenhir(area2));
    }

    @Test
    void mushroomGroupCountWorksOnNonTrivialArray() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MUSHROOMS);
        Zone.Forest forest3 = new Zone.Forest(2, Zone.Forest.Kind.WITH_MUSHROOMS);
        Zone.Forest forest4 = new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS);
        Zone.Forest forest5 = new Zone.Forest(4, Zone.Forest.Kind.WITH_MENHIR);
        Area<Zone.Forest> area = new Area<>(Set.of(forest1, forest2, forest3, forest4), List.of(), 0);
        assertEquals(3, Area.mushroomGroupCount(area));
        assertEquals(0, Area.mushroomGroupCount(new Area<>(Set.of(forest1, forest5), List.of(), 0)));
    }

    @Test
    void animalsWorksOnNonTrivialArea() {
        Animal a1 = new Animal(2000, Animal.Kind.DEER);
        Animal a2 = new Animal(2001, Animal.Kind.TIGER);
        Animal a3 = new Animal(2002, Animal.Kind.MAMMOTH);
        Animal a4 = new Animal(2003, Animal.Kind.AUROCHS);

        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(a1, a2), null);
        Zone.Meadow meadow2 = new Zone.Meadow(1, List.of(a3), null);
        Zone.Meadow meadow3 = new Zone.Meadow(2, List.of(), null);
        Zone.Meadow meadow4 = new Zone.Meadow(3, List.of(a4), null);
        Area<Zone.Meadow> area = new Area<>(Set.of(meadow1, meadow2, meadow3, meadow4), List.of(), 0);
        Area<Zone.Meadow> area2 = new Area<>(Set.of(), List.of(), 0);
        assertEquals(Set.of(a1, a2, a3, a4), Area.animals(area, Set.of()));
        assertEquals(Set.of(a1, a2, a3), Area.animals(area, Set.of(a4)));
        assertEquals(Set.of(), Area.animals(area2, Set.of(a1, a2, a3, a4)));
        assertEquals(Set.of(), Area.animals(area2, Set.of()));
    }

    @Test
    void riverFishCountWorksOnNonTrivialArea() {
        Zone.Lake lake = new Zone.Lake(8, 2, Zone.SpecialPower.LOGBOAT);
        Zone.River river1 = new Zone.River(0, 1, null);
        Zone.River river2 = new Zone.River(1, 2, null);
        Zone.River river3 = new Zone.River(2, 0, lake);
        Zone.River river4 = new Zone.River(3, 0, lake);
        Area<Zone.River> area = new Area<>(Set.of(river1, river2, river3, river4), List.of(), 0);
        assertEquals(5, Area.riverFishCount(area));
    }

    @Test
    void riverSystemFishCountWorksOnNonTrivialArea() {
        Zone.Lake lake = new Zone.Lake(8, 2, Zone.SpecialPower.LOGBOAT);
        Zone.River river1 = new Zone.River(0, 1, null);
        Zone.River river2 = new Zone.River(1, 2, null);
        Zone.River river3 = new Zone.River(2, 0, lake);
        Zone.River river4 = new Zone.River(3, 0, lake);
        Area<Zone.Water> area = new Area<>(Set.of(river1, river2, river3, river4, lake), List.of(), 3);
        assertEquals(5, Area.riverSystemFishCount(area));
    }

    @Test
    void  lakeCountWorksOnNonTrivialExample() {
        Zone.Lake lake1 = new Zone.Lake(8, 2, Zone.SpecialPower.LOGBOAT);
        Zone.Lake lake2 = new Zone.Lake(9, 2, Zone.SpecialPower.LOGBOAT);
        Zone.River river1 = new Zone.River(0, 1, null);
        Zone.River river2 = new Zone.River(1, 2, null);
        Zone.River river3 = new Zone.River(2, 0, lake1);
        Zone.River river4 = new Zone.River(3, 0, lake2);
        Area<Zone.Water> area = new Area<>(Set.of(river1, river2, river3, river4, lake1, lake2), List.of(), 3);
        assertEquals(2, Area.lakeCount(area));
    }

    @Test
    void isClosedWorks() {
        Zone.Lake lake1 = new Zone.Lake(8, 2, Zone.SpecialPower.LOGBOAT);
        Zone.Lake lake2 = new Zone.Lake(9, 2, Zone.SpecialPower.LOGBOAT);
        Zone.River river1 = new Zone.River(0, 1, null);
        Zone.River river2 = new Zone.River(1, 2, null);
        Zone.River river3 = new Zone.River(2, 0, lake1);
        Zone.River river4 = new Zone.River(3, 0, lake2);
        Area<Zone.Water> area = new Area<>(Set.of(river1, river2, river3, river4, lake1, lake2), List.of(), 3);
        assertFalse(area.isClosed());
        Area<Zone.Water> area2 = new Area<>(Set.of(river1, river2, river3, river4, lake1, lake2), List.of(), 0);
        assertThrows(IllegalArgumentException.class,() ->  new Area<>(Set.of(river1, river2, river3, river4, lake1, lake2), List.of(), -1));
        assertEquals(0, area2.openConnections());
    }

    @Test
    void isOccupiedWorks() {
        Area<Zone.Forest> forest1 = forestAreaMaker(0);
        assertTrue(forest1.isOccupied());

        Zone.Lake lake1 = new Zone.Lake(8, 2, Zone.SpecialPower.LOGBOAT);
        Zone.Lake lake2 = new Zone.Lake(9, 2, Zone.SpecialPower.LOGBOAT);
        Zone.River river1 = new Zone.River(0, 1, null);
        Zone.River river2 = new Zone.River(1, 2, null);
        Zone.River river3 = new Zone.River(2, 0, lake1);
        Zone.River river4 = new Zone.River(3, 0, lake2);
        Area<Zone.Water> area = new Area<>(Set.of(river1, river2, river3, river4, lake1, lake2), null, 3);
        assertFalse(area.isOccupied());
    }

    @Test
    void majorityOccupantsWorksOnNonTrivialArea() {
        Area<Zone.Forest> area1 = forestAreaMaker(0);
        assertEquals(Set.of(PlayerColor.RED, PlayerColor.BLUE), area1.majorityOccupants());
        Area<Zone.Forest> area2 = new Area<>(Set.of(), List.of(), 0);
        assertEquals(Set.of(), area2.majorityOccupants());
        Area<Zone.Water> area3 = new Area<>(Set.of(), List.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.BLUE, PlayerColor.YELLOW), 0);
        assertEquals(Set.of(PlayerColor.BLUE), area3.majorityOccupants());
    }

    @Test
    void connectToWorksOnNonTrivialAreas() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest4 = new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS);
        Area<Zone.Forest> area = new Area<>(Set.of(forest1, forest2), List.of(PlayerColor.BLUE), 2);
        Area<Zone.Forest> area2 = new Area<>(Set.of(forest3, forest4), List.of(PlayerColor.BLUE, PlayerColor.RED), 2);
        Area<Zone.Forest> expected = new Area<>(Set.of(forest1, forest2, forest3, forest4), List.of(PlayerColor.BLUE, PlayerColor.BLUE, PlayerColor.RED), 2);
        assertEquals(expected, area.connectTo(area2));
    }

    @Test
    void connectToWorksOnOneArea() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest3 = new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest4 = new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS);
        Area<Zone.Forest> area = new Area<>(Set.of(forest1, forest2, forest3, forest4), List.of(PlayerColor.BLUE, PlayerColor.RED), 4);
        Area<Zone.Forest> expected = new Area<>(Set.of(forest1, forest2, forest3, forest4), List.of(PlayerColor.BLUE, PlayerColor.RED), 2);
        assertEquals(expected, area.connectTo(area));

    }

    @Test
    public void testWithInitialOccupant() {
        Set<Zone> zones = new HashSet<>();
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Zone> area = new Area<>(zones, occupants, 0);
        PlayerColor newOccupant = PlayerColor.RED;
        Area<Zone> newArea = area.withInitialOccupant(newOccupant);
        assertEquals(1, newArea.occupants().size());
        assertEquals(newOccupant, newArea.occupants().get(0));
        assertTrue(area.occupants().isEmpty());
    }

    @Test
    public void testWithInitialOccupantWhenOccupantsExist() {
        Set<Zone> zones = new HashSet<>();
        List<PlayerColor> occupants = new ArrayList<>();
        occupants.add(PlayerColor.BLUE);
        Area<Zone> area = new Area<>(zones, occupants, 0);
        PlayerColor newOccupant = PlayerColor.RED;
        assertThrows(IllegalArgumentException.class, () -> area.withInitialOccupant(newOccupant));
    }

    @Test
    void withoutOccupantWhenOccupantNotInArea() {
        Set<Zone> zones = new HashSet<>();
        List<PlayerColor> occupants = new ArrayList<>();
        occupants.add(PlayerColor.BLUE);
        Area<Zone> area = new Area<>(zones, occupants, 0);
        assertThrows(IllegalArgumentException.class, () -> area.withoutOccupant(PlayerColor.RED));
    }

    @Test
    void withoutOccupantWhenOccupantInArea() {
        Set<Zone> zones = new HashSet<>();
        List<PlayerColor> occupants = new ArrayList<>();
        PlayerColor occupant = PlayerColor.BLUE;
        PlayerColor occupant2 = PlayerColor.RED;
        occupants.add(occupant);
        occupants.add(occupant2);
        Area<Zone> area = new Area<>(zones, occupants, 0);
        Area<Zone> newArea = area.withoutOccupant(occupant);
        List expected = new ArrayList<>();
        expected.add(occupant2);
        assertEquals(expected, newArea.occupants());

    }

    @Test
    void tileIdsWhenNoZones() {
        Area<Zone> area = new Area<>(Set.of(), List.of(), 0);
        assertEquals(Set.of(), area.tileIds());
    }

    @Test
    public void testTileIds() {
        // Create Zones with unique IDs
        Zone zone1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone zone2 = new Zone.Forest(2, Zone.Forest.Kind.PLAIN);
        Zone zone3 = new Zone.Forest(3, Zone.Forest.Kind.WITH_MENHIR);

        // Create an Area with the Zones
        Set<Zone> zones = new HashSet<>(Arrays.asList(zone1, zone2, zone3));
        Area<Zone> area = new Area<>(zones, new ArrayList<>(), 0);

        // Call tileIds method
        Set<Integer> tileIds = area.tileIds();

        // Assert that the returned set contains all the IDs of the zones in the area
        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3)), tileIds);
    }

    @Test
    void zoneWithSpecialPowerWhenNoSpecialPower() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(), Zone.SpecialPower.HUNTING_TRAP);
        Zone.Forest forest1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.River river1 = new Zone.River(2, 0, null);
        Zone.Meadow meadow2 = new Zone.Meadow(3, List.of(), Zone.SpecialPower.SHAMAN);
        Area<Zone> area1 = new Area<>(Set.of(meadow1, forest1, river1, meadow2), List.of(), 0);
        assertNull(area1.zoneWithSpecialPower(Zone.SpecialPower.LOGBOAT));
        assertNull(area1.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE));
        assertNull(area1.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP));
    }

    @Test
    void zoneWithSpecialPowerWhenSpecialPower() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(), Zone.SpecialPower.HUNTING_TRAP);
        Zone.Forest forest1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.River river1 = new Zone.River(2, 0, null);
        Zone.Meadow meadow2 = new Zone.Meadow(3, List.of(), Zone.SpecialPower.SHAMAN);
        Area<Zone> area1 = new Area<>(Set.of(meadow1, forest1, river1, meadow2), List.of(), 0);
        assertEquals(meadow1, area1.zoneWithSpecialPower(Zone.SpecialPower.HUNTING_TRAP));
    }

    @Test
    void zoneWithSpecialPowerWhenSpecialPowerNull() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(), Zone.SpecialPower.HUNTING_TRAP);
        Zone.Forest forest1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow2 = new Zone.Meadow(3, List.of(), Zone.SpecialPower.SHAMAN);
        Area<Zone> area1 = new Area<>(Set.of(meadow1, forest1, meadow2), List.of(), 0);
        assertEquals(forest1, area1.zoneWithSpecialPower(null));
    }
}
