package com.fusoft.walkboner.models;

public class Message {
    public static String TYPE_TEXT = "TEXT";
    public static String TYPE_IMAGE = "IMAGE";

    private String senderUid;
    private String senderAvatar;
    private String messageUid;
    private String message;
    private String image;
    private String sendedAt;
    private String type;

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSendedAt() {
        return sendedAt;
    }

    public void setSendedAt(String sendedAt) {
        this.sendedAt = sendedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
