package com.fusoft.walkboner.utils;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.BuildConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppUpdate {
    public static void checkForUpdate(UpdateListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("versions").document("current").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.getString("version").contentEquals(BuildConfig.VERSION_NAME)) {
                    listener.OnUpdateAvailable(documentSnapshot.getString("version"), documentSnapshot.getString("changeLog"), documentSnapshot.getString("download"), Boolean.parseBoolean(documentSnapshot.getString("isRequired")));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.OnError(e.getMessage());
            }
        });
    }

    public interface UpdateListener {
        default void OnUpdateAvailable(String version, String changeLog, String downloadUrl, boolean isRequired) {}

        default void OnError(String reason) {}
    }
}
