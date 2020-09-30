package com.example.donde.utils;


import androidx.annotation.NonNull;

public class EventID {
    public String eventID;

    public <T extends EventID> T withID(@NonNull final String id) {
        this.eventID = id;
        return (T) this;
    }
}
