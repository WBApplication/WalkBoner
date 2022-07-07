package com.fusoft.walkboner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.Profile;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.widget.RoundLinearLayout;
import de.dlyt.yanndroid.oneui.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private ToolbarLayout settingsToolbar;
    private RoundLinearLayout pinLockButton;
    private RoundLinearLayout passwordChangeButton, moderationButton;
    private Switch discreteSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();
        setup();
    }

    private void initView() {
        settingsToolbar = (ToolbarLayout) findViewById(R.id.settings_toolbar);
        pinLockButton = (RoundLinearLayout) findViewById(R.id.pin_lock_button);
        passwordChangeButton = (RoundLinearLayout) findViewById(R.id.password_change_button);
        discreteSwitch = (Switch) findViewById(R.id.discrete_switch);
        moderationButton = findViewById(R.id.moderation_button);
    }

    private void setup() {
        Authentication authentication = new Authentication(new AuthenticationListener() {
            @Override
            public void UserAlreadyLoggedIn(boolean pinRequired) {

            }

            @Override
            public void UserRequiredToBeLogged() {

            }

            @Override
            public void OnLogin(boolean isSuccess, boolean pinRequired, @Nullable String reason) {

            }

            @Override
            public void OnRegister(boolean isSuccess, @Nullable String reason) {

            }
        });

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

        pinLockButton.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, PinSetupActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        settingsToolbar.setNavigationButtonOnClickListener(v -> {
            finish();
        });
    }
}
