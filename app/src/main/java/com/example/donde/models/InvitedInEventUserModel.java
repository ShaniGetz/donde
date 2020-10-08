package com.example.donde.models;

import com.google.firebase.firestore.GeoPoint;

public class InvitedInEventUserModel {
    private String invitedInEventUserID;
    private String invitedInEventUserName;
    private String invitedInEventUserEmail;
    private String invitedInEventUserStatus;
    private GeoPoint invitedInEventUserCurrentLocation;
    private String invitedInEventUserProfilePicURL;
    private boolean invitedInEventUserIsGoing;


    // required empty constructor
    public InvitedInEventUserModel() {

    }

    // TODO: implement logic for isGoing
}
