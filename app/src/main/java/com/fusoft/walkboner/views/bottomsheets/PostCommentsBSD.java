package com.fusoft.walkboner.views.bottomsheets;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.adapters.recyclerview.PostCommentsAdapter;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.funcions.GetPostComments;
import com.fusoft.walkboner.database.funcions.PostCommentsListener;
import com.fusoft.walkboner.models.Comment;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.GetTimeStamp;
import com.fusoft.walkboner.utils.UidGenerator;
import com.fusoft.walkboner.views.Avatar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.List;

import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.view.Toast;

public class PostCommentsBSD {

    private static Avatar newCommentAvatarImage;
    private static EditText newCommentContentEdittext;
    private static ImageView newCommentSendButton;
    private static RecyclerView commentsRecyclerView;
    private static LinearLayout loadingCommentsLinear;

    public static void show(String postUID, Context context) {
        Authentication authentication = new Authentication(null);

        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                if (user.isUserBanned()) {
                    newCommentAvatarImage.setAvatarOwnerPrivileges(Avatar.BANNED);
                } else if (user.isUserAdmin()) {
                    newCommentAvatarImage.setAvatarOwnerPrivileges(Avatar.ADMIN);
                } else if (user.isUserModerator()) {
                    newCommentAvatarImage.setAvatarOwnerPrivileges(Avatar.MODERATOR);
                } else {
                    newCommentAvatarImage.setAvatarOwnerPrivileges(Avatar.USER);
                }

                if (!user.getUserAvatar().contentEquals("default")) {
                    newCommentAvatarImage.setImageFromUrl(user.getUserAvatar());
                }
            }

            @Override
            public void OnUserNotFinded() {

            }

            @Override
            public void OnError(String reason) {

            }
        });

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_post_comment);

        newCommentAvatarImage = (Avatar) bottomSheetDialog.findViewById(R.id.new_comment_avatar_image);
        newCommentContentEdittext = (EditText) bottomSheetDialog.findViewById(R.id.new_comment_content_edittext);
        newCommentSendButton = (ImageView) bottomSheetDialog.findViewById(R.id.new_comment_send_button);
        commentsRecyclerView = (RecyclerView) bottomSheetDialog.findViewById(R.id.comments_recycler_view);
        loadingCommentsLinear = bottomSheetDialog.findViewById(R.id.loading_comments_linear);

        bottomSheetDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        commentsRecyclerView.setLayoutManager(layoutManager);

        newCommentSendButton.setOnClickListener(v -> {
            if (!newCommentContentEdittext.getText().toString().isEmpty()) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                HashMap<String, Object> map = new HashMap<>();
                map.put("commentUid", UidGenerator.Generate());
                map.put("commentContent", newCommentContentEdittext.getText().toString());
                map.put("userUid", authentication.getCurrentUserUid());
                map.put("createdAt", GetTimeStamp.getString());

                firestore.collection("posts").document(postUID).collection("comments").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        newCommentContentEdittext.setText("");
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        GetPostComments.get(postUID, new PostCommentsListener() {
            @Override
            public void OnCommentsReceived(List<Comment> comments) {
                PostCommentsAdapter adapter = new PostCommentsAdapter(context, comments);
                commentsRecyclerView.setAdapter(adapter);
                loadingCommentsLinear.setVisibility(View.GONE);
            }

            @Override
            public void OnError(String reason) {

            }
        });
    }
}
