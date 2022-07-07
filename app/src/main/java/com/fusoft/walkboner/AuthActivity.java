package com.fusoft.walkboner;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.settings.Settings;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import de.dlyt.yanndroid.oneui.view.Toast;

public class AuthActivity extends AppCompatActivity {

    // TODO: Implement Auth Functions with Firebase

    private LinearLayout mainLinear;
    private MaterialTextView currentModeText;
    private EditText emailEdt;
    private EditText usernameEdt;
    private EditText passwordEdt;
    private MaterialButton doneButton;
    private MaterialButton changeButton;

    private boolean isRegister = true;

    private Authentication auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initView();
        setup();
    }

    private void initView() {
        mainLinear = findViewById(R.id.main_linear);
        mainLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        currentModeText = (MaterialTextView) findViewById(R.id.current_mode_text);
        emailEdt = (EditText) findViewById(R.id.email_edt);
        usernameEdt = (EditText) findViewById(R.id.username_edt);
        passwordEdt = (EditText) findViewById(R.id.password_edt);
        doneButton = (MaterialButton) findViewById(R.id.done_button);
        changeButton = (MaterialButton) findViewById(R.id.change_button);

        auth = new Authentication(new AuthenticationListener() {
            @Override
            public void UserAlreadyLoggedIn(boolean pinRequired) {

            }

            @Override
            public void UserRequiredToBeLogged() {

            }

            @Override
            public void OnLogin(boolean isSuccess, boolean pinRequired, @Nullable String reason) {
                if (!isSuccess) {
                    toggleInputs(true);
                    showMessage(reason);
                } else {
                    new Settings(AuthActivity.this).setPasswordHashed(passwordEdt.getText().toString());
                    redirectUser(pinRequired);
                    /* DO NOT PLACE OTHER METHODS AFTER redirectUser(boolean) */
                }
            }

            @Override
            public void OnRegister(boolean isSuccess, @Nullable String reason) {
                if (!isSuccess) {
                    toggleInputs(true);
                    showMessage(reason);
                } else {
                    redirectUser(false);
                }
            }
        });
    }

    private void setup() {
        doneButton.setOnClickListener(v -> {
            if (isRegister) {
                Register();
            } else {
                Login();
            }
        });

        changeButton.setOnClickListener(v -> changeState());
    }

    private void redirectUser(boolean isPinRequired) {
        String write_storage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String read_storage = Manifest.permission.READ_EXTERNAL_STORAGE;
        int write_storage_result = checkSelfPermission(write_storage);
        int read_storage_result = checkSelfPermission(read_storage);


        if (write_storage_result == PackageManager.PERMISSION_GRANTED || read_storage_result == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent intent = new Intent(AuthActivity.this, PermissionsActivity.class);
            intent.putExtra("isPinRequired", isPinRequired);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    private void Login() {
        if (isEmailCorrect()) {
            if (!passwordEdt.getText().toString().isEmpty() || passwordEdt.getText().toString().length() >= 8) {
                toggleInputs(false);
                auth.login(emailEdt.getText().toString(), passwordEdt.getText().toString());
            } else {
                showMessage("Hasło nie może być puste, oraz musi zawierać więcej niż 8 znaków.");
            }
        } else {
            showMessage("Niepoprawny adres E-Mail!");
        }
    }

    private void Register() {
        if (isEmailCorrect()) {
            if (!usernameEdt.getText().toString().isEmpty() || usernameEdt.getText().toString().length() >= 4) {
                if (!passwordEdt.getText().toString().isEmpty() || passwordEdt.getText().toString().length() >= 8) {
                    toggleInputs(false);
                    auth.register(emailEdt.getText().toString(), usernameEdt.getText().toString(), passwordEdt.getText().toString());
                } else {
                    showMessage("Hasło nie może być puste, oraz musi zawierać więcej niż 8 znaków.");
                }
            } else {
                showMessage("Nazwa Użytkownika nie może być pusta, oraz musi zawierać więcej niż 3 znaki.");
            }
        } else {
            showMessage("Niepoprawny adres E-Mail!");
        }
    }

    private void showMessage(String text) {
        Toast.makeText(AuthActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void toggleInputs(boolean isOn) {
        emailEdt.setEnabled(isOn);
        usernameEdt.setEnabled(isOn);
        passwordEdt.setEnabled(isOn);
        doneButton.setEnabled(isOn);
        changeButton.setEnabled(isOn);
    }

    private void changeState() {
        if (isRegister) {
            // Change To Login
            currentModeText.setText("Logowanie");
            usernameEdt.setVisibility(View.GONE);
            doneButton.setText("Zaloguj");
            changeButton.setText("Rejestracja");
        } else {
            // Change To Register
            currentModeText.setText("Rejestracja");
            usernameEdt.setVisibility(View.VISIBLE);
            doneButton.setText("Zarejestruj");
            changeButton.setText("Zaloguj");
        }

        isRegister = !isRegister;
    }

    private boolean isEmailCorrect() {
        return (!TextUtils.isEmpty(emailEdt.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(emailEdt.getText().toString()).matches());
    }
}