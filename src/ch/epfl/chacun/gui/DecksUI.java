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

public class DecksUI {
    private DecksUI() {}

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
        tileToPlaceView.setFitHeight(ImageLoader.LARGE_TILE_FIT_SIZE);
        tileToPlaceView.setFitWidth(ImageLoader.LARGE_TILE_FIT_SIZE);

        if (tile.getValue() != null) {
            tileToPlaceView.setImage(ImageLoader.largeImageForTile(tile.getValue().id()));
        }
        Text tileToPlaceText = new Text(textToShow.getValue());

        HBox tileBox = new HBox();
        tileBox.setId("decks");

        //Menhir tiles initialisation
        StackPane menhirTilePane = new StackPane();
        ImageView menhirImage = new ImageView();
        menhirImage.setFitWidth(menhirImage.getFitWidth() / 2);
        menhirImage.setFitHeight(menhirImage.getFitHeight() / 2);
        menhirImage.setId("MENHIR");
        Text tilesLeftMenhir = new Text(menhirTilesLeft.getValue().toString());
        tilesLeftMenhir.setWrappingWidth(menhirImage.getFitWidth() * 0.8);
        menhirTilePane.getChildren().addAll(menhirImage, tilesLeftMenhir);

        //Normal tiles initialisation
        StackPane normalTilePane = new StackPane();
        ImageView normalImage = new ImageView();
        normalImage.setFitWidth(normalImage.getFitWidth() / 2);
        normalImage.setFitHeight(normalImage.getFitHeight() / 2);
        normalImage.setId("NORMAL");
        Text tilesLeftNormal = new Text(normalTilesLeft.getValue().toString());
        tilesLeftNormal.setWrappingWidth(normalImage.getFitWidth() * 0.8);
        normalTilePane.getChildren().addAll(normalImage, tilesLeftNormal);

        normalTilesLeft.addListener((o, oldTilesLeft, newTilesLeft) -> {
            tilesLeftNormal.setText(newTilesLeft.toString());
        });

        menhirTilesLeft.addListener((o, oldTilesLeft, newTilesLeft) -> {
            tilesLeftMenhir.setText(newTilesLeft.toString());
        });

        ObservableValue<Boolean> isNotEmpty = textToShow.map(s -> !s.isEmpty());
        tileToPlaceText.textProperty().bind(textToShow);
        tileToPlaceText.visibleProperty().bind(isNotEmpty);
        tileToPlaceView.visibleProperty().bind(tileToPlaceText.visibleProperty().not());

        tileToPlaceText.setOnMouseClicked(event -> handler.accept(null));

        tileBox.getChildren().addAll(normalTilePane, menhirTilePane);
        nextTile.getChildren().addAll(tileToPlaceView, tileToPlaceText);
        generalBox.getChildren().addAll(tileBox, nextTile);
        return generalBox;
    }
}
