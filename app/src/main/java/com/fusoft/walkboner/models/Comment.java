package com.fusoft.walkboner.models;

import java.util.List;

public class Comment {
    private String commentUid;
    private String userUid;
    private String commentContent;
    private User userCommentDetails;
    private List<String> likes;
    private boolean isUserLikedComment;

    public String getCommentUid() {
        return commentUid;
    }

    public void setCommentUid(String commentUid) {
        this.commentUid = commentUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public User getUserCommentDetails() {
        return userCommentDetails;
    }

    public void setUserCommentDetails(User userCommentDetails) {
        this.userCommentDetails = userCommentDetails;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public boolean isUserLikedComment() {
        return isUserLikedComment;
    }

    public void setUserLikedComment(boolean userLikedComment) {
        isUserLikedComment = userLikedComment;
    }
}
