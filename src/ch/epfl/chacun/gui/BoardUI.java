package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
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
        boardGrid.setId("board-grid");

        for (int x = -reach; x <= reach; ++x) {
            for (int y = -reach; y <= reach; ++y) {

                Group cell = new Group();
                ImageView cellImage = new ImageView();
                cellImage.setFitWidth(ImageLoader.NORMAL_TILE_FIT_SIZE);
                cellImage.setFitHeight(ImageLoader.NORMAL_TILE_FIT_SIZE);
                cell.getChildren().add(cellImage);

                WritableImage emptyTile = new WritableImage(ImageLoader.NORMAL_TILE_FIT_SIZE, ImageLoader.NORMAL_TILE_FIT_SIZE);
                emptyTile.getPixelWriter().setColor(0, 0, Color.gray(0.98));
                ImageView emptyTileImage = new ImageView(emptyTile);

                ObservableValue<PlacedTile> placedTile = state.map(s -> s.board().lastPlacedTile());


                // Add hover property listeners to the cell
                BooleanProperty isHovered = new SimpleBooleanProperty();
                cell.hoverProperty().addListener((observable, oldValue, newValue) -> isHovered.set(newValue));

                // Add left mouse pressed and released listeners to the cell
                BooleanProperty isLeftMousePressed = new SimpleBooleanProperty(false);
                cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        isLeftMousePressed.set(true);
                    }
                });
                cell.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        isLeftMousePressed.set(false);
                    }
                });

                // Add right mouse pressed and released listeners to the cell (with alt pressed too)
                BooleanProperty isOptionPressed = new SimpleBooleanProperty(false);
                BooleanProperty isRightMousePressed = new SimpleBooleanProperty(false);
                cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY && !event.isAltDown()) {
                        isRightMousePressed.set(true);
                    } else if (event.getButton() == MouseButton.SECONDARY && event.isAltDown()) {
                        isOptionPressed.set(true);
                        isRightMousePressed.set(true);
                    }
                });
                cell.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        isRightMousePressed.set(false);
                        isOptionPressed.set(false);
                    }
                });

                // Manages the background image, rotation and veil color of each cell.
                ObservableValue<Tile> tileToPlace = state.map(GameState::tileToPlace);
                int finalY = y;
                int finalX = x;
                Pos tilePos = new Pos(finalX, finalY);
                tileToPlace.addListener((o, oldVal, newVal) -> {

                    CellData cellData = new CellData(emptyTileImage, rot.getValue(), null);
                    ObservableValue<CellData> obsCellData = Bindings.createObjectBinding(() -> {
                        return cellData.bindCellData(
                                tilePos,
                                isLeftMousePressed,
                                isRightMousePressed,
                                isOptionPressed,
                                isHovered,
                                highlightedTiles,
                                state,
                                rotateTile,
                                placeTile);
                    }, isLeftMousePressed, isRightMousePressed, isOptionPressed, isHovered, highlightedTiles, state);

                    cellImage.imageProperty().bind(obsCellData.map(c -> c.backgroundImage.getImage()));
                    cell.rotateProperty().bind(obsCellData.map(c -> c.rotation.degreesCW()));
                    cellImage.effectProperty().bind(obsCellData.map(c -> {
                        Blend blend = new Blend(BlendMode.SRC_OVER, null, c.veilColor);
                        blend.setOpacity(0.5);
                        return blend;
                    }));

                    // Manages the canceled animal markers and the occupant Images of each cell if the cell contains a tile.
                    if (state.getValue().board().tileAt(tilePos) != null) {
                        PlacedTile placedTile1 = state.getValue().board().tileAt(tilePos);
                        PlayerColor occupantPlacerColor = state.getValue().board().tileAt(tilePos).placer();
                        for (Occupant occ : placedTile1.potentialOccupants()) {
                            if (occ.kind() == Occupant.Kind.HUT) {
                                Node occPath = Icon.newFor(occupantPlacerColor, occ.kind());
                                occPath.setId(STR. "hut_\{ occ.zoneId() }" );
                                occPath.visibleProperty().bind(visibleOccupants.map(occSet -> occSet.contains(occ)));
                                occPath.setOnMouseClicked(e -> selectOcc.accept(occ));
                                occPath.rotateProperty().bind(obsCellData.map(c -> c.rotation.negated().degreesCW()));
                                cell.getChildren().add(occPath);
                            } else {
                                Node occPath = Icon.newFor(occupantPlacerColor, occ.kind());
                                occPath.setId(STR. "pawn_\{ occ.zoneId() }" );
                                occPath.visibleProperty().bind(visibleOccupants.map(occSet -> occSet.contains(occ)));
                                occPath.setOnMouseClicked(e -> selectOcc.accept(occ));
                                occPath.rotateProperty().bind(obsCellData.map(c -> c.rotation.negated().degreesCW()));
                                cell.getChildren().add(occPath);
                            }
                        }

                        for (Animal a : animalsInTile(placedTile1, state)) {
                            ImageView crossedAnimal = new ImageView();
                            crossedAnimal.setFitWidth(ImageLoader.MARKER_FIT_SIZE);
                            crossedAnimal.setFitHeight(ImageLoader.MARKER_FIT_SIZE);
                            crossedAnimal.setId(STR. "marker_\{ a.id() }" );
                            crossedAnimal.getStyleClass().add("marker");
                            crossedAnimal.visibleProperty().bind(state.map(s -> s.board().cancelledAnimals().contains(a)));
                            crossedAnimal.rotateProperty().bind(obsCellData.map(c -> c.rotation.degreesCW()));
                            cell.getChildren().add(crossedAnimal);
                        }
                    }


                });


                boardGrid.add(cell, x + reach, y + reach);
            }
        }
        ScrollPane boardScrollPane = new ScrollPane(boardGrid);
        boardScrollPane.getStylesheets().add("board.css");
        boardScrollPane.setId("board-scroll-pane");
        boardScrollPane.setVvalue(0);
        boardScrollPane.setHvalue(0);
        return boardScrollPane;
    }

    private static List<Animal> animalsInTile(PlacedTile tile, ObservableValue<GameState> state) {
        List<Animal> result = new ArrayList<>();
        for (Zone.Meadow meadow : tile.meadowZones()) {
            result.addAll(Area.animals(state.getValue().board().meadowArea(meadow), Set.of()));
        }
        return result;
    }

    public static class CellData {
        private ImageView backgroundImage;
        private Rotation rotation;
        private ColorInput veilColor;

        public CellData(ImageView backgroundImage, Rotation rotation, ColorInput veilColor) {
            this.backgroundImage = backgroundImage;
            this.rotation = rotation;
            this.veilColor = veilColor;
        }

        public Rotation getRotation() {
            return rotation;
        }

        public void setRotation(Rotation rotation) {
            this.rotation = rotation;
        }

        public CellData bindCellData(Pos pos,
                                     BooleanProperty leftMouseClicked,
                                     BooleanProperty rightMouseClicked,
                                     BooleanProperty optionClicked,
                                     BooleanProperty isHovered,
                                     ObservableValue<Set<Integer>> highlightedTiles,
                                     ObservableValue<GameState> state,
                                     Consumer<Rotation> rotateTile,
                                     Consumer<Pos> placeTile
        ) {

            WritableImage emptyTile = new WritableImage(1, 1);
            emptyTile.getPixelWriter().setColor(0, 0, Color.gray(0.98));
            ImageView emptyTileImage = new ImageView(emptyTile);
            GameState gameState = state.getValue();
            Tile tileToPlace = state.getValue().tileToPlace();

            if (gameState.board().tileAt(pos) == null) {
                ColorInput veil = null ;
                Set<Pos> insertionPositions = gameState.board().insertionPositions();
                if (insertionPositions.contains(pos)) {
                    if (gameState.nextAction() == GameState.Action.PLACE_TILE && isHovered.get()) {
                        ImageView tileImage = new ImageView(ImageLoader.normalImageForTile(tileToPlace.id()));

                        if (optionClicked.get() && rightMouseClicked.get()) {
                            rotation = rotation.add(Rotation.LEFT);
                            rotateTile.accept(Rotation.LEFT);
                        } else if (!optionClicked.get() && rightMouseClicked.get()) {
                            rotation = rotation.add(Rotation.RIGHT);
                            rotateTile.accept(Rotation.RIGHT);
                        }

                        if (!gameState.board().canAddTile(new PlacedTile(tileToPlace, gameState.currentPlayer(), rotation, pos)) &&
                        gameState.nextAction() == GameState.Action.PLACE_TILE) {
                            veil = new ColorInput(
                                    pos.x(),
                                    pos.y(),
                                    ImageLoader.NORMAL_TILE_FIT_SIZE,
                                    ImageLoader.NORMAL_TILE_FIT_SIZE,
                                    Color.WHITE);
                        }
                        if (gameState.board().canAddTile(new PlacedTile(tileToPlace, gameState.currentPlayer(), rotation, pos)) &&
                                leftMouseClicked.get()) {
                            placeTile.accept(pos);
                        }
                        return new CellData(tileImage, rotation, veil);
                    } else if (gameState.nextAction() == GameState.Action.PLACE_TILE) {
                        veil = new ColorInput(
                                pos.x(),
                                pos.y(),
                                ImageLoader.NORMAL_TILE_FIT_SIZE,
                                ImageLoader.NORMAL_TILE_FIT_SIZE,
                                ColorMap.fillColor(gameState.currentPlayer())); //TODO check if this line causes any problems
                        return new CellData(emptyTileImage, rotation, veil);
                    }
                }
            } else {
                PlacedTile placedTile = gameState.board().tileAt(pos);
                ImageView tileImage = new ImageView(ImageLoader.normalImageForTile(placedTile.id()));

                rotation = placedTile.rotation();
                ColorInput veil = null;

                if (!highlightedTiles.getValue().contains(placedTile.id()) && !highlightedTiles.getValue().isEmpty()) {
                    veil = new ColorInput(
                            pos.x(),
                            pos.y(),
                            ImageLoader.NORMAL_TILE_FIT_SIZE,
                            ImageLoader.NORMAL_TILE_FIT_SIZE,
                            Color.BLACK);
                }
                return new CellData(tileImage, rotation, veil);
            }
            return new CellData(emptyTileImage, rotation, null);
        }
    }
}
