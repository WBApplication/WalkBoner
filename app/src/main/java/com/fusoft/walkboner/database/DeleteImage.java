package com.fusoft.walkboner.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fusoft.walkboner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteImage {
    public static void Delete(Context context, String imageUrl, @Nullable DeleteTask listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance(context.getString(R.string.storage_url));
        StorageReference imageToDelete = storage.getReferenceFromUrl(imageUrl);
        imageToDelete.delete().addOnSuccessListener(unused -> {
            if (listener != null) listener.OnDeleted();
        }).addOnFailureListener(e -> {
            if (listener != null) listener.OnError(e.getMessage());
        });
    }

    public interface DeleteTask {
        void OnDeleted();

        void OnError(String reason);
    }
}
