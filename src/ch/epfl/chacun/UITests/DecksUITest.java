package ch.epfl.chacun.UITests;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import ch.epfl.chacun.gui.DecksUI;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class DecksUITest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        Tile tile = Tiles.TILES.getFirst();
        ObservableValue<Tile> obsTile = new SimpleObjectProperty<>(tile);
        ObservableValue<Integer> normalLeft = new SimpleObjectProperty<>(12);
        ObservableValue<Integer> menhirLeft = new SimpleObjectProperty<>(5);
        ObservableValue<String> textToShow = new SimpleObjectProperty<>("Do not display  image");
        Consumer<Occupant> cons = occupant -> System.out.println("Hello World");
        var decksNode = DecksUI.create(obsTile, normalLeft, menhirLeft, textToShow, cons);
        var rootNode = new BorderPane(decksNode);
        primaryStage.setScene(new Scene(rootNode));

        primaryStage.setTitle("ChaCuN test");
        primaryStage.show();

    }

}
