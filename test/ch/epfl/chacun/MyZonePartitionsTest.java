package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyZonePartitionsTest {

    public static final ZonePartitions INITIAL_ZONE_PARTITIONS = new ZonePartitions(new ZonePartition<>(),
            new ZonePartition<>(), new ZonePartition<>(), new ZonePartition<>());

    private static List<Zone> getZones1() {
        Zone.Forest forest = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow1 = new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.AUROCHS)), null);
        Zone.Lake lake = new Zone.Lake(8, 1, null);
        Zone.River river = new Zone.River(3, 0, lake);
        Zone.Meadow meadow2 = new Zone.Meadow(2, new ArrayList<>(), null);

        return List.of(meadow1, forest, meadow2, river, lake);
    }

    private static ZonePartitions getZonePartitionsTemplate1() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        return new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build());
    }

    private static Tile getTileTemplate1() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);

        return new Tile(1, Tile.Kind.NORMAL, new TileSide.Meadow(meadow1), new TileSide.Forest(forest),
                new TileSide.Forest(forest), new TileSide.River(meadow2, river, meadow1));
    }

    private static List<Zone> getZones2() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, new ArrayList<>(), null);
        Zone.River river1 = new Zone.River(1, 0, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, List.of(new Animal(0, Animal.Kind.DEER)), null);
        Zone.River river2 = new Zone.River(3, 0, null);
        Zone.Meadow meadow3 = new Zone.Meadow(4, List.of(new Animal(0, Animal.Kind.TIGER)), null);

        return List.of(meadow1, river1, meadow2, river2, meadow3);
    }

    private static ZonePartitions getZonePartitionsTemplate2() {
        List<Zone> zones = getZones2();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.River river1 = (Zone.River) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river2 = (Zone.River) zones.get(3);
        Zone.Meadow meadow3 = (Zone.Meadow) zones.get(4);


        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 4);
        meadowsBuilder.addSingleton(meadow2, 2);
        meadowsBuilder.addSingleton(meadow3, 2);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river1, 2);
        riversBuilder.addSingleton(river2, 2);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river1, 2);
        riverSystemsBuilder.addSingleton(river2, 2);

        return new ZonePartitions(new ZonePartition<>(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build());
    }

    private static Tile getTileTemplate2() {
        List<Zone> zones = getZones2();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.River river1 = (Zone.River) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river2 = (Zone.River) zones.get(3);
        Zone.Meadow meadow3 = (Zone.Meadow) zones.get(4);

        return new Tile(2, Tile.Kind.NORMAL, new TileSide.River(meadow1, river1, meadow2), new TileSide.River(meadow2, river1, meadow1),
                new TileSide.River(meadow1, river2, meadow3), new TileSide.River(meadow3, river2, meadow1));
    }

    private static ZonePartitions getZonePartitionsOfTwoTiles() {
        List<Zone> zones1 = getZones1();
        Zone.Meadow meadow11 = (Zone.Meadow) zones1.get(0);
        Zone.Forest forest1 = (Zone.Forest) zones1.get(1);
        Zone.Meadow meadow12 = (Zone.Meadow) zones1.get(2);
        Zone.River river11 = (Zone.River) zones1.get(3);
        Zone.Lake lake1 = (Zone.Lake) zones1.get(4);


        List<Zone> zones2 = getZones2();
        Zone.Meadow meadow21 = (Zone.Meadow) zones2.get(0);
        Zone.River river21 = (Zone.River) zones2.get(1);
        Zone.Meadow meadow22 = (Zone.Meadow) zones2.get(2);
        Zone.River river22 = (Zone.River) zones2.get(3);
        Zone.Meadow meadow32 = (Zone.Meadow) zones2.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest1, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow11, 2);
        meadowsBuilder.addSingleton(meadow12, 1);
        meadowsBuilder.addSingleton(meadow21, 4);
        meadowsBuilder.addSingleton(meadow22, 2);
        meadowsBuilder.addSingleton(meadow32, 2);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river11, 1);
        riversBuilder.addSingleton(river21, 2);
        riversBuilder.addSingleton(river22, 2);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river11, 2);
        riverSystemsBuilder.addSingleton(lake1, 1);
        riverSystemsBuilder.union(river11, lake1);
        riverSystemsBuilder.addSingleton(river21, 2);
        riverSystemsBuilder.addSingleton(river22, 2);

        return new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build());
    }

    private static ZonePartitions getZonePartitionsUnion() {
        List<Zone> zones1 = getZones1();
        Zone.Meadow meadow11 = (Zone.Meadow) zones1.get(0);
        Zone.Forest forest1 = (Zone.Forest) zones1.get(1);
        Zone.Meadow meadow12 = (Zone.Meadow) zones1.get(2);
        Zone.River river11 = (Zone.River) zones1.get(3);
        Zone.Lake lake1 = (Zone.Lake) zones1.get(4);


        List<Zone> zones2 = getZones2();
        Zone.Meadow meadow21 = (Zone.Meadow) zones2.get(0);
        Zone.River river21 = (Zone.River) zones2.get(1);
        Zone.Meadow meadow22 = (Zone.Meadow) zones2.get(2);
        Zone.River river22 = (Zone.River) zones2.get(3);
        Zone.Meadow meadow32 = (Zone.Meadow) zones2.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest1, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow11, 2);
        meadowsBuilder.addSingleton(meadow12, 1);
        meadowsBuilder.addSingleton(meadow21, 4);
        meadowsBuilder.addSingleton(meadow22, 2);
        meadowsBuilder.addSingleton(meadow32, 2);
        meadowsBuilder.union(meadow11, meadow22);
        meadowsBuilder.union(meadow12, meadow21);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river11, 1);
        riversBuilder.addSingleton(river21, 2);
        riversBuilder.addSingleton(river22, 2);
        riversBuilder.union(river11, river21);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river11, 2);
        riverSystemsBuilder.addSingleton(lake1, 1);
        riverSystemsBuilder.union(river11, lake1);
        riverSystemsBuilder.addSingleton(river21, 2);
        riverSystemsBuilder.addSingleton(river22, 2);
        riverSystemsBuilder.union(river11, river21);

        return new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build());
    }

    @Test
    void zonePartitionsBuilderConstructor() {
        assertEquals(getZonePartitionsTemplate1(), new ZonePartitions.Builder(getZonePartitionsTemplate1()).build());
        assertEquals(getZonePartitionsTemplate2(), new ZonePartitions.Builder(getZonePartitionsTemplate2()).build());
    }

    @Test
    void addTileTestAddsCorrectZonesToPartition() {
        ZonePartitions.Builder partitionsBuilder1 = new ZonePartitions.Builder(INITIAL_ZONE_PARTITIONS);
        partitionsBuilder1.addTile(getTileTemplate1());
        assertEquals(getZonePartitionsTemplate1(), partitionsBuilder1.build());

        ZonePartitions.Builder partitionsBuilder2 = new ZonePartitions.Builder(INITIAL_ZONE_PARTITIONS);
        partitionsBuilder2.addTile(getTileTemplate2());
        assertEquals(getZonePartitionsTemplate2(), partitionsBuilder2.build());
    }

    @Test
    void connectSidesConnectsCorrectSidesTogether() {
        List<Zone> zones1 = getZones1();
        Zone.Meadow meadow11 = (Zone.Meadow) zones1.get(0);
        Zone.Meadow meadow12 = (Zone.Meadow) zones1.get(2);
        Zone.River river1 = (Zone.River) zones1.get(3);

        List<Zone> zones2 = getZones2();
        Zone.Meadow meadow21 = (Zone.Meadow) zones2.get(0);
        Zone.River river2 = (Zone.River) zones2.get(1);
        Zone.Meadow meadow22 = (Zone.Meadow) zones2.get(2);

        TileSide tileSide1 = new TileSide.River(meadow12, river1, meadow11);
        TileSide tileSide2 = new TileSide.River(meadow22, river2, meadow21);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsOfTwoTiles());
        builder.connectSides(tileSide1, tileSide2);

        assertEquals(getZonePartitionsUnion(), builder.build());
    }

    @Test
    void connectSidesThrowsIllegalArgumentExceptionIfSidesAreOfDifferentType() {
        List<Zone> zones1 = getZones1();
        Zone.Forest forest1 = (Zone.Forest) zones1.get(1);

        List<Zone> zones2 = getZones2();
        Zone.Meadow meadow21 = (Zone.Meadow) zones2.get(0);
        Zone.River river2 = (Zone.River) zones2.get(1);
        Zone.Meadow meadow22 = (Zone.Meadow) zones2.get(2);

        TileSide tileSide1 = new TileSide.Forest(forest1);
        TileSide tileSide2 = new TileSide.River(meadow22, river2, meadow21);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsOfTwoTiles());

        assertThrows(IllegalArgumentException.class, () -> builder.connectSides(tileSide1, tileSide2));
    }

    @Test
    void addInitialOccupantAddsPawnToForestZone() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        forestsBuilder.addInitialOccupant(forest, PlayerColor.RED);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, forest);

        assertEquals(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()),
                builder.build());
    }

    @Test
    void addInitialOccupantAddsPawnToMeadowZone() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        meadowsBuilder.addInitialOccupant(meadow2, PlayerColor.RED);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, meadow2);

        assertEquals(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()),
                builder.build());
    }

    @Test
    void addInitialOccupantAddsPawnToRiverZone() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        riversBuilder.addInitialOccupant(river, PlayerColor.RED);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, river);

        assertEquals(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()),
                builder.build());
    }

    @Test
    void addInitialOccupantAddsHutToLake() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        riverSystemsBuilder.addInitialOccupant(lake, PlayerColor.RED);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, lake);

        assertEquals(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()),
                builder.build());
    }

    @Test
    void addInitialOccupantAddsHutToRiverSystems() {
        List<Zone> zones = getZones2();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.River river1 = (Zone.River) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river2 = (Zone.River) zones.get(3);
        Zone.Meadow meadow3 = (Zone.Meadow) zones.get(4);


        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 4);
        meadowsBuilder.addSingleton(meadow2, 2);
        meadowsBuilder.addSingleton(meadow3, 2);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river1, 2);
        riversBuilder.addSingleton(river2, 2);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river1, 2);
        riverSystemsBuilder.addSingleton(river2, 2);

        riverSystemsBuilder.addInitialOccupant(river1, PlayerColor.RED);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate2());
        builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, river1);

        assertEquals(new ZonePartitions(new ZonePartition<>(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()),
                builder.build());
    }

    @Test
    void addInitialOccupantThrowsIllegalArgumentExceptionIfAddHutOnForest() {
        List<Zone> zones = getZones1();
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());

        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, forest));
    }

    @Test
    void addInitialOccupantThrowsIllegalArgumentExceptionIfAddHutOnMeadow() {
        List<Zone> zones = getZones1();
        Zone.Meadow meadow = (Zone.Meadow) zones.getFirst();
        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());

        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, meadow));
    }

    @Test
    void addInitialOccupantThrowsIllegalArgumentExceptionIfAddPawnToLake() {
        List<Zone> zones = getZones1();
        Zone.Lake lake = (Zone.Lake) zones.get(4);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());

        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.PAWN, lake));
    }

    @Test
    void addInitialOccupantThrowsIllegalArgumentExceptionIfAddHutOnRiverConnectedToLake() {
        List<Zone> zones = getZones1();
        Zone.River river = (Zone.River) zones.get(3);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());

        assertThrows(IllegalArgumentException.class, () -> builder.addInitialOccupant(PlayerColor.RED, Occupant.Kind.HUT, river));
    }

    @Test
    void removePawnRemovesPawnFromTheCorrectPlayerInForest() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        forestsBuilder.addInitialOccupant(forest, PlayerColor.RED);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()));
        builder.removePawn(PlayerColor.RED, forest);

        assertEquals(getZonePartitionsTemplate1(), builder.build());
    }

    @Test
    void removePawnRemovesPawnFromTheCorrectPlayerInMeadows() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        meadowsBuilder.addInitialOccupant(meadow1, PlayerColor.RED);
        ZonePartitions expected = new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build());
        meadowsBuilder.addInitialOccupant(meadow2, PlayerColor.BLUE);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()));
        builder.removePawn(PlayerColor.BLUE, meadow2);

        assertEquals(expected, builder.build());
    }

    @Test
    void removePawnRemovesPawnFromTheCorrectPlayerInRiver() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        riversBuilder.addInitialOccupant(river, PlayerColor.RED);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()));
        builder.removePawn(PlayerColor.RED, river);

        assertEquals(getZonePartitionsTemplate1(), builder.build());
    }

    @Test
    void removePawnThrowsIllegalArgumentExceptionIfZoneIsNotOccupied() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        forestsBuilder.addInitialOccupant(forest, PlayerColor.RED);
        ZonePartitions expected = new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build());

        ZonePartitions.Builder builder = new ZonePartitions.Builder(expected);
        assertThrows(IllegalArgumentException.class, () -> builder.removePawn(PlayerColor.RED, meadow1));
    }

    @Test
    void removePawnThrowsIllegalArgumentExceptionIfRemovePawnFromWrongPlayerColor() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        forestsBuilder.addInitialOccupant(forest, PlayerColor.BLUE);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()));
        assertThrows(IllegalArgumentException.class, () -> builder.removePawn(PlayerColor.RED, forest));
    }

    @Test
    void removePawnThrowsIllegalArgumentExceptionIfRemovePawnFromLake() {
        List<Zone> zones = getZones1();
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());
        assertThrows(IllegalArgumentException.class, () -> builder.removePawn(PlayerColor.RED, lake));
    }

    @Test
    void clearGatherersRemovesAllOccupantsFromForest() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        Area<Zone.Forest> forestArea = new Area<>(Set.of(forest), List.of(PlayerColor.YELLOW, PlayerColor.GREEN), 2);
        ZonePartition<Zone.Forest> forestPartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartitions.Builder builder = new ZonePartitions.Builder(new ZonePartitions(forestPartition, meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()));

        builder.clearGatherers(forestArea);

        assertEquals(getZonePartitionsTemplate1(), builder.build());
    }

    @Test
    void clearGatherersDoesNothingIfForestHasNoGatherers() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        Area<Zone.Forest> forestArea = new Area<>(Set.of(forest), new ArrayList<>(), 2);
        ZonePartition<Zone.Forest> forestPartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartitions.Builder builder = new ZonePartitions.Builder(new ZonePartitions(forestPartition, meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()));
        builder.clearGatherers(forestArea);

        assertEquals(new ZonePartitions(forestPartition, meadowsBuilder.build(), riversBuilder.build(), riverSystemsBuilder.build()), builder.build());
    }

    @Test
    void clearGatherersThrowsIllegalArgumentExceptionIfZoneDoesNotExist() {
        Zone.Forest forest = new Zone.Forest(22, Zone.Forest.Kind.WITH_MENHIR);
        Area<Zone.Forest> forestArea = new Area<>(Set.of(forest), new ArrayList<>(), 2);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());
        assertThrows(IllegalArgumentException.class, () -> builder.clearGatherers(forestArea));
    }

    @Test
    void clearFishersRemovesAllOccupantsFromRiver() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.River> riversBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riversBuilder.addSingleton(river, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        Area<Zone.River> riverArea = new Area<>(Set.of(river), List.of(PlayerColor.YELLOW, PlayerColor.GREEN), 1);
        ZonePartition<Zone.River> riverPartition = new ZonePartition<>(Set.of(riverArea));
        ZonePartitions.Builder builder = new ZonePartitions.Builder(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riverPartition, riverSystemsBuilder.build()));

        builder.clearFishers(riverArea);

        assertEquals(getZonePartitionsTemplate1(), builder.build());
    }

    @Test
    void clearFishersDoesNothingIfRiverHasNoFishers() {
        List<Zone> zones = getZones1();

        Zone.Meadow meadow1 = (Zone.Meadow) zones.get(0);
        Zone.Forest forest = (Zone.Forest) zones.get(1);
        Zone.Meadow meadow2 = (Zone.Meadow) zones.get(2);
        Zone.River river = (Zone.River) zones.get(3);
        Zone.Lake lake = (Zone.Lake) zones.get(4);

        ZonePartition.Builder<Zone.Forest> forestsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        forestsBuilder.addSingleton(forest, 2);

        ZonePartition.Builder<Zone.Meadow> meadowsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        meadowsBuilder.addSingleton(meadow1, 2);
        meadowsBuilder.addSingleton(meadow2, 1);

        ZonePartition.Builder<Zone.Water> riverSystemsBuilder = new ZonePartition.Builder<>(new ZonePartition<>());
        riverSystemsBuilder.addSingleton(river, 2);
        riverSystemsBuilder.addSingleton(lake, 1);
        riverSystemsBuilder.union(river, lake);

        Area<Zone.River> riverArea = new Area<>(Set.of(river), new ArrayList<>(), 2);
        ZonePartition<Zone.River> riverPartition = new ZonePartition<>(Set.of(riverArea));
        ZonePartitions.Builder builder = new ZonePartitions.Builder(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riverPartition, riverSystemsBuilder.build()));
        builder.clearFishers(riverArea);

        assertEquals(new ZonePartitions(forestsBuilder.build(), meadowsBuilder.build(), riverPartition, riverSystemsBuilder.build()), builder.build());
    }

    @Test
    void clearFishersThrowsIllegalArgumentExceptionIfZoneDoesNotExist() {
        Zone.River river = new Zone.River(22, 1, null);
        Area<Zone.River> riverArea = new Area<>(Set.of(river), new ArrayList<>(), 2);
        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());
        assertThrows(IllegalArgumentException.class, () -> builder.clearFishers(riverArea));
    }

    @Test
    void buildReturnsCorrectZonePartitions() {
        ZonePartitions.Builder builder = new ZonePartitions.Builder(getZonePartitionsTemplate1());
        assertEquals(getZonePartitionsTemplate1(), builder.build());
    }
}
