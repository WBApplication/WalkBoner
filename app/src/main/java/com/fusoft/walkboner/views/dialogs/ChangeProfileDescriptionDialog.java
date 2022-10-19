package com.fusoft.walkboner.views.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.fusoft.walkboner.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;

public class ChangeProfileDescriptionDialog {
    public static void Show(Context context, ProfileDescriptionDialogListener listener) {
        int RED_COLOR = 0xFFFC0303;
        int DEFAULT_COLOR = context.getColor(de.dlyt.yanndroid.oneui.R.color.sesl_secondary_text);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_edit_profile_description)
                .show();

        if (dialog.isShowing()) {
            EditText descriptionEditText = dialog.findViewById(R.id.description_edittext);
            MaterialTextView descriptionLengthTextView = dialog.findViewById(R.id.description_length_textview);
            MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);
            MaterialButton saveButton = dialog.findViewById(R.id.save_button);

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int textLength = editable.length();

                    descriptionLengthTextView.setText(String.valueOf(textLength) + "/200");

                    if (textLength >= 200) {
                        descriptionLengthTextView.setTextColor(RED_COLOR);
                    } else {
                        descriptionLengthTextView.setTextColor(DEFAULT_COLOR);
                    }
                }
            };

            descriptionEditText.addTextChangedListener(textWatcher);

            saveButton.setOnClickListener(v -> {
                listener.OnChange(descriptionEditText.getText().toString());
                dialog.dismiss();
            });

            cancelButton.setOnClickListener(v -> {
                descriptionEditText.removeTextChangedListener(textWatcher);
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
