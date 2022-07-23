package com.fusoft.walkboner.views.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;

public class DisclaimerDialog {
    public void Dialog(Context context, String title, String message, DisclaimerDialogInterface listener) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Rozumiem!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.OnDismiss();
            }
        });
        dialog.show();
    }

    public interface DisclaimerDialogInterface {
        void OnDismiss();
    }
}
