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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BoardUI2 {
    private BoardUI2() {}

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

                // Add right mouse pressed and released listeners to the cell
                BooleanProperty isRightMousePressed = new SimpleBooleanProperty(false);
                cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        isRightMousePressed.set(true);
                    }
                });
                cell.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        isRightMousePressed.set(false);
                    }
                });
                // Check if shift is pressed
                BooleanProperty isShiftPressed = new SimpleBooleanProperty(false);
                cell.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.SHIFT) {
                        isShiftPressed.set(true);
                    }
                });
                cell.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                    if (event.getCode() == KeyCode.SHIFT) {
                        isShiftPressed.set(false);
                    }
                });

                // Manages the background image, rotation and veil color of each cell.
                ObservableValue<Tile> tileToPlace = state.map(GameState::tileToPlace);
                int finalY = y;
                int finalX = x;
                tileToPlace.addListener((o, oldVal, newVal) -> {

                    CellData cellData = new CellData(emptyTileImage, rot.getValue(), null);
                    ObservableValue<CellData> obsCellData = Bindings.createObjectBinding(() -> {
                        return cellData.bindCellData(
                                new Pos(finalX, finalY),
                                isLeftMousePressed,
                                isRightMousePressed,
                                isShiftPressed,
                                isHovered,
                                highlightedTiles,
                                state,
                                rotateTile,
                                placeTile);
                    }, isLeftMousePressed, isRightMousePressed, isShiftPressed, isHovered, highlightedTiles, state);

                    cellImage.imageProperty().bind(obsCellData.map(c -> c.backgroundImage.getImage()));

//                    DoubleProperty oldRotation = new SimpleDoubleProperty();
//                    cellImage.rotateProperty().bind(obsCellData.map(c -> {
//                        double totalRotation = (oldRotation.get() + c.rotation.degreesCW()) % 360;
//                        oldRotation.set(totalRotation);
//                        rot.getValue().add(c.rotation);
////                        cellData.rotation.add(cellData.rotation.negated());
////                        cellData.rotation.add(angleToRotation((int) totalRotation));
//                        return totalRotation;
//                    }));


                    cellImage.rotateProperty().bind(obsCellData.map(c -> c.rotation().degreesCW() + rot.getValue().degreesCW()));

                    cellImage.effectProperty().bind(obsCellData.map(c -> {
                        Blend blend = new Blend(BlendMode.SRC_OVER, null, c.veilColor);
                        blend.setOpacity(0.5);
                        return blend;
                    }));
                });


                // Manages the canceled animal markers and the occupant Images of each cell if the cell contains a tile.
                placedTile.addListener((o, oldVal, newVal) -> {
                    if (state.getValue().board().tileAt(new Pos(finalX, finalY)) != null) {
                        for (Occupant occ : newVal.potentialOccupants()) {
                            if (occ.kind() == Occupant.Kind.HUT) {
                                Node occPath = Icon.newFor(state.getValue().currentPlayer(), occ.kind());
                                occPath.setId(STR. "hut_\{ occ.zoneId() }" );
                                occPath.visibleProperty().bind(visibleOccupants.map(occSet -> occSet.contains(occ)));
                                occPath.setOnMouseClicked(e -> selectOcc.accept(occ));
                                cell.getChildren().add(occPath);
                            } else {
                                Node occPath = Icon.newFor(state.getValue().currentPlayer(), occ.kind());
                                occPath.setId(STR. "pawn_\{ occ.zoneId() }" );
                                occPath.visibleProperty().bind(visibleOccupants.map(occSet -> occSet.contains(occ)));
                                occPath.setOnMouseClicked(e -> selectOcc.accept(occ));
                                cell.getChildren().add(occPath);
                            }
                        }

                        for (Animal a : animalsInTile(newVal, state)) {
                            ImageView crossedAnimal = new ImageView();
                            crossedAnimal.setFitWidth(ImageLoader.MARKER_FIT_SIZE);
                            crossedAnimal.setFitHeight(ImageLoader.MARKER_FIT_SIZE);
                            crossedAnimal.setId(STR. "marker_\{ a.id() }" );
                            crossedAnimal.getStyleClass().add("marker");
                            crossedAnimal.visibleProperty().bind(state.map(s -> s.board().cancelledAnimals().contains(a)));
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
        return boardScrollPane;
    }

    private static List<Animal> animalsInTile(PlacedTile tile, ObservableValue<GameState> state) {
        List<Animal> result = new ArrayList<>();
        for (Zone.Meadow meadow : tile.meadowZones()) {
            result.addAll(Area.animals(state.getValue().board().meadowArea(meadow), Set.of()));
        }
        return result;
    }

    private static Rotation angleToRotation(int angle) throws IllegalArgumentException{
        switch (angle) {
            case 0: return Rotation.NONE;
            case 90: return Rotation.RIGHT;
            case 180: return Rotation.HALF_TURN;
            case 270: return Rotation.LEFT;
            default: throw new IllegalArgumentException();
        }
    }


    private record CellData(ImageView backgroundImage, Rotation rotation, ColorInput veilColor) {

        public CellData bindCellData(Pos pos,
                                     BooleanProperty leftMouseClicked,
                                     BooleanProperty rightMouseClicked,
                                     BooleanProperty shiftClicked,
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
            PlacedTile placedTile = gameState.board().tileAt(pos);

            if (gameState.board().tileAt(pos) == null) {
                ColorInput veil = null ;
                Rotation rotation1 = rotation;
                Set<Pos> insertionPositions = gameState.board().insertionPositions();
                if (insertionPositions.contains(pos)) {
                    if (gameState.nextAction() == GameState.Action.PLACE_TILE && isHovered.get()) {
                        ImageView tileImage = new ImageView(ImageLoader.normalImageForTile(tileToPlace.id()));

                        if (shiftClicked.get() && rightMouseClicked.get()) {
                            rotation1 = rotation1.add(Rotation.LEFT);
                            rotateTile.accept(Rotation.LEFT);
                        } else if (rightMouseClicked.get()) {
                            rotation1 = rotation1.add(Rotation.RIGHT);
                            rotateTile.accept(Rotation.RIGHT);
                        }

                        if (!gameState.board().canAddTile(new PlacedTile(tileToPlace, gameState.currentPlayer(), rotation1, pos))) {
                            veil = new ColorInput(
                                    pos.x(),
                                    pos.y(),
                                    ImageLoader.NORMAL_TILE_FIT_SIZE,
                                    ImageLoader.NORMAL_TILE_FIT_SIZE,
                                    Color.WHITE);
                        }
                        if (gameState.board().canAddTile(new PlacedTile(tileToPlace, gameState.currentPlayer(), rotation1, pos)) &&
                                leftMouseClicked.get()) {
                            placeTile.accept(pos);
                        }
                        return new CellData(tileImage, rotation1, veil);
                    } else {
                        veil = new ColorInput(
                                pos.x(),
                                pos.y(),
                                ImageLoader.NORMAL_TILE_FIT_SIZE,
                                ImageLoader.NORMAL_TILE_FIT_SIZE,
                                ColorMap.fillColor(gameState.currentPlayer())); //TODO check if this line causes any problems
                        return new CellData(emptyTileImage, rotation1, veil);
                    }
                }
            } else {
                ImageView tileImage = new ImageView(ImageLoader.normalImageForTile(placedTile.id()));

                Rotation rotation = placedTile.rotation();
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
