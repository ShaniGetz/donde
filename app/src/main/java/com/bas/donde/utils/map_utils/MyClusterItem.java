package com.bas.donde.utils.map_utils;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyClusterItem implements com.google.maps.android.clustering.ClusterItem {
    private String userID;
    private LatLng position;
    private String title;
    private String snippet;
    private Bitmap iconPictureBitmap;

    public MyClusterItem(String userID, LatLng position, String title, String snippet, Bitmap iconPictureBitmap) {
        this.userID = userID;
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPictureBitmap = iconPictureBitmap;
    }

    public String getUserID() {
        return userID;
    }


    public Bitmap getIconPictureBitmap() {
        return iconPictureBitmap;
    }

    public void setIconPictureBitmap(Bitmap iconPictureBitmap) {
        this.iconPictureBitmap = iconPictureBitmap;
    }

//    public void setIconPicture(int iconPicture) {
//        this.iconPicture = iconPicture;
//    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

}

