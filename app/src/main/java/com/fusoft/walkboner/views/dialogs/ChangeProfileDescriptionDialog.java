package com.fusoft.walkboner.views.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.fusoft.walkboner.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

public class ChangeProfileDescriptionDialog {
    public static void Show(Context context, String oldDescription, ProfileDescriptionDialogListener listener) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setView(R.layout.dialog_edit_profile_description);
        AlertDialog dialog = dialogBuilder.show();

        if (dialog.isShowing()) {
            TextInputEditText descriptionEditText = dialog.findViewById(R.id.description_edittext);
            TextInputLayout descriptionTxtInputLayout = dialog.findViewById(R.id.description_txtinputlayout);
            MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);
            MaterialButton saveButton = dialog.findViewById(R.id.save_button);

            descriptionEditText.setText(oldDescription);

            saveButton.setOnClickListener(v -> {
                if (descriptionEditText.getText().toString().length() <= 200) {
                    listener.OnChange(descriptionEditText.getText().toString());
                    dialog.dismiss();
                } else {
                    descriptionTxtInputLayout.setErrorEnabled(true);
                    descriptionTxtInputLayout.setError("Max. 200 znaków!");
                }
            });

            cancelButton.setOnClickListener(v -> {
                listener.OnDismiss();
                dialog.dismiss();
            });
        }
    }

    public interface ProfileDescriptionDialogListener {
        void OnChange(String newDescription);

        void OnDismiss();
    }
}
