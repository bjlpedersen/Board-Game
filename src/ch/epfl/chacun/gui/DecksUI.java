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

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws AssertionError always
     */
    private DecksUI() {}

    /**
     * Creates a new Node for the decks UI.
     *
     * @param tile the tile to be placed
     * @param normalTilesLeft the number of normal tiles left
     * @param menhirTilesLeft the number of menhir tiles left
     * @param textToShow the text to show on the tile
     * @param handler the handler for the mouse click event on the tile
     * @return a Node representing the decks UI
     */
    public static Node create(ObservableValue<Tile> tile,
                              ObservableValue<Integer> normalTilesLeft,
                              ObservableValue<Integer> menhirTilesLeft,
                              ObservableValue<String> textToShow,
                              Consumer<Occupant> handler) {

        // Create a VBox to hold all elements
        VBox generalBox = new VBox();
        generalBox.getStylesheets().add("decks.css");

        // Create a StackPane to hold the next tile
        StackPane nextTile = new StackPane();
        nextTile.setId("next-tile");

        // Create an ImageView to display the tile to be placed
        ImageView tileToPlaceView = new ImageView();
        tileToPlaceView.setFitHeight(ImageLoader.LARGE_TILE_FIT_SIZE);
        tileToPlaceView.setFitWidth(ImageLoader.LARGE_TILE_FIT_SIZE);

        // Create a Text to display the text to show on the tile
        Text tileToPlaceText = new Text(textToShow.getValue());

        // Create a HBox to hold the tile decks
        HBox tileBox = new HBox();
        tileBox.setId("decks");

        // Initialize menhir tiles
        StackPane menhirTilePane = new StackPane();
        ImageView menhirImage = new ImageView();
        menhirImage.setFitWidth(menhirImage.getFitWidth() / 2);
        menhirImage.setFitHeight(menhirImage.getFitHeight() / 2);
        menhirImage.setId("MENHIR");
        Text tilesLeftMenhir = new Text(menhirTilesLeft.getValue().toString());
        tilesLeftMenhir.setWrappingWidth(menhirImage.getFitWidth() * 0.8);
        menhirTilePane.getChildren().addAll(menhirImage, tilesLeftMenhir);

        // Initialize normal tiles
        StackPane normalTilePane = new StackPane();
        ImageView normalImage = new ImageView();
        normalImage.setFitWidth(normalImage.getFitWidth() / 2);
        normalImage.setFitHeight(normalImage.getFitHeight() / 2);
        normalImage.setId("NORMAL");
        Text tilesLeftNormal = new Text(normalTilesLeft.getValue().toString());
        tilesLeftNormal.setWrappingWidth(normalImage.getFitWidth() * 0.8);
        normalTilePane.getChildren().addAll(normalImage, tilesLeftNormal);

        // Update the text showing the number of normal tiles left when it changes
        normalTilesLeft.addListener((o, oldTilesLeft, newTilesLeft) -> {
            tilesLeftNormal.setText(newTilesLeft.toString());
        });

        // Update the text showing the number of menhir tiles left when it changes
        menhirTilesLeft.addListener((o, oldTilesLeft, newTilesLeft) -> {
            tilesLeftMenhir.setText(newTilesLeft.toString());
        });

        // Update the image of the tile to be placed when it changes
        tile.addListener((o, oldTile, newTile) -> {
            if (newTile != null) {
                tileToPlaceView.setImage(ImageLoader.largeImageForTile(newTile.id()));
            }
        });

        // Bind the visibility of the tile to place text and image to whether the text is empty or not
        ObservableValue<Boolean> isNotEmpty = textToShow.map(s -> !s.isEmpty());
        tileToPlaceText.textProperty().bind(textToShow);
        tileToPlaceText.visibleProperty().bind(isNotEmpty);
        tileToPlaceView.visibleProperty().bind(tileToPlaceText.visibleProperty().not());

        // Add a mouse click event handler to the tile to place text
        tileToPlaceText.setOnMouseClicked(event -> handler.accept(null));

        // Add the normal and menhir tile panes to the tile box
        tileBox.getChildren().addAll(normalTilePane, menhirTilePane);

        // Add the tile box and next tile to the general box
        nextTile.getChildren().addAll(tileToPlaceView, tileToPlaceText);
        generalBox.getChildren().addAll(tileBox, nextTile);

        // Return the general box, which now contains all the elements
        return generalBox;
    }
}