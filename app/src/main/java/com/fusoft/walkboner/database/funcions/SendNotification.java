package com.fusoft.walkboner.database.funcions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fusoft.walkboner.models.Notification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SendNotification {

    private FirebaseFirestore firestore;

    public SendNotification() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void send(String userUid, Notification notification, @Nullable NotificationListener listener) {
        boolean isListening = listener != null;

        HashMap<String, Object> map = new HashMap<>();
        map.put("notificationUid", notification.getNotificationUid());
        map.put("notificationTitle", notification.getNotificationTitle());
        map.put("notificationDescription", notification.getNotificationDescription());
        map.put("notificationAttribute", notification.getNotificationAttribute());
        map.put("isChecked", notification.isChecked());
        map.put("createdAt", notification.getCreatedAt());

        firestore.collection("users").whereEqualTo("userUid", userUid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
            document.getReference().collection("notifications").add(map).addOnSuccessListener(documentReference -> {
                if (isListening) listener.OnSuccess();
            }).addOnFailureListener(e -> {
                if (isListening) listener.OnError(e.getMessage());
            });
        }).addOnFailureListener(e -> {
            if (isListening) listener.OnError(e.getMessage());
        });
    }

    public interface NotificationListener {
        void OnSuccess();

        void OnError(String error);
    }
}
