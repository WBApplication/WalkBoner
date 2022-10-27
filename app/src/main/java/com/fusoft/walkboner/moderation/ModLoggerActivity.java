package com.fusoft.walkboner.moderation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ModLoggerActivity extends AppCompatActivity {
    private MaterialTextView logText;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_logger);

        initView();
        setup();
    }

    @Override
    protected void onDestroy() {
        firestore = null;
        super.onDestroy();
    }

    private void initView() {
        logText = (MaterialTextView) findViewById(R.id.log_text);

        firestore = FirebaseFirestore.getInstance();
    }

    private void setup() {
        firestore.collection("moderationLogs").orderBy("addedAt", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            logText.setText("");
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                logText.setText(logText.getText() + document.getString("content"));
            }
        }).addOnFailureListener(e -> logText.setText(e.getMessage()));
    }
}