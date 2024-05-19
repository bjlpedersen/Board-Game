package ch.epfl.chacun.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ActionsUI {
    private ActionsUI() {}

    public static Node create(ObservableValue<List<String>> obsActions, Consumer<String> executeAction) {
        HBox textBox = new HBox();
        textBox.getStylesheets().add("actions.css");
        textBox.setId("actions");

        Text text = new Text();
        obsActions.addListener((o, oldActions, newActions) -> {
            text.setText(""); // clear the text
            int start = Math.max(0, newActions.size() - 4);
            for (int i = start; i < newActions.size(); ++i) {
                if (i != newActions.size() - 1) {
                    text.setText(text.getText() + (i + 1) + ":" + newActions.get(i) + ", ");
                } else if (i == newActions.size() - 1 && newActions.size() > 1) {
                    text.setText(text.getText() + (i + 1) + ":" + newActions.get(i));
                } else {
                    text.setText(text.getText() + (i + 1) + ":" + newActions.get(i));

                }
            }
        });

        TextField textField = new TextField();
        textField.setId("action-field");
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getText().toUpperCase();
            for (char c : newText.toCharArray()) {
                if (!allowedCharacters.contains(String.valueOf(c))) {
                    // If the new character is not allowed, remove it from the new text
                    newText = newText.replace(String.valueOf(c), "");
                }
            }
            // Set the new text to the modified text
            change.setText(newText);
            return change;
        }));
        textField.setOnAction(event -> {
            String textInField = textField.getText();
            executeAction.accept(textInField);
            textField.clear();
        });
        textBox.getChildren().addAll(List.of(text, textField));
        return textBox;
    }
}
