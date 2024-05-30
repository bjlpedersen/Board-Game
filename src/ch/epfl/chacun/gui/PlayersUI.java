package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Map;

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
     * @param text         the text maker for player names
     * @return a Node representing the players UI
     */
    public static Node create(ObservableValue<GameState> obsGameState, TextMaker text) {
        VBox generalBox = new VBox();
        generalBox.getStylesheets().add("players.css");
        generalBox.setId("players");

        ObservableValue<PlayerColor> currentPlayer = obsGameState.map(GameState::currentPlayer);
        ObservableValue<Map<PlayerColor, Integer>> points = obsGameState.
                map(gameState -> gameState.messageBoard().points());

        obsGameState.addListener((o, oldState, newState) -> {
            points.getValue().clear();
            newState.messageBoard().points().forEach((k, v) -> points.getValue().put(k, v));
        });

        for (PlayerColor player : obsGameState.getValue().players()) {
            TextFlow playerTextFlow = createPlayerTextFlow(
                    player,
                    points,
                    text,
                    currentPlayer,
                    obsGameState,
                    generalBox);
            generalBox.getChildren().add(playerTextFlow);
        }

        return generalBox;
    }

    /**
     * Creates a TextFlow for a player. The TextFlow includes a circle representing the player's color,
     * the player's current points, and icons for the player's occupants.
     *
     * @param player        The player for whom the TextFlow is being created.
     * @param points        An ObservableValue of a Map that maps each PlayerColor to their current points.
     * @param text          A TextMaker for creating the text for the player's points.
     * @param currentPlayer An ObservableValue of the current player.
     * @param obsGameState  An ObservableValue of the current game state.
     * @param generalBox    The VBox that contains the TextFlows for all players.
     * @return A TextFlow for the player.
     */
    private static TextFlow createPlayerTextFlow(PlayerColor player,
                                                 ObservableValue<Map<PlayerColor,
                                                         Integer>> points,
                                                 TextMaker text,
                                                 ObservableValue<PlayerColor> currentPlayer,
                                                 ObservableValue<GameState> obsGameState,
                                                 VBox generalBox) {
        TextFlow playerTextFlow = new TextFlow();
        playerTextFlow.getStyleClass().add("player");

        Circle playerCircle = new Circle(5, ColorMap.fillColor(player));
        Text playerText = createPlayerText(player, points, text);

        // Keep track of the previous currentPlayer
        final PlayerColor[] previousPlayer = {null};

        currentPlayer.addListener((o, oldPlayer, newPlayer) -> {
            // Remove the 'current' style class from the previous currentPlayer
            if (previousPlayer[0] != null) {
                for (Node textFlow : generalBox.getChildren()) {
                    if (textFlow.getUserData() == previousPlayer[0]) {
                        textFlow.getStyleClass().remove("current");
                    }
                }
            }

            // Add the 'current' style class to the new currentPlayer
            for (Node textFlow : generalBox.getChildren()) {
                if (textFlow.getUserData() == newPlayer) {
                    textFlow.getStyleClass().add("current");
                }
            }

            // Update the previous currentPlayer
            previousPlayer[0] = newPlayer;
        });

        playerTextFlow.getChildren().add(playerCircle);
        playerTextFlow.getChildren().add(playerText);

        createOccupantIcons(player, obsGameState, playerTextFlow);

        // Store the player in the userData of the TextFlow
        playerTextFlow.setUserData(player);

        return playerTextFlow;
    }

    /**
     * Creates a Text for a player's points.
     *
     * @param player The player for whom the Text is being created.
     * @param points An ObservableValue of a Map that maps each PlayerColor to their current points.
     * @param text   A TextMaker for creating the text for the player's points.
     * @return A Text for the player's points.
     */
    private static Text createPlayerText(
            PlayerColor player,
            ObservableValue<Map<PlayerColor,
                    Integer>> points,
            TextMaker text) {
        Text playerText = new Text();
        playerText.textProperty().set(text.playerName(player) + " : " +
                points.getValue().getOrDefault(player, 0) + "\n");

        points.addListener((o, oldPoints, newPoints) -> {
            playerText.setText(text.playerName(player) + " : " +
                    points.getValue().getOrDefault(player, 0) + "\n");
        });

        return playerText;
    }

    /**
     * Creates icons for a player's occupants and adds them to a TextFlow.
     *
     * @param player         The player for whom the icons are being created.
     * @param obsGameState   An ObservableValue of the current game state.
     * @param playerTextFlow The TextFlow to which the icons are added.
     */
    private static void createOccupantIcons(
            PlayerColor player,
            ObservableValue<GameState> obsGameState,
            TextFlow playerTextFlow) {
        for (Occupant.Kind o : List.of(Occupant.Kind.HUT, Occupant.Kind.PAWN)) {
            for (int i = 0; i < Occupant.occupantsCount(o); ++i) {
                Node occ = Icon.newFor(player, o);
                int finalI = i;

                ObservableValue<Double> opacity = obsGameState.map(gameState ->
                        gameState.freeOccupantsCount(player, o) < finalI + 1 ? 0.1 : 1);

                occ.opacityProperty().bind(opacity);

                playerTextFlow.getChildren().add(occ);
            }
            playerTextFlow.getChildren().add(new Text("   "));
        }
    }
}
