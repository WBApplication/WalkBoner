package com.fusoft.walkboner.database;

public interface ImageUploadListener {
    void OnImageUploaded(String imageUrl);

    void Progress(int value);

    void OnError(String reason);
}
