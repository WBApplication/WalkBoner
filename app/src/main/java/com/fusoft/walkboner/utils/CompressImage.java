package com.fusoft.walkboner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import kotlin.NotImplementedError;

/*
 * https://stackoverflow.com/questions/5125779/how-to-compress-image-for-imageview-in-android
 *
 * There was a problem with loading images to RecyclerView with full resolution,
 * after picking ~13 Images, app begins to stutter on 4GB RAM Device
 *
 * Compress Image to avoid this on devices with RAM < 6GB
 */

public class CompressImage {
    private static Bitmap bmp1, bmpResult;
    private static ByteArrayOutputStream out;

    private static final String TAG = "CompressImage";
    private static final int COMPRESS_THRESHOLD = 10;

    public enum CompressFor {
        DATABASE,
        LOCAL
    }

    /**
     * Call {@link CompressImage}.Recycle after calling {@link CompressImage}.FromFile
     * @param filePath of image to compress
     * @param compressFor if for local use, export bitmap from file.
     *                    if for database upload, create compressed file and upload, after upload - delete.
     * @return compressed {@link Bitmap}
     */
    @Nullable
    public static Bitmap FromFile(Context context, @NonNull String filePath, @NonNull CompressFor compressFor) {
        if (compressFor.equals(CompressFor.LOCAL)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 128;
            bmp1 = BitmapFactory.decodeFile(filePath);
            out = new ByteArrayOutputStream();
            bmp1.compress(Bitmap.CompressFormat.JPEG, COMPRESS_THRESHOLD, out);
            Log.d(TAG, "Size of original image: " + Formatter.formatShortFileSize(context, bmp1.getByteCount()));
            Log.d(TAG, "Size of image: " + Formatter.formatShortFileSize(context, out.toByteArray().length));
            Log.d(TAG, "Compress Threshold: " + COMPRESS_THRESHOLD);
            Log.d(TAG, "----------------------");

            return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        } else if (compressFor.equals(CompressFor.DATABASE)) {
            throw new NotImplementedError();
        }

        return null;
    }

    /**
     * Recycle {@link Bitmap} and remove reference for Garbage Collector
     */
    public static void Recycle() {
        if (bmp1 != null) {
            bmp1.recycle();
            bmp1 = null;
        }
    }
}
