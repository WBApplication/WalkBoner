package com.fusoft.walkboner.models.album;

import java.util.List;

public class AlbumImage {
    String imageUid;
    String imageUrl;
    List<String> likes;

    boolean isLocked = false;

    int unlockAtLikes = 0;

    public String getImageUid() {
        return imageUid;
    }

    public void setImageUid(String imageUid) {
        this.imageUid = imageUid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getUnlockAtLikes() {
        return unlockAtLikes;
    }

    public void setUnlockAtLikes(int unlockAtLikes) {
        this.unlockAtLikes = unlockAtLikes;
    }
}
