package com.example.donde.utils.offline_manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public abstract class OfflineManager {
    protected static final String EVENT_DETAILS_FILE = "event_details.json";
    protected static final String EVENT_DETAILS_DIR = "event_details_dir";
    protected static final String TAG = "tagOfflineManager";
    protected final String currUserID;

    // app context
    protected Context context;

    protected OfflineManager(Context context) throws Exception {
        this.context = context;
        if (!isLoggedIn()) {
            Toast.makeText(context, "Error, you seem to have been logged out", Toast.LENGTH_SHORT).show();
            throw new Exception("User not logged in exception");
        }else{
            currUserID = FirebaseAuth.getInstance().getUid();
        }
    }

    protected boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    protected void makeDir(File dirToMake) {
        Log.d(TAG, "in OfflineManager makeDir");

        if (dirToMake.mkdir()) {
            Log.d(TAG, String.format("Directory %s has been created.",
                    dirToMake.getAbsolutePath()));

        } else if (dirToMake.isDirectory()) {
            Log.d(TAG, String.format("Directory %s has already been created.",
                    dirToMake.getAbsolutePath()));

        } else {
            Log.d(TAG, String.format("Directory %s could not be created.",
                    dirToMake.getAbsolutePath()));
        }
    }
}
