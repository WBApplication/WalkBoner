package com.fusoft.walkboner.adapters.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.utils.GetPathFromUri;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MediaPickerAdapter extends RecyclerView.Adapter<MediaPickerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<HashMap<String, Object>> mImage;
    private int VIEW_TYPE_PICKER = 0;
    private int VIEW_TYPE_IMAGE = 1;

    private int MAX_IMAGE_LIMIT = 20;

    private OnItemClickListener listener;

    public MediaPickerAdapter(Context context, ArrayList<HashMap<String, Object>> imagesList) {
        mImage = imagesList;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mImage.size()) ? VIEW_TYPE_PICKER : VIEW_TYPE_IMAGE;
    }

    @Override
    public int getItemCount() {
        return Math.min(mImage.size(), MAX_IMAGE_LIMIT) + 1;
    }

    @NonNull
    @Override
    public MediaPickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_IMAGE) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picked_image, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pick_image, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position == mImage.size()) {
            holder.default_select_photo_iv.setOnClickListener(view -> {
                listener.OnClick(VIEW_TYPE_PICKER, position);
            });
        } else {
            if (holder.recent_photos_iv != null) {
                String imagePath = GetPathFromUri.getPath(mContext, (Uri) mImage.get(position).get("imagePath"));

                /*Bitmap image = ImageUtils.getInstant().getCompressedBitmap(imagePath);*/

                File file = new File(imagePath);

                Picasso.get().load(file).into(holder.recent_photos_iv);

                /*Glide.with(holder.recent_photos_iv)
                        .load(image)
                        .into(holder.recent_photos_iv);*/

                PopupMenu menu = new PopupMenu(mContext, holder.image_more_options_button);
                menu.inflate(R.menu.picked_image_menu);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.lock_image_button:
                                // TODO: Set Min SDK 24
                                mImage.get(position).replace("isLocked", menuItem.isChecked());
                                listener.OnLockImageClick(position);
                                return false;
                            case R.id.replace_image_button:
                                listener.OnReplaceImageClick(position);
                                return true;
                            case R.id.delete_image_button:
                                mImage.remove(position);
                                notifyItemRemoved(position);
                                listener.OnDeleteImageClick(position);
                                return true;
                        }

                        return true;
                    }
                });

                menu.getMenu().getItem(0).setChecked((Boolean) mImage.get(position).get("isLocked"));

                holder.image_more_options_button.setOnClickListener(view -> {
                    menu.show();
                });

                holder.recent_photos_iv.setOnClickListener(view -> {
                    listener.OnClick(VIEW_TYPE_IMAGE, position);
                });
            }
        }
    }

    public ArrayList<HashMap<String, Object>> getList() {
        return mImage;
    }

    public void addImage(HashMap<String, Object> imagePath) {
        mImage.add(imagePath);
        notifyItemInserted(mImage.size() - 1);
    }

    public void replaceImage(HashMap<String, Object> imagePath, int position) {
        mImage.set(position, imagePath);
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView recent_photos_iv, image_more_options_button;
        CardView default_select_photo_iv;

        public ViewHolder(View itemView) {
            super(itemView);
            image_more_options_button = itemView.findViewById(R.id.image_more_options_button);
            recent_photos_iv = itemView.findViewById(R.id.picked_image);
            default_select_photo_iv = itemView.findViewById(R.id.pick_image_button);

        }
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void OnClick(int type, int position);

        void OnLockImageClick(int position);

        void OnReplaceImageClick(int position);

        void OnDeleteImageClick(int position);
    }
}
