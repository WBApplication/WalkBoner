package com.fusoft.walkboner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.moderation.ModerationActivity;
import com.fusoft.walkboner.security.BiometricUnlock;
import com.fusoft.walkboner.settings.BiometricUnlockActivity;
import com.fusoft.walkboner.settings.PinSetupActivity;
import com.fusoft.walkboner.settings.PublishLinksActivity;
import com.fusoft.walkboner.settings.Settings;
import com.fusoft.walkboner.views.dialogs.InfoDialog;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.widget.RoundLinearLayout;
import de.dlyt.yanndroid.oneui.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private ToolbarLayout settingsToolbar;
    private RoundLinearLayout pinLockButton;
    private RoundLinearLayout passwordChangeButton, moderationButton;
    private Switch discreteSwitch, snapPostsSwitch;
    private RoundLinearLayout biometricLockButton;
    private RoundLinearLayout openSourceButton;
    private RoundLinearLayout publishLinksButton;

    private Authentication authentication;
    private Settings settings;

    @Override
    protected void onDestroy() {
        authentication = null;
        settings = null;

        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();
        setup();
    }

    private void initView() {
        settings = new Settings(SettingsActivity.this);

        settingsToolbar = (ToolbarLayout) findViewById(R.id.settings_toolbar);
        pinLockButton = (RoundLinearLayout) findViewById(R.id.pin_lock_button);
        passwordChangeButton = (RoundLinearLayout) findViewById(R.id.password_change_button);
        discreteSwitch = (Switch) findViewById(R.id.discrete_switch);
        discreteSwitch.setChecked(settings.isPrivateMode());

        publishLinksButton = findViewById(R.id.publish_links_button);

        snapPostsSwitch = findViewById(R.id.snap_posts_switch);
        snapPostsSwitch.setChecked(settings.shouldSnapPosts());

        moderationButton = findViewById(R.id.moderation_button);
        biometricLockButton = (RoundLinearLayout) findViewById(R.id.biometric_lock_button);
        openSourceButton = (RoundLinearLayout) findViewById(R.id.open_source_button);
    }

    private void setup() {
        authentication = new Authentication(null);

        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                if (user.isUserModerator() || user.isUserAdmin()) {
                    moderationButton.setVisibility(View.VISIBLE);

                    moderationButton.setOnClickListener(v -> {
                        startActivity(new Intent(SettingsActivity.this, ModerationActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    });
                }
            }

            @Override
            public void OnUserNotFinded() {

            }

            @Override
            public void OnError(String reason) {

            }
        });

        snapPostsSwitch.setOnClickListener(v -> {
            settings.toggleSnapPosts(snapPostsSwitch.isChecked());
        });

        discreteSwitch.setOnClickListener(v -> {
            settings.setPrivateMode(discreteSwitch.isChecked());
        });

        pinLockButton.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, PinSetupActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        passwordChangeButton.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, PasswordResetActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        settingsToolbar.setNavigationButtonOnClickListener(v -> {
            finish();
        });

        biometricLockButton.setOnClickListener(v -> {
            BiometricUnlock.checkForHardware(SettingsActivity.this, new BiometricUnlock.HardwareListener() {
                @Override
                public void OnResponse(Boolean success, String response) {
                    if (success) {
                        startActivity(new Intent(SettingsActivity.this, BiometricUnlockActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        new InfoDialog().MakeDialog(SettingsActivity.this, response, "Zamknij", null, new InfoDialog.InfoDialogInterfaceListener() {
                            @Override
                            public void OnPositiveButtonClicked() {

                            }

                            @Override
                            public void OnNegativeButtonClicked() {

                            }
                        });
                    }
                }
            });
        });

        openSourceButton.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, OpenSourceActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        publishLinksButton.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, PublishLinksActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
