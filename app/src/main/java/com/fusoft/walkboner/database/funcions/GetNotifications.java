package com.fusoft.walkboner.database.funcions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fusoft.walkboner.models.Notification;
import com.fusoft.walkboner.utils.CurrentTime;
import com.fusoft.walkboner.utils.UidGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetNotifications {
    public static void Get(String userUid, boolean isFromParent, @Nullable NotificationsListener listener) {
        boolean isListening = listener != null;

        List<Notification> notifications = new ArrayList<>();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("userUid", userUid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
            document.getReference().collection("notifications").orderBy("createdAt", Query.Direction.ASCENDING).get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                if (queryDocumentSnapshots1.isEmpty()) {
                    if (isListening) listener.OnSuccess(null);
                    return;
                }

                for (DocumentSnapshot doc : queryDocumentSnapshots1.getDocuments()) {
                    Notification notificationDetails = new Notification();
                    notificationDetails.setNotificationUid(doc.getString("notificationUid"));
                    notificationDetails.setNotificationTitle(doc.getString("notificationTitle"));
                    notificationDetails.setNotificationDescription(doc.getString("notificationDescription"));
                    notificationDetails.setChecked(doc.get("isChecked", Boolean.class));
                    notificationDetails.setNotificationAttribute(doc.getString("notificationAttribute"));
                    notificationDetails.setCreatedAt(doc.get("createdAt", Long.class));

                    notifications.add(notificationDetails);

                    doc.getReference().update(notificationDetails.toMap(true));
                }

                if (isListening) listener.OnSuccess(notifications);
            }).addOnFailureListener(e -> {
                if (isListening) listener.OnError(e.getMessage());
            });
        }).addOnFailureListener(e -> {
            if (isListening) listener.OnError(e.getMessage());
        });
    }

    public static void Remove(String userUid, String notificationUid, NotificationsDeleteListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("userUid", userUid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                document.getReference().collection("notifications").whereEqualTo("notificationUid", notificationUid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                listener.OnDeleted();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                listener.OnError(e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.OnError(e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.OnError(e.getMessage());
            }
        });
    }

    public interface NotificationsListener {
        void OnSuccess(@Nullable List<Notification> notifications);

        void OnError(String reason);
    }

    public interface NotificationsDeleteListener {
        void OnDeleted();

        void OnError(String reason);
    }
}
