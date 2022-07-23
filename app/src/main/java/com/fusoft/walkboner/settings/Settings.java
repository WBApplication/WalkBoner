package com.fusoft.walkboner.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.fusoft.walkboner.security.Encrypt;

public class Settings {

    private SharedPreferences settings;

    public Settings(Context context) {
        settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public String getPasswordHashed() {
        return settings.getString("password", "");
    }

    public void setPasswordHashed(String password) {
        settings.edit().putString("password", Encrypt.EncryptSHAPassword(password)).apply();
    }

    public boolean isBiometricUnlockEnabled() {
        return settings.getBoolean("isBiometricUnlock", false);
    }

    public void toggleBiometricUnlock(boolean on) {
        settings.edit().putBoolean("isBiometricUnlock", on).apply();
    }

    public boolean showCreatingPostDisclaimer() {
        return settings.getBoolean("showCreatingPostDisclaimer", true);
    }

    public void disableCreatingPostDisclaimer() {
        settings.edit().putBoolean("showCreatingPostDisclaimer", false).apply();
    }
}
