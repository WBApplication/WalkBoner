package com.fusoft.walkboner;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

public class FullPhotoViewerActivity extends AppCompatActivity {
    private ImageView image;
    private LinearLayout errorLinear;
    private MaterialTextView errorReasonText;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    private String errorReason;

    private String imageUrl;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_photo_viewer);

        initView();
        setup();
    }

    private void initView() {
        image = findViewById(R.id.image);
        errorLinear = findViewById(R.id.error_linear);
        errorReasonText = findViewById(R.id.error_reason_text);
    }

    private void setup() {
        mScaleGestureDetector = new ScaleGestureDetector(FullPhotoViewerActivity.this, new ScaleListener());

        if (!getIntent().hasExtra("imageUrl")) {
            errorReasonBuilder("StringExtra imageUrl nie istnieje lub jest pusty!");
            if (!getIntent().hasExtra("type")) {
                errorReasonBuilder("StringExtra type nie istnieje lub jest pusty!");
            } else {
                type = getIntent().getExtras().getString("type");
            }
        } else {
            imageUrl = getIntent().getExtras().getString("imageUrl");
        }

        if (errorReason != null && !errorReason.isEmpty()) {
            image.setVisibility(View.GONE);
            errorLinear.setVisibility(View.VISIBLE);
            errorReasonText.setText(errorReason);
        } else {
            Glide.with(FullPhotoViewerActivity.this).load(imageUrl).into(image);
        }

    }

    private void errorReasonBuilder(String message) {
        if (errorReason.isEmpty()) {
            errorReason = message;
        } else {
            errorReason = errorReason + "\n\n" + message;
        }
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            image.setScaleX(mScaleFactor);
            image.setScaleY(mScaleFactor);
            return true;
        }
    }
}
