package com.fusoft.walkboner.views.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;

public class InfoDialog {
    public void MakeDialog(Context context, String message, @Nullable String positiveButtonText, @Nullable String negativeButtonText, InfoDialogInterfaceListener listener) {
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(context);
        errorDialog.setTitle("Informacja");
        errorDialog.setMessage(message);
        if (positiveButtonText != null) {
            errorDialog.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.OnPositiveButtonClicked();
                }
            });
        }
        if (negativeButtonText != null) {
            errorDialog.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.OnNegativeButtonClicked();
                }
            });
        }
        errorDialog.setCancelable(false);
        errorDialog.show();
    }

    public interface InfoDialogInterfaceListener {
        void OnPositiveButtonClicked();

        void OnNegativeButtonClicked();
    }
}
