package com.fusoft.walkboner;

import android.os.Bundle;
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

import de.dlyt.yanndroid.oneui.view.ViewPager2;

public class MediaViewerActivity extends AppCompatActivity {
    private ViewPropertyAnimator topLinearAnim;

    private ViewPager2 imagesViewpager;
    private WormDotsIndicator tabIndicator;
    private LinearLayout topViewLinear;
    private ImageView moreMenuButton;

    private Authentication auth;

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
        List<AlbumImage> images = new Gson().fromJson(getIntent().getStringExtra("albumImages"), new TypeToken<List<AlbumImage>>() {}.getType());

        MediaViewerViewPager adapter = new MediaViewerViewPager(getSupportFragmentManager(), getLifecycle(), images);
        imagesViewpager.setAdapter(adapter);

        tabIndicator.attachTo(imagesViewpager);

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

    public boolean isImageHaveAttention() {
        return imagesViewpager.isUserInputEnabled();
    }

    public void toggleImageAttention(boolean on) {
        if (on) {
            imagesViewpager.setUserInputEnabled(false);
            //topLinearAnim.cancel();
            topLinearAnim.alpha(0f).setInterpolator(new DecelerateInterpolator()).setDuration(300).start();
        } else {
            imagesViewpager.setUserInputEnabled(true);
            //topLinearAnim.cancel();
            topLinearAnim.alpha(1f).setInterpolator(new DecelerateInterpolator()).setDuration(300).start();
        }
    }
}



