package com.example.donde.models;

import com.google.firebase.firestore.GeoPoint;

public class InvitedUserModel {
    private String eventInvitedUserID;
    private String eventInvitedUserName;
    private String eventInvitedUserEmail;
    private String eventInvitedUserStatus;
    private GeoPoint eventInvitedUserCurrentLocation;
    private String eventInvitedUserProfilePicURL;
    private boolean eventInvitedUserIsGoing;

    public InvitedUserModel(String eventInvitedUserID, String eventInvitedUserName, String eventInvitedUserEmail, String eventInvitedUserProfilePicURL) {
        this.eventInvitedUserID = eventInvitedUserID;
        this.eventInvitedUserName = eventInvitedUserName;
        this.eventInvitedUserEmail = eventInvitedUserEmail;
        this.eventInvitedUserStatus = "Assign a status";
        this.eventInvitedUserCurrentLocation = new GeoPoint(0, 0);
        this.eventInvitedUserProfilePicURL = eventInvitedUserProfilePicURL == null ? "https" +
                "://firebasestorage.googleapis.com/v0/b/donde-4cda4.appspot.com/o/avatar" +
                ".webp?alt=media&token=9922bed2-de35-4690-bde2-ca2dbe9762a3" :
                eventInvitedUserProfilePicURL;
        this.eventInvitedUserIsGoing = true;
    }

    // required empty constructor
    public InvitedUserModel() {

    }

    public String getEventInvitedUserEmail() {
        return eventInvitedUserEmail;
    }

    public void setEventInvitedUserEmail(String eventInvitedUserEmail) {
        this.eventInvitedUserEmail = eventInvitedUserEmail;
    }

    public String getEventInvitedUserID() {
        return eventInvitedUserID;
    }

    public void setEventInvitedUserID(String eventInvitedUserID) {
        this.eventInvitedUserID = eventInvitedUserID;
    }

    public String getEventInvitedUserName() {
        return eventInvitedUserName;
    }

    public void setEventInvitedUserName(String eventInvitedUserName) {
        this.eventInvitedUserName = eventInvitedUserName;
    }

    public String getEventInvitedUserStatus() {
        return eventInvitedUserStatus;
    }

    public void setEventInvitedUserStatus(String eventInvitedUserStatus) {
        this.eventInvitedUserStatus = eventInvitedUserStatus;
    }

    public GeoPoint getEventInvitedUserCurrentLocation() {
        return eventInvitedUserCurrentLocation;
    }

    public void setEventInvitedUserCurrentLocation(GeoPoint eventInvitedUserCurrentLocation) {
        this.eventInvitedUserCurrentLocation = eventInvitedUserCurrentLocation;
    }

    public String getEventInvitedUserProfilePicURL() {
        return eventInvitedUserProfilePicURL;
    }

    public void setEventInvitedUserProfilePicURL(String eventInvitedUserProfilePicURL) {
        this.eventInvitedUserProfilePicURL = eventInvitedUserProfilePicURL;
    }

    // TODO: implement logic for isGoing
    public boolean isEventInvitedUserIsGoing() {
        return eventInvitedUserIsGoing;
    }

    public void setEventInvitedUserIsGoing(boolean eventInvitedUserIsGoing) {
        this.eventInvitedUserIsGoing = eventInvitedUserIsGoing;
    }
}
