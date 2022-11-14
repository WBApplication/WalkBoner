package com.fusoft.walkboner.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.fusoft.walkboner.BuildConfig;
import com.fusoft.walkboner.security.Encrypt;

public class Settings {

    private SharedPreferences settings;

    public enum POST_VIEW_TYPE {
        SMALL_POST(101),
        BIG_POST(102);

        private final int value;
        POST_VIEW_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

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

    public boolean isPrivateMode() {
        return settings.getBoolean("privateMode", false);
    }

    public void setPrivateMode(boolean on) {
        settings.edit().putBoolean("privateMode", on).apply();
    }

    public void setPostViewType(POST_VIEW_TYPE viewType) {
        settings.edit().putInt("postViewType", viewType.getValue()).apply();
    }

    public POST_VIEW_TYPE getPostViewType() {
        if (settings.getInt("postViewType", 101) == POST_VIEW_TYPE.SMALL_POST.value) {
            return POST_VIEW_TYPE.SMALL_POST;
        } else {
            return POST_VIEW_TYPE.BIG_POST;
        }
    }

    public boolean isNavigationRailEnabled() {
        return settings.getBoolean("navigationRailEnabled", false);
    }

    public void setNavigationRailEnabled(boolean on) {
        settings.edit().putBoolean("navigationRailEnabled", on).apply();
    }

    public Settings resetSettings() {
        toggleBiometricUnlock(false);
        enableCreatingPostDisclaimer();
        toggleSnapPosts(true);
        togglePublishLink(false);
        setPrivateMode(false);
        setPostViewType(POST_VIEW_TYPE.SMALL_POST);
        return Settings.this;
    }
}
