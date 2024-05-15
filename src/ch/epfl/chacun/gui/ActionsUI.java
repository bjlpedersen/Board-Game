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

        List<String> actions = obsActions.getValue();
        Text text = new Text();
        int start = Math.max(0, actions.size() - 4);
        for (int i = start; i < actions.size(); ++i) {
            if (i != actions.size() - 1) {
                text.setText(text.getText() + STR. "\{ i }:\{ actions.get(i) }, " );
            } else {
                text.setText(text.getText() + STR. "\{ i }:\{ actions.get(i) }" );
            }
        }

        TextField textField = new TextField();
        textField.setId("action-field");
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText().toUpperCase();
            for (char c : newText.toCharArray()) {
                if (!allowedCharacters.contains(String.valueOf(c))) {
                    return null;
                }
            }
            change.setText(newText);
            return change;
        }));
        textField.setOnAction(event -> {
            String textInField = textField.getText();
            executeAction.accept(textInField);
            textField.clear();
        });
        textBox.getChildren().addAll(Set.of(text, textField));
        return textBox;
    }
}
