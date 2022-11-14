/*
 * Copyright (c) 2022 - WalkBoner.
 * LongToDate.java
 */

package com.fusoft.walkboner.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LongToDate {
    public static String Format(long timestamp) {
        Date date=new Date(timestamp);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return df2.format(date);
    }
}
