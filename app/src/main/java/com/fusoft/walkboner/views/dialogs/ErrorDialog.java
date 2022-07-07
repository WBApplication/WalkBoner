package com.fusoft.walkboner.views.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.fusoft.walkboner.MainActivity;
import com.fusoft.walkboner.PermissionsActivity;
import com.fusoft.walkboner.R;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;

public class ErrorDialog {
    public void SimpleErrorDialog(Context context, String message) {
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(context);
        errorDialog.setTitle("Błąd");
        errorDialog.setMessage(message);
        errorDialog.setPositiveButton("Zamknij", null);
        errorDialog.setCancelable(false);
        errorDialog.show();
    }

    public void ErrorDialog(Context context, String message, DialogInterfaceListener listener) {
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(context);
        errorDialog.setTitle("Błąd");
        errorDialog.setMessage(message);
        errorDialog.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.OnDismissed();
            }
        });
        errorDialog.setCancelable(false);
        errorDialog.show();
    }

    public interface DialogInterfaceListener {
        void OnDismissed();
    }
}
