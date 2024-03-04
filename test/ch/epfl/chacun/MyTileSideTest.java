package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MyTileSideTest {
    @Test
    void zonesWorksForEveryZoneId() {
        for (int zoneId = 0; zoneId < 9; zoneId++) {
            List<Zone> forestList = new ArrayList<>();
            Zone.Forest forest = new Zone.Forest(zoneId, Zone.Forest.Kind.WITH_MENHIR);
            forestList.add(forest);
            List<Zone> meadowList = new ArrayList<>();
            Zone.Meadow meadow = new Zone.Meadow(zoneId, null, Zone.SpecialPower.HUNTING_TRAP);
            meadowList.add(meadow);
            List<Zone> riverList = new ArrayList<>();
            Zone.River river = new Zone.River(zoneId, 2, null);
            Zone.Meadow meadow2 = new Zone.Meadow(5, null, Zone.Meadow.SpecialPower.HUNTING_TRAP);
            riverList.add(meadow);
            riverList.add(river);
            riverList.add(meadow2);

            assertEquals(forestList, new TileSide.Forest(forest).zones());
            assertEquals(meadowList, new TileSide.Meadow(meadow).zones());
            assertEquals(riverList, new TileSide.River(meadow, river, meadow2).zones());

        }
    }

    @Test
    void zonesWorksForTrivialExample() {
        Zone.Forest forest = null;
        TileSide tileSideForest = new TileSide.Forest(forest);
        List<Zone> zones = new ArrayList<>();
        zones.add(null);
        assertEquals(zones, tileSideForest.zones());
    }

    @Test
    void isSameKindAsWorksOnNontrivialExamples() {
        Zone.Meadow meadow1 = new Zone.Meadow(4, null, Zone.Meadow.SpecialPower.WILD_FIRE);
        TileSide tileSideMeadow1 = new TileSide.Meadow(meadow1);
        Zone.Meadow meadow2 = new Zone.Meadow(6, null, Zone.SpecialPower.HUNTING_TRAP);
        TileSide tileSideMeadow2 = new TileSide.Meadow(meadow2);
        Zone.Forest forest = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        TileSide tileSideForest = new TileSide.Forest(forest);
        assertTrue(tileSideMeadow1.isSameKindAs(tileSideMeadow2));
        assertFalse(tileSideMeadow1.isSameKindAs(tileSideForest));
    }
}
