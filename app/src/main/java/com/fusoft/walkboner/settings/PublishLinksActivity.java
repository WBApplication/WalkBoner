package com.fusoft.walkboner.settings;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import de.dlyt.yanndroid.oneui.widget.Switch;

public class PublishLinksActivity extends AppCompatActivity {
    private MaterialTextView publishLinksStateText;
    private MaterialButton createLinksCopyButton;
    private Switch publishLinksSwitch;

    private Settings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_links_settings);
        initView();
    }

    private void initView() {
        publishLinksStateText = findViewById(R.id.publish_links_state_text);
        publishLinksSwitch = findViewById(R.id.publish_links_switch);
        createLinksCopyButton = findViewById(R.id.create_links_copy_button);

        settings = new Settings(PublishLinksActivity.this);

        if (settings.shouldPublishLink()) createLinksCopyButton.setVisibility(View.GONE);
        if (!settings.shouldPublishLink()) createLinksCopyButton.setVisibility(View.VISIBLE);

        publishLinksSwitch.setOnClickListener(v -> {
            settings.togglePublishLink(publishLinksSwitch.isChecked());

            if (publishLinksSwitch.isChecked()) createLinksCopyButton.setVisibility(View.GONE);
            if (!publishLinksSwitch.isChecked()) createLinksCopyButton.setVisibility(View.VISIBLE);
        });
    }
}