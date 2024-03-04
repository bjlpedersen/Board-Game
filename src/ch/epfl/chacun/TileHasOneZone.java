package ch.epfl.chacun;

import java.util.function.Predicate;

public final class TileHasOneZone implements Predicate<Tile> {
    @Override
    public boolean test(Tile tile) {
        return tile.zones().size() == 1;
    }
}
