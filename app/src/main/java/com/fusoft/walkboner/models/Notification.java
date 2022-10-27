package com.fusoft.walkboner.models;

import java.util.HashMap;
import java.util.Objects;

public class Notification {

    private String notificationUid;
    private String notificationTitle;
    private String notificationDescription;
    private String notificationAttribute;
    private boolean isChecked;
    private long createdAt;

    public String getNotificationUid() {
        return notificationUid;
    }

    public void setNotificationUid(String notificationUid) {
        this.notificationUid = notificationUid;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }

    public String getNotificationAttribute() {
        return notificationAttribute;
    }

    public void setNotificationAttribute(String attributes) {
        this.notificationAttribute = attributes;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public HashMap<String, Object> toMap(boolean setChecked) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("notificationUid", getNotificationUid());
        map.put("notificationTitle", getNotificationTitle());
        map.put("notificationDescription", getNotificationDescription());
        map.put("isChecked", setChecked);
        map.put("notificationAttribute", getNotificationAttribute());
        map.put("createdAt", getCreatedAt());

        return map;
    }
}
