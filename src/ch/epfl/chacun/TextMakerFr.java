package ch.epfl.chacun;

import java.util.*;

public class TextMakerFr implements TextMaker {
    Map<PlayerColor, String> map;

    public TextMakerFr(Map<PlayerColor, String> map) {
        this.map = Map.copyOf(map);
    }

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
            } else if (i == listOfScorers.size() - 1){
                result.append(STR. "\{ currentPlayerName }" );
            } else {
                result.append(STR. "\{currentPlayerName}, ");
            }
        }
        return result.toString();
    }

    private String mapOfAnimalsToString(Map<Animal.Kind, Integer> animals) {
        List<String> animalOrder = Arrays.asList("mammouth", "auroch", "cerf");
        Map<String, Integer> namesAndCounts = new HashMap<>();
        namesAndCounts.put("mammouth", animals.get(Animal.Kind.MAMMOTH));
        namesAndCounts.put("auroch", animals.get(Animal.Kind.AUROCHS));
        namesAndCounts.put("cerf", animals.get(Animal.Kind.DEER));

        StringBuilder result = new StringBuilder();

        Collection<Integer> numbers = namesAndCounts.values();
        int nonZeroCount = 0;
        for (int num : numbers) {
            if (num != 0) {
                nonZeroCount += 1;
            }
        }

        for (String animal : animalOrder) {
            int currentCount = namesAndCounts.get(animal);

            //Skip if there are none of this animal
            if (currentCount == 0) {
                continue;
            } else if (currentCount == 1){
                if (nonZeroCount == 2) {
                    result.append(STR. "\{currentCount} \{animal} ");
                    nonZeroCount-= 1;
                } else if (nonZeroCount != 1) {
                    result.append(STR. "\{currentCount} \{animal}, ");
                    nonZeroCount-= 1;
                } else if (!result.isEmpty()){
                    result.append(STR. "et \{currentCount} \{animal}");
                } else {
                    result.append(STR."\{currentCount} \{animal}");
                }
            } else {
                if (nonZeroCount == 2) {
                    result.append(STR. "\{currentCount} \{animal}s ");
                    nonZeroCount-= 1;
                } else if (nonZeroCount != 1) {
                    result.append(STR. "\{currentCount} \{animal}s, ");
                    nonZeroCount-= 1;
                } else if (!result.isEmpty()){
                    result.append(STR. "et \{currentCount} \{animal}s");
                } else {
                    result.append(STR."\{currentCount} \{animal}");
                }
            }

        }
        return result.toString();
    }

    private String PointstoString(Set<PlayerColor> playerColors, int points) {
        if (playerColors.size() > 1) {
            if (points > 1 || points == 0) {
                return STR. "\{ scorersToString(playerColors) } ont remporté \{ points } points en tant qu'occupant·e·s majoritaires" ;
            }
            return STR. "\{ scorersToString(playerColors) } ont remporté \{ points } point en tant qu'occupant·e·s majoritaires" ;
        }
        if (points > 1 || points == 0) {
            return STR. "\{ scorersToString(playerColors) } a remporté \{ points } points en tant qu'occupant·e majoritaire" ;
        }
        return STR. "\{ scorersToString(playerColors) } a remporté \{ points } point en tant qu'occupant·e majoritaire" ;
    }

    @Override
    public String playerName(PlayerColor playerColor) {
        return map.get(playerColor);
    }

    @Override
    public String points(int points) {
        if (points > 1) {
            return STR. "\{ points } points" ;
        }
        return STR. "\{ points } point";
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return STR."\{map.get(player)} a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        if (mushroomGroupCount > 1) {
            return STR. "\{PointstoString(scorers, points)} d'une forêt composée de \{ tileCount } tuiles et de \{ mushroomGroupCount } groupes de champignons.";
        } else if (mushroomGroupCount == 1) {
            return STR. "\{PointstoString(scorers, points)} d'une forêt composée de \{ tileCount } tuiles et de \{ mushroomGroupCount } groupe de champignons.";
        } else {
            return STR. "\{PointstoString(scorers, points)} d'une forêt composée de \{ tileCount } tuiles.";
        }
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        if (fishCount > 0) {
            return STR. "\{PointstoString(scorers, points)} d'une rivière composée de \{tileCount} tuiles et contenant \{fishCount} poissons.";
        } else {
            return STR. "\{PointstoString(scorers, points)} d'une rivière composée de \{ tileCount } tuiles.";
            }
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        if (!mapOfAnimalsToString(animals).isEmpty()) {
            return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } points en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de \{ mapOfAnimalsToString(animals) }." ;
        }
        return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } points en plaçant la fosse à pieux dans un pré." ;
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        if (lakeCount > 1) {
            if (points > 1) {
                return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } points en plaçant la pirogue dans un réseau hydrographique contenant \{ lakeCount } lacs." ;
            }
            return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } point en plaçant la pirogue dans un réseau hydrographique contenant \{ lakeCount } lacs." ;
        }
        if (points > 1) {
            return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } points en plaçant la pirogue dans un réseau hydrographique contenant \{ lakeCount } lac." ;
        }
        return STR. "\{ scorersToString(Set.of(scorer)) } a remporté \{ points } point en plaçant la pirogue dans un réseau hydrographique contenant \{ lakeCount } lac." ;

    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        if (!mapOfAnimalsToString(animals).isEmpty()) {
            return STR."\{PointstoString(scorers, points)} d'un pré contenant \{mapOfAnimalsToString(animals)}.";
        }
        return STR."\{PointstoString(scorers, points)} d'un pré.";

    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        if (fishCount > 1) {
            return STR. "\{PointstoString(scorers, points)} d'un réseau hydrographique contenant \{fishCount} poissons.";
        } else if (fishCount == 1) {
            return STR. "\{PointstoString(scorers, points)} d'un réseau hydrographique contenant \{fishCount} poisson.";
        }
        return STR. "\{PointstoString(scorers, points)} d'un réseau hydrographique.";
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        if (!mapOfAnimalsToString(animals).isEmpty()) {
            String middlePart = "d'un pré contenant la grande fosse à pieux entourée de";
            return STR. "\{ PointstoString(scorers, points) } \{ middlePart } \{ mapOfAnimalsToString(animals) }." ;
        }
        return STR."\{ PointstoString(scorers, points) } d'un pré contenant la grande fosse à pieux.";
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        String middlePart = "d'un réseau hydrographique contenant le radeau et";
        if (lakeCount == 1) {
            return STR. "\{ PointstoString(scorers, points) } \{ middlePart } \{ lakeCount } lac." ;
        }
        return STR. "\{ PointstoString(scorers, points) } \{ middlePart } \{ lakeCount } lacs." ;
    }

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

    @Override
    public String clickToOccupy() {
        return "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
    }

    @Override
    public String clickToUnoccupy() {
        return "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";
    }
}
