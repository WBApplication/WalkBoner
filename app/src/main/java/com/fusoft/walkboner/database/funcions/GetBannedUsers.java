/*
 * Copyright (c) 2022 - WalkBoner.
 * GetBannedUsers.java
 */

package com.fusoft.walkboner.database.funcions;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetBannedUsers {
    public static void Get(@NonNull BannedUsersGetListener listener) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("users").whereEqualTo("isBanned", "true").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<User> users = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                if (!doc.getString("bannedTo").isEmpty() && Long.parseLong(doc.getString("bannedTo")) == 4102444800000L) {
                    User user = new User();
                    user.setUserAvatar(doc.getString("avatar"));
                    user.setUserName(doc.getString("username"));
                    user.setUserUid(doc.getString("userUid"));
                    user.setUserBannedTo(doc.getString("bannedTo"));
                    user.setUserBanReason(doc.getString("banReason"));
                    user.setUserDocumentId(doc.getId());
                    user.setUserBanned(true);
                    users.add(user);
                }
            }

            if (users.isEmpty()) {
                listener.OnError("Brak zbanowanych użytkowników :)");
                return;
            }

            listener.OnReceived(users);
        }).addOnFailureListener(e -> listener.OnError(e.getMessage()));
    }

    public interface BannedUsersGetListener {
        void OnReceived(List<User> users);

        default void OnError(String error) {
        }
    }
}
