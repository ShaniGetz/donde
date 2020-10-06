package com.example.donde.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class EventModel {
    @DocumentId
    private String eventID;
    private String eventCreatorUID;
    private String eventName;
    private String eventDescription;
    private String eventLocationName;
    @ServerTimestamp
    private Date eventTimeCreated;
    @ServerTimestamp
    private Date eventTimeStarting;
    private GeoPoint eventLocation;
    private String eventCreatorName;

    public EventModel(String eventID, String eventCreatorUID, String eventName, String eventDescription, String eventLocationName, Date eventTimeCreated, Date eventTimeStarting, GeoPoint eventLocation, String eventCreatorName) {
        this.eventID = eventID;
        this.eventCreatorUID = eventCreatorUID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocationName = eventLocationName;
        this.eventTimeCreated = eventTimeCreated;
        this.eventTimeStarting = eventTimeStarting;
        this.eventLocation = eventLocation;
        this.eventCreatorName = eventCreatorName;
    }


    public EventModel() {

    }


    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventCreatorUID() {
        return eventCreatorUID;
    }

    public void setEventCreatorUID(String eventCreatorUID) {
        this.eventCreatorUID = eventCreatorUID;
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

    public String getEventLocationName() {
        return eventLocationName;
    }

    public void setEventLocationName(String eventLocationName) {
        this.eventLocationName = eventLocationName;
    }

    public Date getEventTimeCreated() {
        return eventTimeCreated;
    }

    public void setEventTimeCreated(Date eventTimeCreated) {
        this.eventTimeCreated = eventTimeCreated;
    }

    public Date getEventTimeStarting() {
        return eventTimeStarting;
    }

    public void setEventTimeStarting(Date eventTimeStarting) {
        this.eventTimeStarting = eventTimeStarting;
    }

    public GeoPoint getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(GeoPoint eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventCreatorName() {
        return eventCreatorName;
    }

    public void setEventCreatorName(String eventCreatorName) {
        this.eventCreatorName = eventCreatorName;
    }
}
