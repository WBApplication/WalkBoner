package com.fusoft.walkboner;

import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.adapters.viewpager.MediaViewerViewPager;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.database.funcions.userProfile.SetAvatar;
import com.fusoft.walkboner.models.album.AlbumImage;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.List;

import de.dlyt.yanndroid.oneui.sesl.viewpager2.widget.SeslViewPager2;
import de.dlyt.yanndroid.oneui.view.ViewPager2;

public class MediaViewerActivity extends AppCompatActivity {
    private ViewPropertyAnimator topLinearAnim;

    private ViewPager2 imagesViewpager;
    private WormDotsIndicator tabIndicator;
    private LinearLayout topViewLinear;
    private ImageView moreMenuButton;

    private MediaViewerViewPager adapter;

    private Authentication auth;

    private List<AlbumImage> images;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_viewer);

        initView();
        setup();
    }

    private void initView() {
        auth = new Authentication(null);

        imagesViewpager = findViewById(R.id.images_viewpager);
        imagesViewpager.setOffscreenPageLimit(5);

        tabIndicator = findViewById(R.id.tab_indicator);
        topViewLinear = findViewById(R.id.top_view_linear);
        topLinearAnim = topViewLinear.animate();
        moreMenuButton = (ImageView) findViewById(R.id.more_menu_button);
    }

    private void setup() {

        images = new Gson().fromJson(getIntent().getStringExtra("albumImages"), new TypeToken<List<AlbumImage>>() {
        }.getType());
        adapter = new MediaViewerViewPager(getSupportFragmentManager(), getLifecycle(), images);
        imagesViewpager.setAdapter(adapter);

        tabIndicator.attachTo(imagesViewpager);

        if (isSingleMedia()) {
            imagesViewpager.setUserInputEnabled(false);
            topViewLinear.setAlpha(0f);
        }

        if (images.get(0).getImageUrl().contains("mp4")) {
            moreMenuButton.setVisibility(View.GONE);
        }

        imagesViewpager.registerOnPageChangeCallback(new SeslViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (images.get(position).getImageUrl().contains("mp4")) {
                    moreMenuButton.setVisibility(View.GONE);
                    toggleImageAttention(true);
                } else {
                    moreMenuButton.setVisibility(View.VISIBLE);
                    toggleImageAttention(false);
                }

                super.onPageSelected(position);
            }
        });

        moreMenuButton.setOnClickListener(v -> {
            SetAvatar.Set(auth.getCurrentUserUid(), images.get(imagesViewpager.getCurrentItem()).getImageUrl(), new SetAvatar.OnAvatarListener() {
                @Override
                public void OnSuccess() {
                    de.dlyt.yanndroid.oneui.view.Toast.makeText(MediaViewerActivity.this, "Ustawiono Avatar!", de.dlyt.yanndroid.oneui.view.Toast.LENGTH_LONG).show();
                }

                @Override
                public void OnError(String reason) {
                    de.dlyt.yanndroid.oneui.view.Toast.makeText(MediaViewerActivity.this, "Błąd: " + reason, de.dlyt.yanndroid.oneui.view.Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private boolean isSingleMedia() {
        return adapter.getItemCount() == 1;
    }

    public void toggleImageAttention(boolean on) {
        if (!isSingleMedia()) {
            if (on) {
                imagesViewpager.setUserInputEnabled(false);
                topLinearAnim.alpha(0f).setInterpolator(new DecelerateInterpolator()).setDuration(300).start();
            } else {
                imagesViewpager.setUserInputEnabled(true);
                topLinearAnim.alpha(1f).setInterpolator(new DecelerateInterpolator()).setDuration(300).start();
            }
        }
    }
}



