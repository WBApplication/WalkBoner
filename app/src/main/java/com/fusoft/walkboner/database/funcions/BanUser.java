package com.fusoft.walkboner.database.funcions;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.moderation.ModLogger;
import com.fusoft.walkboner.utils.UniqueDeviceIdentity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class BanUser {
    public static void Ban(String userName, String UID, String bannedTo, String banReason, BanUserListener listener) {
        String phoneUID = UniqueDeviceIdentity.getUniqueID();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        HashMap<String, Object> map = new HashMap<>();
        map.put("phoneUid", phoneUID);
        map.put("userUid", UID);
        map.put("banReason", banReason);
        map.put("bannedTo", bannedTo);

        firestore.collection("bannedDevices").add(map).addOnSuccessListener(documentReference -> {
            firestore.collection("users").whereEqualTo("userUid", UID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        HashMap<String, Object> mapNew = new HashMap<>();
                        mapNew.put("isBanned", "true");
                        mapNew.put("bannedTo", bannedTo);
                        mapNew.put("banReason", banReason);
                        String[] attrs = {userName, UID, bannedTo, banReason};
                        ModLogger.Log(firestore, ModLogger.ActionType.BANNED_USER, attrs);
                        firestore.collection("users").document(doc.getId()).update(mapNew);
                        listener.OnUserBanned();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.OnError(e.getMessage());
                }
            });
        }).addOnFailureListener(e -> {
            listener.OnError(e.getMessage());
        });
    }
}
