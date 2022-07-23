package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
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
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.database.funcions.LikePost;
import com.fusoft.walkboner.models.Influencer;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.CopyTextToClipboard;
import com.fusoft.walkboner.utils.Profile;
import com.fusoft.walkboner.views.Avatar;
import com.fusoft.walkboner.views.bottomsheets.PostCommentsBSD;
import com.fusoft.walkboner.views.dialogs.ReportDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.strategy.Direction;
import com.yy.mobile.rollingtextview.strategy.Strategy;

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

public class NewInfluencersAdapter extends RecyclerView.Adapter<NewInfluencersAdapter.ViewHolder> {

    private List<Influencer> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public NewInfluencersAdapter(Context context, List<Influencer> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_influencer_post, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Influencer influencer = mData.get(position);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("userUid", influencer.getInfluencerAddedBy()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                User userData = new User();
                userData.setUserName(doc.get("username").toString());
                userData.setUserAvatar(doc.get("avatar").toString());
                userData.setUserVerified(Boolean.parseBoolean(doc.get("isVerified").toString()));
                userData.setUserModerator(Boolean.parseBoolean(doc.get("isMod").toString()));
                userData.setUserAdmin(Boolean.parseBoolean(doc.get("isAdmin").toString()));
                userData.setUserBanned(Boolean.parseBoolean(doc.get("isBanned").toString()));

                holder.influencerNickText.setText(influencer.getInfluencerNickName());
                holder.celebrityNameText.setText(influencer.getInfluencerFirstName());
                holder.celebritySurnameText.setText(influencer.getInfluencerLastName());
                holder.requestUserNameText.setText(userData.getUserName());

                holder.requestUserUidButton.setOnClickListener(v -> {
                    new CopyTextToClipboard(context, influencer.getInfluencerAddedBy());
                });

                holder.acceptButton.setOnClickListener(v -> {
                    mClickListener.onAcceptClick(holder.itemView, position);
                });

                holder.declineButton.setOnClickListener(v -> {
                    mClickListener.onDeclineClick(holder.itemView, position);
                });

                Glide.with(context).load(influencer.getInfluencerAvatar()).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.influencerImage.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
            }
        });
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView influencerImage;
        MaterialTextView influencerNickText, celebrityNameText, celebritySurnameText, requestUserNameText, requestUserUidButton;
        MaterialButton acceptButton, declineButton;

        ViewHolder(View itemView) {
            super(itemView);
            influencerImage = itemView.findViewById(R.id.influencer_image);
            influencerNickText = itemView.findViewById(R.id.influencer_nick_text);
            celebrityNameText = itemView.findViewById(R.id.celebrity_name_text);
            celebritySurnameText = itemView.findViewById(R.id.celebrity_surname_text);
            requestUserNameText = itemView.findViewById(R.id.request_user_name_text);
            requestUserUidButton = itemView.findViewById(R.id.request_user_uid_button);
            acceptButton = itemView.findViewById(R.id.accept_profile_button);
            declineButton = itemView.findViewById(R.id.delete_profile_button);
        }
    }

    // convenience method for getting data at click position
    public Influencer getInfluencer(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void removeFromList(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onAcceptClick(View view, int position);

        void onDeclineClick(View view, int position);
    }
}