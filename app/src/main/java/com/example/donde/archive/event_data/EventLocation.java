package com.example.donde.archive.event_data;

public class EventLocation {
    String locationName;
    long altitude, longtitude;

    public EventLocation() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public EventLocation(String locationName, long altitude, long longtitude) {
        this.locationName = locationName;
        this.altitude = altitude;
        this.longtitude = longtitude;
    }

    public void setAltitude(long altitude) {
        this.altitude = altitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLongtitude(long longtitude) {
        this.longtitude = longtitude;
    }

    public long getAltitude() {
        return altitude;
    }

    public long getLongtitude() {
        return longtitude;
    }

    public String getLocationName() {
        return locationName;
    }
}
