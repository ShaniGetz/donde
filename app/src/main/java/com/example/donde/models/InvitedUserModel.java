package com.example.donde.models;

public class InvitedUserModel {
    public String eventInvitedUserID;
    public String eventInvitedUserEmail;
    public String eventInvitedUserName;
    public boolean eventInvitedUserIsGoing;

    // required empty constructor
    public InvitedUserModel() {

    }
    public InvitedUserModel(String eventInvitedUserID, String eventInvitedUserEmail, String eventInvitedUserName) {
        this.eventInvitedUserID = eventInvitedUserID;
        this.eventInvitedUserEmail = eventInvitedUserEmail;
        this.eventInvitedUserName = eventInvitedUserName;
    }

    // TODO: implement logic for isGoing
    public boolean isEventInvitedUserIsGoing() {
        return eventInvitedUserIsGoing;
    }

    public void setEventInvitedUserIsGoing(boolean eventInvitedUserIsGoing) {
        this.eventInvitedUserIsGoing = eventInvitedUserIsGoing;
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
