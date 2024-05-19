package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javafx.application.Platform.runLater;

/**
 * This class provides a user interface for the message board in the game.
 * It cannot be instantiated.
 *
 * @author Bjork Pedersen (376143)
 */
public class MessageBoardUI {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws AssertionError always
     */
    private MessageBoardUI() {
        throw new AssertionError("MessageBoardUI class cannot be instantiated");
    }

    /**
     * Creates a new Node for the message board UI.
     *
     * @param observableMessageBoard the observable message board
     * @param observableTileIds the observable tile IDs
     * @return a Node representing the message board UI
     */
    public static Node create(ObservableValue<List<MessageBoard.Message>> observableMessageBoard, ObjectProperty<Set<Integer>> observableTileIds) {

        // Create a VBox to hold all elements
        VBox box = new VBox();

        // Add a listener to the observable message board
        observableMessageBoard.addListener((o, oldObs, newObs) -> {
            // Clear the box
            box.getChildren().clear();

            // For each message in the observable message board
            for (int i = 0; i < newObs.size(); ++i) {
                MessageBoard.Message currentMessage = newObs.get(i);

                // Create a Text to display the message
                Text text = new Text(currentMessage.text());
                text.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE);

                // Add mouse enter and exit event handlers to the Text
                text.setOnMouseEntered(mouseEvent -> {
                    Set<Integer> updatedObservableTileIds = new HashSet<>(observableTileIds.getValue());
                    updatedObservableTileIds.addAll(currentMessage.tileIds());
                    observableTileIds.set(updatedObservableTileIds);
                });
                text.setOnMouseExited(mouseEvent -> {
                    Set<Integer> updatedObservableTileIds = new HashSet<>(observableTileIds.getValue());
                    updatedObservableTileIds.removeAll(currentMessage.tileIds());
                    observableTileIds.set(updatedObservableTileIds);
                });

                // Add the Text to the box
                box.getChildren().add(text);
            }
        });

        // Create a ScrollPane for the box
        ScrollPane messageScrollPane = new ScrollPane(box);
        messageScrollPane.setId("message-board");
        messageScrollPane.getStylesheets().add("message-board.css");

        // Set the vertical scroll value to 1 (bottom) when the ScrollPane is shown
        runLater(() -> messageScrollPane.setVvalue(1));

        // Return the ScrollPane, which now contains all the elements
        return messageScrollPane;
    }
}