package ch.epfl.chacun;

/**
 * This class represents an Animal in the game.
 * An Animal can be of different kinds like MAMMOTH, AUROCHS, DEER, TIGER etc.
 * @author Bjork Pedersen (376143)
 */
public record Animal(int id, Kind kind) {

    /**
     * This enum represents the kind of an Animal.
     * @author Bjork Pedersen (376143)
     */
    public enum Kind {
        MAMMOTH,
        AUROCHS,
        DEER,
        TIGER;
    }

    /**
     * This method calculates the tileId from the given id of the animal.
     *
     * @return The id of the tile that the animal is part of.
     */
    public int tileId() {
        int zoneId = id / 10;
        return zoneId/10;
    }

}