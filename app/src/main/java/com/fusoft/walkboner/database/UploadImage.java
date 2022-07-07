package com.fusoft.walkboner.database;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.utils.GetPathFromUri;
import com.fusoft.walkboner.utils.UidGenerator;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UploadImage {
    public UploadImage(Context context, String storageDirectory, Uri imagePath, ImageUploadListener listener) {
        String uid = UidGenerator.Generate();

        InputStream stream = null;
        Log.e("Upload Image", "FilePath " + imagePath);
        try {
            stream = new FileInputStream(GetPathFromUri.getPath(context, imagePath));
        } catch (FileNotFoundException e) {
            listener.OnError("Zdjęcie które wybrałeś, nie istnieje lub zostało usunięte.");
        }

        FirebaseStorage storage = FirebaseStorage.getInstance(context.getString(R.string.storage_url));
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(storageDirectory + uid + ".jpg");

        UploadTask uploadTask = mountainsRef.putStream(stream);
        uploadTask.addOnFailureListener(exception ->
                listener.OnError(exception.getMessage()))
                .addOnSuccessListener(taskSnapshot ->
                        mountainsRef.getDownloadUrl()
                                .addOnSuccessListener(uri ->
                                        listener.OnImageUploaded(uri.toString()))).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        Double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        listener.Progress(progress.intValue());
                    }
                });
    }
}
