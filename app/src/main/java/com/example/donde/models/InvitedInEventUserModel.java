package com.example.donde.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;


public class InvitedInEventUserModel {
    @DocumentId
    private String documentId;
    private String invitedInEventUserID;
    private String invitedInEventUserName;
    private String invitedInEventUserEmail;
    private String invitedInEventUserStatus;
    private GeoPoint invitedInEventUserCurrentLocation;
    private String invitedInEventUserProfilePicURL;
    private boolean invitedInEventUserIsGoing;

    public InvitedInEventUserModel(String invitedInEventUserID, String invitedInEventUserName,
                                   String invitedInEventUserEmail, String invitedInEventUserStatus, GeoPoint invitedInEventUserCurrentLocation, String invitedInEventUserProfilePicURL, boolean invitedInEventUserIsGoing) {
        this.invitedInEventUserID = invitedInEventUserID;
        this.invitedInEventUserName = invitedInEventUserName;
        this.invitedInEventUserEmail = invitedInEventUserEmail;
        this.invitedInEventUserStatus = invitedInEventUserStatus;
        this.invitedInEventUserCurrentLocation = invitedInEventUserCurrentLocation;
        this.invitedInEventUserProfilePicURL = invitedInEventUserProfilePicURL;
        this.invitedInEventUserIsGoing = invitedInEventUserIsGoing;
    }

    public InvitedInEventUserModel(String invitedInEventUserID, String invitedInEventUserName, String invitedInEventUserEmail) {
        this(invitedInEventUserID, invitedInEventUserName, invitedInEventUserEmail, "Enter status",
                new GeoPoint(0, 0), "https://firebasestorage.googleapis.com/v0/b/donde-4cda4" +
                        ".appspot.com/o/avatar.webp?alt=media&token=9922bed2-de35-4690-bde2" +
                        "-ca2dbe9762a3", false);


    }

    // required empty constructor
    public InvitedInEventUserModel() {

    }

    public String getInvitedInEventUserID() {
        return invitedInEventUserID;
    }

    public void setInvitedInEventUserID(String invitedInEventUserID) {
        this.invitedInEventUserID = invitedInEventUserID;
    }

    public String getInvitedInEventUserName() {
        return invitedInEventUserName;
    }

    public void setInvitedInEventUserName(String invitedInEventUserName) {
        this.invitedInEventUserName = invitedInEventUserName;
    }

    public String getInvitedInEventUserEmail() {
        return invitedInEventUserEmail;
    }

    public void setInvitedInEventUserEmail(String invitedInEventUserEmail) {
        this.invitedInEventUserEmail = invitedInEventUserEmail;
    }

    public String getInvitedInEventUserStatus() {
        return invitedInEventUserStatus;
    }

    public void setInvitedInEventUserStatus(String invitedInEventUserStatus) {
        this.invitedInEventUserStatus = invitedInEventUserStatus;
    }

    public GeoPoint getInvitedInEventUserCurrentLocation() {
        return invitedInEventUserCurrentLocation;
    }

    public void setInvitedInEventUserCurrentLocation(GeoPoint invitedInEventUserCurrentLocation) {
        this.invitedInEventUserCurrentLocation = invitedInEventUserCurrentLocation;
    }

    public String getInvitedInEventUserProfilePicURL() {
        return invitedInEventUserProfilePicURL;
    }

    public void setInvitedInEventUserProfilePicURL(String invitedInEventUserProfilePicURL) {
        this.invitedInEventUserProfilePicURL = invitedInEventUserProfilePicURL;
    }

    public boolean isInvitedInEventUserIsGoing() {
        return invitedInEventUserIsGoing;
    }

    public void setInvitedInEventUserIsGoing(boolean invitedInEventUserIsGoing) {
        this.invitedInEventUserIsGoing = invitedInEventUserIsGoing;
    }

    // TODO: implement logic for isGoing
}
