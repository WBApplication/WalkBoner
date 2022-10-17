package com.fusoft.walkboner;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.security.Encrypt;
import com.fusoft.walkboner.settings.Settings;
import com.fusoft.walkboner.views.dialogs.ErrorDialog;
import com.fusoft.walkboner.views.dialogs.SuccessDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;

public class PasswordResetActivity extends AppCompatActivity {
    private EditText currentPasswordEdittext;
    private EditText newPasswordEdittext;
    private EditText confirmNewPasswordEdittext;
    private MaterialButton changeButton;

    private ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        initView();
        setup();
    }

    private void initView() {
        currentPasswordEdittext = (EditText) findViewById(R.id.current_password_edittext);
        newPasswordEdittext = (EditText) findViewById(R.id.new_password_edittext);
        confirmNewPasswordEdittext = (EditText) findViewById(R.id.confirm_new_password_edittext);
        changeButton = (MaterialButton) findViewById(R.id.change_button);
        changeButton.setEnabled(true);

        loading = new ProgressDialog(PasswordResetActivity.this);
        loading.setIndeterminate(true);
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(false);
        loading.setProgressStyle(ProgressDialog.STYLE_CIRCLE);
    }

    private void setup() {
        currentPasswordEdittext.addTextChangedListener(textWatcher());
        newPasswordEdittext.addTextChangedListener(textWatcher());
        confirmNewPasswordEdittext.addTextChangedListener(textWatcher());

        changeButton.setOnClickListener(v -> {
            loading.show();

            if (Encrypt.EncryptSHAPassword(currentPasswordEdittext.getText().toString()).contentEquals(new Settings(PasswordResetActivity.this).getPasswordHashed())) {
                if (validate()) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    user.updatePassword(newPasswordEdittext.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            loading.dismiss();
                            new SuccessDialog().MakeDialog(PasswordResetActivity.this, "Twoje hasło zostało zmienone!", "Zamknij", new SuccessDialog.InfoDialogInterfaceListener() {
                                @Override
                                public void OnDismissed() {
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading.dismiss();
                            new ErrorDialog().ErrorDialog(PasswordResetActivity.this, "Wystąpił błąd:\n" + e.getMessage(), new ErrorDialog.DialogInterfaceListener() {
                                @Override
                                public void OnDismissed() {
                                    finish();
                                }
                            });
                        }
                    });
                } else {
                    loading.dismiss();
                    new ErrorDialog().SimpleErrorDialog(PasswordResetActivity.this, "Coś poszło nie tak:\n- Któreś z pól jest puste,\n- Powtórzone hasło nie zgadza się z nowym hasłem");
                }
            } else {
                loading.dismiss();
                new ErrorDialog().SimpleErrorDialog(PasswordResetActivity.this, "Nieprawidłowe, aktualne hasło!");
            }
        });
    }

    private boolean validate() {
        if (!currentPasswordEdittext.getText().toString().isEmpty() && currentPasswordEdittext.getText().toString().length() >= 6 && !newPasswordEdittext.getText().toString().isEmpty() && newPasswordEdittext.getText().toString().length() >= 6 && !confirmNewPasswordEdittext.getText().toString().isEmpty() && confirmNewPasswordEdittext.getText().toString().length() >= 6 && newPasswordEdittext.getText().toString().contentEquals(confirmNewPasswordEdittext.getText().toString())) {
            return true;
        }

        return false;
    }

    private TextWatcher textWatcher() {
        TextWatcher tx = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validate();
            }
        };

        return tx;
    }
}
