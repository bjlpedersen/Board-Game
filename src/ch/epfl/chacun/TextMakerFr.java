package ch.epfl.chacun;

import java.util.*;

/**
 * This class is responsible for creating text messages in French for the game.
 * It implements the TextMaker interface.
 *
 * @author Bjork Pedersen (376143)
 */
public class TextMakerFr implements TextMaker {
    /**
     * A map that associates player colors with their names.
     */
    Map<PlayerColor, String> map;
    private final String MAMMOUTH_STRING = "mammouth";
    private final String AUROCH_STRING = "auroch";
    private final String CERF_STRING = "cerf";


    /**
     * Constructs a new TextMakerFr object.
     *
     * @param map A map that associates player colors with their names.
     */
    public TextMakerFr(Map<PlayerColor, String> map) {
        this.map = Map.copyOf(map);
    }

    /**
     * Converts a set of player colors to a string.
     *
     * @param scorers A set of player colors.
     * @return A string representation of the player colors.
     */
    private String scorersToString(Set<PlayerColor> scorers) {
        StringBuilder result = new StringBuilder();
        List<PlayerColor> listOfScorers = new ArrayList<>(scorers);
        List<PlayerColor> colorOrder = Arrays.asList(
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.GREEN,
                PlayerColor.YELLOW,
                PlayerColor.PURPLE);

        listOfScorers.sort(Comparator.comparing(colorOrder::indexOf));
        for (int i = 0; i < listOfScorers.size(); ++i) {
            String currentPlayerName = map.get(listOfScorers.get(i));
            if (i == listOfScorers.size() - 2) {
                result.append(STR. "\{ currentPlayerName } et " );
            } else if (i == listOfScorers.size() - 1) {
                result.append(STR. "\{ currentPlayerName }" );
            } else {
                result.append(STR. "\{ currentPlayerName }, " );
            }
        }
        return result.toString();
    }

    /**
     * Counts the number of non-zero values in a map.
     *
     * @param namesAndCounts A map where the values are to be counted.
     * @return The number of non-zero values in the map.
     */
    private int getNonZeroCount(Map<String, Integer> namesAndCounts) {
        Collection<Integer> numbers = namesAndCounts.values();
        int nonZeroCount = 0;
        for (Integer num : numbers) {
            if (num != null && num != 0) {
                nonZeroCount += 1;
            }
        }
        return nonZeroCount;
    }

    /**
     * Handles the case where there is only one of a certain animal.
     *
     * @param result       The StringBuilder to append the result to.
     * @param animal       The animal to handle.
     * @param currentCount The current count of the animal.
     * @param nonZeroCount The number of non-zero counts.
     */
    private void handleSingleAnimal(StringBuilder result, String animal, int currentCount, int nonZeroCount) {
        if (nonZeroCount == 2) {
            result.append(STR. "\{ currentCount } \{ animal } " );
        } else if (nonZeroCount != 1) {
            result.append(STR. "\{ currentCount } \{ animal }, " );
        } else if (!result.isEmpty()) {
            result.append(STR. "et \{ currentCount } \{ animal }" );
        } else {
            result.append(STR. "\{ currentCount } \{ animal }" );
        }
    }

    /**
     * Handles the case where there are multiple of a certain animal.
     *
     * @param result       The StringBuilder to append the result to.
     * @param animal       The animal to handle.
     * @param currentCount The current count of the animal.
     * @param nonZeroCount The number of non-zero counts.
     */
    private void handleMultipleAnimals(StringBuilder result, String animal, int currentCount, int nonZeroCount) {
        if (nonZeroCount == 2) {
            result.append(STR. "\{ currentCount } \{ animal }s " );
        } else if (nonZeroCount != 1) {
            result.append(STR. "\{ currentCount } \{ animal }s, " );
        } else if (!result.isEmpty()) {
            result.append(STR. "et \{ currentCount } \{ animal }s" );
        } else {
            result.append(STR. "\{ currentCount } \{ animal }" );
        }
    }

    /**
     * Converts a map of animals to a string.
     *
     * @param animals A map of animals.
     * @return A string representation of the animals.
     */
    private String mapOfAnimalsToString(Map<Animal.Kind, Integer> animals) {
        // Define the order of animals
        List<String> animalOrder = Arrays.asList(MAMMOUTH_STRING, AUROCH_STRING, CERF_STRING);

        // Create a map to store the count of each animal
        Map<String, Integer> namesAndCounts = new HashMap<>();
        namesAndCounts.put(MAMMOUTH_STRING, animals.get(Animal.Kind.MAMMOTH));
        namesAndCounts.put(AUROCH_STRING, animals.get(Animal.Kind.AUROCHS));
        namesAndCounts.put(CERF_STRING, animals.get(Animal.Kind.DEER));

        StringBuilder result = new StringBuilder();

        // Count the number of non-zero animal counts
        int nonZeroCount = getNonZeroCount(namesAndCounts);

        // Construct the result string based on the count of each animal
        for (String animal : animalOrder) {
            Integer currentCount = namesAndCounts.get(animal);

            // Skip if there are none of this animal or the count is null
            if (currentCount == null || currentCount == 0) {
                continue;
            } else if (currentCount == 1) {
                // Handle the case where there is only one of this animal
                handleSingleAnimal(result, animal, currentCount, nonZeroCount);
                nonZeroCount -= 1;
            } else {
                // Handle the case where there are multiple of this animal
                handleMultipleAnimals(result, animal, currentCount, nonZeroCount);
                nonZeroCount -= 1;
            }
        }
        return result.toString();
    }

    /**
     * Converts a set of player colors and a number of points to a string.
     *
     * @param playerColors A set of player colors.
     * @param points       The number of points.
     * @return A string representation of the player colors and the points.
     */
    private String PointstoString(Set<PlayerColor> playerColors, int points) {
        if (playerColors.size() > 1) {
            if (points > 1 || points == 0) {
                return STR. "\{ scorersToString(playerColors) } " +
                        STR. "ont remporté \{ points } points en tant qu'occupant·e·s majoritaires" ;
            }
            return STR. "\{ scorersToString(playerColors) } ont remporté \{ points } " +
                    STR."point en tant qu'occupant·e·s majoritaires";
        }
        if (points > 1 || points == 0) {
            return STR. "\{ scorersToString(playerColors) } a " +
                    STR. "remporté \{ points } points en tant qu'occupant·e majoritaire" ;
        }
        return STR. "\{ scorersToString(playerColors) } " +
                STR. "a remporté \{ points } point en tant qu'occupant·e majoritaire" ;
    }

    /**
     * Returns the name of a player.
     *
     * @param playerColor The color of the player.
     * @return The name of the player.
     */
    @Override
    public String playerName(PlayerColor playerColor) {
        return map.get(playerColor);
    }

    /**
     * Returns a string representation of a number of points.
     *
     * @param points The number of points.
     * @return A string representation of the points.
     */
    @Override
    public String points(int points) {
        if (points > 1) {
            return STR. "\{ points } points" ;
        }
        return STR. "\{ points } point" ;
    }

    /**
     * Returns a message indicating that a player closed a forest with a menhir.
     *
     * @param player The color of the player.
     * @return The message.
     */
    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return STR. "\{ map.get(player) } a fermé une forêt contenant un menhir et peut donc placer une tuile menhir." ;
    }

    /**
     * Returns a message indicating the points scored by players for a forest.
     *
     * @param scorers            The colors of the players.
     * @param points             The number of points.
     * @param mushroomGroupCount The number of mushroom groups.
     * @param tileCount          The number of tiles.
     * @return The message.
     */
    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        if (mushroomGroupCount > 1) {
            return STR. "\{ PointstoString(scorers, points) } d'une forêt " +
                    STR. "composée de \{ tileCount } tuiles et de \{ mushroomGroupCount } groupes de champignons." ;
        } else if (mushroomGroupCount == 1) {
            return STR. "\{ PointstoString(scorers, points) } d'une forêt " +
                    STR. "composée de \{ tileCount } tuiles et de \{ mushroomGroupCount } groupe de champignons." ;
        } else {
            return STR. "\{ PointstoString(scorers, points) } " +
                    STR. "d'une forêt composée de \{ tileCount } tuiles." ;
        }
    }

    /**
     * Returns a message indicating the points scored by players for a river.
     *
     * @param scorers   The colors of the players.
     * @param points    The number of points.
     * @param fishCount The number of fish.
     * @param tileCount The number of tiles.
     * @return The message.
     */
    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        if (fishCount > 0) {
            return STR. "\{ PointstoString(scorers, points) } " +
                    STR. "d'une rivière composée de \{ tileCount } tuiles et contenant \{ fishCount } poissons." ;
        } else {
            return STR. "\{ PointstoString(scorers, points) } d'une rivière composée de \{ tileCount } tuiles." ;
        }
    }

    /**
     * Returns a message indicating the points scored by a player for a hunting trap.
     *
     * @param scorer  The color of the player.
     * @param points  The number of points.
     * @param animals A map of animals.
     * @return The message.
     */
    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        if (!mapOfAnimalsToString(animals).isEmpty()) {
            return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } points en plaçant la fosse à pieux"
                    + STR. " dans un pré dans lequel elle est entourée de \{ mapOfAnimalsToString(animals) }." ;
        }
        return STR. "\{ scorersToString(Set.of(scorer)) }" +
                STR. " a remporté \{ points } points en plaçant la fosse à pieux dans un pré." ;
    }

    /**
     * Returns a message indicating the points scored by a player for a logboat.
     *
     * @param scorer    The color of the player.
     * @param points    The number of points.
     * @param lakeCount The number of lakes.
     * @return The message.
     */
    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        if (lakeCount > 1) {
            if (points > 1) {
                return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } points en plaçant " +
                        STR. "la pirogue dans un réseau hydrographique contenant \{ lakeCount } lacs." ;
            }
            return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } " +
                    STR. "point en plaçant la pirogue dans un réseau hydrographique contenant \{ lakeCount } lacs." ;
        }
        if (points > 1) {
            return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } points en " +
                    STR. " plaçant la pirogue dans un réseau hydrographique contenant \{ lakeCount } lac." ;
        }
        return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } point " +
                STR. "en plaçant la pirogue dans un réseau hydrographique contenant \{ lakeCount } lac." ;

    }

    /**
     * Returns a message indicating the points scored by players for a meadow.
     *
     * @param scorers The colors of the players.
     * @param points  The number of points.
     * @param animals A map of animals.
     * @return The message.
     */
    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        if (!mapOfAnimalsToString(animals).isEmpty()) {
            return STR. "\{ PointstoString(scorers, points) } d'un pré contenant \{ mapOfAnimalsToString(animals) }." ;
        }
        return STR. "\{ PointstoString(scorers, points) } d'un pré." ;

    }

    /**
     * Returns a message indicating the points scored by players for a river system.
     *
     * @param scorers   The colors of the players.
     * @param points    The number of points.
     * @param fishCount The number of fish.
     * @return The message.
     */
    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        if (fishCount > 1) {
            return STR. "\{ PointstoString(scorers, points) } " +
                    STR. "d'un réseau hydrographique contenant \{ fishCount } poissons." ;
        } else if (fishCount == 1) {
            return STR. "\{ PointstoString(scorers, points) } d'un réseau hydrographique " +
                    STR. "contenant \{ fishCount } poisson." ;
        }
        return STR. "\{ PointstoString(scorers, points) } d'un réseau hydrographique." ;
    }

    /**
     * Returns a message indicating the points scored by players for a pit trap.
     *
     * @param scorers The colors of the players.
     * @param points  The number of points.
     * @param animals A map of animals.
     * @return The message.
     */
    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        if (!mapOfAnimalsToString(animals).isEmpty()) {
            String middlePart = "d'un pré contenant la grande fosse à pieux entourée de";
            return STR. "\{ PointstoString(scorers, points) } \{ middlePart } \{ mapOfAnimalsToString(animals) }." ;
        }
        return STR. "\{ PointstoString(scorers, points) } d'un pré contenant la grande fosse à pieux." ;
    }

    /**
     * Returns a message indicating the points scored by players for a raft.
     *
     * @param scorers   The colors of the players.
     * @param points    The number of points.
     * @param lakeCount The number of lakes.
     * @return The message.
     */
    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        String middlePart = "d'un réseau hydrographique contenant le radeau et";
        if (lakeCount == 1) {
            return STR. "\{ PointstoString(scorers, points) } \{ middlePart } \{ lakeCount } lac." ;
        }
        return STR. "\{ PointstoString(scorers, points) } \{ middlePart } \{ lakeCount } lacs." ;
    }

    /**
     * Returns a message indicating the players who won the game.
     *
     * @param winners The colors of the winning players.
     * @param points  The number of points.
     * @return The message.
     */
    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        if (winners.size() > 1) {
            String middlePart = "ont remporté la partie avec";
            if (points > 1) {
                return STR. "\{ scorersToString(winners) } \{ middlePart } \{ points } points !" ;
            }
            return STR. "\{ scorersToString(winners) } \{ middlePart } \{ points } point !" ;
        }
        String middlePart = "a remporté la partie avec";
        if (points > 1) {
            return STR. "\{ scorersToString(winners) } \{ middlePart } \{ points } points !" ;
        }
        return STR. "\{ scorersToString(winners) } \{ middlePart } \{ points } point !" ;
    }

    /**
     * Returns a message indicating that the player should click to occupy a tile.
     *
     * @return The message.
     */
    @Override
    public String clickToOccupy() {
        return "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
    }

    /**
     * Returns a message indicating that the player should click to unoccupy a tile.
     *
     * @return The message.
     */
    @Override
    public String clickToUnoccupy() {
        return "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";
    }
}
