package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.fusoft.walkboner.FullPhotoViewerActivity;
import com.fusoft.walkboner.MainActivity;
import com.fusoft.walkboner.ProfileActivity;
import com.fusoft.walkboner.R;
import com.fusoft.walkboner.database.funcions.LikePost;
import com.fusoft.walkboner.models.Comment;
import com.fusoft.walkboner.models.Influencer;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.Profile;
import com.fusoft.walkboner.views.Avatar;
import com.fusoft.walkboner.views.dialogs.ReportDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import de.dlyt.yanndroid.oneui.menu.MenuItem;
import de.dlyt.yanndroid.oneui.menu.PopupMenu;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.widget.ProgressBar;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.BlurViewFacade;
import eightbitlab.com.blurview.RenderScriptBlur;
import kr.co.prnd.readmore.ReadMoreTextView;
import me.virtualiz.blurshadowimageview.BlurShadowImageView;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.ViewHolder> {

    private List<Comment> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public PostCommentsAdapter(Context context, List<Comment> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = mData.get(position);
        User user = comment.getUserCommentDetails();

        if (user.isUserBanned()) {
            holder.userImage.setAvatarOwnerPrivileges(Avatar.BANNED);
        } else if (user.isUserAdmin()) {
            holder.userImage.setAvatarOwnerPrivileges(Avatar.ADMIN);
        } else if (user.isUserModerator()) {
            holder.userImage.setAvatarOwnerPrivileges(Avatar.MODERATOR);
        } else {
            holder.userImage.setAvatarOwnerPrivileges(Avatar.USER);
        }

        if (!user.getUserAvatar().contentEquals("default")) {
            holder.userImage.setImageFromUrl(user.getUserAvatar());
        }

        holder.userNameText.setText(user.getUserName());
        holder.contentText.setText(comment.getCommentContent());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void openProfile(String uid) {
        MainActivity mainActivity = ((MainActivity) context);

        Intent intent = new Intent(mainActivity, ProfileActivity.class);
        intent.putExtra("userUid", uid);
        mainActivity.startActivity(intent);
        mainActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Avatar userImage;
        MaterialTextView userNameText;
        MaterialTextView contentText;
        MaterialTextView likeText;
        ImageView commentLikeButton;

        ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.avatar_image);
            userNameText = itemView.findViewById(R.id.user_name_text);
            contentText = itemView.findViewById(R.id.comment_content_text);
            likeText = itemView.findViewById(R.id.comment_like_text);
            commentLikeButton = itemView.findViewById(R.id.comment_like_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Comment getComment(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}