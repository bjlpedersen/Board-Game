package ch.epfl.chacun;
/**
 * @author Nikita Karpachev (373884)
 * @author Adrian Mikhaiel (380437)
 */

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class TextMakerFrTest {
    TreeMap<PlayerColor, String> names = new TreeMap<>();

    void generateNames() {
        names.put(PlayerColor.BLUE, "Claude");
        names.put(PlayerColor.RED, "Dalia");
        names.put(PlayerColor.GREEN, "Bachir");
        names.put(PlayerColor.YELLOW, "Alice");
        names.put( PlayerColor.PURPLE, "Eva");
    }

    @Test
    void testIsImmuable() {
    }

    @Test
    void playerNameWorks() {
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        assertEquals("Dalia", textMaker.playerName(PlayerColor.RED));
    }

    @Test
    void pointsWorks() {
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        assertEquals("3 points", textMaker.points(3));
        assertEquals("1 point", textMaker.points(1));
    }


    @Test
    void playerClosedForestWithMenhirWorks() {
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        assertEquals("Dalia a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.", textMaker.playerClosedForestWithMenhir(PlayerColor.RED));
        assertEquals("Claude a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.", textMaker.playerClosedForestWithMenhir(PlayerColor.BLUE));
        assertEquals("Bachir a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.", textMaker.playerClosedForestWithMenhir(PlayerColor.GREEN));
        assertEquals("Alice a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.", textMaker.playerClosedForestWithMenhir(PlayerColor.YELLOW));
    }

    @Test
    void playersScoredForestWorks() {
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        assertEquals("Claude, Bachir et Alice ont remporté 3 points en tant qu'occupant·e·s majoritaires" +
                        " d'une forêt composée de 4 tuiles et de 2 groupes de champignons."
                , textMaker.playersScoredForest(Set.of(PlayerColor.YELLOW, PlayerColor.GREEN, PlayerColor.BLUE),
                        3, 2, 4));

        assertEquals("Claude a remporté 3 points en tant qu'occupant·e majoritaire " +
                        "d'une forêt composée de 4 tuiles et de 2 groupes de champignons."
                , textMaker.playersScoredForest(Set.of(PlayerColor.BLUE), 3, 2, 4));

        assertEquals("Claude et Bachir ont remporté 3 points en tant qu'occupant·e·s majoritaires" +
                        " d'une forêt composée de 4 tuiles et de 2 groupes de champignons."
                , textMaker.playersScoredForest(Set.of(PlayerColor.BLUE, PlayerColor.GREEN), 3, 2, 4));

        assertEquals("Dalia et Alice ont remporté 9 points en tant qu'occupant·e·s majoritaires" +
                " d'une forêt composée de 3 tuiles et de 1 groupe de champignons."
                , textMaker.playersScoredForest(Set.of(PlayerColor.RED, PlayerColor.YELLOW), 9, 1, 3));

        assertEquals("Claude a remporté 6 points en tant qu'occupant·e majoritaire " +
                "d'une forêt composée de 3 tuiles.", textMaker.playersScoredForest(Set.of(PlayerColor.BLUE)
                , 6, 0, 3));
    }

    @Test
    void playersScoredRiverWorks() {
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        assertEquals("Claude et Bachir ont remporté 3 points en tant qu'occupant·e·s majoritaires " +
                        "d'une rivière composée de 3 tuiles.", textMaker.playersScoredRiver(Set.of(PlayerColor.BLUE, PlayerColor.GREEN)
                , 3, 0, 3));

        assertEquals("Alice a remporté 8 points en tant qu'occupant·e majoritaire " +
                "d'une rivière composée de 3 tuiles et contenant 5 poissons.", textMaker.playersScoredRiver(Set.of(PlayerColor.YELLOW)
                , 8, 5, 3));

        assertEquals("Claude, Bachir et Alice ont remporté 3 points en tant qu'occupant·e·s majoritaires " +
                "d'une rivière composée de 3 tuiles et contenant 5 poissons.", textMaker.playersScoredRiver(Set.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.YELLOW)
                , 3, 5, 3));
    }

    @Test
    void playerScoredHuntingTrapWorks() {
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        HashMap<Animal.Kind, Integer> animals = new HashMap<>();
        animals.put(Animal.Kind.MAMMOTH, 1);
        animals.put(Animal.Kind.AUROCHS, 2);
        animals.put(Animal.Kind.DEER, 3);

        assertEquals("Bachir a remporté 10 points en plaçant la fosse à pieux dans un pré " +
                        "dans lequel elle est entourée de 1 mammouth, 2 aurochs et 3 cerfs."
                , textMaker.playerScoredHuntingTrap(PlayerColor.GREEN, 10, animals));

        HashMap<Animal.Kind, Integer> animals2 = new HashMap<>();
        animals2.put(Animal.Kind.MAMMOTH, 0);
        animals2.put(Animal.Kind.AUROCHS, 2);
        animals2.put(Animal.Kind.DEER, 3);

        assertEquals("Dalia a remporté 10 points en plaçant la fosse à pieux dans un pré " +
                        "dans lequel elle est entourée de 2 aurochs et 3 cerfs."
                , textMaker.playerScoredHuntingTrap(PlayerColor.RED, 10, animals2));

        HashMap<Animal.Kind, Integer> animals3 = new HashMap<>();
        animals3.put(Animal.Kind.MAMMOTH, 0);
        animals3.put(Animal.Kind.AUROCHS, 0);
        animals3.put(Animal.Kind.DEER, 0);

        assertEquals("Dalia a remporté 0 points en plaçant la fosse à pieux dans un pré.",
                textMaker.playerScoredHuntingTrap(PlayerColor.RED, 0, animals3));
    }


    @Test
    void playerScoredLogBoatWorks() {
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        assertEquals("Alice a remporté 8 points en plaçant la pirogue dans un réseau hydrographique contenant 4 lacs."
                , textMaker.playerScoredLogboat(PlayerColor.YELLOW, 8, 4));

        //Pas sur que ce cas soit possible
        //assertEquals("Bachir a remporté 0 points en plaçant la pirogue dans un réseau hydrographique.",
                //textMaker.playerScoredLogboat(PlayerColor.GREEN, 0, 0));
    }

    @Test
    void playersScoredMeadow(){
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        HashMap<Animal.Kind, Integer> animals = new HashMap<>();
        animals.put(Animal.Kind.MAMMOTH, 0);
        animals.put(Animal.Kind.AUROCHS, 0);
        animals.put(Animal.Kind.DEER, 1);

        assertEquals("Dalia a remporté 1 point en tant qu'occupant·e majoritaire d'un pré contenant 1 cerf.",
                textMaker.playersScoredMeadow(Set.of(PlayerColor.RED), 1, animals));

        HashMap<Animal.Kind, Integer> animals2 = new HashMap<>();
        animals2.put(Animal.Kind.MAMMOTH, 1);
        animals2.put(Animal.Kind.AUROCHS, 0);
        animals2.put(Animal.Kind.DEER, 2);

        assertEquals("Claude et Bachir ont remporté 5 points en tant qu'occupant·e·s majoritaires " +
                        "d'un pré contenant 1 mammouth et 2 cerfs.",
                textMaker.playersScoredMeadow(Set.of(PlayerColor.BLUE, PlayerColor.GREEN), 5, animals2));

        HashMap<Animal.Kind, Integer> animals3 = new HashMap<>();
        animals3.put(Animal.Kind.MAMMOTH, 0);
        animals3.put(Animal.Kind.AUROCHS, 0);
        animals3.put(Animal.Kind.DEER, 0);

        assertEquals("Dalia, Claude et Bachir ont remporté 0 points en tant qu'occupant·e·s majoritaires d'un pré.",
                textMaker.playersScoredMeadow(Set.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN), 0, animals3));
    }

    @Test
    void playersScoredRiverSystem(){
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));

        assertEquals("Alice a remporté 9 points en tant qu'occupant·e majoritaire d'un réseau hydrographique contenant 9 poissons.",
                textMaker.playersScoredRiverSystem(Set.of(PlayerColor.YELLOW), 9, 9));

        assertEquals("Dalia, Claude et Bachir ont remporté 1 point en tant qu'occupant·e·s majoritaires d'un réseau hydrographique contenant 1 poisson.",
                textMaker.playersScoredRiverSystem(Set.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN), 1, 1));

        assertEquals("Alice a remporté 9 points en tant qu'occupant·e majoritaire d'un réseau hydrographique.",
                textMaker.playersScoredRiverSystem(Set.of(PlayerColor.YELLOW), 9, 0));
    }

    @Test
    void playersScoredPitTrap(){
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));
        HashMap<Animal.Kind, Integer> animals = new HashMap<>();
        animals.put(Animal.Kind.MAMMOTH, 2);
        animals.put(Animal.Kind.AUROCHS, 2);
        animals.put(Animal.Kind.DEER, 2);

        assertEquals("Bachir et Alice ont remporté 12 points en tant qu'occupant·e·s majoritaires " +
                        "d'un pré contenant la grande fosse à pieux entourée de 2 mammouths, 2 aurochs et 2 cerfs.",
                textMaker.playersScoredPitTrap(Set.of(PlayerColor.GREEN, PlayerColor.YELLOW), 12, animals));

        HashMap<Animal.Kind, Integer> animals2 = new HashMap<>();
        animals2.put(Animal.Kind.MAMMOTH, 0);
        animals2.put(Animal.Kind.AUROCHS, 1);
        animals2.put(Animal.Kind.DEER, 0);

        assertEquals("Dalia a remporté 2 points en tant qu'occupant·e majoritaire " +
                        "d'un pré contenant la grande fosse à pieux entourée de 1 auroch.",
                textMaker.playersScoredPitTrap(Set.of(PlayerColor.RED), 2, animals2));

        HashMap<Animal.Kind, Integer> animals3 = new HashMap<>();
        animals3.put(Animal.Kind.MAMMOTH, 0);
        animals3.put(Animal.Kind.AUROCHS, 0);
        animals3.put(Animal.Kind.DEER, 0);

        assertEquals("Dalia, Claude et Bachir ont remporté 0 points en tant qu'occupant·e·s majoritaires " +
                        "d'un pré contenant la grande fosse à pieux.",
                textMaker.playersScoredPitTrap(Set.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN), 0, animals3));
    }

    @Test
    void playersScoredRaft(){
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));

        assertEquals("Dalia et Claude ont remporté 10 points en tant qu'occupant·e·s majoritaires " +
                        "d'un réseau hydrographique contenant le radeau et 10 lacs.",
                textMaker.playersScoredRaft(Set.of(PlayerColor.RED, PlayerColor.BLUE), 10, 10));

        assertEquals("Alice a remporté 1 point en tant qu'occupant·e majoritaire " +
                        "d'un réseau hydrographique contenant le radeau et 1 lac.",
                textMaker.playersScoredRaft(Set.of(PlayerColor.YELLOW), 1, 1));

        //Pas sur que ce cas soit possible
        /*assertEquals("Dalia a remporté 0 points en tant qu'occupant·e majoritaire " +
                        "d'un réseau hydrographique contenant le radeau.",
                textMaker.playersScoredRaft(Set.of(PlayerColor.RED), 0, 0));*/

    }

    @Test
    void playersWon(){
        generateNames();
        var textMaker = new TextMakerFr(new TreeMap<>(names));

        assertEquals("Bachir a remporté la partie avec 111 points !", textMaker.playersWon(Set.of(PlayerColor.GREEN), 111));
        assertEquals("Dalia et Alice ont remporté la partie avec 123 points !", textMaker.playersWon(Set.of(PlayerColor.RED, PlayerColor.YELLOW), 123));
    }



}



















