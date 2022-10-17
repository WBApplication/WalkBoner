package com.fusoft.walkboner.database.funcions;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.models.album.Album;
import com.fusoft.walkboner.models.album.AlbumImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetAlbum {
    public void ForInfluencer(String influencerUid, AlbumListener listener) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("influencers").whereEqualTo("influencerUid", influencerUid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshotsFirst) {
                queryDocumentSnapshotsFirst.getDocuments().get(0).getReference().collection("albums").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Album> albumsList = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Album album = new Album();
                            album.setAlbumUid(document.getString("albumUid"));
                            album.setAlbumName(document.getString("albumName"));
                            album.setAlbumDescription(document.getString("albumDescription"));
                            album.setAlbumMainImage(document.getString("albumMainImage"));
                            album.setCreatedBy(document.getString("createdBy"));
                            album.setAmountOfImages(Integer.parseInt(document.get("mediaAmount").toString()));

                            List<AlbumImage> albumImages = new ArrayList<>();
                            document.getReference().collection("images").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot images : queryDocumentSnapshots.getDocuments()) {
                                        AlbumImage image = new AlbumImage();

                                        image.setImageUid(images.getString("imageUid"));
                                        image.setImageUrl(images.getString("imageUrl"));

                                        albumImages.add(image);
                                    }

                                    album.setAlbumContent(albumImages);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    listener.OnError(e.getMessage());
                                }
                            });

                            albumsList.add(album);
                        }

                        listener.OnReceived(albumsList);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.OnError(e.getMessage());
            }
        });
    }

    public interface AlbumListener {
        void OnReceived(List<Album> albums);

        void OnError(String reason);
    }
}
