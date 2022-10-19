package com.fusoft.walkboner.uniload;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.views.dialogs.UniloadLinkDialog;
import com.google.android.material.textview.MaterialTextView;

public class UniloadActivity extends AppCompatActivity {
    private MaterialTextView uniloadNotConfiguredText;
    private LinearLayout instagramButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uniload);

        initView();
        setup();
    }

    private void initView() {
        uniloadNotConfiguredText = (MaterialTextView) findViewById(R.id.uniload_not_configured_text);
        instagramButton = (LinearLayout) findViewById(R.id.instagram_button);
    }

    private void setup() {
        instagramButton.setOnClickListener(v -> {
            UniloadLinkDialog.Show(UniloadActivity.this, new UniloadLinkDialog.DialogListener() {
                @Override
                public void OnAddClicked(String url) {

                }

                @Override
                public void OnDismiss() {

                }
            });
        });
    }
}
