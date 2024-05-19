package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a user interface for the players in the game.
 * It cannot be instantiated.
 *
 * @author Bjork Pedersen (376143)
 */
public class PlayersUI {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws AssertionError always
     */
    private PlayersUI() {
        throw new AssertionError("PlayersUI class cannot be instantiated");
    }

    /**
     * Creates a new Node for the players UI.
     *
     * @param obsGameState the observable game state
     * @param text the text maker for player names
     * @return a Node representing the players UI
     */
    public static Node create(ObservableValue<GameState> obsGameState, TextMaker text) {
        // Create a VBox to hold all elements
        VBox generalBox = new VBox();
        generalBox.getStylesheets().add("players.css");
        generalBox.setId("players");

        // Create observables for the current player and points
        ObservableValue<PlayerColor> currentPlayer = obsGameState.map(GameState::currentPlayer);
        ObservableValue<Map<PlayerColor, Integer>> points = obsGameState.map(gameState -> gameState.messageBoard().points());

        // Update the points when the game state changes
        obsGameState.addListener((o, oldState, newState) -> {
            points.getValue().clear();
            newState.messageBoard().points().forEach((k, v) -> points.getValue().put(k, v));
        });

        // Create UI elements for each player
        for (PlayerColor player : obsGameState.getValue().players()) {
            // Create a TextFlow for the player
            TextFlow playerTextFlow = new TextFlow();
            playerTextFlow.getStyleClass().add("player");

            // Create a Circle to represent the player
            Circle playerCircle = new Circle(5, ColorMap.fillColor(player));

            // Create a Text to display the player's points
            Text playerText = new Text();
            playerText.textProperty().set(text.playerName(player) + " : " + points.getValue().getOrDefault(player, 0) + "\n");

            // Update the player's points when they change
            points.addListener((o, oldPoints, newPoints) -> {
                playerText.setText(text.playerName(player) + " : " + points.getValue().getOrDefault(player, 0) + "\n");
            });

            // Highlight the current player
            currentPlayer.addListener((o, oldPlayer, newPlayer) -> {
                for (Node textFlow : generalBox.getChildren()) {
                    textFlow.getStyleClass().remove("current");
                }
                playerTextFlow.getStyleClass().add("current");
            });

            // Add the player's Circle and Text to the TextFlow
            playerTextFlow.getChildren().add(playerCircle);
            playerTextFlow.getChildren().add(playerText);

            // Create UI elements for each of the player's occupants
            for (Occupant.Kind o : List.of(Occupant.Kind.HUT, Occupant.Kind.PAWN)) {
                for (int i = 0; i < Occupant.occupantsCount(o); ++i) {
                    Node occ = Icon.newFor(player, o);
                    int finalI = i;

                    // Create an observable for the opacity of the occupant's icon
                    ObservableValue<Double> opacity = obsGameState.map(gameState ->
                            gameState.freeOccupantsCount(player, o) < finalI + 1 ? 0.1 : 1);

                    // Bind the opacity of the occupant's icon to the observable
                    occ.opacityProperty().bind(opacity);

                    // Add the occupant's icon to the TextFlow
                    playerTextFlow.getChildren().add(occ);
                }
                playerTextFlow.getChildren().add(new Text("   "));
            }

            // Add the player's TextFlow to the VBox
            generalBox.getChildren().add(playerTextFlow);
        }

        // Return the VBox, which now contains all the elements
        return generalBox;
    }
}