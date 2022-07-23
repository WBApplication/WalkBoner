package com.fusoft.walkboner.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class CopyTextToClipboard {
    public CopyTextToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("walkboner", text);
        clipboard.setPrimaryClip(clip);
    }
}
