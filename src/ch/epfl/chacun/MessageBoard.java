package ch.epfl.chacun;

import java.util.*;

/**
 * Represents a message board in the game.
 *
 * @author Bjork Pedersen (376143)
 */
public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    /**
     * Constructor for MessageBoard. Makes MessageBoard immutable by copying the list.
     *
     * @param textMaker The text maker for the messages.
     * @param messages  The list of messages.
     */
    public MessageBoard {
        messages = List.copyOf(messages);
    }

    /**
     * Calculates the points for each player.
     *
     * @return A map of player colors to their respective points.
     */
    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> result = new HashMap<>();
        for (Message message : messages) {
            for (PlayerColor player : message.scorers()) {
                result.put(player, result.getOrDefault(player, 0) + message.points());
            }
        }
        return result;
    }

    /**
     * Updates the message board with a new message (of type playerScoredForest) if needed.
     *
     * @param forest the forest that has just been closed
     * @return A new message board with the added message / same message board.
     * @throws IllegalArgumentException if the forest is not occupied
     */
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        Objects.requireNonNull(forest, "Forest cannot be null");
        if (forest.isOccupied()) {
            List<Message> increasedMessages = new ArrayList<>(messages);
            int mushroomGroupCount = Area.mushroomGroupCount(forest);
            int points = Points.forClosedForest(forest.zones().size(), mushroomGroupCount);
            if (points > 0) {
                increasedMessages.add(createMessage(forest, points, mushroomGroupCount));
            } else {
                increasedMessages.add(createMessage(forest, 0, mushroomGroupCount));
            }
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    /**
     * Creates a new Message for a scored forest.
     *
     * @param forest             The forest that has been scored.
     * @param points             The points scored for the forest.
     * @param mushroomGroupCount The count of mushroom groups in the forest.
     * @return A new Message instance with the provided details.
     */
    private Message createMessage(Area<Zone.Forest> forest, int points, int mushroomGroupCount) {
        return new Message(textMaker.playersScoredForest(forest.majorityOccupants(),
                points,
                mushroomGroupCount,
                forest.tileIds().size()),
                points,
                forest.majorityOccupants(),
                forest.tileIds());
    }


    /**
     * Updates the message board with a new message (of type playerScoredWithMenhir) if needed.
     *
     * @param forest the forest that has the menhir
     * @param player the player who closed the forest
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        Message message = new Message(textMaker.playerClosedForestWithMenhir(player),
                0,
                Set.of(),
                forest.tileIds());
        List<Message> newMessage = new ArrayList<>(messages);
        newMessage.add(message);
        return new MessageBoard(textMaker, newMessage);
    }

    /**
     * Updates the message board with a new message (of type playerScoredRiver) if needed.
     *
     * @param river the river in question
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (river.isOccupied()) {
            return withNewMessage(textMaker.playersScoredRiver(river.majorityOccupants(),
                            Points.forClosedRiver(river.zones().size(), Area.riverFishCount(river)),
                            Area.riverFishCount(river),
                            river.tileIds().size()),
                    Points.forClosedRiver(river.zones().size(), Area.riverFishCount(river)),
                    river.majorityOccupants(),
                    river.tileIds());
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playerScoredHuntingTrap) if needed.
     *
     * @param adjacentMeadow the meadow adjacent to the hunting trap (we need it's animals)
     * @param scorer         the player who scored
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withScoredHuntingTrap(
            PlayerColor scorer,
            Area<Zone.Meadow> adjacentMeadow,
            Set<Animal> deletedAnimals) {
        Set<Animal> animals = Area.animals(adjacentMeadow, deletedAnimals);
        int aurochsCount = 0;
        int deerCount = 0;
        int mammothCount = 0;
        int tigerCount = 0;
        for (Animal animal : animals) {
            switch (animal.kind()) {
                case AUROCHS -> aurochsCount++;
                case DEER -> deerCount++;
                case MAMMOTH -> mammothCount++;
                case TIGER -> tigerCount++;
            }
        }
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.DEER, deerCount);
        map.put(Animal.Kind.AUROCHS, aurochsCount);
        map.put(Animal.Kind.MAMMOTH, mammothCount);
        map.put(Animal.Kind.TIGER, tigerCount);
        int pointsForMeadow = Points.forMeadow(mammothCount, aurochsCount, deerCount);
        if (pointsForMeadow > 0) {
            return withNewMessage(textMaker.playerScoredHuntingTrap(scorer,
                            pointsForMeadow, map),
                    pointsForMeadow,
                    Set.of(scorer),
                    adjacentMeadow.tileIds());
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playerScoredLogboat) if needed.
     *
     * @param riverSystem the aquatic region in question
     * @param scorer      the player who scored
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        Message message = new Message(textMaker.playerScoredLogboat(
                scorer,
                Points.forLogboat(Area.lakeCount(riverSystem)),
                Area.lakeCount(riverSystem)),
                Points.forLogboat(Area.lakeCount(riverSystem)),
                Set.of(scorer),
                riverSystem.tileIds());
        List<Message> newMessage = new ArrayList<>(messages);
        newMessage.add(message);
        return new MessageBoard(textMaker, newMessage);
    }

    /**
     * Updates the message board with a new message (of type playerScoredMeadow) if needed.
     *
     * @param meadow           the meadow in question
     * @param cancelledAnimals the animals that were cancelled
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Map<Animal.Kind, Integer> animalCount = new HashMap<>();
        Set<Animal> animals = Area.animals(meadow, cancelledAnimals);
        for (Animal animal : animals) {
            animalCount.put(animal.kind(), animalCount.getOrDefault(animal.kind(), 0) + 1);
        }
        int pointsForMeadow = Points.forMeadow(animalCount.getOrDefault(Animal.Kind.MAMMOTH, 0),
                animalCount.getOrDefault(Animal.Kind.AUROCHS, 0),
                animalCount.getOrDefault(Animal.Kind.DEER, 0));
        if (meadow.isOccupied() && pointsForMeadow > 0) {
            return withNewMessage(textMaker.playersScoredMeadow(meadow.majorityOccupants(),
                            pointsForMeadow,
                            animalCount),
                    pointsForMeadow,
                    meadow.majorityOccupants(),
                    meadow.tileIds());
        } else {
            return this;
        }
    }


    /**
     * Updates the message board with a new message (of type playerScoredRivreSystem) if needed.
     *
     * @param riverSystem the aquatic region in question
     * @return A new message board with the added message / same message board.
     * @throws IllegalArgumentException if the river system is not occupied or the points for the river system
     *                                  are not greater than 0
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        int riverSystemFishCount = Area.riverSystemFishCount(riverSystem);
        if (riverSystem.isOccupied() && Points.forRiverSystem(riverSystemFishCount) > 0) {
            return withNewMessage(textMaker.playersScoredRiverSystem(riverSystem.majorityOccupants(),
                            Points.forRiverSystem(riverSystemFishCount),
                            riverSystemFishCount),
                    Points.forRiverSystem(riverSystemFishCount),
                    riverSystem.majorityOccupants(),
                    riverSystem.tileIds());
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playerScoredPitTrap) if needed.
     *
     * @param adjacentMeadow   adjacent meadows with animals
     * @param cancelledAnimals the animals that were cancelled
     * @return A new message board with the added message / same message board.
     * @throws IllegalArgumentException if the meadow is not occupied or
     *                                  the points for the meadow are not greater than 0
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalsWithoutCancelled = Area.animals(adjacentMeadow, cancelledAnimals);
        int aurochsCount = 0;
        int mammothCount = 0;
        int deerCount = 0;
        int tigerCount = 0;
        for (Animal animal : animalsWithoutCancelled) {
            switch (animal.kind()) {
                case AUROCHS -> aurochsCount++;
                case MAMMOTH -> mammothCount++;
                case DEER -> deerCount++;
                case TIGER -> tigerCount++;
            }
        }
        if (Points.forMeadow(mammothCount, aurochsCount, deerCount) > 0 && adjacentMeadow.isOccupied()) {
            return withNewMessage(textMaker.playersScoredPitTrap(adjacentMeadow.majorityOccupants(),
                            Points.forMeadow(mammothCount, aurochsCount, deerCount),
                            Map.of(
                                    Animal.Kind.MAMMOTH, mammothCount,
                                    Animal.Kind.AUROCHS, aurochsCount,
                                    Animal.Kind.DEER, deerCount,
                                    Animal.Kind.TIGER, tigerCount)),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    adjacentMeadow.majorityOccupants(),
                    adjacentMeadow.tileIds());
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type PlayerScoredRaft) if needed.
     *
     * @param riverSystem the aquatic area in question
     * @return A new message board with the added message / same message board.
     * @throws IllegalArgumentException if the river system is not occupied
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            return withNewMessage(textMaker.playersScoredRaft(riverSystem.majorityOccupants(),
                            Points.forRaft(Area.lakeCount(riverSystem)),
                            Area.lakeCount(riverSystem)),
                    Points.forRaft(Area.lakeCount(riverSystem)),
                    riverSystem.majorityOccupants(),
                    riverSystem.tileIds());
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playersWon) at the end of the game.
     *
     * @param winners the winners of the game
     * @param points  the points of the winners
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        Message message = new Message(textMaker.playersWon(winners, points), 0, winners, Set.of());
        List<Message> newMessage = new ArrayList<>(messages);
        newMessage.add(message);
        return new MessageBoard(textMaker, newMessage);
    }

    /**
     * Creates a new MessageBoard instance with a new message added to the existing list of messages.
     *
     * @param text    The text of the new message to be added.
     * @param points  The points associated with the new message.
     * @param scorers The set of players who scored, to be associated with the new message.
     * @param tileIds The set of tile IDs associated with the new message.
     * @return A new MessageBoard instance with the new message added to the list of messages.
     */
    private MessageBoard withNewMessage(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        List<Message> newMessages = new ArrayList<>(this.messages);
        newMessages.add(new Message(text, points, scorers, tileIds));
        return new MessageBoard(this.textMaker, newMessages);
    }

    /**
     * Represents a message in the game.
     *
     * @author Bjork Pedersen (376143)
     */
    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {

        /**
         * Constructor for Message. Makes Message immutable by copying the sets.
         *
         * @param text    The text of the message.
         * @param points  The points associated with the message.
         * @param scorers The set of players who scored.
         * @param tileIds The set of tile IDs associated with the message.
         * @throws IllegalArgumentException if the points are not greater than or equal to 0
         */
        public Message {
            Objects.requireNonNull(text);
            Preconditions.checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }

    }
}
