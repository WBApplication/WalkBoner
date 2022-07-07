package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fusoft.walkboner.FullPhotoViewerActivity;
import com.fusoft.walkboner.MainActivity;
import com.fusoft.walkboner.ProfileActivity;
import com.fusoft.walkboner.R;
import com.fusoft.walkboner.database.funcions.LikePost;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.Profile;
import com.fusoft.walkboner.views.dialogs.ReportDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.RollingTextView;
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

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<Post> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private int prevLikesCount = 0;

    // data is passed into the constructor
    public PostsAdapter(Context context, List<Post> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = mData.get(position);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("userUid", post.getUserUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                User userData = new User();
                userData.setUserName(doc.get("username").toString());
                userData.setUserAvatar(doc.get("avatar").toString());
                userData.setUserVerified(Boolean.parseBoolean(doc.get("isVerified").toString()));
                userData.setUserModerator(Boolean.parseBoolean(doc.get("isMod").toString()));
                userData.setUserAdmin(Boolean.parseBoolean(doc.get("isAdmin").toString()));
                userData.setUserBanned(Boolean.parseBoolean(doc.get("isBanned").toString()));

                holder.personNickText.setText(userData.getUserName());
                holder.personMentionNickText.setText("@" + userData.getUserName());
                holder.postDescriptionText.setText(post.getPostDescription());
                //holder.postLikeText.setText(post.getPostLikes().size());

                holder.personAvatarImage.setOnClickListener(view -> {
                    openProfile(post.getUserUid());
                });

                holder.personNickText.setOnClickListener(view -> {
                    openProfile(post.getUserUid());
                });

                holder.personMentionNickText.setOnClickListener(view -> {
                    openProfile(post.getUserUid());
                });

                holder.postImage.setOnClickListener(view -> {
                    Intent intent = new Intent(context, FullPhotoViewerActivity.class);
                    intent.putExtra("imageUrl", post.getPostImage());
                    intent.putExtra("type", "postImage");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(((MainActivity) context), (View) holder.postImage, "postImage");
                    context.startActivity(intent, options.toBundle());
                });

                holder.likesCounterText.setCharStrategy(Strategy.CarryBitAnimation(Direction.SCROLL_DOWN));
                holder.likesCounterText.addCharOrder(CharOrder.Number);
                holder.likesCounterText.setAnimationInterpolator(new OvershootInterpolator());

                Glide.with(context).load(post.getPostImage()).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        float radius = 20f;
                        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.

                        holder.postImage.setVisibility(View.VISIBLE);
                        holder.loadingBar.setVisibility(View.GONE);

                        ViewGroup rootView = (ViewGroup) holder.imageHolder;

                        return false;
                    }
                }).into(holder.postImage);

                if (!userData.getUserAvatar().contentEquals("default")) {
                    Glide.with(context).load(userData.getUserAvatar()).into(holder.personAvatarImage);
                }

                PopupMenu popupMenu = new PopupMenu(holder.moreButton);
                popupMenu.inflate(R.menu.post_popup_menu);

                popupMenu.setBlurEffectEnabled(true);

                popupMenu.setPopupMenuListener(new PopupMenu.PopupMenuListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Zgłoś")) {
                            ReportDialog.ReportPostDialog(context, post.getPostUid());
                        }
                        return true;
                    }

                    @Override
                    public void onMenuItemUpdate(MenuItem menuItem) {

                    }
                });

                holder.moreButton.setOnClickListener(view -> {
                    popupMenu.show();
                });

                new LikePost().LikeWatcher(post.getPostDocumentUid(), (userLiked, likesAmount) -> {
                    post.setUserLikedPost(userLiked);
                    if (userLiked) {
                        holder.postLikeButton.setImageDrawable(context.getDrawable(de.dlyt.yanndroid.oneui.R.drawable.ic_oui_like_on));
                    } else {
                        holder.postLikeButton.setImageDrawable(context.getDrawable(de.dlyt.yanndroid.oneui.R.drawable.ic_oui_like_off));
                    }

                    if (likesAmount > prevLikesCount) {
                        holder.likesCounterText.setCharStrategy(Strategy.CarryBitAnimation(Direction.SCROLL_DOWN));
                    } else {
                        holder.likesCounterText.setCharStrategy(Strategy.CarryBitAnimation(Direction.SCROLL_UP));
                    }

                    prevLikesCount = likesAmount;

                    holder.postLikeButton.setEnabled(true);
                    holder.likesCounterText.setAnimationDuration(200);
                    holder.likesCounterText.setText(String.valueOf(likesAmount), true);
                    holder.postLikeText.setText(parseLikes(likesAmount));
                });

                // TODO: Dodawaj polubione posty do danyh użytkownika w bazie danych

                holder.postLikeButton.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
                        holder.postLikeButton.setEnabled(false);
                        if (post.isUserLikedPost()) {
                            LikePost.UnLike(post.getPostDocumentUid());
                        } else {
                            LikePost.Like(post.getPostDocumentUid());
                        }

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

    private String parseLikes(int amount) {
        if (amount == 0) {
            return " polubień";
        } else if (amount == 1) {
            return " polubienie";
        } else if (amount <= 4 && amount >= 2) {
            return " polubienia";
        } else {
            return " polubień";
        }
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
        ImageView postImage, personAvatarImage, postLikeButton, moreButton;
        RollingTextView likesCounterText;
        MaterialTextView personNickText, personMentionNickText, postLikeText;
        ReadMoreTextView postDescriptionText;
        ConstraintLayout imageHolder;
        ProgressBar loadingBar;

        ViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.image);
            moreButton = itemView.findViewById(R.id.more_button);
            postImage.setVisibility(View.INVISIBLE);
            personAvatarImage = itemView.findViewById(R.id.person_avatar_image);
            postLikeButton = itemView.findViewById(R.id.post_like_button);
            personNickText = itemView.findViewById(R.id.person_nick_text);
            personMentionNickText = itemView.findViewById(R.id.person_mention_nick_text);
            postLikeText = itemView.findViewById(R.id.post_likes_text);
            postDescriptionText = itemView.findViewById(R.id.post_description_text);
            imageHolder = itemView.findViewById(R.id.image_holder);
            loadingBar = itemView.findViewById(R.id.loading_bar);
            likesCounterText = itemView.findViewById(R.id.likes_counter_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Post getPost(int id) {
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
