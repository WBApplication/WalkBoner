package com.fusoft.walkboner.database.funcions.userProfile;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetUserLikedPosts {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    List<Post> posts = new ArrayList<>();

    public void get(Context context, UserLikedPostsListener listener) {
        firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.e("GetUserLikePosts", "Znaleziono użytkownika");
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                doc.getReference().collection("likedPosts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                        Log.e("GetUserLikePosts", "Znaleziono polubione posty użytkownika");
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots1.getDocuments()) {
                            Toast.makeText(context, "Post", Toast.LENGTH_LONG).show();
                            firestore.collection("posts").whereEqualTo("postUid", documentSnapshot.getString("postUid")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots2) {
                                    Log.e("GetUserLikePosts", "Dodano Post");
                                    for (DocumentSnapshot document : queryDocumentSnapshots2.getDocuments()) {
                                        Post post = new Post();
                                        post.setUserUid(document.get("userUid").toString());
                                        post.setPostUid(document.get("postUid").toString());
                                        post.setPostDescription(document.get("postDescription").toString());
                                        post.setPostImage(document.get("postImage").toString());
                                        post.setCreatedAt(document.get("createdAt").toString());
                                        post.setShowsCelebrity(document.get("showsCelebrity").toString());
                                        post.setAllowComments(Boolean.parseBoolean(document.get("allowComments").toString()));
                                        post.setUserLikedPost(false);
                                        post.setPostDocumentUid(document.getId());

                                        List<String> likesList = new ArrayList<>();
                                        // Get Likes
                                        firestore.collection("posts").document(document.getId()).collection("likes").get().addOnSuccessListener(queryDocumentSnapshots3 -> {
                                            for (DocumentSnapshot docLikes : queryDocumentSnapshots3.getDocuments()) {
                                                likesList.add(docLikes.getString("userUid"));
                                                if (docLikes.getString("userUid").contentEquals(user.getUid())) {
                                                    post.setUserLikedPost(true);
                                                }
                                            }
                                        });
                                        post.setPostLikes(likesList);
                                        posts.add(post);
                                    }
                                    Log.e("GetUserLikePosts", "Loading " + posts.size());
                                    listener.OnLoaded(posts);

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
        });
    }

    public interface UserLikedPostsListener {
        void OnLoaded(List<Post> posts);

        void OnError(String error);
    }
}
