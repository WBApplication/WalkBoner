package com.fusoft.walkboner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fusoft.walkboner.database.DeleteImage;
import com.fusoft.walkboner.database.ImageUploadListener;
import com.fusoft.walkboner.database.StorageDirectory;
import com.fusoft.walkboner.database.UploadImage;
import com.fusoft.walkboner.utils.UidGenerator;
import com.fusoft.walkboner.views.dialogs.InfoDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.utils.OnSingleClickListener;
import de.dlyt.yanndroid.oneui.view.Toast;
import de.dlyt.yanndroid.oneui.widget.ProgressBar;

public class AddInfluencerActivity extends AppCompatActivity {
    private LinearLayout userBlockedLinear;
    private MaterialTextView userBlockedReasonText;
    private MaterialTextView pickFromGalleryText;
    private ImageView image;
    private EditText firstNameEdittext;
    private EditText lastNameEdittext;
    private EditText nickNameEdittext;
    private EditText instagramUrlEdittext;
    private EditText tiktokUrlEdittext;
    private EditText youtubeUrlEdittext;
    private MaterialButton doneButton;
    private LinearLayout uploadingImageLinear;
    private ProgressBar uploadingProgressBar;
    private MaterialTextView uploadingStatusText;

    private String uploadedImagePath = "";

    private FirebaseFirestore firestore;

    private ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_influencer);

        initView();
        setup();
    }

    private void initView() {
        userBlockedLinear = (LinearLayout) findViewById(R.id.user_blocked_linear);
        userBlockedReasonText = (MaterialTextView) findViewById(R.id.user_blocked_reason_text);
        pickFromGalleryText = (MaterialTextView) findViewById(R.id.pick_from_gallery_text);
        image = (ImageView) findViewById(R.id.image);
        firstNameEdittext = (EditText) findViewById(R.id.first_name_edittext);
        lastNameEdittext = (EditText) findViewById(R.id.last_name_edittext);
        nickNameEdittext = (EditText) findViewById(R.id.nick_name_edittext);
        instagramUrlEdittext = (EditText) findViewById(R.id.instagram_url_edittext);
        tiktokUrlEdittext = (EditText) findViewById(R.id.tiktok_url_edittext);
        youtubeUrlEdittext = (EditText) findViewById(R.id.youtube_url_edittext);
        doneButton = (MaterialButton) findViewById(R.id.done_button);
        uploadingImageLinear = (LinearLayout) findViewById(R.id.uploading_image_linear);
        uploadingProgressBar = (ProgressBar) findViewById(R.id.uploading_progress_bar);
        uploadingStatusText = (MaterialTextView) findViewById(R.id.uploading_status_text);

        firestore = FirebaseFirestore.getInstance();

        loading = new ProgressDialog(AddInfluencerActivity.this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.setProgressStyle(ProgressDialog.STYLE_CIRCLE);
    }

    private void setup() {
        pickFromGalleryText.setOnClickListener(v -> imageChooser());

        image.setOnClickListener(v -> imageChooser());

        doneButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                new InfoDialog().MakeDialog(AddInfluencerActivity.this, "Każde zgłoszenie jest weryfikowane przed ostatecznym dodaniem. Wyraźne naruszanie zasad będzie skutkować okresowym lub permamentnym banem.", "Wyślij", "Anuluj", new InfoDialog.InfoDialogInterfaceListener() {
                    @Override
                    public void OnPositiveButtonClicked() {
                        loading.show();
                        sendInfluToModerators();
                    }

                    @Override
                    public void OnNegativeButtonClicked() {

                    }
                });
            }
        });
    }

    private void sendInfluToModerators() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        HashMap<String, Object> map = new HashMap<>();
        map.put("influencerUid", UidGenerator.Generate());
        map.put("influencerAddedBy", user.getUid());
        if (firstNameEdittext.getText().toString().isEmpty()) {
            map.put("influencerFirstName", "");
        } else {
            map.put("influencerFirstName", firstNameEdittext.getText().toString());
        }
        if (lastNameEdittext.getText().toString().isEmpty()) {
            map.put("influencerLastName", "");
        } else {
            map.put("influencerLastName", lastNameEdittext.getText().toString());
        }
        map.put("influencerNickName", nickNameEdittext.getText().toString());
        map.put("influencerDescription", "");
        map.put("influencerAvatar", uploadedImagePath);
        map.put("influencerInstagramLink", instagramUrlEdittext.getText().toString());
        map.put("influencerYouTubeLink", youtubeUrlEdittext.getText().toString());
        map.put("influencerTikTokLink", tiktokUrlEdittext.getText().toString());
        map.put("influencerModeratorUid", "");
        map.put("isVerified", "false");
        map.put("isPremium", "false");
        map.put("isHidden", "false");
        map.put("hasInstagram", !instagramUrlEdittext.getText().toString().isEmpty());
        map.put("hasYouTube", !youtubeUrlEdittext.getText().toString().isEmpty());
        map.put("hasTikTok", !tiktokUrlEdittext.getText().toString().isEmpty());
        map.put("isMaintained", "false");
        map.put("isUserFollowing", "true");
        map.put("influencerAddedAt", timestamp.getTime());
        map.put("maintainedTo", timestamp.getTime());
        map.put("viewsCount", 0);

        firestore.collection("moderation").document("influencers").collection("toModerate").add(map).addOnSuccessListener(documentReference -> {
            loading.dismiss();
            Toast.makeText(AddInfluencerActivity.this, "Pomyślnie wysłano!\nDecyzję dostaniesz w powiadomieniu.", Toast.LENGTH_LONG).show();
            finish();
        }).addOnFailureListener(e -> {

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
                    Uri filePath = result.getData().getData();
                    Glide.with(AddInfluencerActivity.this).load(filePath).into(image);
                    pickFromGalleryText.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);

                    if (!uploadedImagePath.isEmpty()) {
                        DeleteImage.Delete(AddInfluencerActivity.this, uploadedImagePath, new DeleteImage.DeleteTask() {
                            @Override
                            public void OnDeleted() {
                                Toast.makeText(AddInfluencerActivity.this, "Poprzednie zdjęcie zostało usunięte z bazy.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void OnError(String reason) {

                            }
                        });
                        uploadedImagePath = "";
                    }

                    uploadingImageLinear.setVisibility(View.VISIBLE);
                    new UploadImage(AddInfluencerActivity.this, StorageDirectory.INFLUENCER_PATH, filePath, new ImageUploadListener() {
                        @Override
                        public void OnImageUploaded(String imageUrl) {
                            uploadedImagePath = imageUrl;
                            uploadingImageLinear.setVisibility(View.GONE);
                        }

                        @Override
                        public void Progress(int value) {
                            uploadingProgressBar.setProgress(value);
                        }

                        @Override
                        public void OnError(String reason) {
                            uploadingProgressBar.setVisibility(View.GONE);
                            uploadingStatusText.setText(reason);
                        }
                    });
                }
            });
}
