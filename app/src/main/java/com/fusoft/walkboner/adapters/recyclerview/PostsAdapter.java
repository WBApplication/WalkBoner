package com.fusoft.walkboner.adapters.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fusoft.walkboner.FullPhotoViewerActivity;
import com.fusoft.walkboner.MainActivity;
import com.fusoft.walkboner.ProfileActivity;
import com.fusoft.walkboner.R;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.database.funcions.GetPopularPosts;
import com.fusoft.walkboner.database.funcions.LikePost;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.Profile;
import com.fusoft.walkboner.views.Avatar;
import com.fusoft.walkboner.views.bottomsheets.PostCommentsBSD;
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
import de.dlyt.yanndroid.oneui.sesl.recyclerview.GridLayoutManager;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.widget.ProgressBar;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.BlurViewFacade;
import eightbitlab.com.blurview.RenderScriptBlur;
import kr.co.prnd.readmore.ReadMoreTextView;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private HeartClickListener mHeartListener;
    private Context context;
    private int prevLikesCount = 0;

    private int BEST_POSTS_ITEM = 0;
    private int POST_ITEM = 1;

    // data is passed into the constructor
    public PostsAdapter(Context context, List<Post> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BEST_POSTS_ITEM) {
            return new PostsAdapter.BestPostsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_header, parent, false));
        }
        return new PostsAdapter.PostsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mData.get(position).isHeader()) {
            ((PostsAdapter.BestPostsViewHolder) holder).bind(position);
        } else {
            ((PostsAdapter.PostsViewHolder) holder).bind(position);
        }
    }

    private class BestPostsViewHolder extends RecyclerView.ViewHolder {
        RecyclerView popularPostsRecyclerView;

        BestPostsViewHolder(final View itemView) {
            super(itemView);

            popularPostsRecyclerView = itemView.findViewById(R.id.popular_posts_recycler_view);
        }

        void bind(int position) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
            popularPostsRecyclerView.setLayoutManager(gridLayoutManager);

            GetPopularPosts.get(new GetPopularPosts.PostsListener() {
                @Override
                public void OnLoaded(List<Post> posts) {
                    PopularPostsAdapter popularPostsAdapter = new PopularPostsAdapter(context, posts);
                    popularPostsRecyclerView.setAdapter(popularPostsAdapter);
                }

                @Override
                public void OnError(String error) {

                }
            }, 3);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).isHeader()) {
            return BEST_POSTS_ITEM;
        } else {
            return POST_ITEM;
        }
    }

    private class PostsViewHolder extends RecyclerView.ViewHolder {
        Avatar personAvatarImage;
        ImageView postImage, postLikeButton, moreButton;
        RollingTextView likesCounterText;
        MaterialTextView personNickText, personMentionNickText, postLikeText;
        ReadMoreTextView postDescriptionText;
        ConstraintLayout imageHolder;
        ProgressBar loadingBar;
        ImageView commentsButton;

        PostsViewHolder(final View itemView) {
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
            commentsButton = itemView.findViewById(R.id.post_comments_button);
        }

        void bind(int position) {
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

                    personNickText.setText(userData.getUserName());
                    personMentionNickText.setText("@" + userData.getUserName());
                    postDescriptionText.setText(post.getPostDescription());
                    //holder.postLikeText.setText(post.getPostLikes().size());

                    personAvatarImage.setOnClickListener(view -> {
                        openProfile(post.getUserUid());
                    });

                    personNickText.setOnClickListener(view -> {
                        openProfile(post.getUserUid());
                    });

                    personMentionNickText.setOnClickListener(view -> {
                        openProfile(post.getUserUid());
                    });

                    postImage.setOnClickListener(view -> {
                        Intent intent = new Intent(context, FullPhotoViewerActivity.class);
                        intent.putExtra("imageUrl", post.getPostImage());
                        intent.putExtra("type", "postImage");
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) context, (View) postImage, "postImage");
                        context.startActivity(intent, options.toBundle());
                    });

                    likesCounterText.setCharStrategy(Strategy.CarryBitAnimation(Direction.SCROLL_DOWN));
                    likesCounterText.addCharOrder(CharOrder.Number);
                    likesCounterText.setAnimationInterpolator(new OvershootInterpolator());

                    RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                    Glide.with(context).load(post.getPostImage()).apply(requestOptions).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            float radius = 20f;
                            // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
                            postImage.setVisibility(View.VISIBLE);
                            loadingBar.setVisibility(View.GONE);

                            ViewGroup rootView = (ViewGroup) imageHolder;

                            return false;
                        }
                    }).into(postImage);

                    if (userData.isUserBanned()) {
                        personAvatarImage.setAvatarOwnerPrivileges(Avatar.BANNED);
                    } else if (userData.isUserAdmin()) {
                        personAvatarImage.setAvatarOwnerPrivileges(Avatar.ADMIN);
                    } else if (userData.isUserModerator()) {
                        personAvatarImage.setAvatarOwnerPrivileges(Avatar.MODERATOR);
                    } else {
                        personAvatarImage.setAvatarOwnerPrivileges(Avatar.USER);
                    }

                    if (!userData.getUserAvatar().contentEquals("default")) {
                        personAvatarImage.setImageFromUrl(userData.getUserAvatar());
                    }

                    PopupMenu popupMenu = new PopupMenu(moreButton);
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

                    Authentication authentication = new Authentication(null);
                    if (post.getUserUid().contentEquals(authentication.getCurrentUserUid())) {
                        popupMenu.getMenu().findItem(R.id.delete_popup_button).setVisible(true);
                    }

                    moreButton.setOnClickListener(view -> {
                        popupMenu.show();
                    });

                    commentsButton.setOnClickListener(v -> {
                        PostCommentsBSD.show(post.getPostDocumentUid(), context);
                    });

                    new LikePost().LikeWatcher(post.getPostDocumentUid(), (userLiked, likesAmount) -> {
                        post.setUserLikedPost(userLiked);
                        if (userLiked) {
                            postLikeButton.setImageDrawable(context.getDrawable(de.dlyt.yanndroid.oneui.R.drawable.ic_oui_like_on));
                        } else {
                            postLikeButton.setImageDrawable(context.getDrawable(de.dlyt.yanndroid.oneui.R.drawable.ic_oui_like_off));
                        }

                        if (likesAmount > prevLikesCount) {
                            likesCounterText.setCharStrategy(Strategy.CarryBitAnimation(Direction.SCROLL_DOWN));
                        } else {
                            likesCounterText.setCharStrategy(Strategy.CarryBitAnimation(Direction.SCROLL_UP));
                        }

                        prevLikesCount = likesAmount;

                        postLikeButton.setEnabled(true);
                        likesCounterText.setAnimationDuration(200);
                        likesCounterText.setText(String.valueOf(likesAmount), true);
                        postLikeText.setText(parseLikes(likesAmount));
                    });

                    // TODO: Dodawaj polubione posty do danyh użytkownika w bazie danych

                    postLikeButton.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View view) {
                            postLikeButton.setEnabled(false);
                            if (mHeartListener != null) {
                                mHeartListener.onHeartClick(position);
                            }

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
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("userUid", uid);
        context.startActivity(intent);
    }

    // convenience method for getting data at click position
    Post getPost(int id) {
        return mData.get(id);
    }

    public void removeFromList(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void setHeartClickListener(HeartClickListener heartClickListener) {
        this.mHeartListener = heartClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface HeartClickListener {
        void onHeartClick(int position);
    }
}
