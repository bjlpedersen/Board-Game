package ch.epfl.chacun;

import java.util.*;

/**
 * Represents a message board in the game.
 * @author Bjork Pedersen (376143)
 */
public record MessageBoard(TextMaker textMaker, List<Message> messages){

    /**
     * Constructor for MessageBoard. Makes MessageBoard immutable by copying the list.
     * @param textMaker The text maker for the messages.
     * @param messages The list of messages.
     */
    public MessageBoard {
        messages = List.copyOf(messages);
    }

    /**
     * Calculates the points for each player.
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
     * @param forest the forest that has just been closed
     * @return A new message board with the added message / same message board.
     * @throws IllegalArgumentException if the forest is not occupied
     */
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if (forest.isOccupied()) {
            List<Message> increasedMessages = new ArrayList<>(messages);
            int points = Points.forClosedForest(forest.zones().size(), Area.mushroomGroupCount(forest));
            if (points > 0) {
                increasedMessages.add(new Message(textMaker.playersScoredForest(forest.majorityOccupants(),
                        Points.forClosedForest(forest.zones().size(), Area.mushroomGroupCount(forest)),
                        Area.mushroomGroupCount(forest),
                        forest.tileIds().size()),
                        points,
                        forest.majorityOccupants(),
                        forest.tileIds()));
            } else {
                increasedMessages.add(new Message(textMaker.playersScoredForest(forest.majorityOccupants(),
                        Points.forClosedForest(forest.zones().size(), Area.mushroomGroupCount(forest)),
                        Area.mushroomGroupCount(forest),
                        forest.tileIds().size()),
                        0,
                        Set.of(),
                        forest.tileIds()));
            }
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }


    /**
     * Updates the message board with a new message (of type playerScoredWithMenhir) if needed.
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
     * @param river the river in question
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (river.isOccupied()) {
            List<Message> increasedMessages = new ArrayList<>(messages);
            increasedMessages.add(new Message(textMaker.playersScoredRiver(river.majorityOccupants(),
                    Points.forClosedRiver(river.zones().size(), Area.riverFishCount(river)),
                    Area.riverFishCount(river),
                    river.tileIds().size()),
                    Points.forClosedRiver(river.zones().size(), Area.riverFishCount(river)),
                    river.majorityOccupants(),
                    river.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playerScoredHuntingTrap) if needed.
     * @param adjacentMeadow the meadow adjacent to the hunting trap (we need it's animals)
     * @param scorer the player who scored
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow) {
        Set<Animal> animals = Area.animals(adjacentMeadow, Set.of());
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
        if (Points.forMeadow(mammothCount, aurochsCount, deerCount) > 0) {
            List<Message> increasedMessages = new ArrayList<>(messages);
            increasedMessages.add(new Message(textMaker.playerScoredHuntingTrap(scorer, Points.forMeadow(mammothCount, aurochsCount, deerCount), map),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    Set.of(scorer),
                    adjacentMeadow.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playerScoredLogboat) if needed.
     * @param riverSystem the aquatic region in question
     * @param scorer the player who scored
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        Message message = new Message(textMaker.playerScoredLogboat(scorer, Points.forLogboat(Area.lakeCount(riverSystem)), Area.lakeCount(riverSystem)),
                Points.forLogboat(Area.lakeCount(riverSystem)),
                Set.of(scorer),
                riverSystem.tileIds());
        List<Message> newMessage = new ArrayList<>(messages);
        newMessage.add(message);
        return new MessageBoard(textMaker, newMessage);
    }

    /**
     * Updates the message board with a new message (of type playerScoredMeadow) if needed.
     * @param meadow the meadow in question
     * @param cancelledAnimals the animals that were cancelled
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        int mammothCount = 0;
        int aurochsCount = 0;
        int deerCount = 0;
        int tigerCount = 0;
        Set<Animal> animals = Area.animals(meadow, cancelledAnimals);
        for (Animal animal : animals) {
            switch (animal.kind()) {
                case MAMMOTH -> mammothCount++;
                case AUROCHS -> aurochsCount++;
                case DEER -> deerCount++;
                case TIGER -> tigerCount++;
            }
        }

        Map<Animal.Kind, Integer> fullAnimalMap = Map.of(Animal.Kind.MAMMOTH,
                mammothCount, Animal.Kind.AUROCHS,
                aurochsCount, Animal.Kind.DEER, deerCount,
                Animal.Kind.TIGER, tigerCount);
        Map<Animal.Kind, Integer> reducedAnimalMap = new HashMap<>();
        for (Animal.Kind kind : fullAnimalMap.keySet()) {
            if (fullAnimalMap.get(kind) > 0) {
                reducedAnimalMap.put(kind, fullAnimalMap.get(kind));
            }
        }

        if (meadow.isOccupied() && Points.forMeadow(mammothCount, aurochsCount, deerCount) > 0) {
            List<Message> increasedMessages = new ArrayList<>(messages);
            increasedMessages.add(new Message(textMaker.playersScoredMeadow(meadow.majorityOccupants(),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    reducedAnimalMap),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    meadow.majorityOccupants(),
                    meadow.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playerScoredRivreSystem) if needed.
     * @param riverSystem the aquatic region in question
     * @return A new message board with the added message / same message board.
     * @throws IllegalArgumentException if the river system is not occupied or the points for the river system are not greater than 0
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied() && Points.forRiverSystem(Area.riverSystemFishCount(riverSystem)) > 0) {
            List<Message> increasedMessages = new ArrayList<>(messages);
            increasedMessages.add(new Message(textMaker.playersScoredRiverSystem(riverSystem.majorityOccupants(),
                    Points.forRiverSystem(Area.riverSystemFishCount(riverSystem)),
                    Area.riverSystemFishCount(riverSystem)),
                    Points.forRiverSystem(Area.riverSystemFishCount(riverSystem)),

                    riverSystem.majorityOccupants(),
                    riverSystem.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playerScoredPitTrap) if needed.
     * @param adjacentMeadow adjacent meadows with animals
     * @param cancelledAnimals the animals that were cancelled
     * @return A new message board with the added message / same message board.
     * @throws IllegalArgumentException if the meadow is not occupied or the points for the meadow are not greater than 0
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
        if (Points.forMeadow(mammothCount, aurochsCount, deerCount) > 0  && adjacentMeadow.isOccupied()) {
            List<Message> increasedMessages = new ArrayList<>(messages);
            increasedMessages.add(new Message(textMaker.playersScoredPitTrap(adjacentMeadow.majorityOccupants(),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    Map.of(Animal.Kind.MAMMOTH, mammothCount, Animal.Kind.AUROCHS, aurochsCount, Animal.Kind.DEER, deerCount, Animal.Kind.TIGER, tigerCount)),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    adjacentMeadow.majorityOccupants(),
                    adjacentMeadow.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type PlayerScoredRaft) if needed.
     * @param riverSystem the aquatic area in question
     * @return A new message board with the added message / same message board.
     * @throws IllegalArgumentException if the river system is not occupied
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            List<Message> increasedMessages = new ArrayList<>(messages);
            increasedMessages.add(new Message(textMaker.playersScoredRaft(riverSystem.majorityOccupants(),
                    Points.forRaft(Area.lakeCount(riverSystem)),
                    Area.lakeCount(riverSystem)),
                    Points.forRaft(Area.lakeCount(riverSystem)),
                    riverSystem.majorityOccupants(),
                    riverSystem.tileIds()));

            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    /**
     * Updates the message board with a new message (of type playersWon) at the end of the game.
     * @param winners the winners of the game
     * @param points the points of the winners
     * @return A new message board with the added message / same message board.
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        Message message = new Message(textMaker.playersWon(winners, points), 0, winners, Set.of());
        List<Message> newMessage = new ArrayList<>(messages);
        newMessage.add(message);
        return new MessageBoard(textMaker, newMessage);
    }

    /**
     * Represents a message in the game.
     * @author Bjork Pedersen (376143)
     */
    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {

        /**
         * Constructor for Message. Makes Message immutable by copying the sets.
         * @param text The text of the message.
         * @param points The points associated with the message.
         * @param scorers The set of players who scored.
         * @param tileIds The set of tile IDs associated with the message.
         * @throws IllegalArgumentException if the points are not greater than or equal to 0
         */
        public Message {
            if (text == null) {
                throw new NullPointerException();
            }
            Preconditions.checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }

    }
}
