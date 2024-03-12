package ch.epfl.chacun;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MyAreaTest2 {

    @Test
    void areaConstructorThrowsWhenOpenConnectionsIsBelowZero(){

        var forestZone = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var forestZone2 = new Zone.Forest(12, Zone.Forest.Kind.PLAIN);
        var forestZone3 = new Zone.Forest(13, Zone.Forest.Kind.PLAIN);

        assertThrows(IllegalArgumentException.class, () -> {
            new Area<>(Set.of(forestZone,forestZone2,forestZone3),
                    List.of(PlayerColor.RED), -10);});

        assertDoesNotThrow(() -> {
            new Area<>(Set.of(forestZone,forestZone2,forestZone3),
                    List.of(PlayerColor.RED), 10);});

    }

    @Test
    void hasMenhirAndMushroomGroupCountWorksFine(){

        var forestZone1 = new Zone.Forest(10, Zone.Forest.Kind.WITH_MENHIR);
        var forestZone2 = new Zone.Forest(11, Zone.Forest.Kind.PLAIN);
        var forestZone3 = new Zone.Forest(12, Zone.Forest.Kind.WITH_MUSHROOMS);
        var forestZone4 = new Zone.Forest(13, Zone.Forest.Kind.WITH_MENHIR);
        var forestZone5 = new Zone.Forest(14, Zone.Forest.Kind.WITH_MUSHROOMS);
        var forestZone6 = new Zone.Forest(15, Zone.Forest.Kind.WITH_MUSHROOMS);

        // hasMenhir tests
        assertTrue(Area.hasMenhir(new Area<>(Set.of(forestZone1, forestZone2, forestZone3),
                List.of(PlayerColor.RED), 1)));

        assertTrue(Area.hasMenhir(new Area<>(Set.of(forestZone4, forestZone1, forestZone6),
                List.of(PlayerColor.RED), 2)));

        assertFalse(Area.hasMenhir(new Area<>(Set.of(forestZone2, forestZone3, forestZone5),
                List.of(PlayerColor.RED), 3)));

        // mushroomGroupCount tests

        assertEquals(0,Area.mushroomGroupCount(new Area<>(Set.of(forestZone1, forestZone2, forestZone4),
                List.of(PlayerColor.RED), 1)));

        assertEquals(1,Area.mushroomGroupCount(new Area<>(Set.of(forestZone1, forestZone2, forestZone3),
                List.of(PlayerColor.RED), 1)));

        assertEquals(2,Area.mushroomGroupCount(new Area<>(Set.of(forestZone3, forestZone4, forestZone5),
                List.of(PlayerColor.RED), 1)));

        assertEquals(3,Area.mushroomGroupCount(new Area<>(Set.of(forestZone3, forestZone5, forestZone6, forestZone1),
                List.of(PlayerColor.RED), 1)));

    }

    @Test
    void animalsWorksCorrectly(){

        var animals1 = new ArrayList<Animal>(
                Set.of(new Animal(100, Animal.Kind.DEER),
                        new Animal(101, Animal.Kind.MAMMOTH),
                        new Animal(102, Animal.Kind.DEER) ));

        var animals2 = new HashSet<Animal>(
                Set.of(new Animal(103, Animal.Kind.TIGER),
                        new Animal(101, Animal.Kind.MAMMOTH),
                        new Animal(104, Animal.Kind.MAMMOTH),
                        new Animal(105, Animal.Kind.AUROCHS) ));

        var animals3 = new HashSet<Animal>(
                Set.of(new Animal(106, Animal.Kind.TIGER),
                        new Animal(107, Animal.Kind.MAMMOTH),
                        new Animal(108, Animal.Kind.AUROCHS) ));

        var animals4 = new HashSet<Animal>(
                Set.of(new Animal(100, Animal.Kind.DEER),
                        new Animal(101, Animal.Kind.MAMMOTH),
                        new Animal(102, Animal.Kind.DEER),
                        new Animal(108, Animal.Kind.AUROCHS) ));

        var meadowZone1 = new Zone.Meadow(11,animals1,null);

        assertEquals(Set.of(new Animal(100, Animal.Kind.DEER),
                        new Animal(102, Animal.Kind.DEER)),
                Area.animals(new Area<>(Set.of(meadowZone1),List.of(),2),animals2));

        assertEquals(Set.of(new Animal(100, Animal.Kind.DEER),
                        new Animal(101, Animal.Kind.MAMMOTH),
                        new Animal(102, Animal.Kind.DEER)),
                Area.animals(new Area<>(Set.of(meadowZone1),List.of(),2),animals3));

        assertEquals(Set.of(), Area.animals(new Area<>(Set.of(meadowZone1),List.of(),2),animals4));

        assertEquals(Set.of(new Animal(100, Animal.Kind.DEER),
                new Animal(101, Animal.Kind.MAMMOTH),
                new Animal(102, Animal.Kind.DEER)), Area.animals(new Area<>(Set.of(meadowZone1),List.of(),2),Set.of()));







    }

    @Test
    void riverFishCountAndRiverSystemFishCountAndLakeCountAndTileIDsWorks(){

        var zoneLake1 = new Zone.Lake(18, 2, null);
        var zoneLake2 = new Zone.Lake(19, 3, null);
        var zoneLake3 = new Zone.Lake(11, 1, null);
        var zoneLake4 = new Zone.Lake(10, 4, null);
        var zoneRiver1 = new Zone.River(14, 3, zoneLake1);
        var zoneRiver2 = new Zone.River(15, 2, zoneLake2);
        var zoneRiver3 = new Zone.River(16, 1, zoneLake1);
        var zoneRiver4 = new Zone.River(17, 0, zoneLake2);
        var zoneRiver5 = new Zone.River(23, 1, null);
        var zoneRiver6 = new Zone.River(12, 0, null);
        var zoneForest = new Zone.Forest(14, Zone.Forest.Kind.PLAIN);

        var area1 = new Area<>(Set.of(zoneRiver1, zoneRiver2), List.of(),2); // 2 different lakes : 10
        var area2 = new Area<>(Set.of(zoneRiver1, zoneRiver3),List.of(),2); // 2 same lake : 6
        var area3 = new Area<>(Set.of(zoneRiver5,zoneRiver6),List.of(),2); // 0 lake
        var area4 = new Area<>(Set.of(zoneRiver5,zoneRiver4,zoneRiver2,zoneRiver1),List.of(),2); // 2 same 1 differen lakes :11
        var area5 = new Area<>(Set.of(zoneRiver1,zoneRiver3,zoneRiver6),List.of(),2); // 2 same + null lake : 6
        var area6 = new Area<Zone.River>(Set.of(), List.of(),2); // no river

        var area7 = new Area<Zone.Water>(Set.of(zoneRiver1, zoneRiver2, zoneLake1, zoneLake2), List.of(),2); // 2 rivers and 2 lakes but already connected :10
        var area8 = new Area<Zone.Water>(Set.of(zoneRiver1, zoneRiver2, zoneLake1, zoneLake2, zoneLake3), List.of(),2); // 2 rivers and 3 lakes but 2 already connected:11
        var area9 = new Area<Zone.Water>(Set.of(zoneRiver1, zoneRiver4,zoneLake1,zoneLake2, zoneLake3, zoneLake4), List.of(),2); // 2 rivers and 4 lakes but 2 already connected:13
        var area10 = new Area<Zone.Water>(Set.of(zoneRiver5, zoneRiver6, zoneLake4, zoneLake2), List.of(),2); // 2 rivers and 2 lakes but already connected :8



        // river fish count
        assertEquals(10, Area.riverFishCount(area1));
        assertEquals(6, Area.riverFishCount(area2));
        assertEquals(1, Area.riverFishCount(area3));
        assertEquals(11, Area.riverFishCount(area4));
        assertEquals(6, Area.riverFishCount(area5));
        assertEquals(0, Area.riverFishCount(area6));

        // hydro system fish count : by definition every connected lake is already in this system : no need to search for connected lakes
        assertEquals(10, Area.riverSystemFishCount(area7));
        assertEquals(11, Area.riverSystemFishCount(area8));
        assertEquals(13, Area.riverSystemFishCount(area9));
        assertEquals(8, Area.riverSystemFishCount(area10));


        // lake count
        assertEquals(2,Area.lakeCount(area7));
        assertEquals(3,Area.lakeCount(area8));
        assertEquals(4,Area.lakeCount(area9));
        assertEquals(2,Area.lakeCount(area10));

        // tileIds

        Set<Integer> expectedNos = Set.of();
        assertEquals(expectedNos, area6.tileIds());
        assertEquals(Set.of(1),area2.tileIds());
        assertEquals(Set.of(1,2),area3.tileIds());

    }

    @Test
    void isClosedAndIsOccupiedWorks(){
        var area1 = new Area<>(Set.of(), List.of(PlayerColor.RED), 0);
        var area2 = new Area<>(Set.of(), List.of(PlayerColor.RED, PlayerColor.YELLOW), 3);
        var area3 = new Area<>(Set.of(), List.of(), 300);

        assertTrue(area1.isClosed());
        assertFalse(area2.isClosed());
        assertFalse(area3.isClosed());

        assertTrue(area1.isOccupied());
        assertTrue(area2.isOccupied());
        assertFalse(area3.isOccupied());

    }

    @Test
    void majorityOccupantsWorks(){
        var area1 = new Area<>(Set.of(),
                List.of(PlayerColor.RED,
                        PlayerColor.YELLOW,
                        PlayerColor.GREEN), 0); // all once

        var area2 = new Area<>(Set.of(),
                List.of(PlayerColor.RED,
                        PlayerColor.RED,
                        PlayerColor.GREEN,
                        PlayerColor.YELLOW), 0); // 2 red 1 green 1 yellow

        var area3 = new Area<>(Set.of(), List.of(PlayerColor.RED,
                PlayerColor.RED,PlayerColor.RED), 0); // 3 red

        var area4 = new Area<>(Set.of(), List.of(PlayerColor.RED,
                PlayerColor.GREEN,PlayerColor.GREEN,
                PlayerColor.YELLOW,
                PlayerColor.YELLOW), 0); // 1 red 2 green 2 yellow

        var area5 = new Area<>(Set.of(), List.of(PlayerColor.RED,
                PlayerColor.GREEN,PlayerColor.GREEN,
                PlayerColor.YELLOW,
                PlayerColor.YELLOW,
                PlayerColor.RED), 0); // 2 red 2 green 2 yellow

        var area6 = new Area<>(Set.of(), List.of(), 0); // none

        assertEquals(Set.of(PlayerColor.RED,
                PlayerColor.YELLOW,
                PlayerColor.GREEN),area1.majorityOccupants());

        assertEquals(Set.of(PlayerColor.RED),area2.majorityOccupants());

        assertEquals(Set.of(PlayerColor.RED),area3.majorityOccupants());

        assertEquals(Set.of(PlayerColor.GREEN, PlayerColor.YELLOW),area4.majorityOccupants());
        assertEquals(Set.of(PlayerColor.RED,
                PlayerColor.YELLOW,
                PlayerColor.GREEN),area5.majorityOccupants());

        assertEquals(Set.of(),area6.majorityOccupants());

    }

    @Test
    void connectToWorks(){
        // different areas:
        var area1 = new Area<Zone.Meadow>(Set.of(
                new Zone.Meadow(10, List.of(new Animal(100, Animal.Kind.TIGER)),null),
                new Zone.Meadow(11, List.of(new Animal(101, Animal.Kind.TIGER)),null)), List.of(PlayerColor.RED), 4);

        var area2 = new Area<Zone.Meadow>(Set.of(
                new Zone.Meadow(10, List.of(new Animal(100, Animal.Kind.AUROCHS)),null),
                new Zone.Meadow(11, List.of(new Animal(101, Animal.Kind.DEER)),null)), List.of(PlayerColor.RED,PlayerColor.YELLOW), 4);

        var area3 = new Area<Zone.Forest>(Set.of(
                new Zone.Forest(10, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(11, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.RED, PlayerColor.YELLOW), 2);

        var expectedArea1 = new Area<Zone.Meadow>(Set.of(
                new Zone.Meadow(10, List.of(new Animal(100, Animal.Kind.TIGER)),null),
                new Zone.Meadow(11, List.of(new Animal(101, Animal.Kind.TIGER)),null),
                new Zone.Meadow(10, List.of(new Animal(100, Animal.Kind.AUROCHS)),null),
                new Zone.Meadow(11, List.of(new Animal(101, Animal.Kind.DEER)),null)),
                List.of(PlayerColor.RED,PlayerColor.RED,PlayerColor.YELLOW),6);

        var expectedArea2 = new Area<Zone.Forest>(Set.of(
                new Zone.Forest(10, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(11, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.RED, PlayerColor.YELLOW), 0);

        assertEquals(expectedArea1,area1.connectTo(area2));
        assertEquals(expectedArea1,area2.connectTo(area1));

        // same areas: example #240 in ED, the case where you call this function two times

        // 1 1     1 1
        // 1 2 ->  1 1

        assertEquals(expectedArea2,area3.connectTo(area3));

    }

    @Test
    void withInitialOccupantAndWithoutOccupantAndWithoutOccupantsWorks(){

        // withInitialOccupant

        var area1 = new Area<>(Set.of(),
                List.of(PlayerColor.RED,
                        PlayerColor.YELLOW,
                        PlayerColor.GREEN), 0); // with occupant

        var area2 = new Area<>(Set.of(), List.of(), 0); // no occupant

        var area3 = new Area<>(Set.of(),
                List.of(PlayerColor.RED,
                        PlayerColor.YELLOW), 0); // with occupant


        var area2Expected = new Area<>(Set.of(), List.of(PlayerColor.YELLOW), 0);

        assertEquals(area2Expected, area2.withInitialOccupant(PlayerColor.YELLOW));
        assertThrows(IllegalArgumentException.class, () -> area1.withInitialOccupant(PlayerColor.YELLOW) );

        // withoutOccupant

        assertThrows(IllegalArgumentException.class, () -> area2.withoutOccupant(PlayerColor.YELLOW) );
        assertThrows(IllegalArgumentException.class, () -> area3.withoutOccupant(PlayerColor.GREEN) );
        assertEquals(area3,area1.withoutOccupant(PlayerColor.GREEN));

        // withoutOccupants

        assertEquals(area2, area2.withoutOccupants()); // no occupant
        assertEquals(area2, area3.withoutOccupants());
        assertEquals(area2, area1.withoutOccupants());


    }

    @Test
    void zoneWithSpecialPowerWorks(){

        // there is always one special power in an area

        var zoneLake1 = new Zone.Lake(18, 2, null);
        var zoneLake2 = new Zone.Lake(19, 2, null);

        var zoneMeadow1 = new Zone.Meadow(10, List.of(),null);
        var zoneMeadow2 = new Zone.Meadow(10, List.of(), Zone.SpecialPower.SHAMAN);

        var area1 = new Area<>(Set.of(), List.of(PlayerColor.RED), 0);
        var area2 = new Area<>(Set.of(zoneMeadow1,zoneMeadow2), List.of(PlayerColor.RED), 0);
        var area3 = new Area<>(Set.of(zoneLake1,zoneLake2), List.of(), 0);


        assertNull(area1.zoneWithSpecialPower(Zone.SpecialPower.SHAMAN)); // empty zones
        assertEquals(zoneMeadow2, area2.zoneWithSpecialPower(Zone.SpecialPower.SHAMAN));
        assertNull(area2.zoneWithSpecialPower(Zone.SpecialPower.HUNTING_TRAP));

        assertNull(area3.zoneWithSpecialPower(Zone.SpecialPower.RAFT));

    }


}