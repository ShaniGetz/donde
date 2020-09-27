package com.example.donde.archive;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class EventsListModel {
    @DocumentId
    private String quiz_id;
    private String name, description, creator_username, location_name;
    //    private long longtitude, latitude;
    private Timestamp time_created;
    private Timestamp time_starting;
    private GeoPoint location;
    private ArrayList<String> invited_people;

    public EventsListModel() {

    }

    public EventsListModel(String quiz_id, String name, String description,
                           String creator_username, String location_name, Timestamp time_created,
                           Timestamp time_starting, GeoPoint location, ArrayList<String> invited_people) {
        this.quiz_id = quiz_id;
        this.name = name;
        this.description = description;
        this.creator_username = creator_username;
        this.location_name = location_name;
        this.time_created = time_created;
        this.time_starting = time_starting;
        this.location = location;
        this.invited_people = invited_people;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator_username() {
        return creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Timestamp getTime_created() {
        return time_created;
    }

    public void setTime_created(Timestamp time_created) {
        this.time_created = time_created;
    }

    public Timestamp getTime_starting() {
        return time_starting;
    }

    public void setTime_starting(Timestamp time_starting) {
        this.time_starting = time_starting;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public ArrayList<String> getInvited_people() {
        return invited_people;
    }

    public void setInvited_people(ArrayList<String> invited_people) {
        this.invited_people = invited_people;
    }
}
