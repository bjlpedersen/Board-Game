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

public class MessageBoardUI {
    private MessageBoardUI() {}

    public static Node create(ObservableValue<List<MessageBoard.Message>> observableMessageBoard, ObjectProperty<Set<Integer>> observableTileIds) {

        VBox box = new VBox();

        observableMessageBoard.addListener((o, oldObs, newObs) -> {
            for (int i = oldObs.size(); i < newObs.size(); ++i) {
                MessageBoard.Message currentMessage = newObs.get(i);
                Text text = new Text(currentMessage.text());
                text.setWrappingWidth(ImageLoader.LARGE_TILE_FIT_SIZE);
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

                box.getChildren().add(text);
            }
        });

        ScrollPane messageScrollPane = new ScrollPane(box);
        messageScrollPane.setId("message-board");
        messageScrollPane.getStylesheets().add("message-board.css");
        runLater(() -> messageScrollPane.setVvalue(1));
        return messageScrollPane;
    }
}
