package com.fusoft.walkboner.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textview.MaterialTextView;

public class BiometricUnlockActivity extends AppCompatActivity {
    private MaterialTextView biometricStateText;
    private MaterialSwitch biometricToggleSwitch;

    private Settings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_unlock);
        initView();
        setup();
    }

    private void initView() {
        biometricStateText = findViewById(R.id.biometric_state_text);
        biometricToggleSwitch = findViewById(R.id.biometric_toggle_switch);

        settings = new Settings(BiometricUnlockActivity.this);
    }

    private void setup() {
        updateUI(settings.isBiometricUnlockEnabled(), true);

        biometricToggleSwitch.setOnClickListener(v -> {
            settings.toggleBiometricUnlock(biometricToggleSwitch.isChecked());
            updateUI(biometricToggleSwitch.isChecked(), false);
        });
    }

    private void updateUI(boolean state, boolean checkboxes) {
        if (state) {
            biometricStateText.setText("Włączone");
            if (checkboxes) biometricToggleSwitch.setChecked(true);
        } else {
            biometricStateText.setText("Wyłączone");
            if (checkboxes) biometricToggleSwitch.setChecked(false);
        }
    }
}
