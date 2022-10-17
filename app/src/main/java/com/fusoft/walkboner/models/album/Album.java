package com.fusoft.walkboner.models.album;

import java.util.List;

public class Album {
    String albumUid;
    String albumName;
    String albumDescription;
    String albumMainImage;
    String createdBy;
    int amountOfImages;
    boolean isSingleImage;
    List<String> likes;
    List<AlbumImage> albumContent;

    public String getAlbumUid() {
        return albumUid;
    }

    public void setAlbumUid(String albumUid) {
        this.albumUid = albumUid;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumDescription() {
        return albumDescription;
    }

    public void setAlbumDescription(String albumDescription) {
        this.albumDescription = albumDescription;
    }

    public String getAlbumMainImage() {
        return albumMainImage;
    }

    public void setAlbumMainImage(String albumMainImage) {
        this.albumMainImage = albumMainImage;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getAmountOfImages() {
        return amountOfImages;
    }

    public void setAmountOfImages(int amountOfImages) {
        this.amountOfImages = amountOfImages;
    }

    public boolean isSingleImage() {
        return amountOfImages == 1;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

        public List<AlbumImage> getAlbumContent() {
        return albumContent;
    }

    public void setAlbumContent(List<AlbumImage> albumContent) {
        this.albumContent = albumContent;
    }
}
