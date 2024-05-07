package ch.epfl.chacun.UITests;

import ch.epfl.chacun.*;
import ch.epfl.chacun.gui.DecksUI;
import ch.epfl.chacun.gui.MessageBoardUI;
import ch.epfl.chacun.gui.PlayersUI;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ch.epfl.chacun.UITests.Tiles.TILES;


public class DecksUITest2 extends Application{

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        var playerNames = Map.of(PlayerColor.RED, "Bryan",
                PlayerColor.BLUE, "Okkes");
        var playerColors = playerNames.keySet().stream()
                .sorted()
                .toList();

        var tilesByKind = TILES.stream()
                .collect(Collectors.groupingBy(Tile::kind));
        var tileDecks =
                new TileDecks(List.of(TILES.get(56)),
                        List.of(TILES.get(19), TILES.get(0), TILES.get(24)),
                        List.of(TILES.get(79)));

        var textMaker = new TextMakerFr(playerNames);

        var gameState =
                GameState.initial(playerColors,
                        tileDecks,
                        textMaker);
        //gameState = gameState.withStartingTilePlaced();

        var gameStateO = new SimpleObjectProperty<>(gameState);

        var playersNode = PlayersUI.create(gameStateO, textMaker);


        var messages0 = new SimpleObjectProperty<List<MessageBoard.Message>>();
        messages0.bind(gameStateO.map( g -> g.messageBoard().messages()));

        var messageNode = MessageBoardUI.create(messages0, new SimpleObjectProperty<>(Set.of()));


        //var tileToPlace0 = new SimpleObjectProperty<>(gameState.tileToPlace());
        var tileToPlace0 = new SimpleObjectProperty<Tile>();
        tileToPlace0.bind(gameStateO.map(GameState::tileToPlace));

        //var remainingNormalTiles0 = new SimpleObjectProperty<>(gameState.tileDecks().deckSize(Tile.Kind.NORMAL));
        var remainingNormalTiles0 = new SimpleObjectProperty<Integer>();
        remainingNormalTiles0.bind(gameStateO.map(g -> g.tileDecks().deckSize(Tile.Kind.NORMAL)));

        //var remainingMenhirTiles0 = new SimpleObjectProperty<>(gameState.tileDecks().deckSize(Tile.Kind.MENHIR));
        var remainingMenhirTiles0 = new SimpleObjectProperty<Integer>();
        remainingMenhirTiles0.bind(gameStateO.map(g -> g.tileDecks().deckSize(Tile.Kind.MENHIR)));

        var text0 = new SimpleObjectProperty<String>();
        text0.bind(gameStateO.map(g -> g.messageBoard().textMaker().clickToOccupy()));
        //var occupant = null;


        var decksNode = DecksUI.create(tileToPlace0, remainingNormalTiles0, remainingMenhirTiles0, text0, r -> System.out.println("clicked on text"));

        var rootNode = new BorderPane(messageNode, playersNode, null, decksNode, null);
        primaryStage.setScene(new Scene(rootNode));


        gameStateO.set(gameStateO.getValue().withStartingTilePlaced());
        gameStateO.set(gameStateO.getValue().withPlacedTile(new PlacedTile(
                gameStateO.getValue().tileToPlace(),
                gameStateO.getValue().currentPlayer(),
                Rotation.NONE,
                gameStateO.getValue().board().lastPlacedTile().pos().neighbor(Direction.W))));
        gameStateO.set(gameStateO.getValue().withNewOccupant(new Occupant(
                Occupant.Kind.HUT,
                gameStateO.getValue().board().lastPlacedTile().tile().e().zones().get(1).id())));
        gameStateO.set(gameStateO.getValue().withPlacedTile(new PlacedTile(
                gameStateO.getValue().tileToPlace(),
                gameStateO.getValue().currentPlayer(),
                Rotation.NONE,
                gameStateO.getValue().board().lastPlacedTile().pos().neighbor(Direction.S))));
        gameStateO.set(gameStateO.getValue().withNewOccupant(new Occupant(
                Occupant.Kind.PAWN,
                gameStateO.getValue().board().lastPlacedTile().tile().n().zones().get(1).id())));

        primaryStage.setTitle("ChaCuN test");
        primaryStage.show();


    }

    record ConstantConsumer() implements Consumer<Occupant>{

        @Override
        public void accept(Occupant occupant) {

        }
    }
}
