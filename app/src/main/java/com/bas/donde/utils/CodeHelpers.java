package com.bas.donde.utils;

public class CodeHelpers {
    public static void myAssert(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }

    }

}
