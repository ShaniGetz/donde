package com.example.donde.utils;

public class InvitedUser {
    public String eventInvitedUserID;
    public String eventInvitedUserEmail;
    public String eventInvitedUserName;

    public InvitedUser(String eventInvitedUserID, String eventInvitedUserEmail, String eventInvitedUserName) {
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
