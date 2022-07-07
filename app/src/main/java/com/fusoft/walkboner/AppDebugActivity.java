package com.fusoft.walkboner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.dlyt.yanndroid.oneui.widget.OptionButton;

public class AppDebugActivity extends AppCompatActivity {
    private OptionButton crashButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_app);
        initView();
        setup();
    }

    private void initView() {
        crashButton = (OptionButton) findViewById(R.id.crash_button);
    }

    private void setup() {
        crashButton.setOnClickListener(v -> {
            int a = 0;
            int b = 2;
            int c = Integer.parseInt("dick");
            int d = b / a * c + Integer.parseInt("BBC") % Integer.parseInt("PORT");
        });
    }
}
