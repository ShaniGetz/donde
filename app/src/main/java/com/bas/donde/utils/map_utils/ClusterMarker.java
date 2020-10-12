package com.bas.donde.utils.map_utils;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {
    private String userID;
    private LatLng position;
    private String title;
    private String snippet;
//    private int iconPicture;
    private Bitmap iconPictureBitmap;
//    private User user;

    public ClusterMarker(String userID, LatLng position, String title, String snippet, Bitmap iconPictureBitmap) {
        this.userID = userID;
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPictureBitmap = iconPictureBitmap;
//        this.iconPicture = iconPicture;
    }

    public String getUserID() { return userID; }

//    public int getIconPicture() {
//        return iconPicture;
//    }


    public Bitmap getIconPictureBitmap() {
        return iconPictureBitmap;
    }

    public void setIconPictureBitmap(Bitmap iconPictureBitmap) {
        this.iconPictureBitmap = iconPictureBitmap;
    }

//    public void setIconPicture(int iconPicture) {
//        this.iconPicture = iconPicture;
//    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

}

