package com.fusoft.walkboner.models;

public class User {
    private String userUid;
    private String userEmail;
    private String userName;
    private String userAvatar;
    private String userDescription;
    private String userBanReason;
    private String userBannedTo;
    private String userDocumentId;
    private boolean isUserVerified;
    private boolean isUserModerator;
    private boolean isUserAdmin;
    private boolean isUserBanned;
    private boolean showFirstTimeTip;
    private long createdAt;

    public String getUserDocumentId() {
        return userDocumentId;
    }

    public void setUserDocumentId(String userDocumentId) {
        this.userDocumentId = userDocumentId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getUserBanReason() {
        return userBanReason;
    }

    public void setUserBanReason(String userBanReason) {
        this.userBanReason = userBanReason;
    }

    public String getUserBannedTo() {
        return userBannedTo;
    }

    public void setUserBannedTo(String userBannedTo) {
        this.userBannedTo = userBannedTo;
    }

    public boolean isUserVerified() {
        return isUserVerified;
    }

    public void setUserVerified(boolean userVerified) {
        isUserVerified = userVerified;
    }

    public boolean isUserModerator() {
        return isUserModerator;
    }

    public void setUserModerator(boolean userModerator) {
        isUserModerator = userModerator;
    }

    public boolean isUserAdmin() {
        return isUserAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        isUserAdmin = userAdmin;
    }

    public boolean isUserBanned() {
        return isUserBanned;
    }

    public void setUserBanned(boolean userBanned) {
        isUserBanned = userBanned;
    }

    public boolean isShowFirstTimeTip() {
        return showFirstTimeTip;
    }

    public void setShowFirstTimeTip(boolean showFirstTimeTip) {
        this.showFirstTimeTip = showFirstTimeTip;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
