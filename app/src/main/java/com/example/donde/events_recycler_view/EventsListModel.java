package com.example.donde.events_recycler_view;

import com.example.donde.utils.EventID;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class EventsListModel extends EventID {
    @DocumentId
    private String eventID;
    private String creatorUID;
    private String eventName, eventDescription, eventLocationName;
    //    private long longtitude, latitude;
    private Timestamp timeCreated;
    private Timestamp timeStarting;
    private GeoPoint eventLocation;
    private ArrayList<String> invitedUsers;

    public EventsListModel() {

    }

    public EventsListModel(String eventID, String eventName, String eventDescription,
                           String creator_id, String eventLocationName,
                           Timestamp timeCreated,
                           Timestamp timeStarting, GeoPoint location, ArrayList<String> invited_people) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.creatorUID = creator_id;
//        this.creator_username = creator_username;
        this.eventLocationName = eventLocationName;
        this.timeCreated = timeCreated;
        this.timeStarting = timeStarting;
        this.eventLocation = location;
        this.invitedUsers = invited_people;
    }

    public String getCreatorUID() {
        return creatorUID;
    }

    public void setCreatorUID(String creatorUID) {
        this.creatorUID = creatorUID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

//    public String getCreator_username() {
//        return creator_username;
//    }

//    public void setCreator_username(String creator_username) {
//        this.creator_username = creator_username;
//    }

    public String getEventLocationName() {
        return eventLocationName;
    }

    public void setEventLocationName(String eventLocationName) {
        this.eventLocationName = eventLocationName;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Timestamp getTimeStarting() {
        return timeStarting;
    }

    public void setTimeStarting(Timestamp timeStarting) {
        this.timeStarting = timeStarting;
    }

    public GeoPoint getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(GeoPoint eventLocation) {
        this.eventLocation = eventLocation;
    }

    public ArrayList<String> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(ArrayList<String> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }
}
