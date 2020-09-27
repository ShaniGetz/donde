package com.example.donde.archive.event_data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class EventDate {

    LocalDateTime eventDateTime;

    public EventDate() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public EventDate(int year, int month, int dayofMonth, int hour, int minute) {
        eventDateTime = LocalDateTime.of(year, month, dayofMonth, hour, minute);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getYear() {
        return eventDateTime.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getMonth() {
        return eventDateTime.getMonthValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getDayOfMonth() {
        return eventDateTime.getDayOfMonth();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getDayOfWeekInt() {
        return eventDateTime.getDayOfWeek().getValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDayOfWeekName() {
        return eventDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);
    }
}
