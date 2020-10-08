package com.example.donde.models;

import com.google.firebase.firestore.DocumentId;

public class InvitedEventModel {

    @DocumentId
    private String documentId;
    private String userInvitedEventId;
    private String userInvitedEventName;
    private String userInvitedEventLocationName;
    private String userInvitedEventCreatorName;

    // required empty constructor
    public InvitedEventModel() {
    }
}
