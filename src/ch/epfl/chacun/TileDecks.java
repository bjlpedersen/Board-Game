package ch.epfl.chacun;

import java.util.List;
import java.util.function.Predicate;

/**
 * This record represents the decks of tiles in the game.
 * It contains a list of start tiles, normal tiles, and menhir tiles.
 * @author Bjork Pedersen (376143)
 */
public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {

    /**
     * Constructor for the TileDecks record.
     * Makes a copy of the provided lists of tiles.
     */
    public TileDecks {
        List<Tile> copyOfStartTiles = List.copyOf(startTiles);
        List<Tile> copyOfNormalTiles = List.copyOf(normalTiles);
        List<Tile> copyOfMenhirTiles = List.copyOf(menhirTiles);
    }

    /**
     * Returns the size of the deck of the given kind of tile.
     *
     * @param kind the kind of tile
     * @return the size of the deck
     */
    public int deckSize(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles.size();
            case NORMAL -> normalTiles.size();
            case MENHIR -> menhirTiles.size();
        };
    }

    /**
     * Returns the top tile of the deck of the given kind of tile.
     *
     * @param kind the kind of tile
     * @return the top tile of the deck
     */
    public Tile topTile(Tile.Kind kind) {
        return switch (kind) {
            case START -> {
                if (startTiles.isEmpty()) {
                    yield null;
                }
                yield startTiles.getFirst();
            }
            case NORMAL -> {
                if (normalTiles.isEmpty()) {
                    throw new IllegalArgumentException();
                }
                yield normalTiles.getFirst();
            }
            case MENHIR -> {
                if (menhirTiles.isEmpty()) {
                    yield null;
                }
                yield menhirTiles.getFirst();
            }
        };
    }

    /**
     * Returns a new TileDecks with the top tile of the given kind of tile drawn.
     *
     * @param kind the kind of tile
     * @return a new TileDecks with the top tile drawn
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        return switch (kind) {
            case START -> {
                Preconditions.checkArgument(!startTiles.isEmpty());
                yield new TileDecks(startTiles.subList(1, startTiles().size()), normalTiles, menhirTiles);
            }
            case NORMAL -> {
                Preconditions.checkArgument(!normalTiles.isEmpty());
                yield new TileDecks(startTiles, normalTiles.subList(1, startTiles().size()), menhirTiles);
            }
            case MENHIR -> {
                Preconditions.checkArgument(!menhirTiles.isEmpty());
                yield new TileDecks(startTiles, normalTiles, menhirTiles.subList(1, menhirTiles.size()));
            }
        };
    }

    /**
     * Returns a new TileDecks with the top tile of the given kind of tile drawn until a tile that satisfies the given predicate is found.
     *
     * @param kind the kind of tile
     * @param predicate the predicate to test the tiles against
     * @return a new TileDecks with the top tile drawn until a tile that satisfies the given predicate is found
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        TileDecks trimmedTileDeck = new TileDecks(startTiles, normalTiles, menhirTiles);
        switch (kind) {
            case START:
                for (Tile tile : startTiles) {
                    if (!predicate.test(tile)) {
                        trimmedTileDeck = withTopTileDrawn(Tile.Kind.START);
                    }
                }
                break;
            case NORMAL:
                for (Tile tile: normalTiles) {
                    if (!predicate.test(tile)) {
                        trimmedTileDeck = withTopTileDrawn(Tile.Kind.NORMAL);
                    }
                }
                break;
            case MENHIR:
                for (Tile tile : menhirTiles) {
                    while (!predicate.test(tile)) {
                        trimmedTileDeck = withTopTileDrawn(Tile.Kind.MENHIR);
                    }
                }
                break;
        };
        return trimmedTileDeck;
    }
}