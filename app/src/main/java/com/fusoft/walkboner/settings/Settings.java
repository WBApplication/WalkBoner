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

    public void enableCreatingPostDisclaimer() {
        settings.edit().putBoolean("showCreatingPostDisclaimer", true).apply();
    }

    public void disableCreatingPostDisclaimer() {
        settings.edit().putBoolean("showCreatingPostDisclaimer", false).apply();
    }

    public boolean skipUpdate() {
        return settings.getBoolean("skipUpdate", false);
    }

    public void skipUpdateNextTime(boolean value) {
        settings.edit().putBoolean("skipUpdate", value).apply();
    }

    public void toggleSnapPosts(boolean on) {
        settings.edit().putBoolean("snapPosts", on).apply();
    }

    public boolean shouldSnapPosts() {
        return settings.getBoolean("snapPosts", true);
    }

    public void togglePublishLink(boolean on) {
        settings.edit().putBoolean("publishPosts", on).apply();
    }

    public boolean shouldPublishLink() {
        return settings.getBoolean("publishPosts", false);
    }

    public Settings resetSettings() {
        toggleBiometricUnlock(false);
        enableCreatingPostDisclaimer();
        toggleSnapPosts(true);
        togglePublishLink(false);
        return Settings.this;
    }
}
