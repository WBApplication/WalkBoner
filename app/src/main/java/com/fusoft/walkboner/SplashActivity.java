package com.fusoft.walkboner;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.funcions.UnbanUser;
import com.fusoft.walkboner.database.funcions.UnbanUserListener;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.UidGenerator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;
import de.dlyt.yanndroid.oneui.view.Toast;

public class SplashActivity extends AppCompatActivity {
    private MaterialTextView logoText, importantText, bannedText;

    private Authentication authentication;

    private Boolean isLoggedIn = false;
    private Boolean isPinRequired = false;

    private AlertDialog.Builder errorDialog;

    private User userData = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authentication = new Authentication(
                new AuthenticationListener() {
                    @Override
                    public void UserAlreadyLoggedIn(boolean pinRequired) {
                        isLoggedIn = true;
                        isPinRequired = pinRequired;
                    }

                    @Override
                    public void UserRequiredToBeLogged() {
                        isLoggedIn = false;
                    }

                    @Override
                    public void OnLogin(boolean isSuccess, boolean pinRequired, @Nullable String reason) {

                    }

                    @Override
                    public void OnRegister(boolean isSuccess, @Nullable String reason) {

                    }
                }
        );

        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                userData = user;
            }

            @Override
            public void OnUserNotFinded() {

            }

            @Override
            public void OnError(String reason) {

            }
        });

        initView();
        setup();
    }

    private void initView() {
        logoText = (MaterialTextView) findViewById(R.id.logo_text);
        bannedText = findViewById(R.id.banned_text);
        bannedText.setVisibility(View.GONE);
        importantText = findViewById(R.id.important_text);
        importantText.setVisibility(View.GONE);

        errorDialog = new AlertDialog.Builder(SplashActivity.this);
        errorDialog.setTitle("Błąd");
        errorDialog.setMessage("Wystąpił nieznany błąd w połączeniu z serwerem.\nSprawdź połączenie z internetem lub skontaktuj się z administratorem.");
        errorDialog.setPositiveButton("Zamknij", (dialogInterface, i) -> finish());
        errorDialog.setNegativeButton("Discord", (dialogInterface, i) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.discord_invite_url))));
        });
        errorDialog.setCancelable(false);
    }

    private void setup() {
        logoText.setAlpha(0f);
        logoText.setScaleX(0.8f);
        logoText.setScaleY(0.8f);
        logoText.animate().alpha(1f).scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (isLoggedIn == null) {
                        importantText.setVisibility(View.VISIBLE);
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (isLoggedIn == null) {
                                errorDialog.show();
                            } else {
                                redirectUser();
                            }
                        }, 5000);
                    } else {
                        redirectUser();
                    }
                }, 5000);
                super.onAnimationEnd(animation);
            }
        }).start();
    }

    private void redirectUser() {

        String write_storage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String read_storage = Manifest.permission.READ_EXTERNAL_STORAGE;
        int write_storage_result = checkSelfPermission(write_storage);
        int read_storage_result = checkSelfPermission(read_storage);

        if (userData != null && userData.isUserBanned()) {
            long bannedTo = Long.parseLong(userData.getUserBannedTo());

            if (new Timestamp(System.currentTimeMillis()).getTime() >= bannedTo) {
                UnbanUser.Unban(userData.getUserUid(), new UnbanUserListener() {
                    @Override
                    public void OnUserUnbanned() {
                        Toast.makeText(SplashActivity.this, "Zostałeś Odbanowany!\nBądź ostrożny!", Toast.LENGTH_SHORT).show();
                        if (isPinRequired) {
                            startActivity(new Intent(SplashActivity.this, UnlockAppActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }

                    @Override
                    public void OnError(String reason) {
                        Toast.makeText(SplashActivity.this, reason, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                importantText.setVisibility(View.GONE);
                bannedText.setVisibility(View.VISIBLE);
                bannedText.setText("Urządzenie Zbanowane\nPowód:\n" + userData.getUserBanReason() + "\n\nZbanowano do: " + getDate(Long.parseLong(userData.getUserBannedTo())) + "\n\nJeśli uważasz, że nie powinieneś dostać bana, odwołaj się na serwerze Discord\n\nTwój Identyfikator\n(kliknij by skopiować)\n" + userData.getUserUid());
            }
        } else {
            if (!isLoggedIn) {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            } else if (isLoggedIn && write_storage_result == PackageManager.PERMISSION_DENIED || read_storage_result == PackageManager.PERMISSION_DENIED) {
                Intent intent = new Intent(SplashActivity.this, PermissionsActivity.class);
                intent.putExtra("isPinRequired", isPinRequired);
                startActivity(intent);
            } else if (isLoggedIn && isPinRequired) {
                startActivity(new Intent(SplashActivity.this, UnlockAppActivity.class));
            } else if (isLoggedIn && !isPinRequired) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm dd-MM-yyyy", cal).toString();
        return date;
    }
}
