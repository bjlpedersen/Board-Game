package ch.epfl.chacun;

import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class TextMakerImplementation implements TextMaker {

    @Override
    public String playerName(PlayerColor playerColor) {
        return playerColor.toString();
    }

    @Override
    public String points(int points) {
        return points + " points";
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return player.toString() + " closed a forest with a menhir";
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        return new StringJoiner(" ")
                .add(scorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(mushroomGroupCount))
                .add(String.valueOf(tileCount))
                .toString();
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        return new StringJoiner(" ")
                .add(scorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(fishCount))
                .add(String.valueOf(tileCount))
                .toString();
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        return new StringJoiner(" ")
                .add(scorer.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(animals))
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
        return new StringJoiner(" ")
                .add(scorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(animals))
                .toString();
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        return new StringJoiner(" ")
                .add(scorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(fishCount))
                .toString();
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return new StringJoiner(" ")
                .add(scorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(animals))
                .toString();
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return new StringJoiner(" ")
                .add(scorers.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(lakeCount))
                .toString();
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return new StringJoiner(" ")
                .add(winners.toString())
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
