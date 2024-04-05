package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameStateTestbizz {

    static Board boardWithStartTile = Board.EMPTY.withNewTile(new PlacedTile(initialGameState().tileDecks().topTile(Tile.Kind.START),
            null,
            Rotation.NONE, new
            Pos(0,0)));
    static TextMaker textMaker = new BasicTextMaker();
    static GameState initialGameState() {
        //Création liste de joueurs
        //TODO bien implémenter la liste de joueur comme LinkedList voir ed#960
        List<PlayerColor> players = new LinkedList<>();
        Collections.addAll(players,
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.GREEN,
                PlayerColor.YELLOW,
                PlayerColor.PURPLE);

        //Création tileDecks
        List<Tile> startTiles = new ArrayList<>();
        startTiles.add(Tiles.TILES.get(56));

        List<Tile> normalTiles = new ArrayList<>();
        Collections.addAll(normalTiles,
                Tiles.TILES.get(0),
                Tiles.TILES.get(26),
                Tiles.TILES.get(64),
                Tiles.TILES.get(32),
                Tiles.TILES.get(60));
        normalTiles = List.copyOf(normalTiles);
        List<Tile> menhirTiles = new ArrayList<>(Tiles.TILES.subList(91, 95));
        menhirTiles.add(Tiles.TILES.get(85));
        menhirTiles.add(Tiles.TILES.get(79));
        menhirTiles = List.copyOf(menhirTiles);
        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        //Création du GameState de base
        GameState initialGameState = GameState.initial(players, tileDecks, textMaker);
        return initialGameState;
    }

    //2) Création du GameState avec nextAction = PLACE_TILE c-à-d pose la tuile de départ sur le board etc
    //gère la transistion de GS START_GAME à PLACE_TILE
    static GameState withStartTilePlaced() {
        GameState initialGameState = initialGameState();
        return initialGameState.withStartingTilePlaced();
    }

    //Le premier joueur pose une tuile normale
    static GameState withFirstTilePlaced() {
        //Cas 1 : Tuile sans pouvoir spécial qui peut être occupée
        Tile firstTile = Tiles.TILES.getFirst();
        PlacedTile placedTile1_East = new PlacedTile(firstTile,
                PlayerColor.RED,
                Rotation.NONE,
                new Pos(1,0));

        return withStartTilePlaced().withPlacedTile(placedTile1_East);
    }

    //Le premier joueur décide d'occuper la tuile qu'il a placé
    static GameState withFirstTileOccupied() {
        Occupant occupant = new Occupant(Occupant.Kind.PAWN, 0_1);
        return withFirstTilePlaced().withNewOccupant(occupant);
    }


    //Le deuxième joueur s'apprête à placer la tile 26 au nord de la tuile 0
    static GameState withSecondTilePlaced() {

        Tile secondTile = Tiles.TILES.get(26);
        PlacedTile placedTileNorthOf0 = new PlacedTile(secondTile,
                PlayerColor.BLUE,
                Rotation.RIGHT,
                new Pos(1,1));

        return withFirstTileOccupied().withPlacedTile(placedTileNorthOf0);
    }

    // le deuxième joueur ne place pas de pions
    static GameState withSecondTileNotOccupied() {
        return withSecondTilePlaced().withNewOccupant(null);
    }

    //Le troisième joueur s'apprête à placer la tile 64 à l'ouest de la tuile 26
    static GameState withThirdTilePlaced() {
        Tile thirdTile = Tiles.TILES.get(64);
        PlacedTile placedTileWest = new PlacedTile(thirdTile,
                PlayerColor.GREEN,
                Rotation.NONE,
                new Pos(0, 1));

        return withSecondTileNotOccupied().withPlacedTile(placedTileWest);
    }

    @Test
    void initialGameStateWorks() {
        //expected GameState
        //Création liste de joueurs
        List<PlayerColor> players = new ArrayList<>();
        Collections.addAll(players,
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.GREEN,
                PlayerColor.YELLOW,
                PlayerColor.PURPLE);

        //Création tileDecks
        List<Tile> startTiles = new ArrayList<>();
        startTiles.add(Tiles.TILES.get(56));

        List<Tile> normalTiles = initialGameState().tileDecks().normalTiles();



        List<Tile> menhirTiles = new ArrayList<>(Tiles.TILES.subList(91, 95));
        menhirTiles.add(Tiles.TILES.get(85));
        menhirTiles.add(Tiles.TILES.get(79));
        menhirTiles = List.copyOf(menhirTiles);

        TileDecks tileDecks = new TileDecks(startTiles, normalTiles, menhirTiles);

        //Création TextMaker
        TextMaker textMaker = new BasicTextMaker();
        MessageBoard messageBoard = new MessageBoard(textMaker, List.of());

        GameState expected = new GameState(players,
                tileDecks,
                null,
                Board.EMPTY,
                GameState.Action.START_GAME,
                messageBoard);

        assertEquals(expected, initialGameState());
    }


    //TODO ajouter assertNull() quand j'aurai un gameState avec l'action END_GAME
    @Test
    void currentPlayerWorks() {
        //Renvoie null si action == START ou END
        assertNull(initialGameState().currentPlayer());
        //Sinon renvoie le première joueur de la liste
        assertEquals(PlayerColor.RED, withStartTilePlaced().currentPlayer());
    }

    @Test
    void withStartingTilePlacedThrows() {
        GameState actualStartingTilePlacedGS = withStartTilePlaced();
        assertThrows(IllegalArgumentException.class, actualStartingTilePlacedGS::withStartingTilePlaced);
    }

    @Test
    void withStartingTilePlacedWorks() {
        //Appelle withStartingTilePlace
        GameState actualStartingTilePlacedGS = withStartTilePlaced();

        GameState startingGameState = initialGameState();
        TileDecks tileDeckWithoutStartTile = new TileDecks(new ArrayList<>(startingGameState.tileDecks().startTiles()),
                new ArrayList<>(startingGameState.tileDecks().normalTiles()),
                new ArrayList<>(startingGameState.tileDecks().menhirTiles()))
                .withTopTileDrawn(Tile.Kind.START);

        Tile tileToPlace = tileDeckWithoutStartTile.topTile(Tile.Kind.NORMAL);
        TileDecks expectedTileDeck = tileDeckWithoutStartTile.withTopTileDrawn(Tile.Kind.NORMAL);


        GameState expected = new GameState(actualStartingTilePlacedGS.players(),
                expectedTileDeck,
                tileToPlace,
                boardWithStartTile,
                GameState.Action.PLACE_TILE,
                new MessageBoard(textMaker, new ArrayList<>()));

        assertEquals(expected, actualStartingTilePlacedGS);
    }

    @Test
    void freeOccupantsCountWorksTrivial() {
        //Renvoie bien le bon compte (5 ou 3) si aucun occupant n'a encore été placé
        GameState initialGS = initialGameState();
        for (PlayerColor player : initialGS.players()) {
            assertEquals(5, initialGS.freeOccupantsCount(player, Occupant.Kind.PAWN));
            assertEquals(3, initialGS.freeOccupantsCount(player, Occupant.Kind.HUT));
        }
    }

    // TODO tester cette méthode quand on a un GameState avec des occupants qui ont été placés
    @Test
    void freeOccupantsCountWorks() {
        GameState finalGameState = withThirdTilePlaced();
        assertEquals(4, finalGameState.freeOccupantsCount(PlayerColor.RED, Occupant.Kind.PAWN));
        for (PlayerColor player : finalGameState.players()) {
            if (player != PlayerColor.RED) {
                assertEquals(5, finalGameState.freeOccupantsCount(player, Occupant.Kind.PAWN));
            }
            assertEquals(3, finalGameState.freeOccupantsCount(player, Occupant.Kind.HUT));
        }
    }

    @Test
    void withPlacedTileThrows() {
        PlacedTile randomPlacedTile = new PlacedTile(Tiles.TILES.getFirst(),
                PlayerColor.RED,
                Rotation.NONE,
                new Pos(0,-1));
        assertThrows(IllegalArgumentException.class, () -> {
            initialGameState().withPlacedTile(randomPlacedTile);
        });
    }

    @Test
    void withPlacedTileWorks() {
        //Initial GS
        GameState startTilePlacedGS = withStartTilePlaced();

        //Cas 1 : Tuile sans pouvoir spécial qui peut être occupée
        Tile firstTile = Tiles.TILES.getFirst();
        PlacedTile placedTile1_East = new PlacedTile(firstTile,
                PlayerColor.RED,
                Rotation.NONE,
                new Pos(1,0));

        //Building the expected resulting GS
        TileDecks tileDeckWithoutFirstPTile = new TileDecks(new ArrayList<>(startTilePlacedGS.tileDecks().startTiles()),
                new ArrayList<>(startTilePlacedGS.tileDecks().normalTiles()),
                new ArrayList<>(startTilePlacedGS.tileDecks().menhirTiles()));

        Board boardWithFirstPT = boardWithStartTile.withNewTile(placedTile1_East);
        GameState expected = new GameState(startTilePlacedGS.players(),
                tileDeckWithoutFirstPTile,
                null,
                boardWithFirstPT,
                GameState.Action.OCCUPY_TILE,
                new MessageBoard(textMaker, new ArrayList<>()));

        //Actual resulting GS
        GameState actual = withFirstTilePlaced();
        assertEquals(expected, actual);

        //Tuile logboat

        //Tuile pit_trap
        //Tuile shaman
    }

    @Test
    void withNewOccupantThrows() {
        //GS avec la tuile 56
        assertThrows(IllegalArgumentException.class, () -> {
            withStartTilePlaced().withNewOccupant(new Occupant(Occupant.Kind.PAWN, 0_0));
        });
    }

    @Test
    void withNewOccupantsWorks() {
        //GS avec la tuile 56 et 0 à l'est
        GameState gameStateFirstTilePlaced = withFirstTilePlaced();
        Occupant occupant = new Occupant(Occupant.Kind.PAWN, 0_1);

        List<PlayerColor> players = new ArrayList<>(gameStateFirstTilePlaced.players());
        players.addLast(players.getFirst());
        players.remove(0);

        TileDecks expectedTileDecks = new TileDecks(List.of(),
                List.of(Tiles.TILES.get(64), Tiles.TILES.get(32), Tiles.TILES.get(60)),
                gameStateFirstTilePlaced.tileDecks().menhirTiles());


        //expected GS
        GameState expected = new GameState(players,
                expectedTileDecks,
                Tiles.TILES.get(26),
                gameStateFirstTilePlaced.board().withOccupant(occupant),
                GameState.Action.PLACE_TILE,
                gameStateFirstTilePlaced.messageBoard());

        assertEquals(expected, gameStateFirstTilePlaced.withNewOccupant(occupant));
        assertEquals(expected, withFirstTileOccupied());
    }


    @Test
    void SecondPlacedTileWorks() {
        //expected resulting gameState

        Tile secondTile = Tiles.TILES.get(26);
        PlacedTile placedTileNorthOf0 = new PlacedTile(secondTile,
                PlayerColor.BLUE,
                Rotation.RIGHT,
                new Pos(1,1));

        TileDecks expectedTileDecks = new TileDecks(new ArrayList<>(withFirstTileOccupied().tileDecks().startTiles()),
                new ArrayList<>(withFirstTileOccupied().tileDecks().normalTiles()),
                new ArrayList<>(withFirstTileOccupied().tileDecks().menhirTiles()));

        Board expectedBoard = withFirstTileOccupied().board().withNewTile(placedTileNorthOf0);

        GameState expected = new GameState(withFirstTileOccupied().players(),
                expectedTileDecks,
                null,
                expectedBoard,
                GameState.Action.OCCUPY_TILE,
                withFirstTileOccupied().messageBoard());

        assertEquals(expected, withSecondTilePlaced());
    }

    @Test
    void WithNewOccupantWorksWithNullOccupant() {
        Tile tileToPlace = withSecondTilePlaced().tileDecks().topTile(Tile.Kind.NORMAL);
        List<PlayerColor> expectedPlayers = new LinkedList<>(withSecondTilePlaced().players());
        Collections.rotate(expectedPlayers, -1);


        //TODO Revoir le test : la pose de la tuile 26 ferme une fôret donc le message board devrait être updaté
        // ce qui n'est en réalité pas le cas
        //MessageBoard updatedMessageBoard = withFirstTileOccupied().messageBoard().withScoredForest();
        GameState expected = new GameState(expectedPlayers,
                withSecondTilePlaced().tileDecks().withTopTileDrawn(Tile.Kind.NORMAL),
                tileToPlace,
                withSecondTilePlaced().board(),
                GameState.Action.PLACE_TILE,
                withSecondTilePlaced().messageBoard());

        assertEquals(expected, withSecondTileNotOccupied());
    }

    @Test
    void withThirdTileWorks() {
        Tile thirdTile = Tiles.TILES.get(64);
        PlacedTile placedTileWest = new PlacedTile(thirdTile,
                PlayerColor.GREEN,
                Rotation.NONE,
                new Pos(0, 1));

        GameState expected = new GameState(withSecondTileNotOccupied().players(),
                withSecondTileNotOccupied().tileDecks(),
                null,
                withSecondTileNotOccupied().board().withNewTile(placedTileWest),
                GameState.Action.OCCUPY_TILE,
                withSecondTileNotOccupied().messageBoard());

        assertEquals(expected, withThirdTilePlaced());
    }
}