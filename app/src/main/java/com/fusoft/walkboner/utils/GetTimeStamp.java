package com.fusoft.walkboner.utils;

import java.sql.Timestamp;

public class GetTimeStamp {
    public static String getString() {
        return String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());
    }

    public static long getLong() {
        return new Timestamp(System.currentTimeMillis()).getTime();
    }
}
