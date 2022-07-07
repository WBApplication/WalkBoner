package com.fusoft.walkboner.database.funcions.userProfile;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.database.funcions.GetPosts;
import com.fusoft.walkboner.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class GetUserPosts {
    public void get(GetPosts.PostsListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        List<Post> posts = new ArrayList<>();

        firestore.collection("posts").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
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
                firestore.collection("posts").document(document.getId()).collection("likes").get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                    for (DocumentSnapshot docLikes : queryDocumentSnapshots1.getDocuments()) {
                        likesList.add(docLikes.getString("userUid"));
                        if (docLikes.getString("userUid").contentEquals(user.getUid())) {
                            post.setUserLikedPost(true);
                        }
                    }
                });
                post.setPostLikes(likesList);
                posts.add(post);
            }

            listener.OnLoaded(posts);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.OnError(e.getMessage());
            }
        });
    }

    public interface PostsListener {
        void OnLoaded(List<Post> posts);

        void OnError(String error);
    }
}
