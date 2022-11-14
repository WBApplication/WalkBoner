package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fusoft.walkboner.R;
import com.fusoft.walkboner.models.album.Album;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {

    private List<Album> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;
    private Context context;

    // data is passed into the constructor
    public AlbumsAdapter(Context context, List<Album> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Album album = mData.get(position);

        Glide.with(context)
                .load(album.getAlbumMainImage())
                .addListener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.loadingProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.albumImage);

        if (album.isSingleImage()) {
            holder.contentAmountDescriptionImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.media_single_outline));
        } else {
            holder.contentAmountDescriptionImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.media_multiple_outline));
        }

        holder.itemView.setOnClickListener(v -> {
            if (mClickListener != null) mClickListener.onItemClick(mData.get(position), position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (mLongClickListener != null) mLongClickListener.onAlbumLongClicked(album);
            return false;
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView albumImage, contentAmountDescriptionImage;
        LinearLayout loadingProgressBar;

        ViewHolder(View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.album_main_image);
            contentAmountDescriptionImage = itemView.findViewById(R.id.content_amount_description_image);
            loadingProgressBar = itemView.findViewById(R.id.loading_progress_bar);
        }
    }

    // convenience method for getting data at click position
    Album getAlbum(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }
}