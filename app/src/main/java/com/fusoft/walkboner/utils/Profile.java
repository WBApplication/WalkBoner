package com.fusoft.walkboner.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile {

    User userData = null;

    public void GetProfileForUser(String UID, UserInfoListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Log.e("GetProfileForUser", "UserUID: " + UID);

        firestore.collection("users").whereEqualTo("userUid", UID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                userData = new User();
                userData.setUserUid(doc.get("userUid").toString());
                userData.setUserEmail(doc.get("email").toString());
                userData.setUserName(doc.get("username").toString());
                userData.setUserAvatar(doc.get("avatar").toString());
                userData.setUserDescription(doc.get("description").toString());
                userData.setUserBanReason(doc.get("banReason").toString());
                userData.setUserVerified(Boolean.parseBoolean(doc.get("isVerified").toString()));
                userData.setUserModerator(Boolean.parseBoolean(doc.get("isMod").toString()));
                userData.setUserAdmin(Boolean.parseBoolean(doc.get("isAdmin").toString()));
                userData.setUserBanned(Boolean.parseBoolean(doc.get("isBanned").toString()));
                userData.setCreatedAt(Long.parseLong(doc.get("createdAt").toString()));
                Log.e("GetProfileForUser", "DataCollected");
                listener.OnUserDataReceived(userData);

                return;
            }

            if (userData == null) {
                listener.OnUserNotFinded();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.OnError(e.getMessage());
            }
        });
    }
}
