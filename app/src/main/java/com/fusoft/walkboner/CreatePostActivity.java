package com.fusoft.walkboner;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.AuthenticationListener;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.DeleteImage;
import com.fusoft.walkboner.database.ImageUploadListener;
import com.fusoft.walkboner.database.StorageDirectory;
import com.fusoft.walkboner.database.UploadImage;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.UidGenerator;
import com.fusoft.walkboner.views.dialogs.ErrorDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.HashMap;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.view.Toast;
import de.dlyt.yanndroid.oneui.widget.ProgressBar;
import de.dlyt.yanndroid.oneui.widget.Spinner;
import de.dlyt.yanndroid.oneui.widget.Switch;

public class CreatePostActivity extends AppCompatActivity {
    private EditText descriptionEdt;
    private ImageView image;
    private Spinner celebritiesSpinner;
    private Switch allowCommentsSwitch;
    private MaterialButton addButton;
    private MaterialTextView maxTextSize;
    private ProgressDialog loading;
    private LinearLayout mainLinear;
    private LinearLayout uploadingImageLinear;
    private ProgressBar imageUploadingProgressBar;
    private MaterialTextView maxLettersText;

    private Uri imageStoragePath;
    private String imageUrl = "";
    private String documentUid = "";

    private boolean allowComments = true;

    FirebaseFirestore firestore;

    private Authentication authentication;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        loading = new ProgressDialog(CreatePostActivity.this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.setProgressStyle(ProgressDialog.STYLE_CIRCLE);

        firestore = FirebaseFirestore.getInstance();

        authentication = new Authentication(null);

        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User userD) {
                user = userD;
                addButton.setEnabled(true);
            }

            @Override
            public void OnUserNotFinded() {

            }

            @Override
            public void OnError(String reason) {

            }
        });

        initView();
        setup();
    }

    private void initView() {
        descriptionEdt = (EditText) findViewById(R.id.description_edt);
        image = (ImageView) findViewById(R.id.image);
        celebritiesSpinner = (Spinner) findViewById(R.id.celebrities_spinner);
        allowCommentsSwitch = (Switch) findViewById(R.id.allow_comments_switch);
        addButton = (MaterialButton) findViewById(R.id.add_button);
        maxTextSize = findViewById(R.id.max_letters_text);
        mainLinear = (LinearLayout) findViewById(R.id.main_linear);
        mainLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        uploadingImageLinear = (LinearLayout) findViewById(R.id.uploading_image_linear);
        imageUploadingProgressBar = (ProgressBar) findViewById(R.id.image_uploading_progress_bar);
        maxLettersText = (MaterialTextView) findViewById(R.id.max_letters_text);
    }

    private void setup() {
        image.setOnClickListener(v -> {
            imageChooser();
        });

        addButton.setOnClickListener(v -> {
            if (imageStoragePath != null) {
                if (!imageUrl.isEmpty()) {
                    DeleteImage.Delete(CreatePostActivity.this, imageUrl, new DeleteImage.DeleteTask() {
                        @Override
                        public void OnDeleted() {

                        }

                        @Override
                        public void OnError(String reason) {

                        }
                    });
                }
                uploadingImageLinear.setVisibility(View.VISIBLE);
                new UploadImage(CreatePostActivity.this, StorageDirectory.POSTS_PATH, imageStoragePath, new ImageUploadListener() {
                    @Override
                    public void OnImageUploaded(String imageUrlString) {
                        imageUrl = imageUrlString;
                        firestore.collection("posts").add(defaultMap()).addOnSuccessListener(documentReference -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("documentUid", documentReference.getId());
                            firestore.collection("posts").document(documentReference.getId()).update(map);
                            finish();
                        }).addOnFailureListener(e -> {
                            new ErrorDialog().SimpleErrorDialog(CreatePostActivity.this, e.getMessage());
                        });
                    }

                    @Override
                    public void Progress(int value) {
                        imageUploadingProgressBar.setProgress(value);
                    }

                    @Override
                    public void OnError(String reason) {
                        Toast.makeText(CreatePostActivity.this, reason, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CreatePostActivity.this, "Wybierz Zdjęcie, które chcesz załączyć do postu", Toast.LENGTH_SHORT).show();
            }
        });

        allowCommentsSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            allowComments = b;
        });

        descriptionEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 201) {
                    maxTextSize.setTextColor(getColor(android.R.color.holo_red_light));
                } else {
                    maxTextSize.setTextColor(getColor(de.dlyt.yanndroid.oneui.R.color.sesl_secondary_text));
                }
            }
        });
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(i);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageStoragePath = result.getData().getData();
                }
            });

    @NonNull
    private HashMap<String, Object> defaultMap() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        HashMap<String, Object> map = new HashMap<>();
        map.put("userUid", user.getUserUid());
        map.put("postUid", UidGenerator.Generate());
        map.put("postDescription", descriptionEdt.getText().toString());
        map.put("postImage", imageUrl);
        map.put("documentUid", "");
        map.put("createdAt", String.valueOf(timestamp.getTime()));
        map.put("showsCelebrity", "celebrityUid");
        map.put("allowComments", String.valueOf(allowComments));

        return map;
    }
}