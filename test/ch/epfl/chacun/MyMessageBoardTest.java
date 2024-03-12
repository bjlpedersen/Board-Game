package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MyMessageBoardTest {

    @Test
    void pointsWorksForTrivialAndNonTrivial() {
        Zone.Forest forest = new Zone.Forest(0, Zone.Forest.Kind.WITH_MENHIR);
        Area<Zone.Forest> forestArea1 = new Area<>(Set.of(forest), List.of(PlayerColor.RED), 0);
        TextMakerImplementation textMaker = new TextMakerImplementation();
        MessageBoard.Message message1 = new MessageBoard.Message(textMaker.playersScoredForest(Set.of(), 1, 0, 2), 1, Set.of(PlayerColor.RED), Set.of(0, 1, 2));
        MessageBoard.Message message2 = new MessageBoard.Message(textMaker.playerScoredHuntingTrap(PlayerColor.RED, 0, null), 0, Set.of(PlayerColor.BLUE), Set.of(3, 4));
        MessageBoard board = new MessageBoard(textMaker, List.of(message1, message2));
        board.withClosedForestWithMenhir(PlayerColor.RED, forestArea1);
        assertEquals(3, board.messages().size());
    }
}
