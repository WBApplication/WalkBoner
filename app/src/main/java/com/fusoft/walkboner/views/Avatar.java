package com.fusoft.walkboner.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fusoft.walkboner.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class Avatar extends FrameLayout {

    private Context context;
    private ProgressBar avatarLoading;
    private CircularImageView avatarImage;

    public static final String ADMIN = "ADMIN";
    public static final String MODERATOR = "MODERATOR";
    public static final String USER = "USER";
    public static final String BANNED = "BANNED";

    private boolean isMeasured = false;

    public Avatar(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public Avatar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public Avatar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRootView = inflater.inflate(R.layout.view_avatar, this, false);
        this.addView(mRootView);

        avatarLoading = (ProgressBar) mRootView.findViewById(R.id.avatar_loading);
        avatarImage = (CircularImageView) mRootView.findViewById(R.id.avatar_image);
    }

    public void setAvatarOwnerPrivileges(String privilege) {
        switch (privilege) {
            case ADMIN:
                avatarImage.setBorderColorStart(0xFFDED116);
                avatarImage.setBorderColorEnd(0xFF0381FE);
                avatarImage.setBorderColorDirection(CircularImageView.GradientDirection.TOP_TO_BOTTOM);
                break;
            case MODERATOR:
                avatarImage.setBorderColor(0xFF19E619);
                break;
            case USER:
                avatarImage.setBorderColor(0xFF0381FE);
                break;
            case BANNED:
                avatarImage.setBorderColor(0xFFE6192A);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (avatarImage != null && avatarImage.getDrawable() == null) {
            setImageDrawable(R.drawable.face_man_profile);
        }
    }

    public void setImageDrawable(int image) {
        avatarImage.setImageDrawable(context.getDrawable(image));
        avatarImage.setVisibility(View.VISIBLE);
        avatarLoading.setVisibility(View.GONE);
    }

    public void setImageFromURI(Uri uri) {
        avatarImage.setImageURI(uri);
        avatarImage.setVisibility(View.VISIBLE);
        avatarLoading.setVisibility(View.GONE);
    }

    public void setImageFromUrl(String url) {
        avatarImage.setVisibility(View.GONE);
        avatarLoading.setVisibility(View.VISIBLE);

        Glide.with(context).load(url).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                avatarImage.setVisibility(View.VISIBLE);
                avatarLoading.setVisibility(View.GONE);
                return false;
            }
        }).into(avatarImage);
    }
}
