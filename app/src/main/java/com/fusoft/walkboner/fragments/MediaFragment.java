package com.fusoft.walkboner.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fusoft.walkboner.FullPhotoViewerActivity;
import com.fusoft.walkboner.MediaViewerActivity;
import com.fusoft.walkboner.R;
import com.fusoft.walkboner.views.Player;
import com.google.android.material.textview.MaterialTextView;
import com.ortiz.touchview.OnTouchImageViewListener;
import com.ortiz.touchview.TouchImageView;

public class MediaFragment extends Fragment {
    private final String TAG = "MediaPlayer";
    
    View mRootView;

    private TouchImageView image;
    private Player player;
    private LinearLayout errorLinear, loadingLinear;
    private MaterialTextView errorReasonText;

    private Drawable imageLoaded;

    @Override
    public void onDestroyView() {
        player.DestroyPlayer();
        player = null;

        super.onDestroyView();
    }

    public static MediaFragment newInstance(String imageUrl) {
        MediaFragment fragment = new MediaFragment();

        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);

        return fragment;
    }

    public String getImageUrl() {
        return getArguments().getString("imageUrl", "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_media, container, true);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        setup();
    }

    private void initView() {
        image = mRootView.findViewById(R.id.image);
        player = mRootView.findViewById(R.id.player);
        errorLinear = (LinearLayout) mRootView.findViewById(R.id.error_linear);
        errorReasonText = (MaterialTextView) mRootView.findViewById(R.id.error_reason_text);
        loadingLinear = mRootView.findViewById(R.id.loading_progress_bar);
    }

    private void setup() {
        if (getImageUrl().contains("mp4")) {
            Log.d(TAG, "setup: Media is VIDEO");
            image.setVisibility(View.GONE);
            player.setVisibility(View.VISIBLE);

            loadVideo();
        } else {
            Log.d(TAG, "setup: Media is PICTURE");
            image.setVisibility(View.VISIBLE);
            player.setVisibility(View.GONE);

            loadImage();
        }

        image.setOnTouchImageViewListener(new OnTouchImageViewListener() {
            @Override
            public void onMove() {
                if (image.isZoomed()) {
                    Log.d("Teest", "isZoomed -> Locking ViewPager Input");

                    if (getActivity() != null && getActivity() instanceof MediaViewerActivity) {
                        ((MediaViewerActivity) getActivity()).toggleImageAttention(true);
                    }
                } else {
                    Log.d("Teest", "isNotZoomed -> Unlocking ViewPager Input");

                    if (getActivity() != null && getActivity() instanceof MediaViewerActivity) {
                        ((MediaViewerActivity) getActivity()).toggleImageAttention(false);
                    }
                }
            }
        });
    }

    private void loadVideo() {
        player.setListener(new Player.VideoListener() {
            @Override
            public void OnLoaded() {
                loadingLinear.setVisibility(View.GONE);
            }

            @Override
            public void OnStateChanged(boolean isPlaying) {
                if (isPlaying) {
                    if (getActivity() != null && getActivity() instanceof MediaViewerActivity) {
                        ((MediaViewerActivity) getActivity()).toggleImageAttention(true);
                    }
                } else {
                    if (getActivity() != null && getActivity() instanceof MediaViewerActivity) {
                        ((MediaViewerActivity) getActivity()).toggleImageAttention(false);
                    }
                }
            }

            @Override
            public void OnError(String reason) {
                loadingLinear.setVisibility(View.GONE);
                errorLinear.setVisibility(View.VISIBLE);
                errorReasonText.setText(reason);
            }
        });

        player.playFromUrl(getImageUrl());
    }

    private void loadImage() {
        Glide.with(image)
                .load(getImageUrl())
                .placeholder(R.drawable.transparent_background)
                .error(R.drawable.transparent_background)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loadingLinear.setVisibility(View.GONE);
                        errorLinear.setVisibility(View.VISIBLE);
                        if (e != null) {
                            errorReasonText.setText("GlideException: " + e.getMessage());
                        } else {
                            errorReasonText.setText("GlideException: Nieznany Błąd");
                        }

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadingLinear.setVisibility(View.GONE);

                        return false;
                    }
                })
                .into(image);

        /*Picasso.get()
                .load(getImageUrl())
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Teest", "Picasso: Image loaded");
                        loadingLinear.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Teest", "Picasso: Image load error");
                        loadingLinear.setVisibility(View.GONE);
                        errorLinear.setVisibility(View.VISIBLE);
                        errorReasonText.setText(e.getMessage());
                    }
                });*/
    }
}
