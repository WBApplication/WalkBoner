/*
 * Copyright (c) 2022 - WalkBoner.
 * BannedUsersAdapter.java
 */

package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.databinding.ItemModBannedUserBinding;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.models.album.Album;
import com.fusoft.walkboner.utils.LongToDate;
import com.fusoft.walkboner.views.Avatar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.dlyt.yanndroid.oneui.view.RecyclerView;

public class BannedUsersAdapter extends RecyclerView.Adapter<BannedUsersAdapter.ViewHolder> {
    private List<User> mData;
    private LayoutInflater mInflater;
    private ModUnbanListener listener;

    // data is passed into the constructor
    public BannedUsersAdapter(Context context, List<User> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_mod_banned_user, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        User user = mData.get(i);

        holder.userAvatar.setImageFromUrl(user.getUserAvatar());
        holder.userNameText.setText(user.getUserName());
        holder.banReasonText.setText(user.getUserBanReason());

        holder.unbanUserButton.setOnClickListener(v -> {
            listener.OnUnbanClickListener(mData.get(i).getUserUid(), i);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void filterList(List<User> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public void deleteFromList(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Avatar userAvatar;
        MaterialButton unbanUserButton;
        MaterialTextView userNameText, banReasonText;

        ViewHolder(View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar_image);
            unbanUserButton = itemView.findViewById(R.id.unban_user_button);
            userNameText = itemView.findViewById(R.id.user_name_text);
            banReasonText = itemView.findViewById(R.id.user_banned_reason_text);
        }
    }

    public void setListener(ModUnbanListener listener) {
        this.listener = listener;
    }

    public interface ModUnbanListener {
        void OnUnbanClickListener(String userUid, int position);
    }
}
