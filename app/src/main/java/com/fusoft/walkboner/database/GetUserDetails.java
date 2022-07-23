package com.fusoft.walkboner.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetUserDetails {
    public interface UserDetailsListener {
        void OnReceived(User user);

        void OnError(String reason);
    }

    public static void getByUid(String UID, @NonNull UserDetailsListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("userUid", UID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                User userData = new User();
                userData.setUserUid(UID);
                userData.setUserEmail(doc.get("email").toString());
                userData.setUserName(doc.get("username").toString());
                userData.setUserAvatar(doc.get("avatar").toString());
                userData.setUserDescription(doc.get("description").toString());
                userData.setUserBanReason(doc.get("banReason").toString());
                userData.setUserVerified(Boolean.parseBoolean(doc.get("isVerified").toString()));
                userData.setUserModerator(Boolean.parseBoolean(doc.get("isMod").toString()));
                userData.setUserAdmin(Boolean.parseBoolean(doc.get("isAdmin").toString()));
                userData.setUserBanned(Boolean.parseBoolean(doc.get("isBanned").toString()));
                userData.setUserBannedTo(doc.get("bannedTo").toString());
                userData.setShowFirstTimeTip(Boolean.parseBoolean(doc.getString("showFirstTimeTip")));
                userData.setCreatedAt(Long.parseLong(doc.get("createdAt").toString()));
                listener.OnReceived(userData);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.OnError(e.getMessage());
            }
        });
    }
}
