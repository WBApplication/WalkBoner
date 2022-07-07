package com.fusoft.walkboner.database.funcions;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.utils.UniqueDeviceIdentity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class UnbanUser {
    public static void Unban(String UID, UnbanUserListener listener) {
        String phoneUID = UniqueDeviceIdentity.getUniqueID();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("bannedDevices").whereEqualTo("phoneUid", UniqueDeviceIdentity.getUniqueID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            firestore.collection("users").whereEqualTo("userUid", UID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                        HashMap<String, Object> mapNew = new HashMap<>();
                                        mapNew.put("isBanned", "false");
                                        mapNew.put("bannedTo", "");
                                        mapNew.put("banReason", "");
                                        firestore.collection("users").document(doc.getId()).update(mapNew).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                listener.OnUserUnbanned();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                listener.OnError(e.getMessage());
                                            }
                                        });
                                    }
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
            }
        });
    }
}
