package ch.epfl.chacun.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class ActionsUI {
    private static final int MAX_ACTIONS_DISPLAYED = 4;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ActionsUI() {}


    /**
     * Creates a Node containing an HBox with a Text and TextField. The Text displays the actions and the TextField
     * allows the user to input an action to execute. The Text is updated whenever the observable actions change.
     * The TextField only allows certain characters to be inputted and executes an action when the enter key is pressed.
     *
     * @param obsActions An ObservableValue of a List of Strings representing the observable actions.
     * @param executeAction A Consumer of a String representing the action to execute.
     * @return A Node containing an HBox with a Text and TextField.
     */
    public static Node create(ObservableValue<List<String>> obsActions, Consumer<String> executeAction) {
        HBox textBox = new HBox();
        textBox.getStylesheets().add("actions.css");
        textBox.setId("actions");

        Text text = new Text();
        obsActions.addListener((o, oldActions, newActions) -> {
            text.setText(createActionText(newActions));
        });

        TextField textField = createTextField(executeAction);

        textBox.getChildren().addAll(List.of(text, textField));
        return textBox;
    }

    /**
     * Creates a String representing the actions. The String is formatted as "(index + 1):(action), " for all actions
     * except the last one, which is formatted as "(index + 1):(action)".
     *
     * @param actions A List of Strings representing the actions.
     * @return A String representing the actions.
     */
    private static String createActionText(List<String> actions) {
        StringBuilder actionText = new StringBuilder();
        int start = Math.max(0, actions.size() - MAX_ACTIONS_DISPLAYED);
        for (int i = start; i < actions.size(); ++i) {
            if (i != actions.size() - 1) {
                actionText.append(i + 1).append(":").append(actions.get(i)).append(", ");
            } else {
                actionText.append(i + 1).append(":").append(actions.get(i));
            }
        }
        return actionText.toString();
    }

    /**
     * Creates a TextField that only allows certain characters
     * to be inputted and executes an action when the enter key is pressed.
     * The allowed characters are "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".
     * When the enter key is pressed, the text in the TextField is
     * passed to the executeAction Consumer and the TextField is cleared.
     *
     * @param executeAction A Consumer of a String representing the action to execute.
     * @return A TextField that only allows certain characters to be
     * inputted and executes an action when the enter key is pressed.
     */
    private static TextField createTextField(Consumer<String> executeAction) {
        TextField textField = new TextField();
        textField.setId("action-field");
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getText().toUpperCase();
            for (char c : newText.toCharArray()) {
                if (!allowedCharacters.contains(String.valueOf(c))) {
                    newText = newText.replace(String.valueOf(c), "");
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
        return textField;
    }
}
