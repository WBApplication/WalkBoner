package com.fusoft.walkboner.utils.downloader;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.fusoft.walkboner.utils.UidGenerator;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

public class UpdateDownload {
    public static void Download(Context context, String url, DownloadListener listener) {
        String TAG = "WalkBoner Updater";

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/WalkBoner/update/" + UidGenerator.Generate(5) + ".apk";

        Log.d(TAG, path);
        Log.d(TAG, "Preparing download for url " + url);
        FileDownloader.setup(context);
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "Pending...");
                    }

                    @Override
                    protected void started(BaseDownloadTask task) {
                        Log.d(TAG, "Started...");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "Connected...");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        listener.OnProgress(100 * soFarBytes / totalBytes);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        listener.OnDownloaded(path);
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

    public interface DownloadListener {
        void OnDownloaded(String path);

        void OnProgress(int progress);

        void OnError(String reason);
    }
}
