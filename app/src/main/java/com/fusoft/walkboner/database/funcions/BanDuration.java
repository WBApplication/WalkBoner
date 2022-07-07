package com.fusoft.walkboner.database.funcions;

import java.sql.Timestamp;

public class BanDuration {
    private static long ONE_DAY_LONG = 86400000;

    public static String ONE_DAY() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return String.valueOf(timestamp.getTime() + ONE_DAY_LONG);
    }

    public static String TWO_DAY() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return String.valueOf(timestamp.getTime() + (ONE_DAY_LONG * 2));
    }

    public static String WEEK() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return String.valueOf(timestamp.getTime() + (ONE_DAY_LONG * 7));
    }

    public static String TWO_WEEKS() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return String.valueOf(timestamp.getTime() + (ONE_DAY_LONG * 14));
    }

    public static String MONTH() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return String.valueOf(timestamp.getTime() + (ONE_DAY_LONG * 31));
    }

    /**
     *
     * @return Fri Jan 01 2100 01:00:00
     */
    public static String FOREVER() {
        return "4102444800000"; // Fri Jan 01 2100 01:00:00
    }
}
