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
import com.fusoft.walkboner.settings.MainUiActivity;
import com.fusoft.walkboner.settings.PinSetupActivity;
import com.fusoft.walkboner.settings.PostsUiActivity;
import com.fusoft.walkboner.settings.PublishLinksActivity;
import com.fusoft.walkboner.settings.Settings;
import com.fusoft.walkboner.views.dialogs.InfoDialog;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {
    private MaterialButton pinLockButton;
    private MaterialButton passwordChangeButton, moderationButton;
    private MaterialButton biometricLockButton;
    private MaterialButton openSourceButton;
    private MaterialButton publishLinksButton;
    private MaterialButton changeLogButton;
    private MaterialButton mainUiButton, postUiButton;

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

        pinLockButton = (MaterialButton) findViewById(R.id.pin_lock_button);
        passwordChangeButton = (MaterialButton) findViewById(R.id.password_change_button);
        changeLogButton = findViewById(R.id.change_log_button);
        postUiButton = findViewById(R.id.post_ui_button);
        mainUiButton = findViewById(R.id.main_ui_button);

        publishLinksButton = findViewById(R.id.publish_links_button);

        moderationButton = findViewById(R.id.moderation_button);
        biometricLockButton = (MaterialButton) findViewById(R.id.biometric_lock_button);
        openSourceButton = (MaterialButton) findViewById(R.id.open_source_button);
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

        mainUiButton.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, MainUiActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        postUiButton.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, PostsUiActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        changeLogButton.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, ChangeLogActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        pinLockButton.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, PinSetupActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        passwordChangeButton.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, PasswordResetActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
