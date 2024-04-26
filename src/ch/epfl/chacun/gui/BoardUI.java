package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class BoardUI {
    private BoardUI() {}

    public static Node create(int reach,
                              ObservableValue<GameState> state,
                              ObservableValue<Rotation> rot,
                              ObservableValue<Set<Occupant>> visibleOccupants,
                              ObservableValue<Set<Integer>> highlightedTiles,
                              Consumer<Rotation> rotateTile,
                              Consumer<Pos> placeTile,
                              Consumer<Occupant> selectOcc) {


        GridPane boardGrid = new GridPane();
        boardGrid.setId("board.grid");

        List<Image> tilesPlaced = new ArrayList<>();

        for (int x = -reach; x <= reach; ++x) {
            for (int y = -reach; y <= reach; ++y) {
                Group tile = new Group();

                WritableImage emptyTileImage = new WritableImage(1, 1);
                emptyTileImage.getPixelWriter().setColor(0, 0, Color.gray(0.98));
                tile.getChildren().add(new ImageView(emptyTileImage));

                ObservableValue<PlacedTile> obsTile = state.map(gameState -> gameState.board().lastPlacedTile());

                obsTile.addListener((o, oldTile, newTile) -> {
                    if (oldTile == null && newTile != null) {
                        tile.getChildren().removeFirst();
                        tile.getChildren().add(new ImageView(ImageLoader.normalImageForTile(newTile.id())));
                        tilesPlaced.add(ImageLoader.normalImageForTile(newTile.id()));
                        for (Animal a : state.getValue().board().cancelledAnimals()) {
                            ImageView crossedAnimal = new ImageView("marker_" + a.id());
                            tile.getChildren().add(crossedAnimal);
                        }
                        Map<Occupant.Kind, String> occKindToString = Map.of(
                                Occupant.Kind.PAWN, "pawn",
                                Occupant.Kind.HUT, "hut");
                        for (Occupant occ : newTile.potentialOccupants()) {
                            ImageView occImage = new ImageView(STR."\{occKindToString.get(occ.kind())}_\{occ.zoneId()}");
                            ObservableValue<Integer> rotation = state.map(gameState -> {
                                switch (gameState.board().lastPlacedTile().rotation()) {
                                    case Rotation.RIGHT -> {return 270;}
                                    case Rotation.LEFT -> {return 90;}
                                    case NONE -> {return 180;}
                                    default -> {return 0;}
                                }
                            });
                            occImage.rotateProperty().bind(rotation);
                            occImage.setOnMouseClicked(event -> selectOcc.accept(occ));
                            tile.getChildren().add(occImage);
                        }
                    }
                });
                boardGrid.getChildren().add(tile);
            }
        }
        ScrollPane boardScrollPane = new ScrollPane(boardGrid);
        boardScrollPane.getStylesheets().add("board.css");
        boardScrollPane.setId("board-scroll-pane");
        return boardScrollPane;
    }
}
