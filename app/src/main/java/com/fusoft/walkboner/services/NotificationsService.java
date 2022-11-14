package com.fusoft.walkboner.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.fusoft.walkboner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationsService extends Service {
    private int unreadedNotifications = 0;
    private String TITLE = "";
    private String MESSAGE = "";

    private int ID = 10;

    private String TAG = "NotifiService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("userUid")) {
            NotificationManager mNotificationManager;

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), "WalkBoner_Notifications");

            NotificationCompat.Builder bigText = new NotificationCompat.Builder(getApplicationContext(), "WalkBoner_Notifications");
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            mBuilder.setContentTitle("WalkBoner");
            mBuilder.setContentText(formattedText());
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setSmallIcon(R.drawable.front_logo);

            mNotificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // === Removed some obsoletes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "WalkBoner_Notifications";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "WalkBoner Notifications",
                        NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }

            Log.d(TAG, "onStartCommand: Service Started!");

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Log.d(TAG, "onStartCommand: Firestore Instance Created!");
            firestore.collection("users").whereEqualTo("userUid", intent.getStringExtra("userUid")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    if (document != null) Log.d(TAG, "onSuccess: User Finded!");
                    if (document == null) return;
                    document.getReference().collection("notifications").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            Log.d(TAG, "onEvent: Event Received");
                            unreadedNotifications = 0;
                            for (DocumentSnapshot document : value.getDocuments()) {
                                if (Boolean.FALSE.equals(document.get("isChecked"))) {
                                    unreadedNotifications++;

                                    if (unreadedNotifications == 1) {
                                        TITLE = document.getString("notificationTitle");
                                        MESSAGE = document.getString("notificationDescription");
                                    }
                                }
                            }
                            Log.d(TAG, "onEvent: User have " + unreadedNotifications + " unreaded notifications!");
                            if (unreadedNotifications == 1) {
                                mBuilder.setContentTitle("WalkBoner - " + TITLE);
                                mBuilder.setContentText(MESSAGE);
                                mNotificationManager.notify(1, mBuilder.build());
                            } else if (unreadedNotifications != 0) {
                                mBuilder.setContentTitle("WalkBoner");
                                mBuilder.setContentText(formattedText());
                                mNotificationManager.notify(1, mBuilder.build());
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            // Build Notification for User Notifications


            // Build Notification for Service
            final String CHANNELID = "WalkBoner Service";
            NotificationChannel channel = null;
            Notification.Builder notification = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = new NotificationChannel(
                        CHANNELID,
                        CHANNELID,
                        NotificationManager.IMPORTANCE_LOW
                );

                getSystemService(NotificationManager.class).createNotificationChannel(channel);
                notification = new Notification.Builder(this, CHANNELID)
                        .setContentText("Możesz ukryć to powiadomienie przytrzymując je lub w ustawieniach aplikacji!")
                        .setContentTitle("WalkBoner Service")
                        .setSmallIcon(R.drawable.front_logo);
            }

            startForeground(ID, notification.build());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String formattedText() {
        if (unreadedNotifications == 1) {
            return "Masz " + unreadedNotifications + " nieprzeczytane powiadomienie!";
        } else if (unreadedNotifications > 1 && unreadedNotifications <= 4) {
            return "Masz " + unreadedNotifications + " nieprzeczytane powiadomienia!";
        } else {
            return "Masz " + unreadedNotifications + " nieprzeczytanych powiadomień!";
        }
    }
}
