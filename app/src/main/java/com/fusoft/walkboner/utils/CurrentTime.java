package com.fusoft.walkboner.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CurrentTime {
    public static long Get() {
        return Calendar.getInstance().getTime().getTime();
    }

    public static String GetFormatted() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        return df.format(c);
    }
}
