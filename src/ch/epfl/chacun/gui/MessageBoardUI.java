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
    private static final double TEXT_WRAPPING_WIDTH = ImageLoader.LARGE_TILE_FIT_SIZE;


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
    public static Node create(ObservableValue<List<MessageBoard.Message>> observableMessageBoard,
                              ObjectProperty<Set<Integer>> observableTileIds) {
        VBox box = new VBox();
        observableMessageBoard.addListener((o, oldObs, newObs) -> {
            box.getChildren().clear();
            for (int i = 0; i < newObs.size(); ++i) {
                MessageBoard.Message currentMessage = newObs.get(i);
                Text text = createMessageText(currentMessage, observableTileIds);
                box.getChildren().add(text);
            }
        });

        return createScrollPane(box);
    }

    /**
     * Creates a Text for a message on the message board. The Text includes the message's text and event handlers
     * for when the mouse enters and exits the Text. When the mouse enters the Text, the message's tile IDs are added
     * to the observable tile IDs. When the mouse exits the Text, the message's tile IDs are removed from the observable tile IDs.
     *
     * @param currentMessage The message for which the Text is being created.
     * @param observableTileIds An ObjectProperty of a Set of Integers representing the observable tile IDs.
     * @return A Text for the message.
     */
    private static Text createMessageText(MessageBoard.Message currentMessage, ObjectProperty<Set<Integer>> observableTileIds) {
        Text text = new Text(currentMessage.text());
        text.setWrappingWidth(TEXT_WRAPPING_WIDTH);
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
        return text;
    }

/**
 * Creates a ScrollPane for a VBox. The ScrollPane includes the VBox and has the ID "message-board". It also has
 * the stylesheet "message-board.css". After the ScrollPane is created, its vertical scroll value is set to 1.
 *
 * @param box The VBox for which the ScrollPane is being created.
 * @return A ScrollPane for the VBox.
 */
private static ScrollPane createScrollPane(VBox box) {
    ScrollPane messageScrollPane = new ScrollPane(box);
    messageScrollPane.setId("message-board");
    messageScrollPane.getStylesheets().add("message-board.css");
    runLater(() -> messageScrollPane.setVvalue(1));
    return messageScrollPane;
}
}