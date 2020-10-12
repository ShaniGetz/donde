package com.bas.donde.utils;

import java.util.Calendar;

public class UIHelpers {

    private static String getCurrentDateString() {
        Calendar mcurrentTime = Calendar.getInstance();
        int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        int month = mcurrentTime.get(Calendar.MONTH);
        int year = mcurrentTime.get(Calendar.YEAR);
        return String.format("%s/%s/%s", day, month, year);
    }

    private static String getCurrentTimeString() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        return String.format("%s:%s", minute, hour);

    }

//    private static String dateToString() {
//
//    }

}
