package com.fusoft.walkboner.database.funcions;

import android.content.Context;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.database.GetUserDetails;
import com.fusoft.walkboner.models.Comment;
import com.fusoft.walkboner.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetPostComments {
    private static User userDetails;

    private static int a = 0;

    public static void get(String postUID, PostCommentsListener listener) {
        Authentication authentication = new Authentication(null);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<Comment> commentList = new ArrayList<>();

        firestore.collection("posts").document(postUID).collection("comments").orderBy("createdAt", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot commentDocument : queryDocumentSnapshots.getDocuments()) {
                    userDetails = new User();
                    GetUserDetails.getByUid(commentDocument.getString("userUid"), new GetUserDetails.UserDetailsListener() {
                        @Override
                        public void OnReceived(User user) {
                            userDetails = user;

                            Comment comment = new Comment();
                            comment.setCommentUid(commentDocument.getString("commentUid"));
                            comment.setCommentContent(commentDocument.getString("commentContent"));
                            comment.setUserCommentDetails(userDetails);

                            List<String> likesList = new ArrayList<>();
                            firestore.collection("posts").document(postUID).collection("likes").get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                for (DocumentSnapshot docLikes : queryDocumentSnapshots1.getDocuments()) {
                                    likesList.add(docLikes.getString("userUid"));
                                    if (docLikes.getString("userUid").contentEquals(authentication.getCurrentUserUid())) {
                                        comment.setUserLikedComment(true);
                                    }
                                }
                            });
                            comment.setLikes(likesList);
                            commentList.add(comment);

                            a++;

                            listener.OnCommentsReceived(commentList);
                        }

                        @Override
                        public void OnError(String reason) {

                        }
                    });
                }
            }
        });
    }
}
