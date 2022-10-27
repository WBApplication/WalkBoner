package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

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
import com.fusoft.walkboner.models.Influencer;
import com.fusoft.walkboner.models.Notification;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.models.album.Album;
import com.fusoft.walkboner.utils.Profile;
import com.fusoft.walkboner.views.dialogs.ReportDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notification> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public NotificationsAdapter(Context context, List<Notification> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = mData.get(position);

        holder.titleTextView.setText(notification.getNotificationTitle());
        holder.messageTextView.setText(notification.getNotificationDescription());
        holder.deleteButton.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onDeleteClick(notification, position);
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView titleTextView, messageTextView;
        ImageView deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text);
            messageTextView = itemView.findViewById(R.id.description_text);
            deleteButton = itemView.findViewById(R.id.delete_notification_button);
        }
    }

    // convenience method for getting data at click position
    Notification getNotification(int id) {
        return mData.get(id);
    }

    public void deleteAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onDeleteClick(Notification album, int position);
    }
}