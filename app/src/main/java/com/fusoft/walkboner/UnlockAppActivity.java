package com.fusoft.walkboner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import com.fusoft.walkboner.security.BiometricUnlock;
import com.fusoft.walkboner.settings.Settings;
import com.fusoft.walkboner.views.dialogs.ErrorDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import de.dlyt.yanndroid.oneui.widget.ProgressBar;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class UnlockAppActivity extends AppCompatActivity {
    private OtpTextView otpView;
    private MaterialTextView forgotPinButton;
    private ProgressBar progressBar;
    private LinearLayout enterPinLinear;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private String userPIN;
    private Settings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_app);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        firestore = FirebaseFirestore.getInstance();

        initView();
    }

    private void initView() {
        otpView = (OtpTextView) findViewById(R.id.otp_view);
        forgotPinButton = (MaterialTextView) findViewById(R.id.forgot_pin_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        enterPinLinear = (LinearLayout) findViewById(R.id.enter_pin_linear);

        settings = new Settings(UnlockAppActivity.this);

        if (settings.isBiometricUnlockEnabled()) {
            setupBiometric();
        } else {
            setupPIN();
        }
    }

    private void setupBiometric() {
        BiometricUnlock.requestUnlock(UnlockAppActivity.this, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(UnlockAppActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
    }

    private void setupPIN() {
        firestore.collection("users").whereEqualTo("userUid", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    documentSnapshot.getReference().collection("pin").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                userPIN = doc.getString("PIN");
                                toggleLoading(false);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new ErrorDialog().ErrorDialog(UnlockAppActivity.this, e.getMessage() + "\nLinia: 76\nSpróbuj Ponownie Później!", new ErrorDialog.DialogInterfaceListener() {
                                @Override
                                public void OnDismissed() {
                                    finishAndRemoveTask();
                                }
                            });
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new ErrorDialog().ErrorDialog(UnlockAppActivity.this, e.getMessage() + "\nLinia: 89\nSpróbuj Ponownie Później!", new ErrorDialog.DialogInterfaceListener() {
                    @Override
                    public void OnDismissed() {
                        finishAndRemoveTask();
                    }
                });
            }
        });

        otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                otpView.resetState();
            }

            @Override
            public void onOTPComplete(String otp) {
                if (otp.contentEquals(userPIN)) {
                    otpView.showSuccess();
                    startActivity(new Intent(UnlockAppActivity.this, MainActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    otpView.showError();
                }
            }
        });
    }

    private void toggleLoading(boolean on) {
        if (on) {
            progressBar.setVisibility(View.VISIBLE);
            enterPinLinear.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            enterPinLinear.setVisibility(View.VISIBLE);
        }
    }
}
