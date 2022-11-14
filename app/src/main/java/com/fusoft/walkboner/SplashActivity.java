package com.fusoft.walkboner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.database.funcions.UnbanUser;
import com.fusoft.walkboner.database.funcions.UnbanUserListener;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.settings.Settings;
import com.fusoft.walkboner.singletons.UserSingletone;
import com.google.android.material.textview.MaterialTextView;

import java.sql.Timestamp;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;
import de.dlyt.yanndroid.oneui.view.Toast;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private String TAG = "SplashActivtiy";

    private MaterialTextView logoText, importantText, bannedText, appDetailsText;

    private Authentication authentication;
    private Settings settings;

    private AlertDialog.Builder errorDialog;

    private User userData = null;

    @Override
    protected void onDestroy() {
        authentication = null;
        settings = null;
        errorDialog = null;
        userData = null;

        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        settings = new Settings(SplashActivity.this);

        authentication = new Authentication(new AuthenticationListener() {
            @Override
            public void UserAlreadyLoggedIn(boolean success, boolean isBanned, boolean pinRequired, @Nullable String reason, @Nullable User userData) {
                String write_storage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                String read_storage = Manifest.permission.READ_EXTERNAL_STORAGE;
                String camera = Manifest.permission.CAMERA;
                int write_storage_result = checkSelfPermission(write_storage);
                int read_storage_result = checkSelfPermission(read_storage);
                int camera_result = checkSelfPermission(camera);
                boolean write_granted = write_storage_result == PackageManager.PERMISSION_GRANTED;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // On API Level > 28 WRITE_EXTERNAL_STORAGE is always False
                    write_granted = true;
                }
                boolean read_granted = read_storage_result == PackageManager.PERMISSION_GRANTED;
                boolean camera_granted = camera_result == PackageManager.PERMISSION_GRANTED;

                log("<- SplashActivity ->");
                log("Listener Triggered");
                log("Write Permission Granted: " + write_granted);
                log("Read Permission Granted: " + read_granted);
                log("Camera Permission Granted: " + camera_granted);

                if (success) {
                    log("Successfully to this time");

                    UserSingletone.getInstance().setUser(userData);

                    if (pinRequired || settings.isBiometricUnlockEnabled()) {
                        log("Pin is Required => Boolean Value: " + pinRequired);
                        log("Biometric Required? => Boolean Value: " + settings.isBiometricUnlockEnabled());
                        if (!write_granted || !read_granted || !camera_granted) {
                            log("Also Permissions needed");
                            Intent intent = new Intent(SplashActivity.this, PermissionsActivity.class);
                            intent.putExtra("isPinRequired", true);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            log("Redirecting to PermissionsActivity");
                            finish();
                        } else {
                            log("Permissions already granted");
                            startActivity(new Intent(SplashActivity.this, UnlockAppActivity.class));
                            log("Redirecting to UnlockAppActivity");
                            finish();
                        }
                    } else {
                        log("Pin is not Required");
                        if (!write_granted || !read_granted || !camera_granted) {
                            log("Also Permissions needed");
                            Intent intent = new Intent(SplashActivity.this, PermissionsActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            log("Redirecting to PermissionsActivity");
                            finish();
                        } else {
                            log("Permissions already granted");
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            log("Redirecting to MainActivity");
                            finish();
                        }
                    }
                } else {
                    if (!isBanned) {
                        importantText.setVisibility(View.VISIBLE);
                        importantText.setText(reason);
                    } else {
                        long bannedTo = Long.parseLong(userData.getUserBannedTo());

                        if (new Timestamp(System.currentTimeMillis()).getTime() >= bannedTo) {
                            UnbanUser.Unban(userData.getUserUid(), new UnbanUserListener() {
                                @Override
                                public void OnUserUnbanned() {
                                    Toast.makeText(SplashActivity.this, "Zostałeś Odbanowany!\nBądź ostrożny!", Toast.LENGTH_SHORT).show();
                                    if (pinRequired || settings.isBiometricUnlockEnabled()) {
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
                            logoText.setVisibility(View.GONE);
                            bannedText.setVisibility(View.VISIBLE);
                            bannedText.setText(reason);
                        }
                    }
                }
            }

            @Override
            public void UserRequiredToBeLogged() {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                finish();
            }
        });

        initView();
        setup();
    }

    private void initView() {
        logoText = findViewById(R.id.logo_text);
        bannedText = findViewById(R.id.banned_text);
        bannedText.setVisibility(View.GONE);
        importantText = findViewById(R.id.important_text);
        importantText.setVisibility(View.GONE);
        appDetailsText = findViewById(R.id.app_details_text);

        String appDetails = BuildConfig.VERSION_NAME;
        if (BuildConfig.DEBUG)
            appDetails = appDetails + "\n" + "DEBUG MODE";

        appDetailsText.setText(appDetails);

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
        logoText.animate().alpha(1f).scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    private void log(String message) {
        Log.d(TAG, message);
    }
}
