package com.example.donde.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class InvitedInUserEventModel {

    @DocumentId
    private String documentId;
    private String invitedInUserEventId;
    private String invitedInUserEventName;
    private String invitedInUserEventLocationName;
    private String invitedInUserEventCreatorName;
    @ServerTimestamp
    private Date invitedInUserEventTimeStarting;
    private boolean invitedInUserEventIsGoing;

    public InvitedInUserEventModel(String invitedInUserEventId, String invitedInUserEventName,
                                   String invitedInUserEventLocationName,
                                   String invitedInUserEventCreatorName,
                                   boolean invitedInUserEventIsGoing, Date invitedInUserEventTimeStarting) {
        this.invitedInUserEventId = invitedInUserEventId;
        this.invitedInUserEventName = invitedInUserEventName;
        this.invitedInUserEventLocationName = invitedInUserEventLocationName;
        this.invitedInUserEventCreatorName = invitedInUserEventCreatorName;
        this.invitedInUserEventIsGoing = invitedInUserEventIsGoing;
        this.invitedInUserEventTimeStarting = invitedInUserEventTimeStarting;
    }

    public InvitedInUserEventModel(String invitedInUserEventId, String invitedInUserEventName,
                                   String invitedInUserEventLocationName,
                                   String invitedInUserEventCreatorName, Date invitedInUserEventModelTimeStarting) {
        this(invitedInUserEventId, invitedInUserEventName, invitedInUserEventLocationName,
                invitedInUserEventCreatorName, false, invitedInUserEventModelTimeStarting);
    }

    // required public empty constructor
    public InvitedInUserEventModel() {
    }

    public Date getInvitedInUserEventTimeStarting() {
        return invitedInUserEventTimeStarting;
    }

    public void setInvitedInUserEventTimeStarting(Date invitedInUserEventModelTimeStarting) {
        this.invitedInUserEventTimeStarting = invitedInUserEventModelTimeStarting;
    }

    public String getInvitedInUserEventId() {
        return invitedInUserEventId;
    }

    public void setInvitedInUserEventId(String invitedInUserEventId) {
        this.invitedInUserEventId = invitedInUserEventId;
    }

    public String getInvitedInUserEventName() {
        return invitedInUserEventName;
    }

    public void setInvitedInUserEventName(String invitedInUserEventName) {
        this.invitedInUserEventName = invitedInUserEventName;
    }

    public String getInvitedInUserEventLocationName() {
        return invitedInUserEventLocationName;
    }

    public void setInvitedInUserEventLocationName(String invitedInUserEventLocationName) {
        this.invitedInUserEventLocationName = invitedInUserEventLocationName;
    }

    public String getInvitedInUserEventCreatorName() {
        return invitedInUserEventCreatorName;
    }

    public void setInvitedInUserEventCreatorName(String invitedInUserEventCreatorName) {
        this.invitedInUserEventCreatorName = invitedInUserEventCreatorName;
    }

    public boolean isInvitedInUserEventIsGoing() {
        return invitedInUserEventIsGoing;
    }

    public void setInvitedInUserEventIsGoing(boolean invitedInUserEventIsGoing) {
        this.invitedInUserEventIsGoing = invitedInUserEventIsGoing;
    }

}
