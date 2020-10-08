package com.example.donde.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class InvitedInUserEventModel {

    @DocumentId
    private String documentId;
    private String  invitedInUserEventId;
    private String  invitedInUserEventName;
    private String  invitedInUserEventLocationName;
    private String  invitedInUserEventCreatorName;
    private boolean invitedInUserEventIsGoing;



    // required public empty constructor
    public InvitedInUserEventModel() {
    }

}
