package com.fusoft.walkboner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.fusoft.walkboner.utils.AppUpdate;
import com.fusoft.walkboner.utils.downloader.UpdateDownload;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;

public class UpdateActivity extends AppCompatActivity {

    private LinearLayout loadingUpdateLinear;
    private ProgressBar loadingUpdateProgress;
    private MaterialTextView loadingUpdateErrorText;
    private LinearLayout updateDetailsLinear;
    private MaterialTextView changeLogText;
    private MaterialButton updateButton;
    private MaterialButton laterButton;
    private LinearLayout updateDownloadingLinear;
    private ProgressBar updateProgressBar;
    private MaterialTextView updateProgressText;
    private LinearLayout continueInstallationLinear;

    private com.fusoft.walkboner.settings.Settings settings;

    private String url;

    private boolean isUpdateLoaded = false;
    private boolean isRequired = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        initView();
        setup();
    }

    @Override
    public void onBackPressed() {
        if (isUpdateLoaded) {
            if (isRequired) {
                finishAffinity();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void initView() {
        loadingUpdateLinear = (LinearLayout) findViewById(R.id.loading_update_linear);
        loadingUpdateProgress = (ProgressBar) findViewById(R.id.loading_update_progress);
        loadingUpdateErrorText = (MaterialTextView) findViewById(R.id.loading_update_error_text);
        updateDetailsLinear = (LinearLayout) findViewById(R.id.update_details_linear);
        changeLogText = (MaterialTextView) findViewById(R.id.change_log_text);
        updateButton = (MaterialButton) findViewById(R.id.update_button);
        laterButton = (MaterialButton) findViewById(R.id.later_button);
        updateDownloadingLinear = (LinearLayout) findViewById(R.id.update_downloading_linear);
        updateProgressBar = (ProgressBar) findViewById(R.id.update_progress_bar);
        updateProgressText = (MaterialTextView) findViewById(R.id.update_progress_text);
        continueInstallationLinear = (LinearLayout) findViewById(R.id.continue_installation_linear);

        settings = new com.fusoft.walkboner.settings.Settings(UpdateActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                Intent intent = new Intent(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))));
                someActivityResultLauncher.launch(intent);
            }
        }
    }

    private void setup() {
        AppUpdate.checkForUpdate(new AppUpdate.UpdateListener() {
            @Override
            public void OnUpdateAvailable(String version, String changeLog, String downloadUrl, boolean isRequired) {
                isUpdateLoaded = true;
                UpdateActivity.this.isRequired = isRequired;
                loadingUpdateLinear.setVisibility(View.GONE);
                updateDetailsLinear.setVisibility(View.VISIBLE);

                url = downloadUrl;

                changeLogText.setText(changeLog);

                if (isRequired) {
                    laterButton.setEnabled(false);
                }
            }

            @Override
            public void OnError(String reason) {
                loadingUpdateProgress.setVisibility(View.GONE);
                loadingUpdateErrorText.setVisibility(View.VISIBLE);
                loadingUpdateErrorText.setText(reason);
            }
        });

        updateButton.setOnClickListener(v -> {
            Log.d("WalkBoner Updater", "Update Clicked");
            updateDetailsLinear.setVisibility(View.GONE);
            updateDownloadingLinear.setVisibility(View.VISIBLE);

            UpdateDownload.Download(UpdateActivity.this, url, new UpdateDownload.DownloadListener() {
                @Override
                public void OnDownloaded(String path) {
                    updateDownloadingLinear.setVisibility(View.GONE);
                    continueInstallationLinear.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri uri = FileProvider.getUriForFile(UpdateActivity.this,
                                getApplicationContext().getPackageName() + ".provider", new File(path));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uriFromFile(new File(path)),
                                "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAffinity();
                    }
                }

                @Override
                public void OnProgress(int progress) {
                    updateProgressBar.setProgress(progress);
                    updateProgressText.setText(progress + "%");
                }

                @Override
                public void OnError(String reason) {
                    updateProgressBar.setVisibility(View.GONE);

                    if (reason.contains("404")) {
                        updateProgressText.setText("Błąd: Nie znaleziono pliku aktualizacji.\nMożliwe, że został on usunięty z powodu błędu w nowej wersji i musisz poczekać aż zostanie on naprawiony lub, link do pliku nowej wersji jest nieprawidłowy!\nZgłoś błąd administratorowi na serwerze Discord jeśli nie widzisz nigdzie noty czemu ten błąd może występować!");
                    } else {
                        updateProgressText.setText("Błąd: " + reason);
                    }
                }
            });
        });

        laterButton.setOnClickListener(v -> {
            settings.skipUpdateNextTime(true);
            finish();
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                }
            });

    private Uri uriFromFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(UpdateActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
