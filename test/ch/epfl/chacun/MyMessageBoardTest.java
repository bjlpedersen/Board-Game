package ch.epfl.chacun;

import ch.epfl.chacun.utils.ChaCuNUtils;
import ch.epfl.chacun.utils.Tuples;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class MyMessageBoardTest {

    private static final TextMaker TEXT_MAKER = new TextMakerTest();

    private static List<MessageBoard.Message> getInitialMessages() {
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("1", 3, Set.of(PlayerColor.RED, PlayerColor.GREEN), Set.of(12, 33, 46)));
        messages.add(new MessageBoard.Message("2", 10, Set.of(PlayerColor.BLUE, PlayerColor.PURPLE), Set.of(1, 56, 17)));
        messages.add(new MessageBoard.Message("3", 22, Set.of(PlayerColor.YELLOW), Set.of(2, 70)));
        messages.add(new MessageBoard.Message("4", 1, Set.of(PlayerColor.PURPLE, PlayerColor.YELLOW), Set.of(4)));
        messages.add(new MessageBoard.Message("5", 0, Set.of(PlayerColor.RED), Set.of(12, 33, 46)));
        return messages;
    }

    private static Map<PlayerColor, Integer> getInitialScorersPointsMap() {
        Map<PlayerColor, Integer> map = new HashMap<>();
        map.put(PlayerColor.RED, 3);
        map.put(PlayerColor.GREEN, 3);
        map.put(PlayerColor.BLUE, 10);
        map.put(PlayerColor.YELLOW, 22 + 1);
        map.put(PlayerColor.PURPLE, 10 + 1);
        return map;
    }

    private static Area<Zone.Forest> getOccupiedForestTemplate() {
        return new Area<>(Set.of(new Zone.Forest(11, Zone.Forest.Kind.PLAIN), new Zone.Forest(22, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(33, Zone.Forest.Kind.WITH_MENHIR), new Zone.Forest(44, Zone.Forest.Kind.WITH_MUSHROOMS)),
                List.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.RED), 0);
    }

    private static Area<Zone.Forest> getUnoccupiedForestTemplate() {
        return new Area<>(Set.of(new Zone.Forest(11, Zone.Forest.Kind.PLAIN), new Zone.Forest(22, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(33, Zone.Forest.Kind.WITH_MENHIR), new Zone.Forest(44, Zone.Forest.Kind.WITH_MUSHROOMS)),
                new ArrayList<>(), 0);
    }

    private static Area<Zone.River> getOccupiedRiverTemplate() {
        return new Area<>(Set.of(new Zone.River(11, 2, null),
                new Zone.River(22, 0, new Zone.Lake(28, 5, null)),
                new Zone.River(33, 10, null)), List.of(PlayerColor.YELLOW, PlayerColor.GREEN, PlayerColor.GREEN),
                5);
    }

    private static Area<Zone.River> getUnOccupiedRiverTemplate() {
        return new Area<>(Set.of(new Zone.River(11, 2, null),
                new Zone.River(22, 0, new Zone.Lake(28, 5, null)),
                new Zone.River(33, 10, null)), new ArrayList<>(), 5);
    }

    List<MessageBoard.Message> addMessageAfterDefaultList(MessageBoard.Message message) {
        var expectedMessage = new ArrayList<>(getInitialMessages());
        expectedMessage.add(message);

        return List.copyOf(expectedMessage);
    }

    MessageBoard.Message createMessage(Set<PlayerColor> scorer, int points, Function<Tuples.Pair<Set<PlayerColor>, Integer>, String> message) {
        return new MessageBoard.Message(message.apply(new Tuples.Pair<>(scorer, points)), points, scorer, Set.of());
    }

    MessageBoard.Message createMessage(Set<PlayerColor> scorer, int points, Function<Tuples.Pair<Set<PlayerColor>, Integer>, String> message, Integer... tileId) {
        return new MessageBoard.Message(message.apply(new Tuples.Pair<>(scorer, points)), points, scorer, Set.of(tileId));
    }

    MessageBoard.Message createMessage(PlayerColor scorer, int points, Function<Tuples.Pair<PlayerColor, Integer>, String> message) {
        return new MessageBoard.Message(message.apply(new Tuples.Pair<>(scorer, points)), points, Set.of(scorer), Set.of());
    }

    MessageBoard.Message createMessage(PlayerColor scorer, int points, Function<Tuples.Pair<PlayerColor, Integer>, String> message, Integer... tileID) {
        return new MessageBoard.Message(message.apply(new Tuples.Pair<>(scorer, points)), points, Set.of(scorer), Set.of(tileID));
    }

    @Test
    void messageConstructorThrowsIllegalArgumentExceptionIfArgumentsAreNull() {
        assertThrows(NullPointerException.class, () -> new MessageBoard.Message(null, 0, new HashSet<>(), new HashSet<>()));
        assertThrows(NullPointerException.class, () -> new MessageBoard.Message("", 0, null, new HashSet<>()));
        assertThrows(NullPointerException.class, () -> new MessageBoard.Message("", 0, new HashSet<>(), null));
    }

    @Test
    void messageConstructorThrowsIllegalArgumentExceptionIfPointsAreNegative() {
        assertThrows(IllegalArgumentException.class, () -> new MessageBoard.Message("", -1, new HashSet<>(), new HashSet<>()));
    }

    @Test
    void messageConstructorGuaranteesImmutabilityOfTheRecord() {
        Set<PlayerColor> scorers = new HashSet<>(Set.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW));
        Set<Integer> tileIds = new HashSet<>(Set.of(1, 2, 3, 4, 5, 6, 7));
        MessageBoard.Message expectedMessage = new MessageBoard.Message("", 2, scorers, tileIds);
        scorers.remove(PlayerColor.RED);
        tileIds.clear();
        MessageBoard.Message actualMessage = new MessageBoard.Message("", 2, scorers, tileIds);
        assertNotEquals(expectedMessage, actualMessage);
    }

    @Test
    void pointsReturnsCorrectMap() {
        MessageBoard messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());
        assertEquals(getInitialScorersPointsMap(), messageBoard.points());
    }

    @Test
    void withScoredForestReturnsMessageBoardWithCorrectAdditionalMessageIfForestIsOccupied() {
        List<MessageBoard.Message> expectedMessages = getInitialMessages();
        expectedMessages.add(new MessageBoard.Message(TEXT_MAKER.playersScoredForest(Set.of(PlayerColor.RED)
                , Points.forClosedForest(4, 1), 1, 4), Points.forClosedForest(4, 1), Set.of(PlayerColor.RED), Set.of(1, 2, 3, 4)));
        MessageBoard expectedMessageBoard = new MessageBoard(TEXT_MAKER, expectedMessages);

        MessageBoard actualMessageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());
        actualMessageBoard = actualMessageBoard.withScoredForest(getOccupiedForestTemplate());

        assertEquals(expectedMessageBoard, actualMessageBoard);
    }

    @Test
    void withScoredForestReturnsMessageBoardUnchangedIfForestIsNotOccupied() {
        assertEquals(new MessageBoard(TEXT_MAKER, getInitialMessages()), new MessageBoard(TEXT_MAKER, getInitialMessages()).withScoredForest(getUnoccupiedForestTemplate()));
    }

    @Test
    void withClosedForestWithMenhirReturnsMessageBoardWithCorrectAdditionalMessage() {
        List<MessageBoard.Message> expectedMessages = getInitialMessages();
        expectedMessages.add(new MessageBoard.Message(TEXT_MAKER.playerClosedForestWithMenhir(PlayerColor.RED),
                0, new HashSet<>(), Set.of(1, 2, 3, 4)));
        MessageBoard expectedMessageBoard = new MessageBoard(TEXT_MAKER, expectedMessages);

        MessageBoard actualMessageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());
        actualMessageBoard = actualMessageBoard.withClosedForestWithMenhir(PlayerColor.RED, getOccupiedForestTemplate());

        assertEquals(expectedMessageBoard, actualMessageBoard);
    }

    @Test
    void withScoredRiverReturnsMessageBoardWithCorrectMessageIfRiverIsOccupied() {
        List<MessageBoard.Message> expectedMessages = getInitialMessages();
        expectedMessages.add(new MessageBoard.Message(TEXT_MAKER.playersScoredRiver(Set.of(PlayerColor.GREEN)
                , Points.forClosedRiver(3, 17), 17, 3), Points.forClosedRiver(3, 17),
                Set.of(PlayerColor.GREEN), Set.of(1, 2, 3)));
        MessageBoard expectedMessageBoard = new MessageBoard(TEXT_MAKER, expectedMessages);

        MessageBoard actualMessageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());
        actualMessageBoard = actualMessageBoard.withScoredRiver(getOccupiedRiverTemplate());

        assertEquals(expectedMessageBoard, actualMessageBoard);
    }

    @Test
    void withScoredRiverReturnsMessageBoardUnchangedIfRiverIsNotOccupied() {
        assertEquals(new MessageBoard(TEXT_MAKER, getInitialMessages()), new MessageBoard(TEXT_MAKER, getInitialMessages()).withScoredRiver(getUnOccupiedRiverTemplate()));
    }

    @Test
    void withScoredHuntingTrap() {

    }

    @Test
    void withScoredLogboatOneLake() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(PlayerColor.RED, Points.forLogboat(1), pair -> TEXT_MAKER.playerScoredLogboat(pair.a(), pair.b(), 1), 10, 12, 13)
        );

        Area<Zone.Water> riverSystem = ChaCuNUtils.createAreaWithNoOccupant(
                3,
                ChaCuNUtils.createLake(101, 5, Zone.SpecialPower.LOGBOAT),
                ChaCuNUtils.createRiverZone(103, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertEquals(expectedMessages, messageBoard.withScoredLogboat(PlayerColor.RED, riverSystem).messages());
    }

    @Test
    void withScoredLogboatManyLake() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(PlayerColor.RED, Points.forLogboat(2), pair -> TEXT_MAKER.playerScoredLogboat(pair.a(), pair.b(), 2), 10, 12, 13)
        );

        Area<Zone.Water> riverSystem = ChaCuNUtils.createAreaWithNoOccupant(
                3,
                ChaCuNUtils.createLake(101, 5, Zone.SpecialPower.LOGBOAT),
                ChaCuNUtils.createLake(102, 5, Zone.SpecialPower.LOGBOAT),
                ChaCuNUtils.createRiverZone(103, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertEquals(expectedMessages, messageBoard.withScoredLogboat(PlayerColor.RED, riverSystem).messages());
    }

    @Test
    void withScoredLogboatNoLakeException() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        Area<Zone.Water> riverSystem = ChaCuNUtils.createAreaWithNoOccupant(
                3,
                ChaCuNUtils.createRiverZone(103, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertThrows(IllegalArgumentException.class, () -> messageBoard.withScoredLogboat(PlayerColor.RED, riverSystem));
    }

    @Test
    void withScoredMeadowOneAnimalOneOccupant() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(
                        PlayerColor.BLUE,
                        Points.forMeadow(0, 0, 1),
                        pair -> TEXT_MAKER.playersScoredMeadow(Set.of(pair.a()), pair.b(), Map.of(Animal.Kind.DEER, 1)),
                        10
                )
        );

        Area<Zone.Meadow> meadowArea = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE),
                ChaCuNUtils.createMeadowZone(
                        101,
                        new Animal(1012, Animal.Kind.DEER)
                )
        );

        assertEquals(expectedMessages, messageBoard.withScoredMeadow(meadowArea, Set.of()).messages());
    }

    @Test
    void withScoredMeadowManyAnimalsOneOccupant() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var points = Points.forMeadow(2, 2, 2);
        var animalToNumberMap = Map.of(Animal.Kind.DEER, 2, Animal.Kind.AUROCHS, 2, Animal.Kind.MAMMOTH, 2);
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(
                        PlayerColor.BLUE,
                        points,
                        pair -> TEXT_MAKER.playersScoredMeadow(Set.of(pair.a()), pair.b(), animalToNumberMap),
                        10
                )
        );

        Area<Zone.Meadow> meadowArea = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE),
                ChaCuNUtils.createMeadowZone(
                        101,
                        new Animal(1012, Animal.Kind.DEER),
                        new Animal(1013, Animal.Kind.DEER),
                        new Animal(1014, Animal.Kind.MAMMOTH),
                        new Animal(1015, Animal.Kind.MAMMOTH),
                        new Animal(1016, Animal.Kind.AUROCHS),
                        new Animal(1017, Animal.Kind.AUROCHS)
                )
        );

        assertEquals(expectedMessages, messageBoard.withScoredMeadow(meadowArea, Set.of()).messages());
    }

    @Test
    void withScoredMeadowManyAnimalsManyZonesOneOccupant() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var points = Points.forMeadow(2, 2, 2);
        var animalToNumberMap = Map.of(Animal.Kind.DEER, 2, Animal.Kind.AUROCHS, 2, Animal.Kind.MAMMOTH, 2);
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(
                        PlayerColor.BLUE,
                        points,
                        pair -> TEXT_MAKER.playersScoredMeadow(Set.of(pair.a()), pair.b(), animalToNumberMap),
                        10, 11, 20
                )
        );

        Area<Zone.Meadow> meadowArea = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE),
                ChaCuNUtils.createMeadowZone(
                        101,
                        new Animal(1012, Animal.Kind.DEER),
                        new Animal(1013, Animal.Kind.DEER)
                ),
                ChaCuNUtils.createMeadowZone(
                        201,
                        new Animal(1014, Animal.Kind.MAMMOTH),
                        new Animal(1015, Animal.Kind.MAMMOTH)
                ),
                ChaCuNUtils.createMeadowZone(
                        111,
                        new Animal(1016, Animal.Kind.AUROCHS),
                        new Animal(1017, Animal.Kind.AUROCHS)
                )
        );

        assertEquals(expectedMessages, messageBoard.withScoredMeadow(meadowArea, Set.of()).messages());
    }

    @Test
    void withScoredMeadowManyAnimalsManyZonesManyOccupants() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var points = Points.forMeadow(2, 2, 2);
        var animalToNumberMap = Map.of(Animal.Kind.DEER, 2, Animal.Kind.AUROCHS, 2, Animal.Kind.MAMMOTH, 2);
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(
                        PlayerColor.BLUE,
                        points,
                        pair -> TEXT_MAKER.playersScoredMeadow(Set.of(pair.a()), pair.b(), animalToNumberMap),
                        10, 11, 20
                )
        );

        Area<Zone.Meadow> meadowArea = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE, PlayerColor.RED, PlayerColor.BLUE),
                ChaCuNUtils.createMeadowZone(
                        101,
                        new Animal(1012, Animal.Kind.DEER),
                        new Animal(1013, Animal.Kind.DEER)
                ),
                ChaCuNUtils.createMeadowZone(
                        201,
                        new Animal(1014, Animal.Kind.MAMMOTH),
                        new Animal(1015, Animal.Kind.MAMMOTH)
                ),
                ChaCuNUtils.createMeadowZone(
                        111,
                        new Animal(1016, Animal.Kind.AUROCHS),
                        new Animal(1017, Animal.Kind.AUROCHS)
                )
        );

        assertEquals(expectedMessages, messageBoard.withScoredMeadow(meadowArea, Set.of()).messages());
    }

    @Test
    void withScoredMeadowManyAnimalsManyZonesManyMajorityOccupants() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var points = Points.forMeadow(2, 2, 2);
        var animalToNumberMap = Map.of(Animal.Kind.DEER, 2, Animal.Kind.AUROCHS, 2, Animal.Kind.MAMMOTH, 2);
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(
                        Set.of(PlayerColor.BLUE, PlayerColor.RED),
                        points,
                        pair -> TEXT_MAKER.playersScoredMeadow(pair.a(), pair.b(), animalToNumberMap),
                        10, 11, 20
                )
        );

        Area<Zone.Meadow> meadowArea = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.RED),
                ChaCuNUtils.createMeadowZone(
                        101,
                        new Animal(1012, Animal.Kind.DEER),
                        new Animal(1013, Animal.Kind.DEER)
                ),
                ChaCuNUtils.createMeadowZone(
                        201,
                        new Animal(1014, Animal.Kind.MAMMOTH),
                        new Animal(1015, Animal.Kind.MAMMOTH)
                ),
                ChaCuNUtils.createMeadowZone(
                        111,
                        new Animal(1016, Animal.Kind.AUROCHS),
                        new Animal(1017, Animal.Kind.AUROCHS)
                )
        );

        assertEquals(expectedMessages, messageBoard.withScoredMeadow(meadowArea, Set.of()).messages());
    }

    @Test
    void withScoredMeadowManyAnimalsWithCancelledAnimals() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var cancelledAnimals = Set.of(
                new Animal(1012, Animal.Kind.DEER),
                new Animal(1015, Animal.Kind.MAMMOTH)
        );
        var points = Points.forMeadow(1, 2, 1);
        var animalToNumberMap = Map.of(Animal.Kind.DEER, 1, Animal.Kind.AUROCHS, 2, Animal.Kind.MAMMOTH, 1);

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(
                        Set.of(PlayerColor.BLUE, PlayerColor.RED),
                        points,
                        pair -> TEXT_MAKER.playersScoredMeadow(pair.a(), pair.b(), animalToNumberMap),
                        10, 11, 20
                )
        );

        Area<Zone.Meadow> meadowArea = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.RED),
                ChaCuNUtils.createMeadowZone(
                        101,
                        new Animal(1012, Animal.Kind.DEER),
                        new Animal(1013, Animal.Kind.DEER)
                ),
                ChaCuNUtils.createMeadowZone(
                        201,
                        new Animal(1014, Animal.Kind.MAMMOTH),
                        new Animal(1015, Animal.Kind.MAMMOTH)
                ),
                ChaCuNUtils.createMeadowZone(
                        111,
                        new Animal(1016, Animal.Kind.AUROCHS),
                        new Animal(1017, Animal.Kind.AUROCHS)
                )
        );

        assertEquals(expectedMessages, messageBoard.withScoredMeadow(meadowArea, cancelledAnimals).messages());
    }

    @Test
    void withScoredMeadowNoAnimals() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        Area<Zone.Meadow> meadowArea = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE),
                ChaCuNUtils.createMeadowZone(
                        101
                )
        );

        assertEquals(getInitialMessages(), messageBoard.withScoredMeadow(meadowArea, Set.of()).messages());
    }

    @Test
    void withScoredMeadowNoOccupants() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        Area<Zone.Meadow> meadowArea = ChaCuNUtils.createClosedAreaWithNoOccupants(
                ChaCuNUtils.createMeadowZone(
                        101
                )
        );

        assertEquals(getInitialMessages(), messageBoard.withScoredMeadow(meadowArea, Set.of()).messages());
    }

    @Test
    void withScoredRiverSystemOneOccupant() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        final var fishCount = 5 + 3 + 7 + 22;
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(PlayerColor.BLUE, Points.forRiverSystem(fishCount), pair -> TEXT_MAKER.playersScoredRiverSystem(Set.of(pair.a()), pair.b(), fishCount), 10, 11, 12)
        );

        Area<Zone.Water> riverSystem = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE),
                ChaCuNUtils.createRiverZone(101, 5),
                ChaCuNUtils.createRiverZone(102, 3),
                ChaCuNUtils.createRiverZone(111, 7),
                ChaCuNUtils.createRiverZone(121, 22)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRiverSystem(riverSystem).messages());
    }

    @Test
    void withScoredRiverSystemManyOccupant() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        final var fishCount = 5 + 3 + 7 + 22;
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(PlayerColor.BLUE, Points.forRiverSystem(fishCount), pair -> TEXT_MAKER.playersScoredRiverSystem(Set.of(pair.a()), pair.b(), fishCount), 10, 11, 12)
        );

        Area<Zone.Water> riverSystem = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE, PlayerColor.RED, PlayerColor.BLUE),
                ChaCuNUtils.createRiverZone(101, 5),
                ChaCuNUtils.createRiverZone(102, 3),
                ChaCuNUtils.createRiverZone(111, 7),
                ChaCuNUtils.createRiverZone(121, 22)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRiverSystem(riverSystem).messages());
    }

    @Test
    void withScoredRiverSystemManyMajorityOccupant() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        final var fishCount = 5 + 3 + 7 + 22;
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(Set.of(PlayerColor.BLUE, PlayerColor.RED), Points.forRiverSystem(fishCount), pair -> TEXT_MAKER.playersScoredRiverSystem(pair.a(), pair.b(), fishCount), 10, 11, 12)
        );

        Area<Zone.Water> riverSystem = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.RED),
                ChaCuNUtils.createRiverZone(101, 5),
                ChaCuNUtils.createRiverZone(102, 3),
                ChaCuNUtils.createRiverZone(111, 7),
                ChaCuNUtils.createRiverZone(121, 22)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRiverSystem(riverSystem).messages());
    }

    @Test
    void withScoredRiverSystemOneOccupantWithLakes() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        final var fishCount = 5 + 3 + 7 + 22 + 34 + 14;
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(PlayerColor.BLUE, Points.forRiverSystem(fishCount), pair -> TEXT_MAKER.playersScoredRiverSystem(Set.of(pair.a()), pair.b(), fishCount), 9, 10, 11, 12)
        );

        Area<Zone.Water> riverSystem = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE),
                ChaCuNUtils.createLake(91, 34),
                ChaCuNUtils.createLake(92, 14),
                ChaCuNUtils.createRiverZone(101, 5),
                ChaCuNUtils.createRiverZone(102, 3),
                ChaCuNUtils.createRiverZone(111, 7),
                ChaCuNUtils.createRiverZone(121, 22)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRiverSystem(riverSystem).messages());
    }

    @Test
    void withScoredRiverSystemOneOccupantWithLakesInRiver() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        final var fishCount = 5 + 3 + 7 + 22 + 34 + 14;
        var expectedMessages = addMessageAfterDefaultList(
                createMessage(PlayerColor.BLUE, Points.forRiverSystem(fishCount), pair -> TEXT_MAKER.playersScoredRiverSystem(Set.of(pair.a()), pair.b(), fishCount), 10, 11, 12)
        );

        Zone.Lake lake1 = ChaCuNUtils.createLake(108, 34);
        Zone.Lake lake2 = ChaCuNUtils.createLake(118, 14);
        Area<Zone.Water> riverSystem = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE),
                ChaCuNUtils.createRiverZone(101, 5, lake1),
                lake1,
                ChaCuNUtils.createRiverZone(102, 3),
                ChaCuNUtils.createRiverZone(111, 7, lake2),
                lake2,
                ChaCuNUtils.createRiverZone(121, 22)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRiverSystem(riverSystem).messages());
    }

    @Test
    void withScoredRiverSystemNoPoints() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        Area<Zone.Water> riverSystem = ChaCuNUtils.createClosedAreaWithOccupants(
                List.of(PlayerColor.BLUE),
                ChaCuNUtils.createRiverZone(101, 0),
                ChaCuNUtils.createRiverZone(102, 0),
                ChaCuNUtils.createRiverZone(111, 0),
                ChaCuNUtils.createRiverZone(121, 0)
        );

        assertEquals(getInitialMessages(), messageBoard.withScoredRiverSystem(riverSystem).messages());
    }

    @Test
    void withScoredRiverSystemNoOccupants() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        Area<Zone.Water> riverSystem = ChaCuNUtils.createClosedAreaWithNoOccupants(
                ChaCuNUtils.createRiverZone(101, 0),
                ChaCuNUtils.createRiverZone(102, 0),
                ChaCuNUtils.createRiverZone(111, 0),
                ChaCuNUtils.createRiverZone(121, 0)
        );

        assertEquals(getInitialMessages(), messageBoard.withScoredRiverSystem(riverSystem).messages());
    }

    @Test
    void withScoredPitTrap() {
        // TODO : how do you get the tiles along one tile ?
    }

    @Test
    void withScoredRaftOneLakeNoOccupants() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = getInitialMessages();

        Area<Zone.Water> riverSystemWithOneLake = ChaCuNUtils.createAreaWithNoOccupant(
                3,
                ChaCuNUtils.createLake(101, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createRiverZone(103, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRaft(riverSystemWithOneLake).messages());
    }

    @Test
    void withScoredRaftOneLakeOneOccupants() {
        var players = Set.of(PlayerColor.RED);
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(players, Points.forRaft(1), pair -> TEXT_MAKER.playersScoredRaft(pair.a(), pair.b(), 1), 10, 12, 13)
        );

        Area<Zone.Water> riverSystemWithOneLake = ChaCuNUtils.createAreaWithOccupant(
                3,
                PlayerColor.RED,
                ChaCuNUtils.createLake(101, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createRiverZone(103, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRaft(riverSystemWithOneLake).messages());
    }

    @Test
    void withScoredRaftOneLakeManyOccupants() {
        var players = Set.of(PlayerColor.RED);
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(players, Points.forRaft(1), pair -> TEXT_MAKER.playersScoredRaft(pair.a(), pair.b(), 1), 10, 11, 12, 13)
        );

        Area<Zone.Water> riverSystemWithOneLake = ChaCuNUtils.createAreaWithOccupant(
                3,
                List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.BLUE),
                ChaCuNUtils.createLake(101, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createRiverZone(113, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRaft(riverSystemWithOneLake).messages());
    }

    @Test
    void withScoredRaftManyLakeManyOccupants() {
        var players = Set.of(PlayerColor.RED);
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(players, Points.forRaft(3), pair -> TEXT_MAKER.playersScoredRaft(pair.a(), pair.b(), 3), 10, 11, 12, 13, 20)
        );

        Area<Zone.Water> riverSystemWithManyLakes = ChaCuNUtils.createAreaWithOccupant(
                3,
                List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.BLUE),
                ChaCuNUtils.createLake(101, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createLake(201, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createLake(102, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createRiverZone(113, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRaft(riverSystemWithManyLakes).messages());
    }

    @Test
    void withScoredRaftManyLakeManyMajorityOccupants() {
        var players = Set.of(PlayerColor.RED, PlayerColor.BLUE);
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(players, Points.forRaft(3), pair -> TEXT_MAKER.playersScoredRaft(pair.a(), pair.b(), 3), 10, 11, 12, 13, 20)
        );

        Area<Zone.Water> riverSystemWithManyLakes = ChaCuNUtils.createAreaWithOccupant(
                3,
                List.of(PlayerColor.BLUE, PlayerColor.BLUE, PlayerColor.RED, PlayerColor.RED),
                ChaCuNUtils.createLake(101, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createLake(201, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createLake(102, 5, Zone.SpecialPower.RAFT),
                ChaCuNUtils.createRiverZone(113, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRaft(riverSystemWithManyLakes).messages());
    }

    @Test
    void withScoredRaftLakeInRiver() {
        var players = Set.of(PlayerColor.RED);
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(players, Points.forRaft(2), pair -> TEXT_MAKER.playersScoredRaft(pair.a(), pair.b(), 2), 11, 12, 13, 30)
        );

        Zone.Lake lake1 = ChaCuNUtils.createLake(308, 0, null);
        Zone.Lake lake2 = ChaCuNUtils.createLake(118, 1, Zone.SpecialPower.RAFT);
        Area<Zone.Water> riverSystemWithManyLakes = ChaCuNUtils.createAreaWithOccupant(
                3,
                List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.BLUE),
                ChaCuNUtils.createRiverZone(301, 0, lake1),
                lake1,
                ChaCuNUtils.createRiverZone(113, 2, lake2),
                lake2,
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        assertEquals(expectedMessages, messageBoard.withScoredRaft(riverSystemWithManyLakes).messages());
    }

    @Test
    void withScoredRaftExceptions() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());

        Area<Zone.Water> riverSystemWithNoLakes = ChaCuNUtils.createAreaWithOccupant(
                3,
                PlayerColor.RED,
                ChaCuNUtils.createRiverZone(113, 2),
                ChaCuNUtils.createRiverZone(125, 2),
                ChaCuNUtils.createRiverZone(136, 2)
        );

        // TODO : what happens if we give to the message board a river system with no lakes ?
        assertThrows(IllegalArgumentException.class, () -> messageBoard.withScoredRaft(riverSystemWithNoLakes));
    }

    @Test
    void withWinners() {
        var players = Set.of(PlayerColor.RED, PlayerColor.BLUE);
        int points = 382;

        var expectedMessages = addMessageAfterDefaultList(
                createMessage(players, points, pair -> TEXT_MAKER.playersWon(pair.a(), pair.b()))
        );

        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());
        assertEquals(expectedMessages, messageBoard.withWinners(players, points).messages());
    }

    @Test
    void withWinnersExceptions() {
        var messageBoard = new MessageBoard(TEXT_MAKER, getInitialMessages());
        assertThrows(IllegalArgumentException.class, () -> messageBoard.withWinners(Set.of(PlayerColor.BLUE), -1));
    }
}

/**
 * Simple test class that implements TextMaker useful when testing {@link MessageBoard}
 */
class TextMakerTest implements TextMaker {

    @Override
    public String playerName(PlayerColor playerColor) {
        return playerColor.toString();
    }

    @Override
    public String points(int points) {
        return String.valueOf(points);
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return player.toString();
    }

    // we need these sorting functions because the messages strings are compared, and it's not
    // guaranteed for sets and maps to have the same order every time.
    private <T, K extends Comparable<K>> List<T> sortBy(Set<T> set, Function<T, K> key) {
        var arrayList = new ArrayList<>(set);
        Comparator<K> comparator = Comparator.naturalOrder();

        arrayList.sort((a, b) -> {
            var keyA = key.apply(a);
            var keyB = key.apply(b);

            return comparator.compare(keyA, keyB);
        });

        return List.copyOf(arrayList);
    }

    private <T extends Comparable<T>> List<T> sort(Set<T> set) {
        return sortBy(set, t -> t);
    }

    private <K, V, C extends Comparable<C>> List<Map.Entry<K, V>> sortBy(Map<K, V> set, Function<Map.Entry<K, V>, C> key) {
        var arrayList = new ArrayList<>(set.entrySet());
        Comparator<C> comparator = Comparator.naturalOrder();

        arrayList.sort((a, b) -> {
            var keyA = key.apply(a);
            var keyB = key.apply(b);

            return comparator.compare(keyA, keyB);
        });

        return List.copyOf(arrayList);
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        var sortedScorers = sort(scorers);

        return new StringJoiner(" ")
                .add(sortedScorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(mushroomGroupCount))
                .add(String.valueOf(tileCount))
                .toString();
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        var sortedScorers = sort(scorers);

        return new StringJoiner(" ")
                .add(sortedScorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(fishCount))
                .add(String.valueOf(tileCount))
                .toString();
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        var sortedAnimal = sortBy(animals, Map.Entry::getKey);

        return new StringJoiner(" ")
                .add(scorer.toString())
                .add(String.valueOf(points))
                .add(sortedAnimal.toString())
                .toString();
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return new StringJoiner(" ")
                .add(scorer.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(lakeCount))
                .toString();
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        var sortedAnimal = sortBy(animals, Map.Entry::getKey);
        var sortedScorers = sort(scorers);

        return new StringJoiner(" ")
                .add(sortedScorers.toString())
                .add(String.valueOf(points))
                .add(sortedAnimal.toString())
                .toString();
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        var sortedScorers = sort(scorers);

        return new StringJoiner(" ")
                .add(sortedScorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(fishCount))
                .toString();
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        var sortedScorers = sort(scorers);
        var sortedAnimal = sortBy(animals, Map.Entry::getKey);

        return new StringJoiner(" ")
                .add(sortedScorers.toString())
                .add(String.valueOf(points))
                .add(sortedAnimal.toString())
                .toString();
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        var sortedScorers = sort(scorers);

        return new StringJoiner(" ")
                .add(sortedScorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(lakeCount))
                .toString();
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        var sortedWinner = sort(winners);

        return new StringJoiner(" ")
                .add(sortedWinner.toString())
                .add(String.valueOf(points))
                .toString();
    }

    @Override
    public String clickToOccupy() {
        return null;
    }

    @Override
    public String clickToUnoccupy() {
        return null;
    }
}
