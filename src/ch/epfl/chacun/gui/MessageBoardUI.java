package ch.epfl.chacun.gui;

import ch.epfl.chacun.MessageBoard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;

import static javafx.application.Platform.runLater;

public class MessageBoardUI {
    private MessageBoardUI() {}

    public static Node create(ObservableValue<List<MessageBoard.Message>> observableMessageBoard, ObjectProperty<Set<Integer>> observableTileIds) {
        ScrollPane messageScrollPane = new ScrollPane();
        messageScrollPane.setId("message-board");
        messageScrollPane.getStylesheets().add("message-board.css");
        VBox box = new VBox();

        observableMessageBoard.addListener((o, oldObs, newObs) -> {
            for (int i = oldObs.size(); i < newObs.size(); ++i) {
                MessageBoard.Message currentMessage = newObs.get(i);
                Text text = new Text(currentMessage.text());
                text.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE);
                text.setOnMouseEntered(mouseEvent -> observableTileIds.getValue().addAll(currentMessage.tileIds()));
                text.setOnMouseExited(mouseEvent -> observableTileIds.getValue().removeAll(currentMessage.tileIds()));

                box.getChildren().add(text);
            }
        });

        messageScrollPane.getChildrenUnmodifiable().add(box);
        runLater(() -> messageScrollPane.setVvalue(1));
        return messageScrollPane;
    }
}
