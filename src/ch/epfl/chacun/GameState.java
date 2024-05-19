package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the state of a game.
 * This class encapsulates all the information about the current state of the game,
 * including the players, the board, the tiles, and the next action to be performed.
 * @author Bjork Pedersen (376143)
 */
public record GameState(List<PlayerColor> players,
                        TileDecks tileDecks,
                        Tile tileToPlace, Board board,
                        Action nextAction,
                        MessageBoard messageBoard) {
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
     * @throws IllegalArgumentException if the number of players is less than 2,
     * or if the tile to place and next action are not consistent,
     * or if any of the board, message board, tile decks, or next action are null.
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
        if (board.lastPlacedTile().placer() != null) {
            for (Zone zone : board.lastPlacedTile().tile().zones()) {
                if ( (zone instanceof Zone.Lake &&
                        board.occupantCount(board.lastPlacedTile().placer(), Occupant.Kind.HUT) < 3 &&
                        !board.riverSystemArea((Zone.Water) zone).isOccupied()) ||
                        (zone instanceof Zone.River &&
                        !((Zone.River) zone).hasLake() &&
                        board.occupantCount(board.lastPlacedTile().placer(), Occupant.Kind.HUT) < 3 &&
                        !board.riverSystemArea((Zone.Water) zone).isOccupied()) ) {
                    potentialOccupants.add(new Occupant(Occupant.Kind.HUT, zone.id()));
                }
                if (board.occupantCount(board.lastPlacedTile().placer(), Occupant.Kind.PAWN) < 5 &&
                        zone instanceof Zone.Meadow && !board.meadowArea((Zone.Meadow) zone).isOccupied()) {
                    potentialOccupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
                }
                if (board.occupantCount(board.lastPlacedTile().placer(), Occupant.Kind.PAWN) < 5 &&
                        zone instanceof Zone.Forest && !board.forestArea((Zone.Forest) zone).isOccupied()) {
                    potentialOccupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
                }
                if (board.occupantCount(board.lastPlacedTile().placer(), Occupant.Kind.PAWN) < 5 &&
                        zone instanceof Zone.River &&
                        !board.riverArea((Zone.River) zone).isOccupied()) {
                    potentialOccupants.add(new Occupant(Occupant.Kind.PAWN, zone.id()));
                }
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
        PlacedTile startTilePlaced = new PlacedTile(
                tileDecks.startTiles().get(0),
                null,
                Rotation.NONE,
                Pos.ORIGIN);

        Board newBoard = board.withNewTile(startTilePlaced);
        return new GameState(
                players,
                newTileDecks,
                tileDecks.topTile(Tile.Kind.NORMAL),
                newBoard,
                Action.PLACE_TILE,
                messageBoard);
    }


    /**
     * Checks if the occupation of a tile is possible on the given board.
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
     * Returns a new GameState with the turn of the current player finished and the next move to make.
     * @param tile the tile that was placed during the turn.
     * @return a new GameState with the turn finished.
     */
    private GameState withTurnFinished(PlacedTile tile, Board otherBoard, MessageBoard newMessageBoard) {
        newMessageBoard = updateMessageBoardForClosedAreasInTurnFinished(otherBoard, newMessageBoard);
        if (this.nextAction == Action.PLACE_TILE) {
            newMessageBoard = lastTileClosedRiverSystemWithLogBoat(tile, newMessageBoard);
            if (lastTileClosedForestWithMenhir() &&
                    !tileDecks.menhirTiles().isEmpty() &&
                    tile.tile().kind() == Tile.Kind.NORMAL) {
                return endTurnWithNextTileToPlaceMenhir(newMessageBoard, otherBoard);
            }
            TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, otherBoard::couldPlaceTile);
            return endTurnWithNextTileToPlaceNormal(otherBoard, newTileDecks, newMessageBoard, tile);
        } else {
            newMessageBoard = lastTileClosedRiverSystemWithLogBoat(tile, newMessageBoard);
            if (lastTileClosedForestWithMenhir() && board.lastPlacedTile().kind() != Tile.Kind.MENHIR) {
                return endTurnWithNextTileToPlaceMenhir(newMessageBoard, otherBoard);
            }
            TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(Tile.Kind.NORMAL, otherBoard::couldPlaceTile);
            Pair<Board, MessageBoard> pair = returnPawnsWhenAreaClosed(otherBoard, newMessageBoard);
            otherBoard = pair.getFirst();
            newMessageBoard = pair.getSecond();
            if (tileDecks.normalTiles().isEmpty()) {
                MessageBoard finalMessageBoard = withFinalPointsCounted(newMessageBoard, tile);
                return new GameState(
                        players,
                        newTileDecks,
                        null,
                        otherBoard,
                        Action.END_GAME,
                        finalMessageBoard);
            }
            TileDecks finalTileDeck = newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL);
            Tile nextTileToPlace = newTileDecks.topTile(Tile.Kind.NORMAL);
            if (nextTileToPlace == null) {
                MessageBoard finalMessageBoard = withFinalPointsCounted(newMessageBoard, tile);
                return new GameState(
                        players,
                        finalTileDeck,
                        null,
                        otherBoard,
                        Action.END_GAME,
                        finalMessageBoard);
            } else {
                List<PlayerColor> newPlayers = new ArrayList<>(players);
                newPlayers.add(newPlayers.remove(0));
                return new GameState(
                        newPlayers,
                        finalTileDeck,
                        nextTileToPlace,
                        otherBoard,
                        Action.PLACE_TILE, newMessageBoard);
            }
        }
    }

    /**
     * Called exclusively in withTurnFinished, made to make that method less heavy, is called
     * if withTurnFinished needs to return a new GameState where the next action is PlaceTile and that tile
     * is in the Normal pile
     * Returns a new GameState with the turn of the current player finished and the next move to make.
     * @param otherBoard the board that withTurnFinished has been working on.
     * @param newTileDecks the tileDecks that withTurnFinished has been working on.
     * @param newMessageBoard the messageBoard that withTurnFinished has been working on
     * @param tile the tile that was placed during the turn.
     * @return a new GameState with the turn finished.
     */
    private GameState endTurnWithNextTileToPlaceNormal(
            Board otherBoard,
            TileDecks newTileDecks,
            MessageBoard newMessageBoard,
            PlacedTile tile) {

        // If there are no more normal tiles, count the final points and end the game
        if (tileDecks.normalTiles().isEmpty()) {
            MessageBoard finalMessageBoard = withFinalPointsCounted(newMessageBoard, tile);
            return new GameState(
                    players,
                    newTileDecks,
                    null,
                    otherBoard,
                    Action.END_GAME,
                    finalMessageBoard);
        }

        // Get the next normal tile to place
        Tile nextTileToPlace = newTileDecks.topTile(Tile.Kind.NORMAL);
        TileDecks finalTileDeck = newTileDecks.withTopTileDrawn(Tile.Kind.NORMAL);

        // Rotate the players, so the next player becomes the current player
        List<PlayerColor> newPlayers = new ArrayList<>(players);
        newPlayers.add(newPlayers.remove(0));

        Pair<Board, MessageBoard> pair = returnPawnsWhenAreaClosed(otherBoard, newMessageBoard);
        otherBoard = pair.getFirst();
        newMessageBoard = pair.getSecond();

        // Return a new GameState with the next action being to place a tile
        return new GameState(
                newPlayers,
                finalTileDeck,
                nextTileToPlace,
                otherBoard,
                Action.PLACE_TILE,
                newMessageBoard);
    }


    /**
     * Called exclusively in withTurnFinished, made to make that method less heavy, is called
     * if withTurnFinished needs to update its messageBoard when the last tile closed a riverSystem
     * with a log boat.
     * Returns a new MessageBoard updated (because we have to count the log boat with whichever closed river.
     * @param newMessageBoard the messageBoard that withTurnFinished has been working on
     * @param tile the tile that was placed during the turn.
     * @return a new GameState with the turn finished.
     */
    private MessageBoard lastTileClosedRiverSystemWithLogBoat(PlacedTile tile, MessageBoard newMessageBoard) {
        // Create a set to store water areas with a log boat
        HashSet<Area<Zone.Water>> waterAreasWithLogBoat = new HashSet<>();

        // Iterate over all zones in the tile
        for (Zone zone : tile.tile().zones()) {
            // If the zone is a lake, not already in the set, and has a log boat special power
            if (zone instanceof Zone.Lake &&
                    !waterAreasWithLogBoat.contains(board.riverSystemArea((Zone.Water) zone)) &&
                    zone.specialPower() == Zone.SpecialPower.LOGBOAT) {
                // Add the river system area of the zone to the set
                waterAreasWithLogBoat.add(board.riverSystemArea((Zone.Water) zone));
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
     * @param otherBoard the board that withTurnFinished has been working on.
     * @param newMessageBoard the messageBoard that withTurnFinished has been working on
     * @return a new GameState with the turn finished.
     */
    private GameState endTurnWithNextTileToPlaceMenhir(MessageBoard newMessageBoard, Board otherBoard) {
        newMessageBoard = newMessageBoard.withClosedForestWithMenhir(currentPlayer(), forestClosedWithMenhir());
        TileDecks newTileDecks = tileDecks.withTopTileDrawnUntil(Tile.Kind.MENHIR, otherBoard::couldPlaceTile);
        Tile nextTileToPlace = newTileDecks.topTile(Tile.Kind.MENHIR);
        TileDecks finalTileDeck = newTileDecks.withTopTileDrawn(Tile.Kind.MENHIR);
        finalTileDeck = finalTileDeck.withTopTileDrawn(Tile.Kind.MENHIR);
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
     * @param otherBoard the board being worked on in withTurnFinished
     * @param otherMessageBoard the messageBoard being worked on in withTurnFinished
     * @return the new messageBoard with all the new messages
     */
    private MessageBoard updateMessageBoardForClosedAreasInTurnFinished(
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
                        zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null){
                    otherMessageBoard = otherMessageBoard.
                            withScoredRaft(otherBoard.riverSystemArea((Zone.Water) zone));
                }
            }
            if (zone instanceof Zone.River && otherBoard.riverArea((Zone.River) zone).isClosed()) {

                // Update the message board with the score for the river area
                otherMessageBoard = otherMessageBoard.withScoredRiver(otherBoard.riverArea((Zone.River) zone));
            }

            // If the zone is a forest and the corresponding forest area is closed
            if (zone instanceof Zone.Forest && otherBoard.forestArea((Zone.Forest) zone).isClosed()) {

                // Update the message board with the score for the forest area
                otherMessageBoard = otherMessageBoard.withScoredForest(otherBoard.forestArea((Zone.Forest) zone));
            }
        }
        return otherMessageBoard;
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
        MessageBoard newMessageBoard = new MessageBoard(messageBoard.textMaker(), messageBoard.messages());
        if (tile.specialPowerZone() != null &&
                tile.specialPowerZone().specialPower() == Zone.SpecialPower.SHAMAN &&
                removePawnIsPossible()) {
            return new GameState(players, tileDecks, null, newBoard, Action.RETAKE_PAWN, newMessageBoard);
        }
        if (tile.specialPowerZone() != null &&
                tile.specialPowerZone().specialPower() == Zone.SpecialPower.HUNTING_TRAP ) {

            Zone.Meadow specialPowerZone = (Zone.Meadow) tile.specialPowerZone();
            newMessageBoard = newMessageBoard.withScoredHuntingTrap(
                    currentPlayer(),
                    newBoard.adjacentMeadow(tile.pos(), specialPowerZone),
                    this.board.cancelledAnimals());

            newBoard = newBoard.withMoreCancelledAnimals(Area.animals(
                    newBoard.adjacentMeadow(tile.pos(), specialPowerZone),
                    board.cancelledAnimals()));

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
     * @param otherBoard the board that is being worked on.
     * @param otherMessageBoard the messageBoard is being worked on
     * @return a pair containing the updated Board (without pawns) and the updated messageBoard
     */
    private Pair<Board, MessageBoard> returnPawnsWhenAreaClosed(Board otherBoard, MessageBoard otherMessageBoard) {
        // Iterate over all zones in the last placed tile
        for (Zone zone : otherBoard.lastPlacedTile().tile().zones()) {
            // If the zone is a lake and the corresponding river system area is closed
            if (zone instanceof Zone.Lake && otherBoard.riverSystemArea((Zone.Water) zone).isClosed()) {
                // Update the message board with the score for the river system area
                otherMessageBoard = otherMessageBoard.
                        withScoredRiverSystem(otherBoard.riverSystemArea((Zone.Water) zone));

                // If the river system area has a raft special power,
                // update the message board with the score for the raft
                if (otherBoard.
                        riverSystemArea((Zone.Water) zone).
                        zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null){
                    otherMessageBoard = otherMessageBoard.
                            withScoredRaft(otherBoard.riverSystemArea((Zone.Water) zone));
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
                otherMessageBoard = otherMessageBoard.
                        withScoredForest(otherBoard.forestArea((Zone.Forest) zone));
                // Remove the gatherers or fishers in the forest area from the board
                otherBoard = otherBoard.
                        withoutGatherersOrFishersIn(Set.of(otherBoard.forestArea((Zone.Forest) zone)), Set.of());
            }
        }
        return new Pair<>(otherBoard, otherMessageBoard);
    }

    /**
     * Returns a new GameState with an occupant removed.
     * @param occupant the occupant to be removed.
     * @return a new GameState with the occupant removed.
     * @throws IllegalArgumentException if the next action is not RETAKE_PAWN or if the occupant is not a pawn.
     */
    public GameState withOccupantRemoved(Occupant occupant) {
        Preconditions.checkArgument(
                nextAction == Action.RETAKE_PAWN ||
                        occupant == null ||
                        occupant.kind() == Occupant.Kind.PAWN);

        if (occupant != null && removePawnIsPossible()) {
            Board newBoard = board.withoutOccupant(occupant);
            return new GameState(players, tileDecks, null, newBoard, Action.OCCUPY_TILE, messageBoard);
        }

        if (!occupationIsPossible(board)) {
            return withTurnFinished(board.lastPlacedTile(), board, messageBoard);
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
            Pair<Board, MessageBoard> pair = returnPawnsWhenAreaClosed(
                    newBoard,
                    new MessageBoard(messageBoard.textMaker(), messageBoard.messages()));

            newBoard = pair.getFirst();
            MessageBoard newMessageBoard = pair.getSecond();
            return withTurnFinished(newBoard.lastPlacedTile(), newBoard, newMessageBoard);
        }
        return withTurnFinished(board.lastPlacedTile(), board, messageBoard);
    }

    /**
     * Returns a new MessageBoard with the final points counted.
     * @param newMessageBoard the MessageBoard to count the final points on.
     * @param tile the tile that was placed during the last turn.
     * @return a new MessageBoard with the final points counted.
     */
    private MessageBoard withFinalPointsCounted(MessageBoard newMessageBoard, PlacedTile tile) {
        // Create a new message board and a set of deleted animals
        MessageBoard finalMessageBoard = newMessageBoard;
        Set<Animal> deletedAnimals = new HashSet<>(board.cancelledAnimals());

        // Iterate over all meadow areas on the board
        for (Area<Zone.Meadow> meadowArea : board.meadowAreas()) {
            // Check for special powers in the meadow area
            Zone zoneWithWildFire = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE);
            Zone zoneWithPitTrap = meadowArea.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP);

            // Count the animals in the meadow area
            HashMap<Animal.Kind, Integer> animalCount = animalCount(meadowArea);

            // If the meadow area has a wildfire, update the message board with the score for the meadow
            if (zoneWithWildFire != null) {
                finalMessageBoard = finalMessageBoard.withScoredMeadow(meadowArea, board.cancelledAnimals());
            }
            // If the meadow area has a pit trap, update the message board with the score for the pit trap
            else if (zoneWithPitTrap != null) {
                // Iterate over all animals in the meadow area
                for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
                    // If the animal is a deer and there are tigers in the meadow area,
                    // add the animal to the set of deleted animals
                    if (!Area.animals(
                            board.adjacentMeadow(tile.pos(), (Zone.Meadow) zoneWithPitTrap),
                            deletedAnimals).contains(animal) &&
                            animal.kind() == Animal.Kind.DEER &&
                            animalCount.get(Animal.Kind.TIGER) > 0) {

                        deletedAnimals.add(animal);
                        animalCount.put(Animal.Kind.DEER, animalCount.get(Animal.Kind.DEER) - 1);
                        animalCount.put(Animal.Kind.TIGER, animalCount.get(Animal.Kind.TIGER) - 1);
                    }
                }
                finalMessageBoard = finalMessageBoard.withScoredPitTrap(meadowArea, deletedAnimals);
            }
            // If the meadow area has no special powers, update the message board with the score for the meadow
            else {
                // Iterate over all animals in the meadow area
                for (Animal animal : Area.animals(meadowArea, board.cancelledAnimals())) {
                    // If the animal is a deer and there are tigers in the meadow area,
                    // add the animal to the set of deleted animals
                    if (animal.kind() == Animal.Kind.DEER && animalCount.get(Animal.Kind.TIGER) > 0) {
                        deletedAnimals.add(animal);
                        animalCount.put(Animal.Kind.DEER, animalCount.get(Animal.Kind.DEER) - 1);
                        animalCount.put(Animal.Kind.TIGER, animalCount.get(Animal.Kind.TIGER) - 1);
                    }
                }
                finalMessageBoard = finalMessageBoard.withScoredMeadow(meadowArea, deletedAnimals);
            }
        }

        // Iterate over all river system areas on the board
        for (Area<Zone.Water> waterArea : board.riverSystemAreas()) {
            // If the river system area has a raft, update the message board with the score for the raft
            if (waterArea.zoneWithSpecialPower(Zone.SpecialPower.RAFT) != null) {
                finalMessageBoard = finalMessageBoard.withScoredRaft(waterArea);
            }
            // Update the message board with the score for the river system area
            finalMessageBoard = finalMessageBoard.withScoredRiverSystem(waterArea);
        }

        // Filter the players with the highest score
        Map<PlayerColor, Integer> filteredMap = filterHighestValue(finalMessageBoard.points());

        // Update the message board with the winners of the game
        finalMessageBoard = finalMessageBoard.withWinners(
                filteredMap.keySet(),
                filteredMap.values().stream().mapToInt(Integer::intValue).max().getAsInt());

        return finalMessageBoard;
    }

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
     * @param meadowArea the meadow area to count the animals in.
     * @return a HashMap with the count of each kind of animal in the meadow area.
     */
    private HashMap<Animal.Kind, Integer> animalCount(Area<Zone.Meadow> meadowArea) {
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
     * @author Bjork Pedersen (376143)
     */
    private static class Pair<A, B> {
        private final A first;
        private final B second;

        /**
         * Constructs a new Pair with the given objects.
         *
         * @param first the first object in the Pair
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
