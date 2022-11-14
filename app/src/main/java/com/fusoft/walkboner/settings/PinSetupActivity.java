package com.fusoft.walkboner.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.views.LoadingView;
import com.fusoft.walkboner.views.dialogs.ErrorDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class PinSetupActivity extends AppCompatActivity {
    private MaterialTextView pinStateText;
    private MaterialSwitch pinToggleSwitch;
    private LinearLayout pinEnabledLinear;
    private MaterialButton savePinButton;
    private MaterialButton settingsSecurityQuestionsButton;
    private OtpTextView otpView;

    private LoadingView loading;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private MaterialTextView biometricEnabledDisclaimerText;

    private Settings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_setup);

        initView();
        setup();
    }

    private void initView() {
        pinStateText = (MaterialTextView) findViewById(R.id.pin_state_text);
        pinToggleSwitch = findViewById(R.id.pin_toggle_switch);
        pinEnabledLinear = (LinearLayout) findViewById(R.id.pin_enabled_linear);
        savePinButton = (MaterialButton) findViewById(R.id.save_pin_button);
        biometricEnabledDisclaimerText = (MaterialTextView) findViewById(R.id.biometric_enabled_disclaimer_text);
        biometricEnabledDisclaimerText.setVisibility(View.GONE);
        savePinButton.setEnabled(false);
        settingsSecurityQuestionsButton = (MaterialButton) findViewById(R.id.settings_security_questions_button);
        loading = findViewById(R.id.loadingView);
        otpView = findViewById(R.id.otp_view);

        settings = new Settings(PinSetupActivity.this);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (settings.isBiometricUnlockEnabled()) {
            biometricEnabledDisclaimerText.setVisibility(View.VISIBLE);
        }
    }

    private void error(String reason) {
        new ErrorDialog().ErrorDialog(PinSetupActivity.this, reason, new ErrorDialog.DialogInterfaceListener() {
            @Override
            public void OnDismissed() {
                finish();
            }
        });
    }

    private void setup() {
        loading.show();

        firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    documentSnapshot.getReference().collection("pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                pinToggleSwitch.setChecked(Boolean.parseBoolean(doc.getString("ENABLED")));
                                changeStateText(pinToggleSwitch.isChecked());
                                loading.hide();
                                return;
                            }

                            loading.hide();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            error(e.getMessage() + "\n Linia: 100\nSpróbuj ponownie później!");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                error(e.getMessage() + "\n Linia: 108\nSpróbuj ponownie później!");
            }
        });

        pinToggleSwitch.setOnClickListener(v -> {
            changeStateText(pinToggleSwitch.isChecked());
        });

        otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                if (otpView.getOTP().length() < 6) {
                    savePinButton.setEnabled(false);
                }
            }

            @Override
            public void onOTPComplete(String otp) {
                savePinButton.setEnabled(true);
            }
        });

        savePinButton.setOnClickListener(v -> {
            SetPinInDataBase();
        });
    }

    private void SetPinInDataBase() {
        loading.show();

        firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("PIN", otpView.getOTP());
                    map.put("ENABLED", "true");
                    otpView.setOTP("");

                    documentSnapshot.getReference().collection("pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                // If exist
                                documentSnapshot.getReference().collection("pin").document(doc.getId()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        loading.hide();
                                        finish();
                                    }
                                });
                                return;
                            }
                            // If not
                            documentSnapshot.getReference().collection("pin").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    loading.hide();
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            error(e.getMessage() + "\n\nLinia: 175\n\nSpróbuj ponownie później!");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                error(e.getMessage() + "\n\nLinia: 183\n\nSpróbuj ponownie później!");
            }
        });
    }

    private void DisablePinInDataBase() {
        loading.show();

        firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("PIN", "");
                    map.put("ENABLED", "false");

                    documentSnapshot.getReference().collection("pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            doc.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loading.hide();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.hide();
                                    error(e.getMessage() + "\n\nLinia: 212\n\nSpróbuj ponownie później!");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading.hide();
                            error(e.getMessage() + "\n\nLinia: 220\n\nSpróbuj ponownie później!");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.hide();
                error(e.getMessage() + "\n\nLinia: 229\n\nSpróbuj ponownie później!");
            }
        });
    }

    private void changeStateText(boolean on) {
        if (on) {
            pinStateText.setText("Włączone");
            pinEnabledLinear.setVisibility(View.VISIBLE);
        } else {
            DisablePinInDataBase();
            pinStateText.setText("Wyłączone");
            pinEnabledLinear.setVisibility(View.GONE);
        }
    }
}