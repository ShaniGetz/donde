package com.bas.donde.utils.map_utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class OfflineTileProvider implements TileProvider {
    // TODO: I (alon) moved this class to a new file. Before, it was inside CreateEventActivity.
    //  Check if this broke anything.

    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE_FILE = 16384;
    private static final int BUFFER_SIZE_NETWORK = 8192;
    private String tilesDir;
    private Context context;
    private ConnectivityManager connectivityManager;


    public OfflineTileProvider(Context context) {
        super();
        this.context = context;
        tilesDir = context.getApplicationContext().getFilesDir().toString();
//        Log.d("TAG", tilesDir);

    }

    @Override
    public Tile getTile(int x, int y, int z) {

//        Log.d("TAG", "OfflineTileProvider.getTile(" + x + ", " + y + ", " + z + ")");
        try {
            byte[] data;
            //
//                File file = new File(TILES_DIR, x + "_" + y + ".png");
            File fileOne = new File(tilesDir, x +"_"+y+ ".png");

//            Log.d("getTile", tilesDir + x +"_"+y+ ".png");
            if (fileOne.exists()) {
                data = readTile(new FileInputStream(fileOne), BUFFER_SIZE_FILE);
//                Log.d("reading from file", tilesDir + x +"_"+y+ ".png");

            } else {
                if (connectivityManager == null) {
                    connectivityManager = (ConnectivityManager) context.getSystemService(
                            Context.CONNECTIVITY_SERVICE);
                }
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
//                    Log.w("TAG", "No network");
                    return NO_TILE;
                }

//                Log.d("TAG", "Downloading tile");
                data = readTile(new URL("https://a.tile.openstreetmap.org/" +
                                z + "/" + x + "/" + y + ".png").openStream(),
                        BUFFER_SIZE_NETWORK);

//                Log.d("TAG", tilesDir);


                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOne))) {
                    out.write(data);
                }
            }
            return new Tile(TILE_WIDTH, TILE_HEIGHT, data);
        } catch (Exception ex) {
            Log.e("TAG", "Error loading tile", ex);
            return NO_TILE;
        }
    }

    private byte[] readTile(InputStream in, int bufferSize) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            int i;
            byte[] data = new byte[bufferSize];

            while ((i = in.read(data, 0, bufferSize)) != -1) {
                buffer.write(data, 0, i);
            }
            buffer.flush();

            return buffer.toByteArray();
        } finally {
            in.close();
            buffer.close();
        }
    }


}