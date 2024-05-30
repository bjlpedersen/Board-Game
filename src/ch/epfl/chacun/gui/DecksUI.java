package ch.epfl.chacun.gui;

import ch.epfl.chacun.Occupant;
import ch.epfl.chacun.Tile;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

/**
 * This class provides a user interface for the decks in the game.
 * It cannot be instantiated.
 *
 * @author Bjork Pedersen (376143)
 */
public class DecksUI {
    private static final double TILE_VIEW_SIZE = ImageLoader.LARGE_TILE_FIT_SIZE;


    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws AssertionError always
     */
    private DecksUI() {
    }

    /**
     * Creates a new Node for the decks UI.
     *
     * @param tile            the tile to be placed
     * @param normalTilesLeft the number of normal tiles left
     * @param menhirTilesLeft the number of menhir tiles left
     * @param textToShow      the text to show on the tile
     * @param handler         the handler for the mouse click event on the tile
     * @return a Node representing the decks UI
     */
    public static Node create(ObservableValue<Tile> tile,
                              ObservableValue<Integer> normalTilesLeft,
                              ObservableValue<Integer> menhirTilesLeft,
                              ObservableValue<String> textToShow,
                              Consumer<Occupant> handler) {

        VBox generalBox = new VBox();
        generalBox.getStylesheets().add("decks.css");

        StackPane nextTile = new StackPane();
        nextTile.setId("next-tile");

        ImageView tileToPlaceView = new ImageView();
        tileToPlaceView.setFitHeight(TILE_VIEW_SIZE);
        tileToPlaceView.setFitWidth(TILE_VIEW_SIZE);

        Text tileToPlaceText = new Text(textToShow.getValue());

        HBox tileBox = new HBox();
        tileBox.setId("decks");

        StackPane menhirTilePane = createTilePane("MENHIR", menhirTilesLeft);
        StackPane normalTilePane = createTilePane("NORMAL", normalTilesLeft);

        normalTilesLeft.addListener((o, oldTilesLeft, newTilesLeft) -> {
            ((Text) normalTilePane.getChildren().get(1)).setText(newTilesLeft.toString());
        });

        menhirTilesLeft.addListener((o, oldTilesLeft, newTilesLeft) -> {
            ((Text) menhirTilePane.getChildren().get(1)).setText(newTilesLeft.toString());
        });

        tile.addListener((o, oldTile, newTile) -> {
            if (newTile != null) {
                tileToPlaceView.setImage(ImageLoader.largeImageForTile(newTile.id()));
            }
        });

        ObservableValue<Boolean> isNotEmpty = textToShow.map(s -> !s.isEmpty());
        tileToPlaceText.textProperty().bind(textToShow);
        tileToPlaceText.visibleProperty().bind(isNotEmpty);
        tileToPlaceText.setWrappingWidth(0.8 * ImageLoader.LARGE_TILE_FIT_SIZE);
        tileToPlaceView.visibleProperty().bind(tileToPlaceText.visibleProperty().not());

        tileToPlaceText.setOnMouseClicked(event -> handler.accept(null));

        tileBox.getChildren().addAll(normalTilePane, menhirTilePane);

        nextTile.getChildren().addAll(tileToPlaceView, tileToPlaceText);
        generalBox.getChildren().addAll(tileBox, nextTile);

        return generalBox;
    }

    /**
     * Creates a StackPane for a tile deck. The StackPane includes an ImageView for the tile
     * and a Text for the number of tiles left.
     * The ImageView's ID is set to the provided ID, and its fit width and height are halved.
     * The Text's wrapping width is set to 80% of the ImageView's fit width.
     *
     * @param id        The ID for the ImageView.
     * @param tilesLeft An ObservableValue of the number of tiles left in the deck.
     * @return A StackPane for the tile deck.
     */
    private static StackPane createTilePane(String id, ObservableValue<Integer> tilesLeft) {
        StackPane tilePane = new StackPane();
        ImageView tileImage = new ImageView();
        tileImage.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
        tileImage.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
        tileImage.setId(id);
        Text tilesLeftText = new Text(tilesLeft.getValue().toString());
        tilesLeftText.setWrappingWidth(tileImage.getFitWidth() * 0.85);
        tilePane.getChildren().addAll(tileImage, tilesLeftText);
        return tilePane;
    }
}