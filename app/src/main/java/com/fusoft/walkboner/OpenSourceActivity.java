package com.fusoft.walkboner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;

public class OpenSourceActivity extends AppCompatActivity {
    private MaterialTextView appVersionText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source);
        initView();
    }

    private void initView() {
        appVersionText = (MaterialTextView) findViewById(R.id.app_version_text);

        appVersionText.setText(BuildConfig.VERSION_NAME);
    }
}
