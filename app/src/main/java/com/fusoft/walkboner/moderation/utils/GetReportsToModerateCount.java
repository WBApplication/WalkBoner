package com.fusoft.walkboner.moderation.utils;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class GetReportsToModerateCount {
    private int i = 0;

    public void GetAmount(CounterListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("reports").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.getDocuments() != null) {
                    i = 0;
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        i++;
                    }
                    listener.OnResponse(i);
                } else {
                    listener.OnError(error.getMessage());
                }
            }
        });
    }
}
