package ch.epfl.chacun;

import java.util.List;
import java.util.function.Predicate;

/**
 * This class represents a collection of decks of tiles in the game.
 * Each deck is categorized by the kind of tile it contains.
 *
 * @author Bjork Pedersen (376143)
 */
public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {

    /**
     * Constructor for TileDecks.
     * It initializes the decks with the provided lists of tiles.
     *
     * @param startTiles  List of start tiles.
     * @param normalTiles List of normal tiles.
     * @param menhirTiles List of menhir tiles.
     */
    public TileDecks {
        startTiles = List.copyOf(startTiles);
        normalTiles = List.copyOf(normalTiles);
        menhirTiles = List.copyOf(menhirTiles);
    }

    /**
     * Returns the size of the specified deck.
     *
     * @param kind The kind of the deck.
     * @return The size of the deck.
     */
    public int deckSize(Tile.Kind kind) {
        return getDeck(kind).size();
    }

    /**
     * Returns the top tile of the specified deck.
     *
     * @param kind The kind of the deck.
     * @return The top tile of the deck, or null if the deck is empty.
     */
    public Tile topTile(Tile.Kind kind) {
        List<Tile> deck = getDeck(kind);
        return deck.isEmpty() ? null : deck.get(0);
    }

    /**
     * Returns a new TileDecks instance with the top tile drawn from the specified deck.
     *
     * @param kind The kind of the deck.
     * @return A new TileDecks instance with the top tile drawn.
     * @throws IllegalArgumentException If the deck is empty.
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        List<Tile> deck = getDeck(kind);
        Preconditions.checkArgument(!deck.isEmpty());
        List<Tile> newDeck = deck.subList(1, deck.size());
        return updateDeck(kind, newDeck);
    }

    /**
     * Returns a new TileDecks instance with the top tile drawn from the specified deck until a condition is met.
     *
     * @param kind      The kind of the deck.
     * @param predicate The condition to stop drawing tiles.
     * @return A new TileDecks instance with the top tiles drawn until the condition is met.
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        TileDecks trimmedTileDeck = this;
        List<Tile> deck = getDeck(kind);
        for (Tile tile : deck) {
            if (!predicate.test(tile) && !trimmedTileDeck.getDeck(kind).isEmpty()) {
                trimmedTileDeck = trimmedTileDeck.withTopTileDrawn(kind);
            }
            if (predicate.test(tile)) {
                break;
            }
        }
        return trimmedTileDeck;
    }

    /**
     * Returns the deck of the specified kind.
     *
     * @param kind The kind of the deck.
     * @return The deck of the specified kind.
     */
    private List<Tile> getDeck(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles;
            case NORMAL -> normalTiles;
            case MENHIR -> menhirTiles;
        };
    }

    /**
     * Returns a new TileDecks instance with the specified deck updated.
     *
     * @param kind    The kind of the deck.
     * @param newDeck The new deck to replace the old one.
     * @return A new TileDecks instance with the updated deck.
     */
    private TileDecks updateDeck(Tile.Kind kind, List<Tile> newDeck) {
        return switch (kind) {
            case START -> new TileDecks(newDeck, normalTiles, menhirTiles);
            case NORMAL -> new TileDecks(startTiles, newDeck, menhirTiles);
            case MENHIR -> new TileDecks(startTiles, normalTiles, newDeck);
        };
    }
}