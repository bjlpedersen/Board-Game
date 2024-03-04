package ch.epfl.chacun;

/**
 * This class provides static methods to compute the points scored by a player
 * for various features of the game.
 * @author Bjork Pedersen (376143)
 */
public final class Points {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Points() {}

    /**
     * Computes the points for a closed forest.
     *
     * @param tileCount the number of tiles in the forest
     * @param mushroomGroupCount the number of mushroom groups in the forest
     * @throws IllegalArgumentException if the tile count is less than or equal to 1
     * and if the mushroom group count is less than 0
     * @return the points scored for the forest
     */
    public static int forClosedForest(int tileCount, int mushroomGroupCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(mushroomGroupCount >= 0);
        return 2*tileCount + 3*mushroomGroupCount;
    }

    /**
     * Computes the points for a closed river.
     *
     * @param tileCount the number of tiles in the river
     * @param fishCount the number of fish in the river
     * @throws IllegalArgumentException if the tile count is less than or equal to 1 or
     * if the fish count is less than 0
     * @return the points scored for the river
     */
    public static int forClosedRiver(int tileCount, int fishCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(fishCount >= 0);
        return 1*tileCount + 1*fishCount;
    }

    /**
     * Computes the points for a meadow.
     *
     * @param mammothCount the number of mammoths in the meadow
     * @param aurochsCount the number of aurochs in the meadow
     * @param deerCount the number of deer in the meadow
     * @throws IllegalArgumentException if any of the counts are less than 0
     * @return the points scored for the meadow
     */
    public static int forMeadow(int mammothCount, int aurochsCount, int deerCount) {
        Preconditions.checkArgument(mammothCount >= 0);
        Preconditions.checkArgument(aurochsCount >= 0);
        Preconditions.checkArgument(deerCount >= 0);
        return 3*mammothCount + 2*aurochsCount + 1*deerCount;
    }

    /**
     * Computes the points for a river system.
     *
     * @param fishCount the number of fish in the river system
     * @throws IllegalArgumentException if the fish count is less than 0
     * @return the points scored for the river system
     */
    public static int forRiverSystem(int fishCount) {
        Preconditions.checkArgument(fishCount >= 0);
        return fishCount;
    }

    /**
     * Computes the points for a logboat.
     *
     * @param lakeCount the number of lakes in the logboat
     * @throws IllegalArgumentException if the lake count is less than or equal to 0
     * @return the points scored for the logboat
     */
    public static int forLogboat(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return 2*lakeCount;
    }

    /**
     * Computes the points for a raft.
     *
     * @param lakeCount the number of lakes in the raft
     * @throws IllegalArgumentException if the lake count is less than or equal to 0
     * @return the points scored for the raft
     */
    public static int forRaft(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return lakeCount;
    }
}