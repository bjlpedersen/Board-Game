package ch.epfl.chacun.gui;

import javafx.scene.image.Image;

public class ImageLoader {
    public static final int LARGE_TILE_PIXEL_SIZE = 512;
    public static final int NORMAL_TILE_PIXEL_SIZE = 256;
    public static final int LARGE_TILE_FIT_SIZE = 256;
    public static final int NORMAL_TILE_FIT_SIZE = 128;
    public static final int MARKER_PIXEL_SIZE =98;
    public static final int MARKER_FIT_SIZE = 48;

    private ImageLoader() {}

    public static Image normalImageForTile(int tileId) {
        return new Image(STR."/256/\{tileId}.jpg");
    }

    public static Image largeImageForTile(int tileId) {
        return new Image(STR."/512/\{tileId}.jpg");
    }
}
