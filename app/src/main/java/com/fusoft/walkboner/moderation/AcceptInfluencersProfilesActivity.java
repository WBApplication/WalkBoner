package com.fusoft.walkboner.moderation;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.adapters.recyclerview.NewInfluencersAdapter;
import com.fusoft.walkboner.database.funcions.GetNewInfluencers;
import com.fusoft.walkboner.database.funcions.InfluencersListener;
import com.fusoft.walkboner.models.Influencer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.view.Toast;

public class AcceptInfluencersProfilesActivity extends AppCompatActivity {
    private RecyclerView newProfilesRecyclerView;
    private NewInfluencersAdapter adapter;

    private ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_influencers_profiles);
        initView();
        setup();
    }

    private void initView() {
        newProfilesRecyclerView = (RecyclerView) findViewById(R.id.new_profiles_recycler_view);

        loading = new ProgressDialog(AcceptInfluencersProfilesActivity.this);
        loading.setIndeterminate(true);
        loading.setProgressStyle(ProgressDialog.STYLE_CIRCLE);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
    }

    private void setup() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(AcceptInfluencersProfilesActivity.this);
        newProfilesRecyclerView.setLayoutManager(layoutManager);

        new GetNewInfluencers("", new InfluencersListener() {
            @Override
            public void OnDataReceived(List<Influencer> influencers) {
                adapter = new NewInfluencersAdapter(AcceptInfluencersProfilesActivity.this, influencers);

                adapter.setClickListener(new NewInfluencersAdapter.ItemClickListener() {
                    @Override
                    public void onAcceptClick(View view, int position) {
                        loading.show();
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("influencers").add(adapter.getInfluencer(position).toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AcceptInfluencersProfilesActivity.this, "Pomyślnie Dodano!", Toast.LENGTH_LONG).show();
                                deleteItem(position);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading.dismiss();
                                Toast.makeText(AcceptInfluencersProfilesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onDeclineClick(View view, int position) {
                        loading.show();
                        deleteItem(position);
                    }
                });

                newProfilesRecyclerView.setAdapter(adapter);
            }

            @Override
            public void OnError(String reason) {

            }
        });
    }

    private void deleteItem(int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("moderation").document("influencers").collection("toModerate").whereEqualTo("influencerUid", adapter.getInfluencer(position).getInfluencerUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.getDocuments().get(0).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loading.dismiss();
                        adapter.removeFromList(position);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(AcceptInfluencersProfilesActivity.this, "Nie udało się usunąć!\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.dismiss();
                Toast.makeText(AcceptInfluencersProfilesActivity.this, "Nie znaleziono!\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
