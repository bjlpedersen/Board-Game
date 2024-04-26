package ch.epfl.chacun.gui;

import ch.epfl.chacun.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;

public class PlayersUI {
    private PlayersUI() {}

    public static Node create(ObservableValue<GameState> observable, TextMaker textMaker) {
        VBox box = new VBox();
        box.getStylesheets().add("player.css");
        box.setId("players");

        Set<PlayerColor> activePlayers = new HashSet<>();
        for (PlayerColor player : PlayerColor.ALL) {
            if (textMaker.playerName(player) != null) {
                activePlayers.add(player);
            }
        }

        ObservableValue<Map<PlayerColor, Integer>> pointsO = observable.map(g-> g.messageBoard().points());
        Map<PlayerColor, TextFlow> playerTextFlows = new HashMap<>();
        for (PlayerColor p: activePlayers) {
            TextFlow playerTextFlow = new TextFlow();

            playerTextFlow.setId("player");
            if (p == observable.getValue().currentPlayer()) {
                playerTextFlow.setStyle("current");
            } else {
                playerTextFlow.setStyle("");
            }

            ObservableValue<String> pointsTextO = pointsO.map(s ->
                    textMaker.playerName(p) + " : " + s.get(p).toString() + " points");

            Text pointsText = new Text();
            pointsText.textProperty().bind(pointsTextO);

            playerTextFlow.getChildren().add(pointsText);
            playerTextFlow.getChildren().add(new Circle(5, ColorMap.fillColor(p)));

            List<Occupant.Kind> occupants = List.of(
                    Occupant.Kind.HUT,
                    Occupant.Kind.PAWN);

            for (Occupant.Kind kind : occupants) {
                for (int i = 0; i < Occupant.occupantsCount(kind); ++i) {
                    if (i < observable.getValue().freeOccupantsCount(p, kind)) {
                        Node svgIm = Icon.newFor(ColorMap.fillColor(p), kind);
                        svgIm.opacityProperty().set(1);
                        playerTextFlow.getChildren().add(svgIm);
                    } else {
                        Node svgIm = Icon.newFor(ColorMap.fillColor(p), kind);
                        svgIm.opacityProperty().set(0.1);
                        playerTextFlow.getChildren().add(svgIm);
                    }
                }
            }

            playerTextFlows.put(p, playerTextFlow);
        }

        ObservableValue<PlayerColor> currentPlayer0 = observable.map(GameState::currentPlayer);
        currentPlayer0.addListener((o, oldPlayer, newPlayer) -> {
            for (Node node : playerTextFlows.get(oldPlayer).getChildren()) {
                if (node instanceof Rectangle) node.opacityProperty().set(0);
            }
            for (Node node : playerTextFlows.get(newPlayer).getChildren()) {
                if (node instanceof Rectangle) node.opacityProperty().set(1);
            }
        });

        for (TextFlow flow : playerTextFlows.values()) {
            box.getChildren().add(flow);
        }
        return box;
    }
}
