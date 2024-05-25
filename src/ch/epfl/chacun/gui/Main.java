package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import ch.epfl.chacun.TilesTest;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class Main extends Application {


    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> names = getParameters().getUnnamed();

        long seed = Long.parseUnsignedLong(getParameters().getNamed().values().iterator().next());
        RandomGenerator randomGenerator = RandomGeneratorFactory.getDefault().create(seed);

        List<PlayerColor> colors = PlayerColor.ALL;
        List<PlayerColor> playerColors = new ArrayList<>();
        if (names.size() < 2 || names.size() > 5) throw new IllegalArgumentException("Player count not in range");

        Map<PlayerColor, String> players = new HashMap<>();
        for (int i = 0; i < names.size(); i++) {
            players.put(colors.get(i), names.get(i));
            playerColors.add(colors.get(i));
        }

//        List<Tile> tiles = new ArrayList<>(Tiles.TILES);
//        Collections.shuffle(tiles, randomGenerator);
//        TileDecks tileDecks = tileListToTileDeck(tiles);

        List<Tile> tiles = new ArrayList<>(TilesTest.TILES);
        TileDecks tileDecks = tileListToTileDeck(tiles);


        //PlayersUI parameters initialization
        TextMaker textMaker = new TextMakerFr(players); //Also used for BoardUI
        GameState gameState = GameState.initial(playerColors, tileDecks, textMaker);
        SimpleObjectProperty<GameState> state = new SimpleObjectProperty<>(gameState); //Also used for BoardUI

        //ActionsUI parameters initialization
        SimpleObjectProperty<List<String>> obsActions = new SimpleObjectProperty<>(new ArrayList<>());
        Consumer<String> executeAction = s -> {
            GameState state1 = state.getValue();
            state1 = ActionEncoder.decodeAndApply(state1, s).getGameState();
            state.set(state1);
        };

        //DecksUI parameters initialisation
        ObservableValue<Tile> obsTileToPlace = state.map(GameState::tileToPlace);
        ObservableValue<Integer> normalTilesLeft = state.map(s -> s.tileDecks().normalTiles().size());
        ObservableValue<Integer> menhirTilesLeft = state.map(s -> s.tileDecks().menhirTiles().size());
        ObservableValue<String> textToShow = state.map(newState -> {
            if (newState.nextAction() == GameState.Action.OCCUPY_TILE) {
                return "Cliquez sur le pion\n" +
                        "ou la hutte que\n" +
                        "vous désirez\n" +
                        "placer, ou ici pour\n" +
                        "ne pas en placer";
            }
            else if (newState.nextAction() == GameState.Action.RETAKE_PAWN) {
                return "Cliquez sur le pion\n" +
                        "ou la hutte que\n" +
                        "vous désirez\n" +
                        "reprendre, ou ici pour\n" +
                        "ne pas en reprendre";
            } else return "";
        });
        Consumer<Occupant> handler = o -> {
            GameState state1 = state.getValue();
            if (state1.nextAction() == GameState.Action.OCCUPY_TILE) {
                String action = ActionEncoder.withNewOccupant(state1, null).getEncodedAction();
                List<String> updatedActions = new ArrayList<>(obsActions.getValue());
                updatedActions.add(action);
                obsActions.set(updatedActions);
                state.set(state1.withNewOccupant(null));
            }
            else if (state1.nextAction() == GameState.Action.RETAKE_PAWN) state.set(state1.withOccupantRemoved(null));
        };

        //MessageBoardUI parameters initialization
        ObservableValue<List<MessageBoard.Message>> obsMessageBoard = state.
                map(GameState::messageBoard).map(MessageBoard::messages);


        // BoardUI parameters initialization
        final int REACH = 12;
        SimpleObjectProperty<Rotation> rotation = new SimpleObjectProperty<>(Rotation.NONE);
        ObservableValue<Set<Occupant>> visibleOccupants = state.map(g -> {
            Set<Occupant> occupants = new HashSet<>(g.board().occupants());
            if (g.nextAction() == GameState.Action.OCCUPY_TILE) {
                occupants.addAll(g.lastTilePotentialOccupants());
            }
            return occupants;
        });
        Set<Integer> highlightedTiles = new HashSet<>(); //Also used in MessageBoardUI
        ObjectProperty<Set<Integer>> obsHighlightedTiles = new SimpleObjectProperty<>(highlightedTiles);
        Consumer<Rotation> rotateTile = r -> {
            Rotation currentRotation = rotation.getValue();
            Rotation newRotation = currentRotation.add(r);
            rotation.set(newRotation);
        };
        Consumer<Pos> placeTile = p -> {
            GameState state1 = state.getValue();
            PlacedTile placedTile = new PlacedTile(state1.tileToPlace(), state1.currentPlayer(), rotation.getValue(), p);
            String action = ActionEncoder.withPlacedTile(state1, placedTile).getEncodedAction();
            List<String> updatedActions = new ArrayList<>(obsActions.getValue());
            updatedActions.add(action);
            obsActions.set(updatedActions);
            state.set(state.get().withPlacedTile(placedTile));
        };
        Consumer<Occupant> selectOccupant = o -> {
            GameState state1 = state.getValue();
            String action = "";
            List<String> updatedActions = new ArrayList<>(obsActions.getValue());
            if (state1.nextAction() == GameState.Action.OCCUPY_TILE && o.zoneId() / 10 == state1.board().lastPlacedTile().id()) {
                action = ActionEncoder.withNewOccupant(state1, o).getEncodedAction();
                state.set(state1.withNewOccupant(o));
            }
            else if (state1.nextAction() == GameState.Action.RETAKE_PAWN && o.kind() == Occupant.Kind.PAWN) {
                action = ActionEncoder.withOccupantRemoved(state1, o).getEncodedAction();
                state.set(state1.withOccupantRemoved(o));
            }
            if (!action.isEmpty()) updatedActions.add(action);
            obsActions.set(updatedActions);
        };

        //Creation of all the Nodes and the Scene for the generalUI.
        Node boardUI = BoardUI.create(REACH, state, rotation, visibleOccupants, obsHighlightedTiles, rotateTile, placeTile, selectOccupant);
        Node messageBoardUI = MessageBoardUI.create(obsMessageBoard, obsHighlightedTiles);
        Node playersUI = PlayersUI.create(state, textMaker);
        Node actionsUI = ActionsUI.create(obsActions, executeAction);
        Node decksUI = DecksUI.create(obsTileToPlace, normalTilesLeft, menhirTilesLeft, textToShow, handler);

        BorderPane gameView = new BorderPane();
        BorderPane sidePanel = new BorderPane();
        VBox actionsAndDecks = new VBox(actionsUI, decksUI);

        sidePanel.setTop(playersUI);
        sidePanel.setCenter(messageBoardUI);
        sidePanel.setBottom(actionsAndDecks);

        gameView.setCenter(boardUI);
        gameView.setRight(sidePanel);
        state.set(state.getValue().withStartingTilePlaced());

        //Settings of the primary stage.
        Scene scene = new Scene(gameView);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ChaCuN");
        primaryStage.setHeight(1080);
        primaryStage.setWidth(1440);
        primaryStage.show();

    }

    private static TileDecks tileListToTileDeck(List<Tile> tiles) {
        List<Tile> normalTiles = new ArrayList<>();
        List<Tile> menhirTiles = new ArrayList<>();
        List<Tile> startTiles = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.kind() == Tile.Kind.NORMAL) normalTiles.add(tile);
            if (tile.kind() == Tile.Kind.MENHIR) menhirTiles.add(tile);
            if (tile.kind() == Tile.Kind.START) startTiles.add(tile);
        }
        return new TileDecks(startTiles, normalTiles, menhirTiles);
    }
}
