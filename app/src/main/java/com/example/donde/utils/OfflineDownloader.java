package com.example.donde.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donde.models.InvitedInUserEventModel;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class OfflineDownloader extends AppCompatActivity {
    // the user who is requesting the download
    private FirebaseUser userDownloading;
    // the event that needs to be downloaded
    private InvitedInUserEventModel eventToDownload;

    // app context
    private Context context;
    // file dir of the current app on phone
    private File fileDir;

    public OfflineDownloader(FirebaseUser userDownloading,
                             InvitedInUserEventModel eventToDownload, Context context) {
        this.eventToDownload = eventToDownload;
        this.userDownloading = userDownloading;
        this.fileDir = context.getFilesDir();
    }

    private void downloadEventToOffline() {

        downloadEventGuestsNames();
        downloadEventGuestsProfilePics();
    }

    private void downloadEventGuestsNames() {

    }


    private void downloadEventGuestsProfilePics() {
        // iterate over invited users
        // download each user's profile pic from FirebaseStorage to phone

    }
}
