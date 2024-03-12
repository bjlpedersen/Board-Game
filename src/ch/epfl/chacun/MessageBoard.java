package ch.epfl.chacun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record MessageBoard(TextMaker textMaker, List<Message> messages){

    public MessageBoard {
        messages = List.copyOf(messages);
    }

    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> result = new HashMap<>();
        for (Message message : messages) {
            for (PlayerColor player : message.scorers()) {
                result.put(player, result.getOrDefault(player, 0) + message.points());
            }
        }
        return result;
    }

    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if (forest.isOccupied()) {
            List<Message> increasedMessages = messages;
            increasedMessages.add(new Message(textMaker.playersScoredForest(forest.majorityOccupants(),
                    Points.forClosedForest(forest.zones().size(), Area.mushroomGroupCount(forest)),
                    Area.mushroomGroupCount(forest),
                    forest.tileIds().size()),
                    Points.forClosedForest(forest.zones().size(), Area.mushroomGroupCount(forest)),
                    forest.majorityOccupants(),
                    forest.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        Message message = new Message(textMaker.playerClosedForestWithMenhir(player),
                0,
                forest.majorityOccupants(),
                forest.tileIds());
        List<Message> newMessage = messages;
        newMessage.add(message);
        return new MessageBoard(textMaker, newMessage);
    }

    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (river.isOccupied()) {
            List<Message> increasedMessages = messages;
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
            List<Message> increasedMessages = messages;
            increasedMessages.add(new Message(textMaker.playerScoredHuntingTrap(scorer, Points.forMeadow(mammothCount, aurochsCount, deerCount), map),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    Set.of(scorer),
                    adjacentMeadow.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        Message message = new Message(textMaker.playerScoredLogboat(scorer, Points.forRiverSystem(Area.riverSystemFishCount(riverSystem)), Area.lakeCount(riverSystem)),
                Points.forRiverSystem(Area.riverSystemFishCount(riverSystem)),
                Set.of(scorer),
                riverSystem.tileIds());
        List<Message> newMessage = messages;
        newMessage.add(message);
        return new MessageBoard(textMaker, newMessage);
    }

    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        int mammothCount = 0;
        int aurochsCount = 0;
        int deerCount = 0;
        Set<Animal> animals = Area.animals(meadow, cancelledAnimals);
        for (Animal animal : animals) {
            switch (animal.kind()) {
                case MAMMOTH -> mammothCount++;
                case AUROCHS -> aurochsCount++;
                case DEER -> deerCount++;
            }
        }
        if (meadow.isOccupied() && Points.forMeadow(mammothCount, aurochsCount, deerCount) > 0) {
            List<Message> increasedMessages = messages;
            increasedMessages.add(new Message(textMaker.playersScoredMeadow(meadow.majorityOccupants(),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    Map.of(Animal.Kind.MAMMOTH, mammothCount, Animal.Kind.AUROCHS, aurochsCount, Animal.Kind.DEER, deerCount)),

                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    meadow.majorityOccupants(),
                    meadow.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied() && Points.forRiverSystem(Area.riverSystemFishCount(riverSystem)) > 0) {
            List<Message> increasedMessages = messages;
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

    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animalsWithoutCancelled = Area.animals(adjacentMeadow, cancelledAnimals);
        int aurochsCount = 0;
        int mammothCount = 0;
        int deerCount = 0;
        for (Animal animal : animalsWithoutCancelled) {
            switch (animal.kind()) {
                case AUROCHS -> aurochsCount++;
                case MAMMOTH -> mammothCount++;
                case DEER -> deerCount++;
            }
        }
        if (Points.forMeadow(mammothCount, aurochsCount, deerCount) > 0) {
            List<Message> increasedMessages = messages;
            increasedMessages.add(new Message(textMaker.playersScoredPitTrap(adjacentMeadow.majorityOccupants(),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    Map.of(Animal.Kind.MAMMOTH, mammothCount, Animal.Kind.AUROCHS, aurochsCount, Animal.Kind.DEER, deerCount)),
                    Points.forMeadow(mammothCount, aurochsCount, deerCount),
                    adjacentMeadow.majorityOccupants(),
                    adjacentMeadow.tileIds()));
            return new MessageBoard(textMaker, increasedMessages);
        } else {
            return this;
        }
    }

    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            List<Message> increasedMessages = messages;
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

    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        Message message = new Message(textMaker.playersWon(winners, points), points, winners, Set.of());
        List<Message> newMessage = messages;
        newMessage.add(message);
        return new MessageBoard(textMaker, newMessage);
    }

    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {

        public Message {
            Preconditions.checkArgument(text != null);
            Preconditions.checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }

    }
}
