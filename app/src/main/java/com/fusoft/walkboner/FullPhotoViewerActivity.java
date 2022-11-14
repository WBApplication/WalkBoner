package com.fusoft.walkboner;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FullPhotoViewerActivity extends AppCompatActivity {
    private TouchImageView image;
    private LinearLayout errorLinear, loadingLinear;
    private MaterialTextView errorReasonText;

    private String errorReason = "";
    private String imageUrl = "";
    private String type = "";

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
        loadingLinear = findViewById(R.id.loading_linear);
    }

    private void setup() {
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
            Picasso.get().load(imageUrl).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    loadingLinear.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    loadingLinear.setVisibility(View.GONE);
                    errorReasonBuilder(e.getMessage());
                    errorLinear.setVisibility(View.VISIBLE);
                    errorReasonText.setText(errorReason);
                }
            });
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
}
