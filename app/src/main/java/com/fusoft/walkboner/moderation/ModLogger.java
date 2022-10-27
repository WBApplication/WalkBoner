package com.fusoft.walkboner.moderation;

import android.content.Context;

import com.fusoft.walkboner.utils.CurrentTime;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ModLogger {

    public static String modUserName;
    public static String modUid;

    public enum ActionType {
        BANNED_USER,
        ACCEPTED_NEW_INFLUENCER,
        DECLINED_NEW_INFLUENCER,
        DELETED_INFLUENCER,
        DELETED_ALBUM,
        DELETED_ALBUM_IMAGE
    }

    public static void Log(FirebaseFirestore firestore, ActionType action, String[] attributes) {
        String message = "------------\n";

        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());

        String formattedDate = CurrentTime.GetFormatted();

        if (action.equals(ActionType.BANNED_USER)) {
            Date b = new Date(Long.parseLong(attributes[2]));
            String formattedBannedTo = df.format(b);

            message = message
                    + formattedDate + "\n"
                    + "Moderator: " + modUserName + "\n"
                    + "UID: " + modUid + "\n\n"
                    + "Zbanował użytkownika:" + "\n"
                    + "Nazwa Użytkownika: " + attributes[0] + "\n"
                    + "UID: " + attributes[1] + "\n"
                    + "Do dnia: " + formattedBannedTo + "\n"
                    + "Powód:" + "\n"
                    + attributes[3] + "\n\n";
        }

        if (action.equals(ActionType.DELETED_INFLUENCER)) {
            message = message
                    + formattedDate + "\n"
                    + "Moderator: " + modUserName + "\n"
                    + "UID: " + modUid + "\n\n"
                    + "Ukrył Influencera:" + "\n"
                    + "Influencer: " + attributes[0] + "\n"
                    + "UID: " + attributes[1] + "\n"
                    + "Powód:" + "\n"
                    + attributes[2] + "\n\n";
        }

        logToDatabase(firestore, message, CurrentTime.Get());


    }

    private static void logToDatabase(FirebaseFirestore firestore, String content, long addedAt) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("addedAt", addedAt);

        firestore.collection("moderationLogs").add(map);
    }
}
