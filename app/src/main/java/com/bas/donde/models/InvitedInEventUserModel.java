package com.bas.donde.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;


public class InvitedInEventUserModel {
    @DocumentId
    private String documentId;
    private String invitedInEventUserID;
    private String invitedInEventUserName;
    private String invitedInEventUserEmail;
    private String invitedInEventUserStatus;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public GeoPoint getInvitedInEventUserLastLocation() {
        return invitedInEventUserLastLocation;
    }

    public void setInvitedInEventUserLastLocation(GeoPoint invitedInEventUserLastLocation) {
        this.invitedInEventUserLastLocation = invitedInEventUserLastLocation;
    }

    private GeoPoint invitedInEventUserLastLocation;
    private String invitedInEventUserProfilePicURL;

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

    private boolean invitedInEventUserIsGoing;


    public InvitedInEventUserModel(String invitedInEventUserID, String invitedInEventUserName, String invitedInEventUserEmail, String invitedInEventUserStatus, GeoPoint invitedInEventUserLastLocation, String invitedInEventUserProfilePicURL) {
        this.invitedInEventUserID = invitedInEventUserID;
        this.invitedInEventUserName = invitedInEventUserName;
        this.invitedInEventUserEmail = invitedInEventUserEmail;
        this.invitedInEventUserStatus = invitedInEventUserStatus;
        this.invitedInEventUserLastLocation = invitedInEventUserLastLocation;
        this.invitedInEventUserProfilePicURL = invitedInEventUserProfilePicURL;
        this.invitedInEventUserIsGoing = false;
    }

    // required empty constructor
    public InvitedInEventUserModel() {

    }

    // TODO: implement logic for isGoing
}
