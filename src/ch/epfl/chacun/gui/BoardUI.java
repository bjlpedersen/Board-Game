package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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

import java.util.*;
import java.util.function.Consumer;

/**
 * This class provides a user interface for the board in the game.
 * It cannot be instantiated.
 *
 * @author Bjork Pedersen (376143)
 */
public class BoardUI {
    private BoardUI() {}

    /**
     * Creates the game board UI component.
     *
     * @param reach The reach of the board.
     * @param state The current game state.
     * @param rot The current rotation state.
     * @param visibleOccupants The set of visible occupants.
     * @param highlightedTiles The set of highlighted tiles.
     * @param rotateTile Consumer to handle tile rotation.
     * @param placeTile Consumer to handle tile placement.
     * @param selectOcc Consumer to handle occupant selection.
     * @return The Node representing the game board UI.
     */
    public static Node create(int reach,
                              ObservableValue<GameState> state,
                              SimpleObjectProperty<Rotation> rot,
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

                int finalY = y;
                int finalX = x;
                Pos tilePos = new Pos(finalX, finalY);

                //Create all of the cell properties that handle the mouse clicks/hovering...
                Map<String, BooleanProperty> cellProperties = createCellProperties(cell, rot, rotateTile);                BooleanProperty isHovered = cellProperties.get("isHovered");
                BooleanProperty isLeftMousePressed = cellProperties.get("isLeftMousePressed");
                BooleanProperty isRightMousePressed = cellProperties.get("isRightMousePressed");
                BooleanProperty isOptionPressed = cellProperties.get("isOptionPressed");



                // Manages the background image, rotation and veil color of each cell.
                ObservableValue<Tile> tileToPlace = state.map(GameState::tileToPlace);
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
                                rot,
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
                    ObservableValue<PlacedTile> lastPlacedTile = state.map(g -> g.board().lastPlacedTile());
                    lastPlacedTile.addListener((observableValue, LastPlaced, newLastPlaced) -> {
                        if (state.getValue().board().tileAt(tilePos) != null &&
                                state.getValue().board().tileAt(tilePos).equals(newLastPlaced)) {
                            PlayerColor occupantPlacerColor = newLastPlaced.placer();
                            Map<Occupant.Kind, String> occupantKindToString = Map.
                                    of(Occupant.Kind.PAWN, "pawn_", Occupant.Kind.HUT, "hut_");

                            for (Occupant occ : state.getValue().lastTilePotentialOccupants()) {
                                Node occPath = Icon.newFor(occupantPlacerColor, occ.kind());
                                occPath.setId(occupantKindToString.get(occ.kind()) + occ.zoneId());
                                occPath.visibleProperty().bind(visibleOccupants.map(occSet -> occSet.contains(occ)));
                                occPath.setOnMouseClicked(e -> selectOcc.accept(occ));
                                occPath.rotateProperty().bind(obsCellData.map(c -> c.rotation.negated().degreesCW()));
                                cell.getChildren().add(occPath);
                            }

                            for (Animal a : animalsInTile(newLastPlaced, state)) {
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


                });
                boardGrid.add(cell, x + reach, y + reach);
            }
        }
        ScrollPane boardScrollPane = new ScrollPane(boardGrid);
        boardScrollPane.getStylesheets().add("board.css");
        boardScrollPane.setId("board-scroll-pane");
        boardScrollPane.setVvalue(0.5);
        boardScrollPane.setHvalue(0.5);
        return boardScrollPane;
    }

    /**
     * Creates a map of BooleanProperty instances for a given cell. These properties represent different states of the cell,
     * such as whether it is being hovered over, whether the left or right mouse button is being pressed, and whether the option key is being pressed.
     * This method also handles the logic for rotating the tile when the right mouse button is clicked.
     *
     * @param cell The Group object representing the cell for which to create the properties.
     * @param rot The current rotation state of the game.
     * @param rotateTile A Consumer for handling tile rotation.
     * @return A map where the keys are the names of the properties and the values are the BooleanProperty instances.
     */
    private static Map<String, BooleanProperty> createCellProperties(Group cell, SimpleObjectProperty<Rotation> rot, Consumer<Rotation> rotateTile) {
        Map<String, BooleanProperty> cellProperties = new HashMap<>();

        // Create a BooleanProperty for whether the cell is being hovered over.
        BooleanProperty isHovered = new SimpleBooleanProperty();
        cell.hoverProperty().addListener((observable, oldValue, newValue) -> isHovered.set(newValue));
        cellProperties.put("isHovered", isHovered);

        // Create a BooleanProperty for whether the left mouse button is being pressed.
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
        cellProperties.put("isLeftMousePressed", isLeftMousePressed);

        // Create BooleanProperties for whether the right mouse button is being pressed and whether the option key is being pressed.
        BooleanProperty isOptionPressed = new SimpleBooleanProperty(false);
        BooleanProperty isRightMousePressed = new SimpleBooleanProperty(false);
        cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.SECONDARY && !event.isAltDown()) {
                isRightMousePressed.set(true);
                rotateTile.accept(Rotation.RIGHT);
            } else if (event.getButton() == MouseButton.SECONDARY && event.isAltDown()) {
                isOptionPressed.set(true);
                isRightMousePressed.set(true);
                rotateTile.accept(Rotation.LEFT);
            }
        });
        cell.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                isRightMousePressed.set(false);
                isOptionPressed.set(false);
            }
        });
        cellProperties.put("isOptionPressed", isOptionPressed);
        cellProperties.put("isRightMousePressed", isRightMousePressed);

        return cellProperties;
    }

    /**
     * Returns a list of animals in the given tile.
     *
     * @param tile  The tile to check.
     * @param state The current game state.
     * @return A list of animals in the given tile.
     */
    private static List<Animal> animalsInTile(PlacedTile tile, ObservableValue<GameState> state) {
        List<Animal> result = new ArrayList<>();
        for (Zone.Meadow meadow : tile.meadowZones()) {
            result.addAll(Area.animals(state.getValue().board().meadowArea(meadow), Set.of()));
        }
        return result;
    }

    /**
     * This class represents the data for a cell on the game board.
     * It contains the background image, rotation, and veil color for the cell.
     *
     * @author Bjork Pedersen (376143)
     */
    public static class CellData {
        private ImageView backgroundImage;
        private Rotation rotation;
        private ColorInput veilColor;

        /**
         * Constructs a new CellData object with the given background image, rotation, and veil color.
         *
         * @param backgroundImage the background image for the cell
         * @param rotation the rotation of the cell
         * @param veilColor the veil color for the cell
         */
        public CellData(ImageView backgroundImage, Rotation rotation, ColorInput veilColor) {
            this.backgroundImage = backgroundImage;
            this.rotation = rotation;
            this.veilColor = veilColor;
        }

        /**
         * Binds the cell data to the given properties and observable values.
         * This method handles the logic for updating the cell's image, rotation, and veil color based on the game
         * state and user interactions.
         *
         * @param pos the position of the cell on the board
         * @param leftMouseClicked a property that is true when the left mouse button is pressed
         * @param rightMouseClicked a property that is true when the right mouse button is pressed
         * @param optionClicked a property that is true when the option key is pressed
         * @param isHovered a property that is true when the mouse is hovering over the cell
         * @param highlightedTiles an observable value containing the set of highlighted tile IDs
         * @param state an observable value containing the current game state
         * @param rotateTile a consumer for handling tile rotation
         * @param placeTile a consumer for handling tile placement
         * @return a new CellData object with the updated properties
         */
        public CellData bindCellData(Pos pos,
                                     BooleanProperty leftMouseClicked,
                                     BooleanProperty rightMouseClicked,
                                     BooleanProperty optionClicked,
                                     BooleanProperty isHovered,
                                     ObservableValue<Set<Integer>> highlightedTiles,
                                     SimpleObjectProperty<Rotation> rot,
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
                rotation = rot.getValue();
                ColorInput veil = null ;
                Set<Pos> insertionPositions = gameState.board().insertionPositions();
                if (insertionPositions.contains(pos)) {
                    if (gameState.nextAction() == GameState.Action.PLACE_TILE && isHovered.get()) {
                        ImageView tileImage = new ImageView(ImageLoader.normalImageForTile(tileToPlace.id()));

                        if (optionClicked.get() && rightMouseClicked.get()) {
                            rotation = rotation.add(Rotation.LEFT);
                        } else if (!optionClicked.get() && rightMouseClicked.get()) {
                            rotation = rotation.add(Rotation.RIGHT);
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
                            rot.set(Rotation.NONE);
                        }
                        return new CellData(tileImage, rotation, veil);
                    } else if (gameState.nextAction() == GameState.Action.PLACE_TILE) {
                        veil = new ColorInput(
                                0,
                                0,
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
                            0,
                            0,
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