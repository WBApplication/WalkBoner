package com.fusoft.walkboner.views.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.fusoft.walkboner.R;
import com.google.android.material.button.MaterialButton;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;

public class UniloadLinkDialog {

    public static void Show(@NonNull Context context, @NonNull DialogListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(context).setView(R.layout.dialog_uniload_link)
                .show();

        EditText urlEdittext = dialog.findViewById(R.id.url_post_edittext);
        MaterialButton nextButton = dialog.findViewById(R.id.next_button);

        urlEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (urlEdittext.getText().toString().isEmpty()) {
                    nextButton.setEnabled(false);
                } else {
                    nextButton.setEnabled(true);
                }
            }
        });

        nextButton.setOnClickListener(v -> {
            listener.OnAddClicked(urlEdittext.getText().toString());
        });
    }

    public interface DialogListener {
        void OnAddClicked(String url);

        void OnDismiss();
    }
}
