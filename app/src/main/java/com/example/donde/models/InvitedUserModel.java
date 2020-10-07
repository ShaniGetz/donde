package com.example.donde.models;

public class InvitedUserModel {
    public String eventInvitedUserID;
    public String eventInvitedUserEmail;
    public String eventInvitedUserName;

    public InvitedUserModel(String eventInvitedUserID, String eventInvitedUserEmail, String eventInvitedUserName) {
        this.eventInvitedUserID = eventInvitedUserID;
        this.eventInvitedUserEmail = eventInvitedUserEmail;
        this.eventInvitedUserName = eventInvitedUserName;
    }

    public String getEventInvitedUserID() {
        return eventInvitedUserID;
    }

    public String getEventInvitedUserEmail() {
        return eventInvitedUserEmail;
    }

    public String getEventInvitedUserName() {
        return eventInvitedUserName;
    }
}
