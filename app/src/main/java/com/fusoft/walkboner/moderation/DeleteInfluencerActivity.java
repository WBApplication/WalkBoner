package com.fusoft.walkboner.moderation;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.adapters.recyclerview.InfluencersAdapter;
import com.fusoft.walkboner.database.funcions.GetInfluencers;
import com.fusoft.walkboner.database.funcions.InfluencersListener;
import com.fusoft.walkboner.database.funcions.SendNotification;
import com.fusoft.walkboner.models.Influencer;
import com.fusoft.walkboner.models.Notification;
import com.fusoft.walkboner.utils.CurrentTime;
import com.fusoft.walkboner.utils.UidGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.GridLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.view.Toast;

public class DeleteInfluencerActivity extends AppCompatActivity {
    private ToolbarLayout toolbarDeleteMod;
    private RecyclerView influencersRecyclerview;
    private LinearLayout confirmLinear;
    private MaterialTextView personNickText;
    private MaterialTextView personAddedByNicknameText;
    private MaterialTextView personAddedByUidText;
    private EditText deleteReasonEdittext;
    private MaterialButton deleteButton;
    private RadioGroup reasonsGroup;

    private InfluencersAdapter adapter;
    private ProgressDialog loading;

    private String deleteReason;
    private boolean isMainPage = true;

    private Influencer selectedInfluencer;
    private SendNotification notification;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_delete_influencer);

        initView();
        setup();
    }

    @Override
    protected void onDestroy() {
        selectedInfluencer = null;
        notification = null;
        firestore = null;
        adapter = null;
        loading = null;

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isMainPage) {
            super.onBackPressed();
        } else {
            influencersRecyclerview.setVisibility(View.VISIBLE);
            confirmLinear.setVisibility(View.GONE);
            isMainPage = true;
        }
    }

    private void initView() {
        toolbarDeleteMod = (ToolbarLayout) findViewById(R.id.toolbar_delete_mod);
        influencersRecyclerview = (RecyclerView) findViewById(R.id.influencers_recyclerview);
        confirmLinear = (LinearLayout) findViewById(R.id.confirm_linear);
        personNickText = (MaterialTextView) findViewById(R.id.person_nick_text);
        personAddedByUidText = (MaterialTextView) findViewById(R.id.person_added_by_uid_text);
        deleteReasonEdittext = (EditText) findViewById(R.id.delete_reason_edittext);
        deleteButton = (MaterialButton) findViewById(R.id.delete_button);
        reasonsGroup = (RadioGroup) findViewById(R.id.reasons_group);

        notification = new SendNotification();

        loading = new ProgressDialog(DeleteInfluencerActivity.this);
        loading.setTitle("Poczekaj...");
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
    }

    private void setup() {
        GridLayoutManager layoutManager = new GridLayoutManager(DeleteInfluencerActivity.this, 3);
        influencersRecyclerview.setLayoutManager(layoutManager);

        reasonsGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            deleteReasonEdittext.setEnabled(false);

            switch (reasonsGroup.getCheckedRadioButtonId()) {
                case R.id.troll_radio:
                    deleteReason = "Troll";
                    break;
                case R.id.incorrect_person_radio:
                    deleteReason = "Niepoprawna Postać";
                    break;
                case R.id.not_interesting_radio:
                    deleteReason = "Mało interesująca Postać";
                    break;
                case R.id.false_person_radio:
                    deleteReason = "Fikcyjna Postać";
                    break;
                case R.id.different_radio:
                    deleteReason = "";
                    deleteReasonEdittext.setEnabled(true);
                    break;
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (deleteReason.isEmpty()) { // If reason is empty, then mod gives own reason.
                deleteReason = deleteReasonEdittext.getText().toString();
            }

            if (deleteReason.isEmpty()) { // If mod reason is still empty, then notify mod about this.
                deleteReasonEdittext.setError("Wpisz Powód!");
                return;
            }

            loading.show();
            firestore = FirebaseFirestore.getInstance();
            firestore.collection("influencers").whereEqualTo("influencerUid", selectedInfluencer.getInfluencerUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("isHidden", true);
                    map.put("hiddenReason", deleteReason);
                    document.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            String[] attrs = {selectedInfluencer.getInfluencerNickName(), selectedInfluencer.getInfluencerUid(), deleteReason};
                            ModLogger.Log(firestore, ModLogger.ActionType.DELETED_INFLUENCER, attrs);

                            Notification notificationDetails = new Notification();
                            notificationDetails.setNotificationUid(UidGenerator.Generate(12));
                            notificationDetails.setNotificationTitle("Usunięty Influencer");
                            notificationDetails.setNotificationDescription("Inluencer " + selectedInfluencer.getInfluencerNickName() + " został(a) usunięty/a!\nPowód: " + deleteReason);
                            notificationDetails.setChecked(false);
                            notificationDetails.setNotificationAttribute("");
                            notificationDetails.setCreatedAt(CurrentTime.Get());

                            notification.send(selectedInfluencer.getInfluencerAddedBy(), notificationDetails, new SendNotification.NotificationListener() {
                                @Override
                                public void OnSuccess() {
                                    loading.dismiss();
                                    finish();
                                }

                                @Override
                                public void OnError(String error) {
                                    loading.dismiss();
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading.dismiss();
                            Toast.makeText(DeleteInfluencerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading.dismiss();
                    Toast.makeText(DeleteInfluencerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        loadData();
    }

    private void loadData() {
        influencersRecyclerview.setAdapter(null);

        GetInfluencers getter = new GetInfluencers(GetInfluencers.ORDER_MOST_VIEWED, new InfluencersListener() {
            @Override
            public void OnDataReceived(List<Influencer> influencers) {
                adapter = new InfluencersAdapter(DeleteInfluencerActivity.this, influencers);
                influencersRecyclerview.setAdapter(adapter);
                influencersRecyclerview.setVisibility(View.VISIBLE);

                adapter.setClickListener((influencer, position) -> {
                    selectedInfluencer = influencer;

                    influencersRecyclerview.setVisibility(View.GONE);
                    confirmLinear.setVisibility(View.VISIBLE);
                    isMainPage = false;

                    personNickText.setText(influencer.getInfluencerNickName());
                    personAddedByUidText.setText("Dodany Przez (UID): " + influencer.getInfluencerAddedBy());
                });
            }

            @Override
            public void OnError(String reason) {

            }
        });
    }
}
