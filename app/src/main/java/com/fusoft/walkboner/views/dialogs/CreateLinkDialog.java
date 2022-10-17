package com.fusoft.walkboner.views.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.settings.Settings;
import com.google.android.material.button.MaterialButton;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;

public class CreateLinkDialog {

    private LinearLayout pickImageLinear, imagePickerHolderLinear;
    private EditText linkTitleEdittext;
    private EditText linkDescriptionEdittext;
    private EditText linkUrlEdittext;
    private MaterialButton addLinkButton;
    private AlertDialog dialog;
    private CheckBox publishAnonymousCheckbox;

    private Settings settings;

    public void Show(Context context, DialogListener listener) {
        dialog = new AlertDialog.Builder(context).setView(R.layout.dialog_create_link)
                .show();

        settings = new Settings(context);

        pickImageLinear = dialog.findViewById(R.id.pick_image_linear);
        imagePickerHolderLinear = dialog.findViewById(R.id.image_picker_holder_linear);
        linkTitleEdittext = dialog.findViewById(R.id.link_title_edittext);
        linkDescriptionEdittext = dialog.findViewById(R.id.link_description_edittext);
        linkUrlEdittext = dialog.findViewById(R.id.link_url_edittext);
        addLinkButton = dialog.findViewById(R.id.add_link_button);
        publishAnonymousCheckbox = dialog.findViewById(R.id.publish_anonymous_checkbox);

        imagePickerHolderLinear.setVisibility(View.GONE);
        publishAnonymousCheckbox.setVisibility(View.GONE);

        if (settings.shouldPublishLink()) {
            imagePickerHolderLinear.setVisibility(View.VISIBLE);
            publishAnonymousCheckbox.setVisibility(View.VISIBLE);
        }

        addLinkButton.setOnClickListener(v -> {
            listener.OnAddClicked(linkTitleEdittext.getText().toString(), linkDescriptionEdittext.getText().toString(), linkUrlEdittext.getText().toString(), null);
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                settings = null;
                pickImageLinear = null;
                linkTitleEdittext = null;
                linkDescriptionEdittext = null;
                linkUrlEdittext = null;
                addLinkButton = null;

                listener.OnDismiss();
            }
        });
    }

    public void Dismiss() {
        if (dialog.isShowing()) dialog.dismiss();
    }

    public interface DialogListener {
        void OnAddClicked(String title, String description, String url, String imageUrl);

        void OnDismiss();
    }
}