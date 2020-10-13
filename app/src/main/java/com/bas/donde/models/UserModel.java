package com.bas.donde.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class UserModel {
    String userID;
    String userName;
    String userEmail;
    String userProfilePicURL;
    GeoPoint userLastLocation;
    String userStatus;
    ArrayList<String> userInvitedEventsIDs;

    public UserModel() {
    }

    public UserModel(String userID, String userName, String userEmail, String userProfilePicURL) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfilePicURL = userProfilePicURL;
        this.userStatus = "";
        this.userLastLocation = new GeoPoint(10, 10);
        this.userInvitedEventsIDs = userInvitedEventsIDs;
        this.userInvitedEventsIDs = new ArrayList<>();
    }

    public ArrayList<String> getUserInvitedEventsIDs() {
        return userInvitedEventsIDs;
    }

    public void setUserInvitedEventsIDs(ArrayList<String> userInvitedEventsIDs) {
        this.userInvitedEventsIDs = userInvitedEventsIDs;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePicURL() {
        return userProfilePicURL;
    }

    public void setUserProfilePicURL(String userProfilePicURL) {
        this.userProfilePicURL = userProfilePicURL;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public GeoPoint getUserLastLocation() {
        return userLastLocation;
    }

    public void setUserLastLocation(GeoPoint userLastLocation) {
        this.userLastLocation = userLastLocation;
    }
}
