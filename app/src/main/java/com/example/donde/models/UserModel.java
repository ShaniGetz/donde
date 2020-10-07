package com.example.donde.models;

import com.google.android.gms.maps.model.LatLng;

public class UserModel {
    String userID;
    String userName;
    String userProfilePicURL;
    LatLng userCurLocation;
    String userStatus;


    public UserModel() {
    }

    public UserModel(String userID, String userName, String userProfilePicURL, LatLng userCurLocation, String userStatus) {
        this.userID = userID;
        this.userName = userName;
        this.userProfilePicURL = userProfilePicURL;
        this.userCurLocation = userCurLocation;
        this.userStatus = userStatus;
    }

    public String getUserID(){
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePicURL(){
        return userProfilePicURL;
    }

    public void setUserProfilePicURL(String userProfilePicURL) {
        this.userProfilePicURL = userProfilePicURL;
    }

    public String getUserStatus(){
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public LatLng getUserCurLocation(){
        return userCurLocation;
    }

    public void setUserCurLocation(LatLng userCurLocation) {
        this.userCurLocation = userCurLocation;
    }
}
