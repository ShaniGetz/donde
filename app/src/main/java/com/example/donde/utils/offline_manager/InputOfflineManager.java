package com.example.donde.utils.offline_manager;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.donde.models.EventModel;
import com.example.donde.models.InvitedInUserEventModel;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InputOfflineManager extends OfflineManager {

    private File currUserBaseDir;
    private File currUserEventDir;
    private File eventDetailsDir;
    private File eventDetailsFile;

    public InputOfflineManager(Context context) throws Exception {
        super(context);
        Log.d(TAG, "in OutputOfflineManager constructor");
        currUserBaseDir = context.getDir(currUserID, Context.MODE_PRIVATE);
        if (!currUserBaseDir.isDirectory()) {
            Log.d(TAG, "The user dir was not downloaded");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public InvitedInUserEventModel getEventModel(String eventID) {
        Log.d(TAG, "in getEventModel");

        currUserEventDir = new File(currUserBaseDir, eventID);
        if (!currUserEventDir.isDirectory()) {
            Log.d(TAG, "The event dir was no downloaded");
            return null;
        }
        eventDetailsDir = new File(currUserEventDir, EVENT_DETAILS_DIR);
        if (!currUserEventDir.isDirectory()) {
            Log.d(TAG, "The event details dir was no downloaded");
            return null;
        }
        eventDetailsFile = new File(eventDetailsDir, EVENT_DETAILS_FILE);
        Gson eventGson = new Gson();
        try {
//            FileInputStream eventJsonReader = new FileInputStream(eventDetailsFile);
//            String eventJson = eventJsonReader.read(eventDetailsFile.);
            String eventJson =
                    new String(Files.readAllBytes(Paths.get(eventDetailsFile.getPath())));
            InvitedInUserEventModel eventModel = eventGson.fromJson(eventJson, InvitedInUserEventModel.class);
            return eventModel;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }


}
