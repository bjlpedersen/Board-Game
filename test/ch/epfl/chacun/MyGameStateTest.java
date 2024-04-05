//package ch.epfl.chacun;
//
//
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class MyGameStateTest {
//
//    public static final List<Tile> TILES = Tiles.TILES;
//    public static final TextMade TEXT_MADE = new TextMade();
//
//    //Normal
//    public static TileDecks initialTileDecks1 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(17), TILES.get(27)),
//            List.of(TILES.get(79)));
//    public static GameState initialGameState1 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks1,
//            TEXT_MADE);
//
//    //TopTileDrawnUntil
//    public static TileDecks initialTileDecks2 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(0), TILES.get(83), TILES.get(13), TILES.get(17), TILES.get(4)),
//            List.of(TILES.get(79)));
//
//    public static GameState initialGameState2 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks2,
//            TEXT_MADE);
//
//    //RiverClosed
//    public static TileDecks initialTileDecks3 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(19), TILES.get(0), TILES.get(24)),
//            List.of(TILES.get(79)));
//
//    public static GameState initialGameState3 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks3,
//            TEXT_MADE);
//
//    //MenhirToPlace
//    public static TileDecks initialTileDecks4 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(0), TILES.get(1), TILES.get(17)),
//            List.of(TILES.get(79)));
//
//    public static GameState initialGameState4 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks4,
//            TEXT_MADE);
//
//
//    //OccupationImpossible
//    public static TileDecks initialTileDecks5 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(1), TILES.get(61), TILES.get(0)),
//            List.of(TILES.get(79)));
//
//    public static GameState initialGameState5 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks5,
//            TEXT_MADE);
//
//
//    //HuntingTrap
//    public static TileDecks initialTileDecks6 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(49), TILES.get(27), TILES.get(62), TILES.get(61),TILES.get(1),TILES.get(0),TILES.get(8)),
//            List.of(TILES.get(94)));
//
//    public static GameState initialGameState6 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks6,
//            TEXT_MADE);
//
//    //LogBoat
//    public static TileDecks initialTileDecks7 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(49), TILES.get(27),TILES.get(1),TILES.get(0),TILES.get(8)),
//            List.of(TILES.get(93)));
//
//    public static GameState initialGameState7 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks7,
//            TEXT_MADE);
//
//    //Shaman
//    public static TileDecks initialTileDecks8 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(49), TILES.get(27),TILES.get(1),TILES.get(0),TILES.get(8)),
//            List.of(TILES.get(88)));
//
//    public static GameState initialGameState8 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks8,
//            TEXT_MADE);
//
//
//    //Fire and PitTrap
//    public static TileDecks initialTileDecks9 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(49), TILES.get(27),TILES.get(1),TILES.get(0),TILES.get(69), TILES.get(25), TILES.get(34)),
//            List.of(TILES.get(92), TILES.get(85)));
//
//    public static GameState initialGameState9 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks9,
//            TEXT_MADE);
//
//    //PitTrap
//    public static TileDecks initialTileDecks10 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(49), TILES.get(27),TILES.get(1),TILES.get(0), TILES.get(31)),
//            List.of(TILES.get(92)));
//
//    public static GameState initialGameState10 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks10,
//            TEXT_MADE);
//
//
//    //RiverSystemClosed
//    public static TileDecks initialTileDecks11 = new TileDecks(
//            List.of(TILES.get(56)),
//            List.of(TILES.get(49), TILES.get(27), TILES.get(14)),
//            List.of(TILES.get(92)));
//
//    public static GameState initialGameState11 = GameState.initial(
//            PlayerColor.ALL,
//            initialTileDecks11,
//            TEXT_MADE);
//
//
//
//    /////////////////////////////////////// TESTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//
//    @Test
//    void currentPlayerWorks(){
//        var gameState = initialGameState1;
//        assertNull(gameState.currentPlayer());
//
//        var gameState1 = gameState.withStartingTilePlaced();
//        assertEquals(PlayerColor.RED, gameState1.currentPlayer());
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                gameState1.board().lastPlacedTile().pos().neighbor(Direction.W)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().getFirst().id()));
//        assertEquals(PlayerColor.BLUE, gameState3.currentPlayer());
//    }
//
//    @Test
//    void freeOccupantsCountWorks(){
//        var gameState = initialGameState1;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                gameState1.board().lastPlacedTile().pos().neighbor(Direction.W)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.HUT,
//                gameState2.board().lastPlacedTile().tile().w().zones().get(1).id()));
//
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(1,0)));
//
//        //removed last one because there was no point which is not the case
//
//        assertEquals(5, gameState4.freeOccupantsCount(PlayerColor.RED, Occupant.Kind.PAWN));
//        assertEquals(5, gameState4.freeOccupantsCount(PlayerColor.BLUE, Occupant.Kind.PAWN));
//        assertEquals(5, gameState4.freeOccupantsCount(PlayerColor.GREEN, Occupant.Kind.PAWN));
//        assertEquals(5, gameState4.freeOccupantsCount(PlayerColor.YELLOW, Occupant.Kind.PAWN));
//        assertEquals(5, gameState4.freeOccupantsCount(PlayerColor.PURPLE, Occupant.Kind.PAWN));
//
//        assertEquals(2, gameState4.freeOccupantsCount(PlayerColor.RED, Occupant.Kind.HUT));
//        assertEquals(3, gameState4.freeOccupantsCount(PlayerColor.BLUE, Occupant.Kind.HUT));
//        assertEquals(3, gameState4.freeOccupantsCount(PlayerColor.GREEN, Occupant.Kind.HUT));
//        assertEquals(3, gameState4.freeOccupantsCount(PlayerColor.YELLOW, Occupant.Kind.HUT));
//        assertEquals(3, gameState4.freeOccupantsCount(PlayerColor.PURPLE, Occupant.Kind.HUT));
//    }
//
//    @Test
//    void lastTilePotentialOccupantsWorksOnEasyCase(){
//        var gameState = initialGameState1;
//
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                gameState1.board().lastPlacedTile().pos().neighbor(Direction.W)));
//
//        var lastTilePotentialOccupantsExpected = Set.of(
//                new Occupant(Occupant.Kind.PAWN, 17_0),
//                new Occupant(Occupant.Kind.PAWN, 17_1),
//                new Occupant(Occupant.Kind.HUT, 17_1),
//                new Occupant(Occupant.Kind.PAWN, 17_2),
//                new Occupant(Occupant.Kind.PAWN, 17_3),
//                new Occupant(Occupant.Kind.HUT, 17_3),
//                new Occupant(Occupant.Kind.PAWN, 17_4));
//
//
//        assertEquals(lastTilePotentialOccupantsExpected, gameState2.lastTilePotentialOccupants());
//    }
//
//    @Test
//    void lastTilePotentialOccupantsWorksOnDifficultCase(){
//
//        var gameState = initialGameState3;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                gameState1.board().lastPlacedTile().pos().neighbor(Direction.W)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().e().zones().get(1).id()));
//
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                gameState3.board().lastPlacedTile().pos().neighbor(Direction.S)));
//
//
//
//        //Expected
//
//        var lastTilePotentialOccupantsExpected = Set.of(
//                new Occupant(Occupant.Kind.PAWN, 0),
//                new Occupant(Occupant.Kind.PAWN, 2),
//                new Occupant(Occupant.Kind.PAWN, 3),
//                new Occupant(Occupant.Kind.PAWN, 4),
//                new Occupant(Occupant.Kind.HUT, 8));
//
//
//        assertEquals(lastTilePotentialOccupantsExpected, gameState4.lastTilePotentialOccupants());
//
//    }
//
//    @Test
//    void withPlacedTileWorksWithHuntingTrap(){
//
//        var gameState = initialGameState6;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        //49
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //27
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,0)));
//
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().e().zones().get(2).id()));
//
//        //62
//        var gameState6 = gameState5.withPlacedTile(new PlacedTile(
//                gameState5.tileToPlace(),
//                gameState5.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,-1)));
//
//        //area already occupied
//        assertEquals(GameState.Action.PLACE_TILE, gameState6.nextAction());
//        assertThrows(IllegalArgumentException.class, () -> gameState6.withNewOccupant(null));
//
//        //61
//        var gameState7 = gameState6.withPlacedTile(new PlacedTile(
//                gameState6.tileToPlace(),
//                gameState6.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1,-1)));
//
//        //area already occupied
//        assertEquals(GameState.Action.PLACE_TILE, gameState7.nextAction());
//        assertThrows(IllegalArgumentException.class, () -> gameState7.withNewOccupant(null));
//
//
//        //1
//        var gameState8 = gameState7.withPlacedTile(new PlacedTile(
//                gameState7.tileToPlace(),
//                gameState7.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(1,0)));
//
//        var gameState9 = gameState8.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState8.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //0
//        var gameState10 = gameState9.withPlacedTile(new PlacedTile(
//                gameState9.tileToPlace(),
//                gameState9.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(0,1)));
//
//        var gameState11 = gameState10.withNewOccupant(null);
//
//        //94
//        var gameState12 = gameState11.withPlacedTile(new PlacedTile(
//                gameState11.tileToPlace(),
//                gameState11.currentPlayer(),
//                Rotation.NONE,
//                new Pos(0,-1)));
//
//        var gameState13 = gameState12.withNewOccupant(null);
//        //Expected
//
//        Map<Animal.Kind, Integer> animalMapExpected = new HashMap<>();
//        animalMapExpected.put(Animal.Kind.MAMMOTH, 1);
//        animalMapExpected.put(Animal.Kind.AUROCHS, 1);
//        animalMapExpected.put(Animal.Kind.DEER, 1);
//
//        MessageBoard.Message messageExpected = new MessageBoard.Message(
//                TEXT_MADE.playerScoredHuntingTrap(PlayerColor.RED, 6, animalMapExpected),
//                6,
//                Set.of(PlayerColor.RED),
//                Set.of(56, 49, 61, 94));
//
//        //alperr added tile occupation to finish the turn
//
//        assertEquals(messageExpected, gameState13.messageBoard().messages().get(gameState13.messageBoard().messages().size()-1));
//    }
//
//    @Test
//    void withPlacedTileWorksWithLogBoat(){
//
//        var gameState = initialGameState7;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        //49
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //27
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,0)));
//
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().e().zones().get(2).id()));
//
//        //1
//        var gameState6 = gameState5.withPlacedTile(new PlacedTile(
//                gameState5.tileToPlace(),
//                gameState5.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(1,0)));
//
//        var gameState7 = gameState6.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState6.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //0
//        var gameState8 = gameState7.withPlacedTile(new PlacedTile(
//                gameState7.tileToPlace(),
//                gameState7.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(0,1)));
//
//        var gameState9 = gameState8.withNewOccupant(null);
//
//
//        //93
//        var gameState10 = gameState9.withPlacedTile(new PlacedTile(
//                gameState9.tileToPlace(),
//                gameState9.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,1)));
//
//        var gameState11 = gameState10.withNewOccupant(null);
//        System.out.println(gameState10.nextAction());
//        //Expected
//
//        MessageBoard.Message messageExpected = new MessageBoard.Message(
//                TEXT_MADE.playerScoredLogboat(PlayerColor.YELLOW, 4, 2),
//                4,
//                Set.of(PlayerColor.YELLOW),
//                Set.of(56, 49, 27, 93));
//
//        assertEquals(messageExpected, gameState11.messageBoard().messages().get(gameState11.messageBoard().messages().size()-1));
//
//
//    }
//
//    @Test
//    void withPlacedTileWorksWithShaman(){
//
//        var gameState = initialGameState8;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        //49
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //27
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,0)));
//
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().e().zones().get(2).id()));
//
//        //1
//        var gameState6 = gameState5.withPlacedTile(new PlacedTile(
//                gameState5.tileToPlace(),
//                PlayerColor.YELLOW, //alperr replaced with yellow placer it was greeen yellow had no pawn
//                Rotation.RIGHT,
//                new Pos(1,0)));
//
//
//        var gameState7 = gameState6.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState6.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //0
//        var gameState8 = gameState7.withPlacedTile(new PlacedTile(
//                gameState7.tileToPlace(),
//                gameState7.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(0,1)));
//
//        var gameState9 = gameState8.withNewOccupant(null);
//
//
//        //88
//        var gameState10 = gameState9.withPlacedTile(new PlacedTile(
//                gameState9.tileToPlace(),
//                gameState9.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,1)));
//
//
//        //Expected
//        assertEquals(GameState.Action.RETAKE_PAWN, gameState10.nextAction());
//
//    }
//
//    @Test
//    void withPlacedTileWorksWithForestClosed(){
//
//        var gameState = initialGameState4;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                gameState1.board().lastPlacedTile().pos().neighbor(Direction.E)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().s().zones().get(0).id()));
//
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.HALF_TURN,
//                new Pos(1,1)));
//
//        var gameState5 = gameState4.withNewOccupant(null);
//
//        //Expected
//
//        MessageBoard.Message messageExpected = new MessageBoard.Message(
//                TEXT_MADE.playersScoredForest(Set.of(PlayerColor.RED), 4, 0, 2),
//                4,
//                Set.of(PlayerColor.RED),
//                Set.of(0, 1));
//
//        MessageBoard messageBoardExpected = new MessageBoard(TEXT_MADE, List.of(messageExpected));
//
//        assertEquals(messageBoardExpected, gameState5.messageBoard());
//
//    }
//
//    @Test
//    void withPlacedTileWorksWithRiverClosed(){
//
//        var gameState = initialGameState3;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                gameState1.board().lastPlacedTile().pos().neighbor(Direction.W)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.HUT,
//                gameState2.board().lastPlacedTile().tile().e().zones().get(1).id()));
//
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                gameState3.board().lastPlacedTile().pos().neighbor(Direction.S)));
//
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().n().zones().get(1).id()));
//
//        //Expected
//
//        MessageBoard.Message messageExpected = new MessageBoard.Message(
//                TEXT_MADE.playersScoredRiver(Set.of(PlayerColor.BLUE), 7, 4, 3),
//                7,
//                Set.of(PlayerColor.BLUE),
//                Set.of(56, 19, 0));
//
//        MessageBoard messageBoardExpected = new MessageBoard(TEXT_MADE, List.of(messageExpected));
//
//        assertEquals(messageBoardExpected, gameState5.messageBoard());
//
//    }
//
//    @Test
//    void withPlacedTileWorksWithOccupationImpossible(){
//
//        var gameState = initialGameState5;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1,-1)));
//
//        assertEquals(GameState.Action.PLACE_TILE, gameState4.nextAction());
//
//    }
//
//    @Test
//    void withPlacedTileWorksWithMenhirToPlace(){
//
//        var gameState = initialGameState4;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                gameState1.board().lastPlacedTile().pos().neighbor(Direction.E)));
//
//        var gameState3 = gameState2.withNewOccupant(null);
//
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.HALF_TURN,
//                new Pos(0,1)));
//
//        var gameState5 = gameState4.withNewOccupant(null);
//
//        assertEquals(79, gameState5.tileToPlace().id());
//    }
//
//    /*@Test
//    void withPlacedTileWorksWithTwoConsecutiveMenhirsClosed(){}*/
//
//    @Test
//    void withOccupantRemovedWorksWhenOccupantNull(){
//
//        var gameState = initialGameState8;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        //49
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //27
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,0)));
//
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().e().zones().get(2).id()));
//
//        //1
//        var gameState6 = gameState5.withPlacedTile(new PlacedTile(
//                gameState5.tileToPlace(),
//                PlayerColor.YELLOW, //alperr replaced with yellow placer it was greeen yellow had no pawn
//                Rotation.RIGHT,
//                new Pos(1,0)));
//
//        var gameState7 = gameState6.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState6.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //0
//        var gameState8 = gameState7.withPlacedTile(new PlacedTile(
//                gameState7.tileToPlace(),
//                gameState7.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(0,1)));
//
//        var gameState9 = gameState8.withNewOccupant(null);
//
//
//        //88
//        var gameState10 = gameState9.withPlacedTile(new PlacedTile(
//                gameState9.tileToPlace(),
//                gameState9.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,1)));
//
//
//
//        var gameState11 = gameState10.withOccupantRemoved(null);
//        //Expected
//
//        assertEquals(GameState.Action.OCCUPY_TILE, gameState11.nextAction());
//    }
//
//    /*@Test
//    void withOccupantRemovedWorksWhenOccupantNotNull(){}*/
//
//    /*@Test
//    void withNewOccupantWorksWhenOccupantNull(){}*/
//
//    /*@Test
//    void withNewOccupantWorksWhenOccupantNotNull(){}*/
//
//    @Test
//    void withNewOccupantWorksWhenAreaAlreadyOccupied(){
//
//        var gameState = initialGameState6;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        //49
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //27
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,0)));
//
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().e().zones().get(2).id()));
//
//        //62
//        var gameState6 = gameState5.withPlacedTile(new PlacedTile(
//                gameState5.tileToPlace(),
//                gameState5.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,-1)));
//
//        assertThrows(IllegalArgumentException.class, () -> gameState6.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState6.board().lastPlacedTile().tile().e().zones().get(0).id())));
//
//    }
//
//
//
//    @Test
//    void withStartingTilePlacedWorks(){
//        var gameState = initialGameState1;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameStateExpected = new GameState(
//                PlayerColor.ALL,
//                new TileDecks(List.of(), List.of(TILES.get(27)), List.of(TILES.get(79))),
//                TILES.get(17),
//                Board.EMPTY.withNewTile(new PlacedTile(TILES.get(56), null, Rotation.NONE, Pos.ORIGIN)),
//                GameState.Action.PLACE_TILE,
//                new MessageBoard(TEXT_MADE, List.of()));
//
//        assertEquals(gameStateExpected, gameState1);
//    }
//
//    @Test
//    void withTurnFinishedWorksOnTopTileDrawnUntil(){
//        var gameState = initialGameState2;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.RIGHT,
//                gameState1.board().lastPlacedTile().pos().neighbor(Direction.W)));
//        /*var occupant1 = new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id());*/
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        var boardExpected = Board.EMPTY
//                .withNewTile(new PlacedTile(TILES.get(56), null, Rotation.NONE, Pos.ORIGIN))
//                .withNewTile(new PlacedTile(TILES.get(0), PlayerColor.RED, Rotation.RIGHT, new Pos(-1, 0)))
//                .withOccupant(new Occupant(Occupant.Kind.PAWN, 0));
//
//        var gameStateExpected = new GameState(
//                List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.PURPLE, PlayerColor.RED),
//                new TileDecks(List.of(), List.of(), List.of(TILES.get(79))),
//                TILES.get(4),
//                boardExpected,
//                GameState.Action.PLACE_TILE,
//                new MessageBoard(TEXT_MADE, List.of()));
//
//        assertEquals(gameStateExpected, gameState3);
//    }
//
//
//    @Test
//    void endGameWorksWithFireAndPitTrap(){
//
//        var gameState = initialGameState9;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        //49
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //27
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,0)));
//
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().e().zones().get(2).id()));
//
//        //1
//        var gameState6 = gameState5.withPlacedTile(new PlacedTile(
//                gameState5.tileToPlace(),
//                gameState5.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(1,0)));
//
//        var gameState7 = gameState6.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState6.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //0
//        var gameState8 = gameState7.withPlacedTile(new PlacedTile(
//                gameState7.tileToPlace(),
//                gameState7.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(0,1)));
//
//        var gameState9 = gameState8.withNewOccupant(null);
//
//
//        //92
//        var gameState10 = gameState9.withPlacedTile(new PlacedTile(
//                gameState9.tileToPlace(),
//                gameState9.currentPlayer(),
//                Rotation.NONE,
//                new Pos(0,-1)));
//
//        var gameState11 = gameState10.withNewOccupant(null);
//
//        //69
//        var gameState12 = gameState11.withPlacedTile(new PlacedTile(
//                gameState11.tileToPlace(),
//                gameState11.currentPlayer(),
//                Rotation.LEFT,
//                new Pos(-2,1)));
//
//        var gameState13 = gameState12.withNewOccupant(null);
//
//
//        //25
//        var gameState14 = gameState13.withPlacedTile(new PlacedTile(
//                gameState13.tileToPlace(),
//                gameState13.currentPlayer(),
//                Rotation.LEFT,
//                new Pos(-3,1)));
//
//        var gameState15 = gameState14.withNewOccupant(null);
//
//
//        //34
//        var gameState16 = gameState15.withPlacedTile(new PlacedTile(
//                gameState15.tileToPlace(),
//                gameState15.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(-2,2)));
//
//        var gameState17 = gameState16.withNewOccupant(null);
//
//        //85
//        var gameState18 = gameState17.withPlacedTile(new PlacedTile(
//                gameState17.tileToPlace(),
//                gameState17.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1,-1)));
//
//        //cannot be occupied
//
//        assertEquals(GameState.Action.END_GAME, gameState18.nextAction());
//        assertThrows(IllegalArgumentException.class, () -> gameState18.withNewOccupant(null));
//
//
//        //Expected
//
//        MessageBoard.Message messageExpected1 = new MessageBoard.Message(
//                TEXT_MADE.playerClosedForestWithMenhir(PlayerColor.YELLOW),
//                0,
//                Set.of(),
//                Set.of(56, 0, 1));
//
//        MessageBoard.Message messageExpected2 = new MessageBoard.Message(
//                TEXT_MADE.playerClosedForestWithMenhir(PlayerColor.BLUE),
//                0,
//                Set.of(),
//                Set.of(69, 25, 34));
//
//        Map<Animal.Kind, Integer> mapExpected3 = new HashMap<>();
//        mapExpected3.put(Animal.Kind.AUROCHS, 1);
//        mapExpected3.put(Animal.Kind.DEER, 1);
//
//        MessageBoard.Message messageExpected3 = new MessageBoard.Message(
//                TEXT_MADE.playersScoredMeadow(Set.of(PlayerColor.RED), 3, mapExpected3),
//                3,
//                Set.of(PlayerColor.RED),
//                Set.of(56, 49, 27, 69, 85, 92));
//
//        Map<Animal.Kind, Integer> mapExpected4 = new HashMap<>();
//        mapExpected4.put(Animal.Kind.AUROCHS, 1);
//        mapExpected4.put(Animal.Kind.DEER, 1);
//
//        MessageBoard.Message messageExpected4 = new MessageBoard.Message(
//                TEXT_MADE.playersScoredPitTrap(Set.of(PlayerColor.RED), 3, mapExpected4),
//                3,
//                Set.of(PlayerColor.RED),
//                Set.of(92, 85, 49, 56));
//
//        MessageBoard.Message messageExpected5 = new MessageBoard.Message(
//                TEXT_MADE.playersWon(Set.of(PlayerColor.RED), 6),
//                0,
//                Set.of(),//alperr
//                Set.of());
//
//        MessageBoard messageBoardExpected = new MessageBoard(TEXT_MADE, List.of(messageExpected1, messageExpected2,
//                 messageExpected4,messageExpected3, messageExpected5)); //alperr order 3 le 4
//
//        assertEquals(messageBoardExpected.messages(), gameState18.messageBoard().messages());
//
//    }
//
//
//    @Test
//    void endGameWorksWithPitTrap(){
//
//        var gameState = initialGameState10;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        //49
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //27
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,0)));
//
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().e().zones().get(2).id()));
//
//        //1
//        var gameState6 = gameState5.withPlacedTile(new PlacedTile(
//                gameState5.tileToPlace(),
//                gameState5.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(1,0)));
//
//        var gameState7 = gameState6.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState6.board().lastPlacedTile().tile().n().zones().get(0).id()));
//
//        //0
//        var gameState8 = gameState7.withPlacedTile(new PlacedTile(
//                gameState7.tileToPlace(),
//                gameState7.currentPlayer(),
//                Rotation.RIGHT,
//                new Pos(0,1)));
//
//        var gameState9 = gameState8.withNewOccupant(null);
//
//
//        //92
//        var gameState10 = gameState9.withPlacedTile(new PlacedTile(
//                gameState9.tileToPlace(),
//                gameState9.currentPlayer(),
//                Rotation.NONE,
//                new Pos(0,-1)));
//
//        var gameState11 = gameState10.withNewOccupant(null);
//
//        //31
//        var gameState12 = gameState11.withPlacedTile(new PlacedTile(
//                gameState11.tileToPlace(),
//                gameState11.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1,-1)));
//
//        var gameState13 = gameState12.withNewOccupant(null);
//
//
//        //Expected
//
//        MessageBoard.Message messageExpected1 = new MessageBoard.Message(
//                TEXT_MADE.playerClosedForestWithMenhir(PlayerColor.YELLOW),
//                0,
//                Set.of(),
//                Set.of(56, 0, 1));
//
//
//        Map<Animal.Kind, Integer> mapExpected2 = new HashMap<>();
//        mapExpected2.put(Animal.Kind.AUROCHS, 1);
//        mapExpected2.put(Animal.Kind.TIGER, 1);
//
//        MessageBoard.Message messageExpected2 = new MessageBoard.Message(
//                TEXT_MADE.playersScoredMeadow(Set.of(PlayerColor.RED), 2, mapExpected2),
//                2,
//                Set.of(PlayerColor.RED),
//                Set.of(56, 49, 27, 31, 92));
//
//        Map<Animal.Kind, Integer> mapExpected3 = new HashMap<>();
//        mapExpected3.put(Animal.Kind.AUROCHS, 1);
//        mapExpected3.put(Animal.Kind.TIGER, 1);
//
//        MessageBoard.Message messageExpected3 = new MessageBoard.Message(
//                TEXT_MADE.playersScoredPitTrap(Set.of(PlayerColor.RED), 2, mapExpected3),
//                2,
//                Set.of(PlayerColor.RED),
//                Set.of(92, 31, 49, 56));
//
//        MessageBoard.Message messageExpected4 = new MessageBoard.Message(
//                TEXT_MADE.playersWon(Set.of(PlayerColor.RED), 4),
//                0,
//                Set.of(),//alper red addition
//                Set.of());
//
//        MessageBoard messageBoardExpected = new MessageBoard(TEXT_MADE, List.of(messageExpected1,
//                 messageExpected3,messageExpected2, messageExpected4)); //alper order 3 2
//
//        assertEquals(messageBoardExpected.messages(), gameState13.messageBoard().messages());
//
//    }
//
//
//    @Test
//    void closedRiverSystemWorks(){
//
//        var gameState = initialGameState11;
//        var gameState1 = gameState.withStartingTilePlaced();
//
//        //49
//        var gameState2 = gameState1.withPlacedTile(new PlacedTile(
//                gameState1.tileToPlace(),
//                gameState1.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-1, 0)));
//
//            //river
//        var gameState3 = gameState2.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState2.board().lastPlacedTile().tile().e().zones().get(1).id()));
//
//        //27
//        var gameState4 = gameState3.withPlacedTile(new PlacedTile(
//                gameState3.tileToPlace(),
//                gameState3.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,0)));
//
//            //meadow
//        var gameState5 = gameState4.withNewOccupant(new Occupant(
//                Occupant.Kind.PAWN,
//                gameState4.board().lastPlacedTile().tile().e().zones().get(2).id()));
//
//
//        //14
//        var gameState6 = gameState5.withPlacedTile(new PlacedTile(
//                gameState5.tileToPlace(),
//                gameState5.currentPlayer(),
//                Rotation.NONE,
//                new Pos(-2,1)));
//
//            //river
//        var gameState7 = gameState6.withNewOccupant(new Occupant(
//                Occupant.Kind.HUT,
//                gameState6.board().lastPlacedTile().tile().n().zones().get(1).id()));
//
//        assertEquals(GameState.Action.END_GAME, gameState7.nextAction());
//
//
//        //Expected
//
//        MessageBoard.Message messageExpected1 = new MessageBoard.Message(
//                TEXT_MADE.playersScoredRiver(Set.of(PlayerColor.RED), 5, 1, 4),
//                5,
//                Set.of(PlayerColor.RED),
//                Set.of(56, 49, 27, 14));
//
//        MessageBoard.Message messageExpected2 = new MessageBoard.Message(
//                TEXT_MADE.playersScoredRiverSystem(Set.of(PlayerColor.GREEN), 1, 1),
//                1,
//                Set.of(PlayerColor.GREEN),
//                Set.of(56, 49, 27, 14));
//
//        MessageBoard.Message messageExpected3 = new MessageBoard.Message(
//                TEXT_MADE.playersWon(Set.of(PlayerColor.RED), 5),
//                0,
//                Set.of(),
//                Set.of());
//
//        MessageBoard messageBoardExpected = new MessageBoard(TEXT_MADE, List.of(messageExpected1, messageExpected2,
//                messageExpected3));
//
//        assertEquals(messageBoardExpected.messages(), gameState7.messageBoard().messages());
//
//    }
//
//
//}