package com.fusoft.walkboner.database.funcions;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.HashMap;

public class LikePost {
    public static void Like(String postUID) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        long timeStamp = new Timestamp(System.currentTimeMillis()).getTime();

        HashMap<String, Object> map = new HashMap<>();
        map.put("userUid", user.getUid());
        map.put("likeAt", timeStamp);
        firestore.collection("posts").document(postUID).collection("likes").add(map);
        firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                HashMap<String, Object> mapAddPostToUserLiked = new HashMap<>();
                mapAddPostToUserLiked.put("postUid", postUID);
                mapAddPostToUserLiked.put("likeAt", timeStamp);
                documentSnapshot.getReference().collection("likedPosts").add(mapAddPostToUserLiked);
            }
        });
    }

    public void LikeWatcher(String postUID, LikesListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        firestore.collection("posts").document(postUID).collection("likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int i = 0;
                boolean userLiked = false;

                if (value != null) {
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        i++;
                        if (documentSnapshot.getString("userUid").contentEquals(user.getUid())) {
                            userLiked = true;
                        }
                    }
                }
                HashMap<String, Object> likes = new HashMap<>();
                likes.put("likesAmount", String.valueOf(i));
                firestore.collection("posts").document(postUID).update(likes);
                listener.OnUpdate(userLiked, i);
            }
        });
    }

    public static void UnLike(String postUID) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        firestore.collection("posts").document(postUID).collection("likes").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    doc.getReference().delete();
                }
            }
        });

        firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                documentSnapshot.getReference().collection("likedPosts").whereEqualTo("postUid", postUID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots.getDocuments())
                        {
                            documentSnapshot1.getReference().delete();
                        }
                    }
                });
            }
        });
    }

    public interface LikesListener {
        void OnUpdate(boolean userLiked, int likesAmount);
    }
}
