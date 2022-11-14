package com.fusoft.walkboner.database.funcions.userProfile;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SetDescription {
    public static void Set(String userDocId, String description, OnDescriptionChanged listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(userDocId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("description", description);
            queryDocumentSnapshots.getReference().update(map).addOnSuccessListener(unused -> listener.OnChanged()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.OnError(e.getMessage());
                }
            });
        }).addOnFailureListener(e -> listener.OnError(e.getMessage()));
    }

    public interface OnDescriptionChanged {
        void OnChanged();

        void OnError(String reason);
    }
}
