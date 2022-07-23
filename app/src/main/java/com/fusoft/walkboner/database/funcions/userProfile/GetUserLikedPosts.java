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

    int a = 0;

    private void log(String message) {
        Log.e("UPostsLog", "______________");
        Log.e("UPostsLog", message);
    }

    public void get(Context context, UserLikedPostsListener listener) {
        log("Getting User Liked Posts From Database...");
        firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                log("User Finded!");
                DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);

                userDocument.getReference().collection("likedPosts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        log("Finded Liked Posts with size " + queryDocumentSnapshots.getDocuments().size());
                        for (DocumentSnapshot likedPostsUserDocument : queryDocumentSnapshots.getDocuments()) {
                            log("Getting post data for post with UID " + likedPostsUserDocument.getString("postUid"));
                            firestore.collection("posts").document(likedPostsUserDocument.getString("postUid")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    log("Finded Post with UID " + documentSnapshot.getString("postUid"));
                                    Post post = new Post();
                                    post.setUserUid(documentSnapshot.getString("userUid"));
                                    post.setPostUid(documentSnapshot.getString("postUid"));
                                    post.setPostDocumentUid(documentSnapshot.getId());
                                    post.setPostDescription(documentSnapshot.getString("postDescription"));
                                    post.setPostImage(documentSnapshot.getString("postImage"));
                                    post.setCreatedAt(documentSnapshot.getString("createdAt"));
                                    post.setShowsCelebrity(documentSnapshot.getString("showsCelebrity"));
                                    post.setAllowComments(Boolean.parseBoolean(documentSnapshot.getString("allowComments")));
                                    post.setUserLikedPost(true);

                                    List<String> likesList = new ArrayList<>();
                                    firestore.collection("posts").document(documentSnapshot.getId()).collection("likes").get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                        for (DocumentSnapshot docLikes : queryDocumentSnapshots1.getDocuments()) {
                                            likesList.add(docLikes.getString("userUid"));
                                            if (docLikes.getString("userUid").contentEquals(user.getUid())) {
                                                post.setUserLikedPost(true);
                                            }
                                        }
                                    });
                                    post.setPostLikes(likesList);
                                    log("Post with description | " + post.getPostDescription() + " | finded!");
                                    posts.add(post);
                                    listener.OnLoaded(posts);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    log("Błąd 82: " + e.getMessage());
                                    listener.OnError("Nie udało się uzyskać szczegółów posta: " + e.getMessage());
                                }
                            });
                        }
                        log("listener invoked");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.OnError("Nie udało się przechwycić polubionych postów użytkownika:\n" + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.OnError("Nie znaleziono użytkownika:\n" + e.getMessage());
            }
        });
    }

    public interface UserLikedPostsListener {
        void OnLoaded(List<Post> posts);

        void OnError(String error);
    }
}
