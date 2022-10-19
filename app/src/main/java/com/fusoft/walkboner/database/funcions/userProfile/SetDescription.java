package com.fusoft.walkboner.database.funcions.userProfile;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SetDescription {
    public static void Set(String userUid, String description, OnDescriptionChanged listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("userUid", userUid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("description", description);
            queryDocumentSnapshots.getDocuments().get(0).getReference().update(map).addOnSuccessListener(unused -> listener.OnChanged()).addOnFailureListener(new OnFailureListener() {
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
