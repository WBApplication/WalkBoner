package com.fusoft.walkboner.models;

import java.util.List;

public class Post {
    private String userUid;
    private String postUid;
    private String postDocumentUid;
    private String postDescription;
    private String postImage;
    private String createdAt;
    private String showsCelebrity;
    private boolean allowComments;
    private boolean userLikedPost;
    private List<String> postLikes;
    private boolean isHeader;

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getPostUid() {
        return postUid;
    }

    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }

    public String getPostDocumentUid() {
        return postDocumentUid;
    }

    public void setPostDocumentUid(String postDocumentUid) {
        this.postDocumentUid = postDocumentUid;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getShowsCelebrity() {
        return showsCelebrity;
    }

    public void setShowsCelebrity(String showsCelebrity) {
        this.showsCelebrity = showsCelebrity;
    }

    public boolean isAllowComments() {
        return allowComments;
    }

    public void setAllowComments(boolean allowComments) {
        this.allowComments = allowComments;
    }

    public boolean isUserLikedPost() {
        return userLikedPost;
    }

    public void setUserLikedPost(boolean userLikedPost) {
        this.userLikedPost = userLikedPost;
    }

    public List<String> getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(List<String> postLikes) {
        this.postLikes = postLikes;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }
}
