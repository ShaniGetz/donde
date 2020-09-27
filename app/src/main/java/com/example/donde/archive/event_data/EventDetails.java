package com.example.donde.archive.event_data;

public class EventDetails {
    public static String eventName = "";
    static String eventLocation = "";
    static String eventDate = "";
    static String inviteUser = "";
    static String password = "";
    static String chatWith = "";

    public static void setEventDetails(String eventName, String eventLocation, String eventDate, String inviteUser) {
        EventDetails.eventName = eventName;
        EventDetails.eventLocation = eventLocation;
        EventDetails.eventDate = eventDate;
        EventDetails.inviteUser = inviteUser;
    }
}
