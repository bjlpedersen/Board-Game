package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MyZonePartitionsBuilderTest {

    @Test
    void addTileWorksOnRandomExample() {
        ZonePartitions initial = ZonePartitions.EMPTY;
        ZonePartitions.Builder builder = new ZonePartitions.Builder(initial);
        TileSide.Forest north = new TileSide.Forest(new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR));
        TileSide.Forest east = new TileSide.Forest(new Zone.Forest(1, Zone.Forest.Kind.PLAIN));
        TileSide.River south = new TileSide.River(new Zone.Meadow(2, null, null),
                new Zone.River(3, 0, new Zone.Lake(8, 0, null)),
                new Zone.Meadow(4, null, null));
        TileSide.Meadow west = new TileSide.Meadow(new Zone.Meadow(5, null, null));
        Tile tile1 = new Tile(0, Tile.Kind.NORMAL, north, east, south, west);
        builder.addTile(tile1);
        ZonePartitions partitions = builder.build();
        assertEquals(2, partitions.forests().areas().size());
        assertEquals(3, partitions.meadows().areas().size());
        assertEquals(1, partitions.rivers().areas().size());
        assertEquals(1, partitions.riverSystems().areas().size());
    }

//    @Test
//    void connectSidesWorksOnTrivialAndNonTrivial() {
//        ZonePartitions initial = ZonePartitions.EMPTY;
//        ZonePartitions.Builder builder = new ZonePartitions.Builder(initial);
//        TileSide.Forest north = new TileSide.Forest(new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR));
//        TileSide.Forest east = new TileSide.Forest(new Zone.Forest(1, Zone.Forest.Kind.PLAIN));
//        TileSide.River south = new TileSide.River(new Zone.Meadow(2, null, null),
//                new Zone.River(3, 0, new Zone.Lake(8, 0, null)),
//                new Zone.Meadow(4, null, null));
//        TileSide.Meadow west = new TileSide.Meadow(new Zone.Meadow(5, null, null));
//        Tile tile1 = new Tile(0, Tile.Kind.NORMAL, north, east, south, west);
//        builder.addTile(tile1);
//        builder.connectSides(s1, s2);
//        assertEquals();
//    }

    @Test
    void addInitialOccupantWorksOnTrivialAndNonTrivial() {
        ZonePartitions initial = ZonePartitions.EMPTY;
        ZonePartitions.Builder builder = new ZonePartitions.Builder(initial);
        Zone.Forest forest = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        TileSide.Forest north = new TileSide.Forest(forest);
        TileSide.Forest east = new TileSide.Forest(new Zone.Forest(1, Zone.Forest.Kind.PLAIN));
        TileSide.River south = new TileSide.River(new Zone.Meadow(2, null, null),
                new Zone.River(3, 0, new Zone.Lake(8, 0, null)),
                new Zone.Meadow(4, null, null));
        TileSide.Meadow west = new TileSide.Meadow(new Zone.Meadow(5, null, null));
        Tile tile1 = new Tile(0, Tile.Kind.NORMAL, north, east, south, west);
        builder.addTile(tile1);
        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.BLUE, Occupant.Kind.HUT, forest));
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, forest);
        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.BLUE, Occupant.Kind.PAWN, forest));
        ZonePartitions partitions = builder.build();
        boolean added = false;
        for (Area forests : partitions.forests().areas()) {
            if (forests.occupants().contains(PlayerColor.RED)) {
                added = true;
                break;
            }
        }
        assertTrue(added);
    }

    @Test
    void removePawnWorks() {
        ZonePartitions initial = ZonePartitions.EMPTY;
        ZonePartitions.Builder builder = new ZonePartitions.Builder(initial);
        Zone.Forest forest = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        TileSide.Forest north = new TileSide.Forest(forest);
        TileSide.Forest east = new TileSide.Forest(new Zone.Forest(1, Zone.Forest.Kind.PLAIN));
        TileSide.River south = new TileSide.River(new Zone.Meadow(2, null, null),
                new Zone.River(3, 0, new Zone.Lake(8, 0, null)),
                new Zone.Meadow(4, null, null));
        TileSide.Meadow west = new TileSide.Meadow(new Zone.Meadow(5, null, null));
        Tile tile1 = new Tile(0, Tile.Kind.NORMAL, north, east, south, west);
        builder.addTile(tile1);
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, forest);
        assertThrows(IllegalArgumentException.class, () -> builder.removePawn(PlayerColor.BLUE, forest));
        builder.removePawn(PlayerColor.RED, forest);
        ZonePartitions partitions = builder.build();

        boolean removed = true;
        for (Area forests : partitions.forests().areas()) {
            if (forests.occupants().contains(PlayerColor.RED)) {
                removed = false;
                break;
            }
        }
        assertTrue(removed);
    }

    @Test
    void clearGatherersWorks() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Area<Zone.Forest> area = new Area<>(Set.of(forest1, forest2), List.of(PlayerColor.BLUE, PlayerColor.RED), 2);
        ZonePartition<Zone.Forest> partition = new ZonePartition<>(Set.of(area));
        ZonePartitions initial = new ZonePartitions(partition, new ZonePartition<Zone.Meadow>(),
                new ZonePartition<Zone.River>(),
                new ZonePartition<Zone.Water>());
        ZonePartitions.Builder builder = new ZonePartitions.Builder(initial);
        TileSide.Forest north = new TileSide.Forest(forest1);
        TileSide.Forest east = new TileSide.Forest(forest2);
        TileSide.River south = new TileSide.River(new Zone.Meadow(2, null, null),
                new Zone.River(3, 0, new Zone.Lake(8, 0, null)),
                new Zone.Meadow(4, null, null));
        TileSide.Meadow west = new TileSide.Meadow(new Zone.Meadow(5, null, null));
        Tile tile1 = new Tile(0, Tile.Kind.NORMAL, north, east, south, west);
        builder.addTile(tile1);
        builder.clearGatherers(area);
        ZonePartitions partitions = builder.build();
        assertEquals(List.of(PlayerColor.RED, PlayerColor.BLUE), area.occupants());
    }
}
