package com.fusoft.walkboner.models;

import com.fusoft.walkboner.utils.UidGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Influencer {
    private String influencerUid;
    private String influencerAddedBy;
    private String influencerFirstName;
    private String influencerLastName;
    private String influencerNickName;
    private String influencerDescription;
    private String influencerAvatar;
    private String influencerInstagramLink = "";
    private String influencerYouTubeLink = "";
    private String influencerTikTokLink = "";
    private String influencerModeratorUid;
    private boolean isVerified;
    private boolean isPremium;
    private boolean isHidden;
    private boolean influencerHasInstagram;
    private boolean influencerHasYouTube;
    private boolean influencerHasTikTok;
    private boolean isMaintained;
    private boolean isUserFollowing;
    private long influencerAddedAt;
    private long maintainedTo;
    private int viewsCount;
    private List<String> followers;

    public String getInfluencerUid() {
        return influencerUid;
    }

    public void setInfluencerUid(String influencerUid) {
        this.influencerUid = influencerUid;
    }

    public String getInfluencerAddedBy() {
        return influencerAddedBy;
    }

    public void setInfluencerAddedBy(String influencerAddedBy) {
        this.influencerAddedBy = influencerAddedBy;
    }

    public String getInfluencerFirstName() {
        return influencerFirstName;
    }

    public void setInfluencerFirstName(String influencerFirstName) {
        this.influencerFirstName = influencerFirstName;
    }

    public String getInfluencerLastName() {
        return influencerLastName;
    }

    public void setInfluencerLastName(String influencerLastName) {
        this.influencerLastName = influencerLastName;
    }

    public String getInfluencerNickName() {
        return influencerNickName;
    }

    public void setInfluencerNickName(String influencerNickName) {
        this.influencerNickName = influencerNickName;
    }

    public String getInfluencerDescription() {
        return influencerDescription;
    }

    public void setInfluencerDescription(String influencerDescription) {
        this.influencerDescription = influencerDescription;
    }

    public String getInfluencerAvatar() {
        return influencerAvatar;
    }

    public void setInfluencerAvatar(String influencerAvatar) {
        this.influencerAvatar = influencerAvatar;
    }

    public String getInfluencerInstagramLink() {
        return influencerInstagramLink;
    }

    public void setInfluencerInstagramLink(String influencerInstagramLink) {
        this.influencerInstagramLink = influencerInstagramLink;
    }

    public String getInfluencerYouTubeLink() {
        return influencerYouTubeLink;
    }

    public void setInfluencerYouTubeLink(String influencerYouTubeLink) {
        this.influencerYouTubeLink = influencerYouTubeLink;
    }

    public String getInfluencerTikTokLink() {
        return influencerTikTokLink;
    }

    public void setInfluencerTikTokLink(String influencerTikTokLink) {
        this.influencerTikTokLink = influencerTikTokLink;
    }

    public String getInfluencerModeratorUid() {
        return influencerModeratorUid;
    }

    public void setInfluencerModeratorUid(String influencerModeratorUid) {
        this.influencerModeratorUid = influencerModeratorUid;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isInfluencerHasInstagram() {
        return influencerHasInstagram;
    }

    public void setInfluencerHasInstagram(boolean influencerHasInstagram) {
        this.influencerHasInstagram = influencerHasInstagram;
    }

    public boolean isInfluencerHasYouTube() {
        return influencerHasYouTube;
    }

    public void setInfluencerHasYouTube(boolean influencerHasYouTube) {
        this.influencerHasYouTube = influencerHasYouTube;
    }

    public boolean isInfluencerHasTikTok() {
        return influencerHasTikTok;
    }

    public void setInfluencerHasTikTok(boolean influencerHasTikTok) {
        this.influencerHasTikTok = influencerHasTikTok;
    }

    public boolean isMaintained() {
        return isMaintained;
    }

    public void setMaintained(boolean maintained) {
        isMaintained = maintained;
    }

    public boolean isUserFollowing() {
        return isUserFollowing;
    }

    public void setUserFollowing(boolean userFollowing) {
        isUserFollowing = userFollowing;
    }

    public long getInfluencerAddedAt() {
        return influencerAddedAt;
    }

    public void setInfluencerAddedAt(long influencerAddedAt) {
        this.influencerAddedAt = influencerAddedAt;
    }

    public long getMaintainedTo() {
        return maintainedTo;
    }

    public void setMaintainedTo(long maintainedTo) {
        this.maintainedTo = maintainedTo;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("influencerUid", influencerUid);
        map.put("influencerAddedBy", influencerAddedBy);
        map.put("influencerFirstName", influencerFirstName);
        map.put("influencerLastName", influencerLastName);
        map.put("influencerNickName", influencerNickName);
        map.put("influencerDescription", influencerDescription);
        map.put("influencerAvatar", influencerAvatar);
        map.put("influencerInstagramLink", influencerInstagramLink);
        map.put("influencerYouTubeLink", influencerYouTubeLink);
        map.put("influencerTikTokLink", influencerTikTokLink);
        map.put("influencerModeratorUid", influencerModeratorUid);
        map.put("isVerified", isVerified);
        map.put("isPremium", isPremium);
        map.put("isHidden", isHidden);
        map.put("hasInstagram", influencerHasInstagram);
        map.put("hasYouTube", influencerHasYouTube);
        map.put("hasTikTok", influencerHasTikTok);
        map.put("isMaintained", isMaintained);
        map.put("isUserFollowing", isUserFollowing);
        map.put("influencerAddedAt", influencerAddedAt);
        map.put("maintainedTo", maintainedTo);
        map.put("viewsCount", viewsCount);
        return map;
    }
}
