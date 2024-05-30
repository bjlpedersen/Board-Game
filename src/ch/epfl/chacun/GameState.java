package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the state of a game.
 * This class encapsulates all the information about the current state of the game,
 * including the players, the board, the tiles, and the next action to be performed.
 *
 * @author Bjork Pedersen (376143)
 */
public record GameState(List<PlayerColor> players,
                        TileDecks tileDecks,
                        Tile tileToPlace, Board board,
                        Action nextAction,
                        MessageBoard messageBoard) {
    /**
     * Enum representing the possible actions in the game.
     *
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
     *
     * @param players      List of players in the game.
     * @param tileDecks    The decks of tiles in the game.
     * @param tileToPlace  The tile to be placed next.
     * @param board        The current state of the game board.
     * @param nextAction   The next action to be performed in the game.
     * @param messageBoard The message board for the game.
     * @throws IllegalArgumentException if the number of players is less than 2,
     *                                  or if the tile to place and next action are not consistent,
     *                                  or if any of the board, message board, tile decks, or next action are null.
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
     *
     * @param players   List of players in the game.
     * @param tileDecks The decks of tiles in the game.
     * @param textMaker The text maker for the game.
     * @return A new GameState representing the initial state of the game.
     */
    public static GameState initial(List<PlayerColor> players, TileDecks tileDecks, TextMaker textMaker) {
        return new GameState(
                players,
                tileDecks,
                null,
                Board.EMPTY,
                Action.START_GAME,
                new MessageBoard(textMaker, List.of()));
    }

    /**
     * Gets the current player.
     *
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
     *
     * @param player The player.
     * @param kind   The kind of occupant.
     * @return The number of free occupants of the specified kind for the specified player.
     */
    public int freeOccupantsCount(PlayerColor player, Occupant.Kind kind) {
        return Occupant.occupantsCount(kind) - board.occupantCount(player, kind);
    }

    /**
     * Gets the potential occupants for the last placed tile.
     *
     * @return A set of potential occupants for the last placed tile.
     * @throws IllegalArgumentException if the last placed tile is null.
     */
    public Set<Occupant> lastTilePotentialOccupants() {
        PlacedTile lastPlaced = board.lastPlacedTile();
        Set<Occupant> potentialOccupants = new HashSet<>();
        for (Occupant occ : lastPlaced.potentialOccupants()) {
            if (occ.kind() == Occupant.Kind.HUT && freeOccupantsCount(lastPlaced.placer(), Occupant.Kind.HUT) > 0) {
                switch (lastPlaced.zoneWithId(occ.zoneId())) {
                    case Zone.Lake lake:
                        if (!board.riverSystemArea(lake).isOccupied()) potentialOccupants.add(occ);
                        break;
                    case Zone.River river:
                        if (!board.riverSystemArea(river).isOccupied() && !river.hasLake()) potentialOccupants.add(occ);
                        break;
                    default:
                        break;
                }
            } else {
                if (freeOccupantsCount(lastPlaced.placer(), Occupant.Kind.PAWN) > 0) {
                    switch (lastPlaced.zoneWithId(occ.zoneId())) {
                        case Zone.Meadow meadow:
                            if (!board.meadowArea(meadow).isOccupied()) potentialOccupants.add(occ);
                            break;
                        case Zone.Forest forest:
                            if (!board.forestArea(forest).isOccupied()) potentialOccupants.add(occ);
                            break;
                        case Zone.River river:
                            if (!board.riverArea(river).isOccupied()) potentialOccupants.add(occ);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return potentialOccupants;
    }

    /**
     * Returns a new GameState with the starting tile placed.
     *
     * @return A new GameState with the starting tile placed.
     * @throws IllegalArgumentException if the next action is not START_GAME.
     */
    public GameState withStartingTilePlaced() {
        Preconditions.checkArgument(nextAction == Action.START_GAME);
        TileDecks newTileDecks = new TileDecks(new ArrayList<>(), tileDecks.normalTiles(), tileDecks.menhirTiles());

        PlacedTile startTilePlaced = new PlacedTile(
                tileDecks.startTiles().get(0),
                null,
                Rotation.NONE,
                Pos.ORIGIN);

        Board newBoard = board.withNewTile(startTilePlaced);

        newTileDecks = newTileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, newBoard::couldPlaceTile);
        Tile newTileToPlace = newTileDecks.topTile(Tile.Kind.NORMAL);
        newTileDecks = newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL);
        return new GameState(
                players,
                newTileDecks,
                newTileToPlace,
                newBoard,
                Action.PLACE_TILE,
                messageBoard);
    }

    /**
     * Checks if the occupation of a tile is possible on the given board.
     *
     * @param someBoard the board being worked on.
     * @return true if occupation is possible, false otherwise.
     */
    private boolean occupationIsPossible(Board someBoard) {
        boolean canOccupy = false;

        // Iterate over all zones in the last placed tile
        for (Zone zone : someBoard.lastPlacedTile().tile().zones()) {

            // Check if the zone is a lake or a river with a lake and if it's not occupied
            if (((zone instanceof Zone.River river && river.hasLake()) || zone instanceof Zone.Lake) &&
                    !someBoard.riverSystemArea((Zone.Water) zone).isOccupied()) {

                // If the current player has free huts, set the flag to true
                if (freeOccupantsCount(currentPlayer(), Occupant.Kind.HUT) > 0) {
                    canOccupy = true;
                }
            } else {
                // If the current player has no free pawns, return false
                if (freeOccupantsCount(currentPlayer(), Occupant.Kind.PAWN) == 0) {
                    return false;
                }

                // Check if the zone is a river and if it's not occupied, set the flag to true
                if (zone instanceof Zone.River river && !someBoard.riverSystemArea(river).isOccupied()) {
                    canOccupy = true;
                } else if (zone instanceof Zone.Meadow && !someBoard.meadowArea((Zone.Meadow) zone).isOccupied()) {
                    canOccupy = true;
                } else if (zone instanceof Zone.Forest && !someBoard.forestArea((Zone.Forest) zone).isOccupied()) {
                    canOccupy = true;
                }
            }
        }
        return canOccupy;
    }

    /**
     * Returns the forest area that was closed with a menhir.
     *
     * @return the forest area that was closed with a menhir, or null if no such area exists.
     */
    private Area<Zone.Forest> forestClosedWithMenhir(Board otherBoard) {
        PlacedTile lastPlaced = otherBoard.lastPlacedTile();
        if (lastPlaced == null) {
            return null;
        } else {
            for (Area<Zone.Forest> forestArea : otherBoard.forestsClosedByLastTile()) {
                if (Area.hasMenhir(forestArea)) {
                    return forestArea;
                }
            }
        }
        return null;
    }

    /**
     * Checks if it is possible to remove a pawn.
     *
     * @return true if it is possible to remove a pawn, false otherwise.
     */
    private static boolean removePawnIsPossible(Board board, List<PlayerColor> players) {
        return board.occupantCount(players.getFirst(), Occupant.Kind.PAWN) > 0;
    }

    /**
     * Returns a new GameState with the turn of the current player finished and the next move to make.
     *
     * @param tile the tile that was placed during the turn.
     * @return a new GameState with the turn finished.
     */
    private GameState withTurnFinished(PlacedTile tile, Board otherBoard, MessageBoard newMessageBoard) {
        newMessageBoard = updateMessageBoardForClosedAreasInTurnFinished(otherBoard, newMessageBoard);
        newMessageBoard = lastTileClosedRiverSystemWithLogBoat(tile, newMessageBoard, otherBoard);

        if (shouldPlaceMenhirTile(tile, otherBoard)) {
            return endTurnWithNextTileToPlaceMenhir(newMessageBoard, otherBoard);
        }

        TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, otherBoard::couldPlaceTile);
        Pair<Board, MessageBoard> pair = returnPawnsWhenAreaClosed(otherBoard, newMessageBoard);
        otherBoard = pair.getFirst();
        newMessageBoard = pair.getSecond();

        if (tileDecks.normalTiles().isEmpty() || newTileDecks.topTile(Tile.Kind.NORMAL) == null) {
            Pair<Board, MessageBoard> finalBoardAndMessageBoard = withFinalPointsCounted(
                    newMessageBoard,
                    tile,
                    otherBoard);
            MessageBoard finalMessageBoard = finalBoardAndMessageBoard.getSecond();
            otherBoard = finalBoardAndMessageBoard.getFirst();
            return new GameState(
                    players,
                    newTileDecks,
                    null,
                    otherBoard,
                    Action.END_GAME,
                    finalMessageBoard);
        }

        List<PlayerColor> newPlayers = new ArrayList<>(players);
        newPlayers.add(newPlayers.remove(0));
        return new GameState(
                newPlayers,
                newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL),
                newTileDecks.topTile(Tile.Kind.NORMAL),
                otherBoard,
                Action.PLACE_TILE, newMessageBoard);
    }

    private boolean shouldPlaceMenhirTile(PlacedTile tile, Board otherBoard) {
        return forestClosedWithMenhir(otherBoard) != null &&
                !tileDecks.menhirTiles().isEmpty() &&
                (nextAction == Action.PLACE_TILE ? tile.tile().kind() == Tile.Kind.NORMAL :
                        otherBoard.lastPlacedTile().kind() != Tile.Kind.MENHIR);
    }


    /**
     * Called exclusively in withTurnFinished, made to make that method less heavy, is called
     * if withTurnFinished needs to update its messageBoard when the last tile closed a riverSystem
     * with a log boat.
     * Returns a new MessageBoard updated (because we have to count the log boat with whichever closed river.
     *
     * @param newMessageBoard the messageBoard that withTurnFinished has been working on
     * @param tile            the tile that was placed during the turn.
     * @return a new GameState with the turn finished.
     */
    private MessageBoard lastTileClosedRiverSystemWithLogBoat(PlacedTile tile,
                                                              MessageBoard newMessageBoard,
                                                              Board otherBoard) {
        // Create a set to store water areas with a log boat
        HashSet<Area<Zone.Water>> waterAreasWithLogBoat = new HashSet<>();

        // Iterate over all zones in the tile
        for (Zone zone : tile.tile().zones()) {
            // If the zone is a lake, not already in the set, and has a log boat special power
            if (zone instanceof Zone.Lake &&
                    !waterAreasWithLogBoat.contains(otherBoard.riverSystemArea((Zone.Water) zone)) &&
                    zone.specialPower() == Zone.SpecialPower.LOGBOAT) {
                // Add the river system area of the zone to the set
                waterAreasWithLogBoat.add(otherBoard.riverSystemArea((Zone.Water) zone));
            }
        }
        if (!waterAreasWithLogBoat.isEmpty()) {
            // Iterate over all water areas in the set
            for (Area<Zone.Water> waterArea : waterAreasWithLogBoat) {
                // Update the message board with the score for the log boat
                newMessageBoard = newMessageBoard.withScoredLogboat(currentPlayer(), waterArea);
            }
        }
        return newMessageBoard;
    }

    /**
     * Called exclusively in withTurnFinished, made to make that method less heavy, is called
     * if withTurnFinished needs to return a new GameState where the next action is PlaceTile and that tile
     * is in the Menhir pile
     * Returns a new GameState with the turn of the current player finished and the next move to make.
     *
     * @param otherBoard      the board that withTurnFinished has been working on.
     * @param newMessageBoard the messageBoard that withTurnFinished has been working on
     * @return a new GameState with the turn finished.
     */
    private GameState endTurnWithNextTileToPlaceMenhir(MessageBoard newMessageBoard, Board otherBoard) {
        newMessageBoard = newMessageBoard.withClosedForestWithMenhir(currentPlayer(), forestClosedWithMenhir(otherBoard));
        TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(Tile.Kind.MENHIR, otherBoard::couldPlaceTile);
        Tile nextTileToPlace = newTileDecks.topTile(Tile.Kind.MENHIR);
        TileDecks finalTileDeck = newTileDecks.withTopTileDrawn(Tile.Kind.MENHIR);
        Pair<Board, MessageBoard> pair = returnPawnsWhenAreaClosed(otherBoard, newMessageBoard);
        otherBoard = pair.getFirst();
        newMessageBoard = pair.getSecond();
        return new GameState(
                players,
                finalTileDeck,
                nextTileToPlace,
                otherBoard,
                Action.PLACE_TILE,
                newMessageBoard);
    }

    /**
     * updates the given messageBoard to include the needed new messages.
     *
     * @param otherBoard        the board being worked on in withTurnFinished
     * @param otherMessageBoard the messageBoard being worked on in withTurnFinished
     * @return the new messageBoard with all the new messages
     */
    private static MessageBoard updateMessageBoardForClosedAreasInTurnFinished(
            Board otherBoard,
            MessageBoard otherMessageBoard) {

        // Iterate over all zones in the last placed tile
        for (Zone zone : otherBoard.lastPlacedTile().tile().zones()) {
            if (zone instanceof Zone.Lake && otherBoard.riverSystemArea((Zone.Water) zone).isClosed()) {

                // Update the message board with the score for the river system area
                otherMessageBoard = otherMessageBoard.
                        withScoredRiverSystem(otherBoard.riverSystemArea((Zone.Water) zone));

                // If the river system area has a raft special power,
                // update the message board with the score for the raft
                if (otherBoard.
                        riverSystemArea((Zone.Water) zone).
                        zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null) {
                    otherMessageBoard = otherMessageBoard.
                            withScoredRaft(otherBoard.riverSystemArea((Zone.Water) zone));
                }
            }
        }
        return otherMessageBoard;
    }

    /**
     * Returns a new GameState with a tile placed.
     *
     * @param tile the tile to be placed.
     * @return a new GameState with the tile placed.
     * @throws IllegalArgumentException if the next action is not PLACE_TILE or if the tile has an occupant.
     */
    public GameState withPlacedTile(PlacedTile tile) {
        Preconditions.checkArgument(nextAction == Action.PLACE_TILE || tile.occupant() != null);
        Board newBoard = board.withNewTile(tile);
        MessageBoard newMessageBoard = new MessageBoard(messageBoard.textMaker(), messageBoard.messages());
        if (tile.specialPowerZone() != null) {
            Zone.SpecialPower specialPower = tile.specialPowerZone().specialPower();
            switch (specialPower) {
                case SHAMAN:
                    if (removePawnIsPossible(board, players)) {
                        return new GameState(
                                players,
                                tileDecks,
                                null,
                                newBoard,
                                Action.RETAKE_PAWN,
                                newMessageBoard);
                    }
                    break;
                case HUNTING_TRAP:
                    Zone.Meadow specialPowerZone = (Zone.Meadow) tile.specialPowerZone();
                    newMessageBoard = newMessageBoard.withScoredHuntingTrap(
                            currentPlayer(),
                            newBoard.adjacentMeadow(tile.pos(), specialPowerZone),
                            board.cancelledAnimals());

                    newBoard = newBoard.withMoreCancelledAnimals(Area.animals(
                            newBoard.adjacentMeadow(tile.pos(), specialPowerZone),
                            board.cancelledAnimals()));
                    break;
            }
        }
        if (occupationIsPossible(newBoard)) {
            return new GameState(
                    players,
                    tileDecks,
                    null,
                    newBoard,
                    Action.OCCUPY_TILE,
                    newMessageBoard);
        }
        return withTurnFinished(tile, newBoard, newMessageBoard);
    }

    /**
     * Whenever a tile is placed it can close certain areas which means that the pawns in these areas must be retrieved
     * and the messageBoard must be updated (the given messages must be added this ust be done before the pawns are
     * retrieved because if not the point count can differ.
     *
     * @param otherBoard        the board that is being worked on.
     * @param otherMessageBoard the messageBoard is being worked on
     * @return a pair containing the updated Board (without pawns) and the updated messageBoard
     */
    private static Pair<Board, MessageBoard> returnPawnsWhenAreaClosed(Board otherBoard,
                                                                       MessageBoard otherMessageBoard) {
        // Iterate over all zones in the last placed tile
        for (Zone zone : otherBoard.lastPlacedTile().tile().zones()) {
            // If the zone is a lake and the corresponding river system area is closed
            if (zone instanceof Zone.Lake && otherBoard.riverSystemArea((Zone.Water) zone).isClosed()) {
                // Update the message board with the score for the river system area
                otherMessageBoard = otherMessageBoard.
                        withScoredRiverSystem(otherBoard.riverSystemArea((Zone.Water) zone));

                // If the river system area has a raft special power,
                // update the message board with the score for the raft
                if (otherBoard
                        .riverSystemArea((Zone.Water) zone)
                        .zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null) {
                    otherMessageBoard = otherMessageBoard
                            .withScoredRaft(otherBoard.riverSystemArea((Zone.Water) zone));
                }
            }
            // If the zone is a river and the corresponding river area is closed
            if (zone instanceof Zone.River && otherBoard.riverArea((Zone.River) zone).isClosed()) {
                // Update the message board with the score for the river area
                otherMessageBoard = otherMessageBoard.withScoredRiver(otherBoard.riverArea((Zone.River) zone));
                // Remove the gatherers or fishers in the river area from the board
                otherBoard = otherBoard.
                        withoutGatherersOrFishersIn(Set.of(), Set.of(otherBoard.riverArea((Zone.River) zone)));
            }
            // If the zone is a forest and the corresponding forest area is closed
            if (zone instanceof Zone.Forest && otherBoard.forestArea((Zone.Forest) zone).isClosed()) {
                // Update the message board with the score for the forest area
                otherMessageBoard = otherMessageBoard.withScoredForest(otherBoard.forestArea((Zone.Forest) zone));
                // Remove the gatherers or fishers in the forest area from the board
                otherBoard = otherBoard.
                        withoutGatherersOrFishersIn(Set.of(otherBoard.forestArea((Zone.Forest) zone)), Set.of());
            }
        }
        return new Pair<>(otherBoard, otherMessageBoard);
    }

    /**
     * Returns a new GameState with an occupant removed.
     *
     * @param occupant the occupant to be removed.
     * @return a new GameState with the occupant removed.
     * @throws IllegalArgumentException if the next action is not RETAKE_PAWN or if the occupant is not a pawn.
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        Preconditions.checkArgument(
                nextAction == Action.RETAKE_PAWN ||
                        occupant == null ||
                        occupant.kind() == Occupant.Kind.PAWN);

        if (!occupationIsPossible(board)) {
            return withTurnFinished(board.lastPlacedTile(), board, messageBoard);
        }
        Board newBoard = board;
        if (occupant != null && removePawnIsPossible(board, players)) {
            newBoard = board.withoutOccupant(occupant);
        }
        return new GameState(players, tileDecks, null, newBoard, Action.OCCUPY_TILE, messageBoard);
    }

    /**
     * Returns a new GameState with a new occupant.
     *
     * @param occupant the new occupant.
     * @return a new GameState with the new occupant.
     * @throws IllegalArgumentException if the next action is not OCCUPY_TILE.
     */
    public GameState withNewOccupant(Occupant occupant) {
        Preconditions.checkArgument(nextAction == Action.OCCUPY_TILE);
        MessageBoard newMessageBoard = messageBoard;
        Board newBoard = board;
        if (occupant != null && occupationIsPossible(board)) {
            newBoard = board.withOccupant(occupant);
            Pair<Board, MessageBoard> pair = returnPawnsWhenAreaClosed(
                    newBoard,
                    newMessageBoard);

            newBoard = pair.getFirst();
            newMessageBoard = pair.getSecond();
        }
        return withTurnFinished(newBoard.lastPlacedTile(), newBoard, newMessageBoard);
    }

    /**
     * Returns a new MessageBoard with the final points counted.
     *
     * @param newMessageBoard the MessageBoard to count the final points on.
     * @param tile            the tile that was placed during the last turn.
     * @return a new MessageBoard with the final points counted.
     */
    private Pair<Board, MessageBoard> withFinalPointsCounted(
            MessageBoard newMessageBoard,
            PlacedTile tile,
            Board board) {
        Pair<Board, MessageBoard> finalBoardAndMessageBoard = new Pair<>(board, newMessageBoard);
        Set<Animal> deletedAnimals = new HashSet<>(board.cancelledAnimals());

        finalBoardAndMessageBoard = handleMeadowAreas(
                finalBoardAndMessageBoard.getSecond(),
                tile,
                deletedAnimals
        );
        finalBoardAndMessageBoard = handleRiverSystemAreas(
                finalBoardAndMessageBoard.getSecond(),
                finalBoardAndMessageBoard.getFirst()
        );
        finalBoardAndMessageBoard = handleWinners(
                finalBoardAndMessageBoard.getSecond(),
                finalBoardAndMessageBoard.getFirst()
        );

        return finalBoardAndMessageBoard;
    }

    /**
     * Handles the scoring for meadow areas in the game.
     * This method iterates over all meadow areas in the board, checks for
     * special powers and updates the message board accordingly.
     *
     * @param messageBoard   The current message board.
     * @param tile           The tile that was placed during the last turn.
     * @param deletedAnimals The set of animals that have been deleted.
     * @return The updated message board.
     */
    private Pair<Board, MessageBoard> handleMeadowAreas(MessageBoard messageBoard,
                                                        PlacedTile tile,
                                                        Set<Animal> deletedAnimals) {
        Board newBoard = board;
        for (Area<Zone.Meadow> meadowArea : board.meadowAreas()) {
            Zone zoneWithWildFire = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE);
            Zone zoneWithPitTrap = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP);
            HashMap<Animal.Kind, Integer> animalCount = animalCount(meadowArea, board);

            if (zoneWithWildFire != null) {
                messageBoard = messageBoard.withScoredMeadow(meadowArea, board.cancelledAnimals());
            } else if (zoneWithPitTrap != null) {
                deletedAnimals = handlePitTrap(meadowArea, tile, zoneWithPitTrap, deletedAnimals, animalCount, board);
                newBoard = board.withMoreCancelledAnimals(deletedAnimals);
                messageBoard = messageBoard.withScoredPitTrap(meadowArea, deletedAnimals);
            } else {
                deletedAnimals = handleDeerAndTigers(meadowArea, deletedAnimals, animalCount, board);
                newBoard = board().withMoreCancelledAnimals(deletedAnimals);
                messageBoard = messageBoard.withScoredMeadow(meadowArea, deletedAnimals);
            }
        }
        return new Pair<>(newBoard, messageBoard);
    }

    /**
     * Handles the pit trap special power in the game.
     * This method iterates over all animals in the meadow area, checks for deer and tiger conditions
     * and updates the deleted animals set accordingly.
     *
     * @param meadowArea      The meadow area to check.
     * @param tile            The tile that was placed during the last turn.
     * @param zoneWithPitTrap The zone with the pit trap special power.
     * @param deletedAnimals  The set of animals that have been deleted.
     * @param animalCount     The count of each type of animal in the meadow area.
     * @return The updated set of deleted animals.
     */
    private static Set<Animal> handlePitTrap(Area<Zone.Meadow> meadowArea,
                                             PlacedTile tile,
                                             Zone zoneWithPitTrap,
                                             Set<Animal> deletedAnimals,
                                             HashMap<Animal.Kind,
                                                     Integer> animalCount,
                                             Board board) {
        for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
            if (!Area.animals(board.adjacentMeadow(tile.pos(), (Zone.Meadow) zoneWithPitTrap), deletedAnimals).
                    contains(animal) &&
                    animal.kind() == Animal.Kind.DEER &&
                    animalCount.get(Animal.Kind.TIGER) > 0) {

                deletedAnimals.add(animal);
                animalCount.put(Animal.Kind.DEER, animalCount.get(Animal.Kind.DEER) - 1);
                animalCount.put(Animal.Kind.TIGER, animalCount.get(Animal.Kind.TIGER) - 1);
            }
        }
        return deletedAnimals;
    }

    /**
     * Handles the deer and tigers in the game.
     * This method iterates over all animals in the meadow area, checks for deer
     * and tiger conditions and updates the deleted animals set accordingly.
     *
     * @param meadowArea     The meadow area to check.
     * @param deletedAnimals The set of animals that have been deleted.
     * @param animalCount    The count of each type of animal in the meadow area.
     * @return The updated set of deleted animals.
     */
    private static Set<Animal> handleDeerAndTigers(Area<Zone.Meadow> meadowArea,
                                                   Set<Animal> deletedAnimals,
                                                   HashMap<Animal.Kind,
                                                           Integer> animalCount,
                                                   Board board) {
        for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
            if (animal.kind() == Animal.Kind.DEER && animalCount.get(Animal.Kind.TIGER) > 0) {
                deletedAnimals.add(animal);
                animalCount.put(Animal.Kind.DEER, animalCount.get(Animal.Kind.DEER) - 1);
                animalCount.put(Animal.Kind.TIGER, animalCount.get(Animal.Kind.TIGER) - 1);
            }
        }
        return deletedAnimals;
    }

    /**
     * Handles the scoring for river system areas in the game.
     * This method iterates over all river system areas in the board,
     * checks for special powers and updates the message board accordingly.
     *
     * @param messageBoard The current message board.
     * @return The updated message board.
     */
    private Pair<Board, MessageBoard> handleRiverSystemAreas(MessageBoard messageBoard, Board board1) {
        for (Area<Zone.Water> waterArea : board1.riverSystemAreas()) {
            if (waterArea.zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null) {
                messageBoard = messageBoard.withScoredRaft(waterArea);
            }
            messageBoard = messageBoard.withScoredRiverSystem(waterArea);
        }
        return new Pair<>(board1, messageBoard);
    }

    /**
     * Handles the scoring for winners in the game.
     * This method filters the players with the highest points and updates the message board accordingly.
     *
     * @param messageBoard The current message board.
     * @return The updated message board.
     */
    private Pair<Board, MessageBoard> handleWinners(MessageBoard messageBoard, Board board1) {
        Map<PlayerColor, Integer> filteredMap = filterHighestValue(messageBoard.points());
        messageBoard = messageBoard.withWinners(
                filteredMap.keySet(),
                filteredMap.values().stream().mapToInt(Integer::intValue).max().getAsInt());
        return new Pair<>(board1, messageBoard);
    }

    /**
     * Filters the original map to only include entries with the maximum value.
     * This method first finds the maximum value in the original map.
     * If the original map is empty, it returns an empty map.
     * Otherwise, it creates a new map and populates it with entries from the original map that have the maximum value.
     *
     * @param originalMap The original map to filter. It maps PlayerColor to Integer.
     * @return A new map that includes only the entries from the original map that have the maximum value.
     */
    private static Map<PlayerColor, Integer> filterHighestValue(Map<PlayerColor, Integer> originalMap) {
        // Find the maximum value in the original map
        OptionalInt maxOpt = originalMap.values().stream().mapToInt(Integer::intValue).max();
        if (maxOpt.isEmpty()) {
            return Collections.emptyMap(); // Return an empty map if the original map is empty
        }
        int max = maxOpt.getAsInt();

        // Filter the original map to only include entries with the maximum value
        Map<PlayerColor, Integer> filteredMap = new HashMap<>();
        for (Map.Entry<PlayerColor, Integer> entry : originalMap.entrySet()) {
            if (entry.getValue() == max) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return filteredMap;
    }

    /**
     * Returns a count of the animals in a meadow area with 0 if there is none of the given animal.
     *
     * @param meadowArea the meadow area to count the animals in.
     * @return a HashMap with the count of each kind of animal in the meadow area.
     */
    private static HashMap<Animal.Kind, Integer> animalCount(Area<Zone.Meadow> meadowArea, Board board) {
        HashMap<Animal.Kind, Integer> animalCount = new HashMap<>();
        animalCount.put(Animal.Kind.DEER, 0);
        animalCount.put(Animal.Kind.TIGER, 0);
        animalCount.put(Animal.Kind.AUROCHS, 0);
        animalCount.put(Animal.Kind.MAMMOTH, 0);
        for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
            animalCount.put(animal.kind(), animalCount.get(animal.kind()) + 1);
        }
        return animalCount;
    }

    /**
     * Represents a pair of objects.
     *
     * @author Bjork Pedersen (376143)
     */
    private static class Pair<A, B> {
        private final A first;
        private final B second;

        /**
         * Constructs a new Pair with the given objects.
         *
         * @param first  the first object in the Pair
         * @param second the second object in the Pair
         */
        protected Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        /**
         * Returns the first object in the Pair.
         *
         * @return the first object in the Pair
         */
        public A getFirst() {
            return first;
        }

        /**
         * Returns the second object in the Pair.
         *
         * @return the second object in the Pair
         */
        public B getSecond() {
            return second;
        }
    }
}
