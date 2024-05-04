package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;
import java.util.Set;

public class PlayersUI {
    private PlayersUI() {}

    public static Node create(ObservableValue<GameState> obsGameState, TextMaker text) {
        VBox generalBox = new VBox();
        generalBox.getStylesheets().add("players.css");
        generalBox.setId("players");

        ObservableValue<PlayerColor> currentPlayer = obsGameState.map(GameState::currentPlayer);
        ObservableValue<Map<PlayerColor, Integer>> points = obsGameState.map(gameState -> gameState.messageBoard().points());

        for (PlayerColor player : obsGameState.getValue().players()) {
            TextFlow occupant = new TextFlow();
            occupant.getStyleClass().add("player");
            Circle playerCircle = new Circle(5, ColorMap.fillColor(player));
            Text playerText = new Text();

            ObservableValue<String> pointsText = points.map( playerColor -> {
                return STR." \{text.playerName(player)} : \{points.getValue().getOrDefault(player, 0)} \n";
            });
            playerText.textProperty().bind(pointsText);

            currentPlayer.addListener((o, oldPlayer, newPlayer) -> {
                for (Node textFlow : generalBox.getChildren()) {
                    textFlow.getStyleClass().remove("current");
                }
                occupant.getStyleClass().add("current");
            });
            occupant.getChildren().add(playerCircle);
            occupant.getChildren().add(playerText);

            for (Occupant.Kind o : Set.of(Occupant.Kind.HUT, Occupant.Kind.PAWN)) {
                for (int i = 0; i < Occupant.occupantsCount(o); ++i) {
                    Node occ = Icon.newFor(player, o);
                    int finalI = i;
                    ObservableValue<Double> opacity = obsGameState.map(gameState ->
                        gameState.freeOccupantsCount(player, o) < finalI ? 0.1 : 1);

                    occ.opacityProperty().bind(opacity);
                    occupant.getChildren().add(occ);
                }
                occupant.getChildren().add(new Text("   "));
            }
            generalBox.getChildren().add(occupant);
        }
        return generalBox;
    }
}