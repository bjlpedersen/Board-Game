package ch.epfl.chacun;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyZonePartitionTest {
    @Test
    void areaContainingWithGoodZone(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        ZonePartition<Zone> zonePartitions= new ZonePartition<>(Set.of(area1,area2,area3));
        assertEquals(area2,zonePartitions.areaContaining(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)));
        assertEquals(area1,zonePartitions.areaContaining(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)));
        assertEquals(area3,zonePartitions.areaContaining(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)));
    }

    @Test
    void areaContainingWithWrongZone(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        ZonePartition<Zone> zonePartitions= new ZonePartition<>(Set.of(area1,area2,area3));
        assertThrows(IllegalArgumentException.class, () -> zonePartitions.areaContaining(new Zone.Forest(4, Zone.Forest.Kind.PLAIN)));
    }

    @Test
    void addSingletonWorks(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        Area<Zone> areaTest = new Area<>(Set.of(new Zone.Forest(4, Zone.Forest.Kind.PLAIN)),List.of(),3);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        zonePartitionBuilder.addSingleton(new Zone.Forest(4, Zone.Forest.Kind.PLAIN),3);
        ZonePartition<Zone> zonePartitionBuilt = zonePartitionBuilder.build();
        ZonePartition<Zone> zonePartitionTest = new ZonePartition<>(Set.of(area1,area2,area3,areaTest));
        assertEquals(zonePartitionTest,zonePartitionBuilt);
    }

    @Test
    void addInitialOccupantWorks(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        Area<Zone> areaTest = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)),List.of(PlayerColor.RED),0);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        zonePartitionBuilder.addInitialOccupant(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS),PlayerColor.RED);
        ZonePartition<Zone> zonePartitionBuilt = zonePartitionBuilder.build();
        ZonePartition<Zone> zonePartitionTest = new ZonePartition<>(Set.of(area1,area2,areaTest));
        assertEquals(zonePartitionTest,zonePartitionBuilt);
    }

    @Test
    void addInitialOccupantAreaOccupied(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        assertThrows(IllegalArgumentException.class, () -> zonePartitionBuilder.addInitialOccupant(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR),PlayerColor.RED));
    }
    @Test
    void addInitialOccupantAreaNotBelong(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        assertThrows(IllegalArgumentException.class, () -> zonePartitionBuilder.addInitialOccupant(new Zone.Forest(4, Zone.Forest.Kind.PLAIN),PlayerColor.GREEN));
    }

    @Test
    void removeOccupantWorks(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE,PlayerColor.RED),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        Area<Zone> areaTest = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)),List.of(PlayerColor.GREEN,PlayerColor.PURPLE,PlayerColor.RED),3);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        zonePartitionBuilder.removeOccupant(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR),PlayerColor.RED);
        ZonePartition<Zone> zonePartitionBuilt = zonePartitionBuilder.build();
        ZonePartition<Zone> zonePartitionTest = new ZonePartition<>(Set.of(area1,areaTest,area3));
        assertEquals(zonePartitionTest,zonePartitionBuilt);
    }

    @Test
    void removeOccupantHasNoOccupant(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE,PlayerColor.RED),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        assertThrows(IllegalArgumentException.class, () -> zonePartitionBuilder.removeOccupant(new Zone.Forest(1, Zone.Forest.Kind.PLAIN),PlayerColor.BLUE));
        assertThrows(IllegalArgumentException.class, () -> zonePartitionBuilder.removeOccupant(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS),PlayerColor.RED));
    }

    @Test
    void removeOccupantAreaNotBelong(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE,PlayerColor.RED),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        assertThrows(IllegalArgumentException.class, () -> zonePartitionBuilder.removeOccupant(new Zone.Forest(4, Zone.Forest.Kind.PLAIN),PlayerColor.RED));
    }

    @Test
    void removeAllOccupantOfWorks(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE,PlayerColor.RED),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        Area<Zone> areaTest1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(),2);
        Area<Zone> areaTest2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)),List.of(),3);
        Area<Zone> areaTest3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        zonePartitionBuilder.removeAllOccupantsOf(area1);
        zonePartitionBuilder.removeAllOccupantsOf(area2);
        zonePartitionBuilder.removeAllOccupantsOf(area3);
        ZonePartition<Zone> zonePartitionBuilt = zonePartitionBuilder.build();
        ZonePartition<Zone> zonePartitionTest = new ZonePartition<>(Set.of(areaTest1,areaTest2,areaTest3));
        assertEquals(zonePartitionTest,zonePartitionBuilt);

    }

    @Test
    void removeAllOccupantOfAreaNotBelong(){
        Area<Zone> area1 = new Area<>(Set.of(new Zone.Forest(1, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        Area<Zone> area2 = new Area<>(Set.of(new Zone.Forest(2, Zone.Forest.Kind.WITH_MENHIR)), List.of(PlayerColor.GREEN,PlayerColor.RED,PlayerColor.PURPLE,PlayerColor.RED),3);
        Area<Zone> area3 = new Area<>(Set.of(new Zone.Forest(3, Zone.Forest.Kind.WITH_MUSHROOMS)), List.of(),0);
        ZonePartition<Zone> zonePartitions = new ZonePartition<>(Set.of(area1,area2,area3));
        ZonePartition.Builder<Zone> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitions);
        Area<Zone> areaTest = new Area<>(Set.of(new Zone.Forest(4, Zone.Forest.Kind.PLAIN)), List.of(PlayerColor.GREEN,PlayerColor.RED),2);
        assertThrows(IllegalArgumentException.class, () -> zonePartitionBuilder.removeAllOccupantsOf(areaTest));
    }

    @Test
    void unionWorksOnSameArea(){

        Zone.Forest forestZone1 = new Zone.Forest(561, Zone.Forest.Kind.PLAIN);
        Zone.Forest forestZone2 = new Zone.Forest(573, Zone.Forest.Kind.WITH_MUSHROOMS);
        Zone.Forest forestZone3 = new Zone.Forest(792, Zone.Forest.Kind.WITH_MENHIR);

        Set<Zone.Forest> forestsInTheArea = new HashSet<>();
        forestsInTheArea.add(forestZone1);
        forestsInTheArea.add(forestZone2);
        forestsInTheArea.add(forestZone3);

        List<PlayerColor> occupantsOfTheArea = List.of(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.GREEN);

        int openConnectionsOfTheArea = 2;

        Area<Zone.Forest> forestArea1 = new Area<>(forestsInTheArea, occupantsOfTheArea, openConnectionsOfTheArea);
        Area<Zone.Forest> forestAreaExpected = new Area<>(forestsInTheArea, occupantsOfTheArea, 0);

        ZonePartition<Zone.Forest> zonePartitionBeforeBuilt = new ZonePartition<>(Set.of(forestArea1));
        ZonePartition.Builder<Zone.Forest> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitionBeforeBuilt);
        zonePartitionBuilder.union(forestZone1, forestZone2);
        ZonePartition<Zone.Forest> zonePartitionAfterBuilt = zonePartitionBuilder.build();

        ZonePartition<Zone.Forest> zonePartitionExpected = new ZonePartition<>(Set.of(forestAreaExpected));

        assertEquals(zonePartitionExpected, zonePartitionAfterBuilt);
    }

    @Test
    void unionWorksOnTwoDifferentAreas(){

        //Area1
        Zone.Forest forestZone1 = new Zone.Forest(561, Zone.Forest.Kind.PLAIN);
        Zone.Forest forestZone2 = new Zone.Forest(573, Zone.Forest.Kind.WITH_MUSHROOMS);

        Set<Zone.Forest> forestsInTheArea1 = new HashSet<>();
        forestsInTheArea1.add(forestZone1);
        forestsInTheArea1.add(forestZone2);

        List<PlayerColor> occupantsOfTheArea1 = List.of(PlayerColor.GREEN, PlayerColor.RED);

        int openConnectionsOfTheArea1 = 2;

        Area<Zone.Forest> forestArea1 = new Area<>(forestsInTheArea1, occupantsOfTheArea1, openConnectionsOfTheArea1);

        //Area2

        Zone.Forest forestZone3 = new Zone.Forest(792, Zone.Forest.Kind.WITH_MENHIR);

        Set<Zone.Forest> forestsInTheArea2 = new HashSet<>();
        forestsInTheArea2.add(forestZone3);

        List<PlayerColor> occupantsOfTheArea2 = List.of(PlayerColor.GREEN, PlayerColor.YELLOW);

        int openConnectionsOfTheArea2 = 1;

        Area<Zone.Forest> forestArea2 = new Area<>(forestsInTheArea2, occupantsOfTheArea2, openConnectionsOfTheArea2);

        //AreaExpected

        Set<Zone.Forest> forestsInTheAreaExpected = new HashSet<>();
        forestsInTheAreaExpected.add(forestZone1);
        forestsInTheAreaExpected.add(forestZone2);
        forestsInTheAreaExpected.add(forestZone3);

        List<PlayerColor> occupantsOfTheAreaExpected = List.of(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW);

        int openConnectionsOfTheAreaExpected = 1;

        Area<Zone.Forest> forestAreaExpected = new Area<>(forestsInTheAreaExpected, occupantsOfTheAreaExpected, openConnectionsOfTheAreaExpected);

        //ZonePartitions
        ZonePartition<Zone.Forest> zonePartitionBeforeBuilt = new ZonePartition<>(Set.of(forestArea1, forestArea2));
        ZonePartition.Builder<Zone.Forest> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitionBeforeBuilt);
        zonePartitionBuilder.union(forestZone1, forestZone3);
        ZonePartition<Zone.Forest> zonePartitionAfterBuilt = zonePartitionBuilder.build();

        ZonePartition<Zone.Forest> zonePartitionExpected = new ZonePartition<>(Set.of(forestAreaExpected));

        assertEquals(zonePartitionExpected, zonePartitionAfterBuilt);
    }

    @Test
    void unionWorksOnZoneNotInArea(){

        //Area1
        Zone.Forest forestZone1 = new Zone.Forest(561, Zone.Forest.Kind.PLAIN);

        Set<Zone.Forest> forestsInTheArea1 = new HashSet<>();
        forestsInTheArea1.add(forestZone1);

        List<PlayerColor> occupantsOfTheArea1 = List.of(PlayerColor.GREEN, PlayerColor.RED);

        int openConnectionsOfTheArea1 = 2;

        Area<Zone.Forest> forestArea1 = new Area<>(forestsInTheArea1, occupantsOfTheArea1, openConnectionsOfTheArea1);


        //Area2

        Zone.Forest forestZone3 = new Zone.Forest(792, Zone.Forest.Kind.WITH_MENHIR);

        Set<Zone.Forest> forestsInTheArea2 = new HashSet<>();
        forestsInTheArea2.add(forestZone3);

        List<PlayerColor> occupantsOfTheArea2 = List.of(PlayerColor.GREEN, PlayerColor.YELLOW);

        int openConnectionsOfTheArea2 = 1;

        Area<Zone.Forest> forestArea2 = new Area<>(forestsInTheArea2, occupantsOfTheArea2, openConnectionsOfTheArea2);


        //Forest not in area
        Zone.Forest forestZone2 = new Zone.Forest(573, Zone.Forest.Kind.WITH_MUSHROOMS);


        //ZonePartitions
        ZonePartition<Zone.Forest> zonePartitionBeforeBuilt = new ZonePartition<>(Set.of(forestArea1, forestArea2));
        ZonePartition.Builder<Zone.Forest> zonePartitionBuilder = new ZonePartition.Builder<>(zonePartitionBeforeBuilt);


        assertThrows(IllegalArgumentException.class, () -> zonePartitionBuilder.union(forestZone1, forestZone2));
    }

}
