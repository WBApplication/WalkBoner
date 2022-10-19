package com.fusoft.walkboner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;
import de.dlyt.yanndroid.oneui.view.Toast;

public class PermissionsActivity extends AppCompatActivity {
    private MaterialButton skipButton;
    private MaterialButton acceptButton;

    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        initView();
        setup();
    }

    private void initView() {
        skipButton = (MaterialButton) findViewById(R.id.skip_button);
        acceptButton = (MaterialButton) findViewById(R.id.accept_button);
    }

    private void setup() {
        acceptButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(PermissionsActivity.this, permissions, 100);
            } else {
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(PermissionsActivity.this);
                errorDialog.setTitle("Błąd");
                errorDialog.setMessage("Nie powinieneś tego czytać, ponieważ aplikacja sądzi, że ma już przyznane pozwolenia...\nZgłoś ten błąd na Discordzie.\n\nMożesz pominąć przyznanie pozwoleń.");
                errorDialog.setPositiveButton("Przejdź Dalej", (dialogInterface, i) -> {
                    redirectUser();
                });
                errorDialog.setNegativeButton("Discord", (dialogInterface, i) -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.discord_invite_url))));
                });
                errorDialog.setCancelable(false);
                errorDialog.show();
            }
        });

        skipButton.setOnClickListener(view -> {
            redirectUser();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                redirectUser();
            } else {
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(PermissionsActivity.this);
                errorDialog.setTitle("Błąd");
                errorDialog.setMessage("Coś poszło nie tak...\nJeśli zezwoliłeś, a widzisz ten dialog, zgłoś to na Discordzie.\n\nMożesz pominąć przyznanie pozwoleń.");
                errorDialog.setPositiveButton("Przejdź Dalej", (dialogInterface, i) -> {
                    redirectUser();
                });
                errorDialog.setNegativeButton("Discord", (dialogInterface, i) -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.discord_invite_url))));
                });
                errorDialog.setCancelable(false);
                errorDialog.show();
            }
        }
    }

    private void redirectUser() {
        if (getIntent().hasExtra("isPinRequired") && getIntent().getExtras().getBoolean("isPinRequired", false)) {
            startActivity(new Intent(PermissionsActivity.this, UnlockAppActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            startActivity(new Intent(PermissionsActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}
