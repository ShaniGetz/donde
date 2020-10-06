package com.example.donde.utils.map_utils;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class CustomMapTileProvider implements TileProvider {
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE = 16 * 1024;


    //    private AssetManager mAssets;
    private String tilesDir;

    public CustomMapTileProvider(String tilesDir) {
        this.tilesDir = tilesDir;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        byte[] image = new byte[0];
        try {
            image = readTileImage(x, y, zoom);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
    }

    private byte[] readTileImage(int x, int y, int zoom) throws IOException {
        ByteArrayOutputStream buffer = null;
        FileInputStream fileInputStream = null;

        try {

            fileInputStream = new FileInputStream(tilesDir + "/firstmap.png");
            buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[BUFFER_SIZE];

            while ((nRead = fileInputStream.read(data, 0, BUFFER_SIZE)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (Exception ignored) {
            }
            if (buffer != null) try {
                buffer.close();
            } catch (Exception ignored) {
            }
        }
    }

    private String getTileFilename(int x, int y, int zoom) {
        return "map/" + zoom + '/' + x + '/' + y + ".png";
    }
}