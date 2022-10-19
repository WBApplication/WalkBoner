package com.fusoft.walkboner.uniload.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.utils.UidGenerator;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

public class Download {

    private SharedPreferences settings;

    public Download(@NonNull Context context, String url, DownloaderListener listener) {
        settings = context.getSharedPreferences("uniload", Activity.MODE_PRIVATE);

        String fileType = ".png";

        if (url.contains(".jpg")) fileType = ".jpg";
        if (url.contains(".png")) fileType = ".png";
        if (url.contains(".mp4")) fileType = ".mp4";
        if (url.contains(".webm")) fileType = ".webm";

        String fileName = UidGenerator.Generate(8);

        if (!settings.getString("prefixFileName", "").isEmpty()) {
            fileName = settings.getString("prefixFileName", "") + "_" + fileName;
        }

        if (!settings.getString("sufixFileName", "").isEmpty()) {
            fileName = fileName + "_" + settings.getString("sufixFileName", "");
        }

        FileDownloader.getImpl().create(url)
                .setPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Uniload/" + fileName + fileType)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int progress = ((int) 100 * soFarBytes / totalBytes);
                        listener.OnProgress(progress);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        listener.OnSuccess();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        listener.OnError(e.getMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
    }

    public interface DownloaderListener {
        void OnProgress(int progress);

        void OnSuccess();

        void OnError(String reason);
    }

    public interface MultiDownloaderListener {
        void OnProgress(int progress);

        void OnSuccess();

        void OnError(String reason);
    }
}
