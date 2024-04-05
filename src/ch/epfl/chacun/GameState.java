package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the state of a game.
 * This class encapsulates all the information about the current state of the game, including the players, the board, the tiles, and the next action to be performed.
 * @author Bjork Pedersen (376143)
 */
public record GameState(List<PlayerColor> players, TileDecks tileDecks, Tile tileToPlace, Board board, Action nextAction, MessageBoard messageBoard) {
    /**
     * Enum representing the possible actions in the game.
     * @author Bjork Pedersen (376143)
     */
    public enum Action {
        START_GAME,
        PLACE_TILE,
        RETAKE_PAWN,
        OCCUPY_TILE,
        END_GAME;
    }

    /**
     * Constructor for GameState.
     * @param players List of players in the game.
     * @param tileDecks The decks of tiles in the game.
     * @param tileToPlace The tile to be placed next.
     * @param board The current state of the game board.
     * @param nextAction The next action to be performed in the game.
     * @param messageBoard The message board for the game.
     * @throws IllegalArgumentException if the number of players is less than 2, or if the tile to place and next action are not consistent, or if any of the board, message board, tile decks, or next action are null.
     */
    public GameState {
        Preconditions.checkArgument(players.size() >= 2);
        Preconditions.checkArgument(tileToPlace == null ^ nextAction == Action.PLACE_TILE);
        Objects.requireNonNull(board);
        Objects.requireNonNull(messageBoard);
        Objects.requireNonNull(board);
        Objects.requireNonNull(nextAction);
        players = List.copyOf(players);
    }

    /**
     * Creates the initial state of the game.
     * @param players List of players in the game.
     * @param tileDecks The decks of tiles in the game.
     * @param textMaker The text maker for the game.
     * @return A new GameState representing the initial state of the game.
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(players, tileDecks, null, Board.EMPTY, Action.START_GAME, new MessageBoard(textMaker, List.of()));
    }

    /**
     * Gets the current player.
     * @return The current player, or null if the game has not started or has ended.
     */
    public PlayerColor currentPlayer() {
        if (nextAction == Action.START_GAME || nextAction == Action.END_GAME) {
            return null;
        }
        return players.get(0);
    }

    /**
     * Counts the number of free occupants of a certain kind for a player.
     * @param player The player.
     * @param kind The kind of occupant.
     * @return The number of free occupants of the specified kind for the specified player.
     */
    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return Occupant.occupantsCount(kind) - board.occupantCount(player, kind);
    }

    /**
     * Gets the potential occupants for the last placed tile.
     * @return A set of potential occupants for the last placed tile.
     * @throws IllegalArgumentException if the last placed tile is null.
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        Preconditions.checkArgument(board.lastPlacedTile() != null);
        Set<Occupant> potentialOccupants = new HashSet<>();
        for (Zone zone : board.lastPlacedTile().tile().zones()) {
            if (zone instanceof Zone.River river && river.hasLake() || zone instanceof Zone.Lake) {
                potentialOccupants.add(new Occupant(Occupant.Kind.HUT, zone.id()));
            } else {
                potentialOccupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
            }
        }
        return potentialOccupants;
    }

    /**
     * Returns a new GameState with the starting tile placed.
     * @return A new GameState with the starting tile placed.
     * @throws IllegalArgumentException if the next action is not START_GAME.
     */
    public GameState withStartingTilePlaced() {
        Preconditions.checkArgument(nextAction == Action.START_GAME);
        TileDecks newTileDecks = new TileDecks(new ArrayList<>(), tileDecks.normalTiles(), tileDecks.menhirTiles());
        newTileDecks = newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL);
        PlacedTile startTilePlaced = new PlacedTile(tileDecks.startTiles().get(0), null, Rotation.NONE, new Pos(0, 0));
        Board newBoard = board.withNewTile(startTilePlaced);
        return new GameState(players, newTileDecks, tileDecks.topTile(Tile.Kind.NORMAL), newBoard, Action.PLACE_TILE, messageBoard);
    }


    /**
     * Checks if the occupation of a tile is possible.
     * @return true if occupation is possible, false otherwise.
     */
    private boolean occupationIsPossible(Board someBoard) {
        boolean canOccupy = false;
        for (Zone zone : someBoard.lastPlacedTile().tile().zones()) {
            if (((zone instanceof Zone.River river && river.hasLake()) || zone instanceof Zone.Lake) && !someBoard.riverSystemArea((Zone.Water) zone).isOccupied()) {
                if (freeOccupantsCount(currentPlayer(), Occupant.Kind.HUT) > 0) {
                    canOccupy = true;
                }
            } else {
                if (freeOccupantsCount(currentPlayer(), Occupant.Kind.PAWN) == 0) {
                    return false;
                }
                if (zone instanceof Zone.River river && !someBoard.riverSystemArea(river).isOccupied()) {
                        canOccupy = true;
                } else if (zone instanceof Zone.Meadow) {
                    if (!someBoard.meadowArea((Zone.Meadow) zone).isOccupied()) {
                        canOccupy = true;
                    }
                } else if (zone instanceof Zone.Forest) {
                    if (!someBoard.forestArea((Zone.Forest) zone).isOccupied()) {
                        canOccupy = true;
                    }
                }
            }
        }
        return canOccupy;
    }

    /**
     * Checks if the last placed tile closed a forest with a menhir.
     * @return true if the last placed tile closed a forest with a menhir, false otherwise.
     */
    private boolean lastTileClosedForestWithMenhir() {
        PlacedTile lastPlaced = board.lastPlacedTile();
        if (lastPlaced == null) {
            return false;
        } else {
            for (Area<Zone.Forest> forestArea : board.forestsClosedByLastTile()) {
                if (Area.hasMenhir(forestArea)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the forest area that was closed with a menhir.
     * @return the forest area that was closed with a menhir, or null if no such area exists.
     */
    private Area<Zone.Forest> forestClosedWithMenhir() {
        PlacedTile lastPlaced = board.lastPlacedTile();
        if (lastPlaced == null) {
            return null;
        } else {
            for (Area<Zone.Forest> forestArea : board.forestsClosedByLastTile()) {
                if (Area.hasMenhir(forestArea)) {
                    return forestArea;
                }
            }
        }
        return null;
    }

    /**
     * Checks if it is possible to remove a pawn.
     * @return true if it is possible to remove a pawn, false otherwise.
     */
    private boolean removePawnIsPossible() {
        return board.occupantCount(players.getFirst(), Occupant.Kind.PAWN) > 0;
    }

    /**
     * Returns a new GameState with the turn of the current player finished and the next ove to make.
     * @param tile the tile that was placed during the turn.
     * @return a new GameState with the turn finished.
     */
    private GameState withTurnFinished(PlacedTile tile, Board otherBoard) {
        MessageBoard newMessageBoard = new MessageBoard(messageBoard().textMaker(), messageBoard.messages());
        if (this.nextAction == Action.PLACE_TILE) {
            Set<Area<Zone.Forest>> closedForests = board.forestsClosedByLastTile();
            Set<Area<Zone.River>> closedRivers = board.riversClosedByLastTile();
            if (!closedForests.isEmpty()) {
                for (Area<Zone.Forest> forestArea : closedForests) {
                    newMessageBoard.withScoredForest(forestArea);
                }
            }
            if (!closedRivers.isEmpty()) {
                for (Area<Zone.River> riverArea : closedRivers) {
                    newMessageBoard.withScoredRiver(riverArea);
                }
            }
            if (lastTileClosedForestWithMenhir() && !tileDecks.menhirTiles().isEmpty() && tile.tile().kind() == Tile.Kind.NORMAL) {
                TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(Tile.Kind.MENHIR, otherBoard::couldPlaceTile);
                TileDecks finalTileDeck = newTileDecks.withTopTileDrawn(Tile.Kind.MENHIR);
                Tile nextTileToPlace = finalTileDeck.topTile(Tile.Kind.MENHIR);
                finalTileDeck = finalTileDeck.withTopTileDrawn(Tile.Kind.MENHIR);
                newMessageBoard.withClosedForestWithMenhir(currentPlayer(), forestClosedWithMenhir());
                return new GameState(players, finalTileDeck, nextTileToPlace, otherBoard, Action.PLACE_TILE, newMessageBoard);
            }
            TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, otherBoard::couldPlaceTile);
            TileDecks finalTileDeck = newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL);
            Tile nextTileToPlace = finalTileDeck.topTile(Tile.Kind.NORMAL);
            if (nextTileToPlace == null) {
                MessageBoard finalMessageBoard = withFinalPointsCounted(newMessageBoard, tile);
                return new GameState(players, finalTileDeck, null, otherBoard, Action.END_GAME, finalMessageBoard);
            } else {
                List<PlayerColor> newPlayers = new ArrayList<>(players);
                newPlayers.add(newPlayers.remove(0));
                return new GameState(newPlayers, finalTileDeck, nextTileToPlace, otherBoard, Action.PLACE_TILE, newMessageBoard);
            }
        } else {
            TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, otherBoard::couldPlaceTile);
            TileDecks finalTileDeck = newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL);
            Tile nextTileToPlace = newTileDecks.topTile(Tile.Kind.NORMAL);
            if (nextTileToPlace == null) {
                MessageBoard finalMessageBoard = withFinalPointsCounted(newMessageBoard, tile);
                return new GameState(players, finalTileDeck, null, otherBoard, Action.END_GAME, finalMessageBoard);
            } else {
                List<PlayerColor> newPlayers = new ArrayList<>(players);
                newPlayers.add(newPlayers.remove(0));
                return new GameState(newPlayers, finalTileDeck, nextTileToPlace, otherBoard, Action.PLACE_TILE, newMessageBoard);
            }
        }
    }

    /**
     * Returns a new GameState with a tile placed.
     * @param tile the tile to be placed.
     * @return a new GameState with the tile placed.
     * @throws IllegalArgumentException if the next action is not PLACE_TILE or if the tile has an occupant.
     */
    public GameState withPlacedTile(PlacedTile tile) {
        Preconditions.checkArgument(nextAction == Action.PLACE_TILE || tile.occupant() != null);
        Board newBoard = board.withNewTile(tile);
        MessageBoard newMessageBoard = new MessageBoard(messageBoard().textMaker(), messageBoard.messages());
        if (tile.specialPowerZone() != null && tile.specialPowerZone().specialPower() == Zone.SpecialPower.SHAMAN && removePawnIsPossible()) {
            return new GameState(players, tileDecks, null, newBoard, Action.RETAKE_PAWN, newMessageBoard);
        }
        if (tile.specialPowerZone() != null && tile.specialPowerZone().specialPower() == Zone.SpecialPower.HUNTING_TRAP ) {
            newMessageBoard.withScoredHuntingTrap(currentPlayer(), board.meadowArea((Zone.Meadow) tile.specialPowerZone()));
            board.withMoreCancelledAnimals(Area.animals(board.adjacentMeadow(tile.pos(), (Zone.Meadow) tile.specialPowerZone()), board.cancelledAnimals()));
        }
        if (occupationIsPossible(newBoard)) {
            return new GameState(players, tileDecks, null, newBoard, Action.OCCUPY_TILE, newMessageBoard);
        }
        return withTurnFinished(tile, board);
    }

    /**
     * Returns a new GameState with an occupant removed.
     * @param occupant the occupant to be removed.
     * @return a new GameState with the occupant removed.
     * @throws IllegalArgumentException if the next action is not RETAKE_PAWN or if the occupant is not a pawn.
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.RETAKE_PAWN || occupant == null || occupant.kind() == Occupant.Kind.PAWN);
        if (occupant != null && removePawnIsPossible()) {
            Board newBoard = board.withOccupant(occupant);
        }
        if (!occupationIsPossible(board)) {
            return withTurnFinished(board.lastPlacedTile(), board);
        }
        return new GameState(players, tileDecks, null, board, Action.OCCUPY_TILE, messageBoard);
    }

    /**
     * Returns a new GameState with a new occupant.
     * @param occupant the new occupant.
     * @return a new GameState with the new occupant.
     * @throws IllegalArgumentException if the next action is not OCCUPY_TILE.
     */
    public GameState withNewOccupant(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.OCCUPY_TILE);
        if (occupant != null && occupationIsPossible(board)) {
            Board newBoard = board.withOccupant(occupant);
            return withTurnFinished(newBoard.lastPlacedTile(), newBoard);
        }
        return withTurnFinished(board.lastPlacedTile(), board);
    }

    /**
     * Returns a new MessageBoard with the final points counted.
     * @param newMessageBoard the MessageBoard to count the final points on.
     * @param tile the tile that was placed during the last turn.
     * @return a new MessageBoard with the final points counted.
     */
    private MessageBoard withFinalPointsCounted(MessageBoard newMessageBoard, PlacedTile tile) {
        MessageBoard finalMessageBoard = newMessageBoard;
        Set<Animal> deletedAnimals = board.cancelledAnimals();
        for (Area<Zone.Meadow> meadowArea : board.meadowAreas()) {
            Zone zoneWithWildFire = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE);
            Zone zoneWithPitTrap = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP);
            HashMap<Animal.Kind, Integer> animalCount = animalCount(meadowArea);
            if (zoneWithWildFire != null) {
                finalMessageBoard.withScoredMeadow(meadowArea, board.cancelledAnimals());
            }
            if (zoneWithPitTrap != null) {
                for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
                    if (!Area.animals(board.adjacentMeadow(tile.pos(), (Zone.Meadow) zoneWithPitTrap), board.cancelledAnimals()).contains(animal) && animal.kind() == Animal.Kind.DEER && animalCount.get(Animal.Kind.TIGER) > 0) {
                        deletedAnimals.add(animal);
                        animalCount.put(Animal.Kind.DEER, animalCount.get(Animal.Kind.DEER) - 1);
                        animalCount.put(Animal.Kind.TIGER, animalCount.get(Animal.Kind.TIGER) - 1);
                    }
                }
                if (animalCount.get(Animal.Kind.TIGER) > 0) {
                    for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
                        if (animal.kind() == Animal.Kind.DEER && animalCount.get(Animal.Kind.TIGER) > 0 && !deletedAnimals.contains(animal)) {
                            deletedAnimals.add(animal);
                            animalCount.put(Animal.Kind.DEER, animalCount.get(Animal.Kind.DEER) - 1);
                            animalCount.put(Animal.Kind.TIGER, animalCount.get(Animal.Kind.TIGER) - 1);

                        }
                    }
                }
                finalMessageBoard.withScoredMeadow(meadowArea, board.cancelledAnimals());
                finalMessageBoard.withScoredPitTrap(meadowArea, board.cancelledAnimals());
            } else {
                for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
                    if (animal.kind() == Animal.Kind.DEER && animalCount.get(Animal.Kind.TIGER) > 0) {
                        deletedAnimals.add(animal);
                        animalCount.put(Animal.Kind.DEER, animalCount.get(Animal.Kind.DEER) - 1);
                        animalCount.put(Animal.Kind.TIGER, animalCount.get(Animal.Kind.TIGER) - 1);
                    }
                }
                finalMessageBoard.withScoredMeadow(meadowArea, board.cancelledAnimals());
            }
        }
        for (Area<Zone.Water> waterArea : board.riverSystemAreas()) {
            if (waterArea.zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null) {
                finalMessageBoard.withScoredRaft(waterArea);
            } else {
                finalMessageBoard.withScoredRiverSystem(waterArea);
            }
        }
        return finalMessageBoard;
    }

    /**
     * Returns a count of the animals in a meadow area.
     * @param meadowArea the meadow area to count the animals in.
     * @return a HashMap with the count of each kind of animal in the meadow area.
     */
    private HashMap<Animal.Kind, Integer> animalCount(Area<Zone.Meadow> meadowArea) {
        HashMap<Animal.Kind, Integer> animalCount = new HashMap<>();
        for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
            if (animalCount.containsKey(animal.kind())) {
                animalCount.put(animal.kind(), animalCount.get(animal.kind()) + 1);
            } else {
                animalCount.put(animal.kind(), 1);
            }
        }
        return animalCount;
    }
}
