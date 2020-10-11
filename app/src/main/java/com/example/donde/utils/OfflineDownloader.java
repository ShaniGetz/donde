package com.example.donde.utils;

import com.example.donde.models.InvitedInUserEventModel;
import com.google.firebase.auth.FirebaseUser;

public  class OfflineDownloader {
    // the user who is requesting the download
    private FirebaseUser userDownloading;
    // the event that needs to be downloaded
    private InvitedInUserEventModel eventToDownload;

    public OfflineDownloader(FirebaseUser userDownloading, InvitedInUserEventModel eventToDownload) {
        this.eventToDownload = eventToDownload;
        this.userDownloading=userDownloading;
    }

    private static void downloadEventToOffline() {

    }
}
