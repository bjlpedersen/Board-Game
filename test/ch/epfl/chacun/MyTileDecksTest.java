package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MyTileDecksTest {

    @Test
    void deckSizeWorksOnTrivialDecks() {
        List<Tile> startTile1 = List.of();
        List<Tile> normalTile1 = List.of();
        List<Tile> menhirTile1 = List.of();
        TileDecks decks = new TileDecks(startTile1, normalTile1, menhirTile1);
        assertEquals(0, decks.deckSize(Tile.Kind.START));
        assertEquals(0, decks.deckSize(Tile.Kind.NORMAL));
        assertEquals(0, decks.deckSize(Tile.Kind.MENHIR));

        List<Tile> startTile2 = List.of(new Tile(0, Tile.Kind.START, null, null, null, null));
        TileDecks decks2 = new TileDecks(startTile2, normalTile1, menhirTile1);
        assertEquals(1, decks2.deckSize(Tile.Kind.START));
    }

    @Test
    void topTileWorksOnNonTrivialDecks() {
        List<Tile> startTile1 = List.of();
        List<Tile> normalTile1 = List.of();
        List<Tile> menhirTile1 = List.of();
        TileDecks decks = new TileDecks(startTile1, normalTile1, menhirTile1);
        assertNull(decks.topTile(Tile.Kind.START));

        List<Tile> startTile2 = List.of(new Tile(0, Tile.Kind.START, null, null, null, null));
        TileDecks decks2 = new TileDecks(startTile2, normalTile1, menhirTile1);
        assertEquals(startTile2.get(0), decks2.topTile(Tile.Kind.START));
    }

    @Test
    void withTopTileDrawnOnNonTrivialDecks() {
        List<Tile> startTile1 = List.of();
        List<Tile> normalTile1 = List.of();
        List<Tile> menhirTile1 = List.of();
        TileDecks decks = new TileDecks(startTile1, normalTile1, menhirTile1);
        assertThrows(IllegalArgumentException.class, () -> decks.withTopTileDrawn(Tile.Kind.START));

        List<Tile> startTile2 = List.of(new Tile(0, Tile.Kind.START, null, null, null, null));
        TileDecks decks2 = new TileDecks(startTile2, normalTile1, menhirTile1);
        assertEquals(new TileDecks(startTile1, normalTile1, menhirTile1), decks2.withTopTileDrawn(Tile.Kind.START));
    }

    @Test
    void withTopTileDrawnUntilWorksOnNonTrivialDecks() {
        TileSide northSide = new TileSide.Meadow(new Zone.Meadow(0, null, Zone.SpecialPower.WILD_FIRE));
        TileSide eastSide = new TileSide.Forest(new Zone.Forest(1,  Zone.Forest.Kind.WITH_MENHIR));
        List<Tile> startTile1 = List.of(new Tile(0, Tile.Kind.START, northSide, eastSide, eastSide, northSide));
        List<Tile> normalTile1 = List.of(new Tile(1, Tile.Kind.NORMAL, northSide, eastSide, eastSide, northSide));
        List<Tile> menhirTile1 = List.of();
        List<Tile> startTile2 = List.of();

        TileHasOneZone requirement = new TileHasOneZone();
        assertEquals(
                new TileDecks(startTile2, normalTile1, menhirTile1),
                new TileDecks(startTile1, normalTile1, menhirTile1)
                        .withTopTileDrawnUntil(Tile.Kind.START, requirement)
        );

        Tile oneZoneTile = new Tile(0, Tile.Kind.NORMAL, northSide, northSide, northSide, northSide);
        List<Tile> startTile3 = List.of(
                new Tile(1, Tile.Kind.START, northSide, eastSide, eastSide, northSide),
                oneZoneTile
        );
        List<Tile> startTile4 = List.of(oneZoneTile);

        assertEquals(
                new TileDecks(startTile4, normalTile1, menhirTile1),
                new TileDecks(startTile3, normalTile1, menhirTile1)
                        .withTopTileDrawnUntil(Tile.Kind.START, requirement));
    }
}
