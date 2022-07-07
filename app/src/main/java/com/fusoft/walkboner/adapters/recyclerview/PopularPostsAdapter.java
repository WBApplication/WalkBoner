package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
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
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.widget.ProgressBar;

public class PopularPostsAdapter extends RecyclerView.Adapter<PopularPostsAdapter.ViewHolder> {

    private List<Post> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public PopularPostsAdapter(Context context, List<Post> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_popular_post, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(PopularPostsAdapter.ViewHolder holder, int position) {
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

                holder.postImage.setOnClickListener(view -> {
                    Intent intent = new Intent(context, FullPhotoViewerActivity.class);
                    intent.putExtra("imageUrl", post.getPostImage());
                    intent.putExtra("type", "postImage");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(((MainActivity) context), (View) holder.postImage, "postImage");
                    context.startActivity(intent, options.toBundle());
                });

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

                        return false;
                    }
                }).into(holder.postImage);
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
            return amount + " polubień";
        } else if (amount == 1) {
            return amount + " polubienie";
        } else if (amount <= 4 && amount >= 2) {
            return amount + " polubienia";
        } else {
            return amount + " polubień";
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
        ImageView postImage;
        ProgressBar loadingBar;

        ViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.image);
            postImage.setVisibility(View.INVISIBLE);
            loadingBar = itemView.findViewById(R.id.loading_progress_bar);
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
    void setClickListener(PopularPostsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}