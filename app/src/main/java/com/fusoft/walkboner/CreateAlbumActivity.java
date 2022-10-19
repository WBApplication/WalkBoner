package com.fusoft.walkboner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.adapters.recyclerview.MediaPickerAdapter;
import com.fusoft.walkboner.database.ImageUploadListener;
import com.fusoft.walkboner.database.StorageDirectory;
import com.fusoft.walkboner.database.UploadImage;
import com.fusoft.walkboner.utils.GetPathFromUri;
import com.fusoft.walkboner.utils.UidGenerator;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.GridLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;

public class CreateAlbumActivity extends AppCompatActivity {
    private MediaPickerAdapter adapter;
    private ArrayList<HashMap<String, Object>> imagesList = new ArrayList<>();
    List<String> imagesUrls = new ArrayList<>();

    private RecyclerView mediaPickerRecyclerView;

    private boolean replaceImage = false;
    private int replaceAt = 0;
    int a = 0;
    int b = 0;

    private RecyclerView medaPickerRecyclerView;
    private EditText albumTitleEdittext;
    private EditText albumDescriptionEdittext;
    private MaterialButton addAlbumButton;

    private FirebaseFirestore database;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        initView();
        setup();
    }

    private void initView() {
        database = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        mediaPickerRecyclerView = (RecyclerView) findViewById(R.id.meda_picker_recycler_view);
        medaPickerRecyclerView = (RecyclerView) findViewById(R.id.meda_picker_recycler_view);
        albumTitleEdittext = (EditText) findViewById(R.id.album_title_edittext);
        albumDescriptionEdittext = (EditText) findViewById(R.id.album_description_edittext);
        addAlbumButton = (MaterialButton) findViewById(R.id.add_album_button);
    }

    private void setup() {
        GridLayoutManager layoutManager = new GridLayoutManager(CreateAlbumActivity.this, 20, GridLayoutManager.VERTICAL, true);
        adapter = new MediaPickerAdapter(CreateAlbumActivity.this, imagesList);

        adapter.setOnClickListener(new MediaPickerAdapter.OnItemClickListener() {
            @Override
            public void OnClick(int type, int position) {
                if (type == 0) {
                    imageChooser();
                } else {

                }
            }

            @Override
            public void OnLockImageClick(int position) {

            }

            @Override
            public void OnReplaceImageClick(int position) {
                replaceImage = true;
                replaceAt = position;
                imageChooser();
            }

            @Override
            public void OnDeleteImageClick(int position) {
                validate();
            }
        });

        albumTitleEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validate();
            }
        });

        mediaPickerRecyclerView.setLayoutManager(layoutManager);
        mediaPickerRecyclerView.setAdapter(adapter);

        addAlbumButton.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(CreateAlbumActivity.this);
            progressDialog.setTitle("Wgrywanie Zdjęć");
            progressDialog.setMessage("Postęp (0/" + (adapter.getItemCount() - 1) + ")");
            progressDialog.show();

            for (HashMap<String, Object> imageDetails : adapter.getList()) {
                progressDialog.setIndeterminate(true);

                new UploadImage(CreateAlbumActivity.this, StorageDirectory.ALBUMS_PATH, (Uri) imageDetails.get("imagePath"), new ImageUploadListener() {
                    @Override
                    public void OnImageUploaded(String imageUrl) {
                        imagesUrls.add(imageUrl);
                        a++;
                        progressDialog.setMessage("Postęp (" + a + "/" + (adapter.getItemCount() - 1) + ")");

                        if (imagesUrls.size() == adapter.getItemCount() - 1) {
                            progressDialog.setTitle("Tworzenie Albumu");
                            progressDialog.setMessage("To nie potrwa długo");
                            uploadToDatabase();
                        }
                    }

                    @Override
                    public void Progress(int value) {

                    }

                    @Override
                    public void OnError(String reason) {

                    }
                });
            }
        });
    }

    private void uploadToDatabase() {
        Log.d("CreateAlbumAct", "Uploaded All Images");

        database.collection("influencers").whereEqualTo("influencerUid", getIntent().getStringExtra("influencerUid")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("albumUid", UidGenerator.Generate());
                map.put("albumName", albumTitleEdittext.getText().toString());
                map.put("albumDescription", albumDescriptionEdittext.getText().toString());
                map.put("albumMainImage", imagesUrls.get(0));
                map.put("createdBy", user.getUid());
                map.put("mediaAmount", imagesUrls.size());
                map.put("isPremium", false);

                queryDocumentSnapshots.getDocuments().get(0).getReference().collection("albums").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        for (String url : imagesUrls) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageUrl", url);
                            map.put("imageUid", UidGenerator.Generate());

                            queryDocumentSnapshots.getDocuments().get(0).getReference().collection("albums").document(documentReference.getId()).collection("images").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    b++;

                                    if (b == imagesUrls.size()) {
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void imageChooser() {
        String[] mimeTypes = {"image/*", "video/*"};

        Intent i = new Intent();
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        i.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(i);
    }

    private void validate() {
        addAlbumButton.setEnabled(!albumTitleEdittext.getText().toString().isEmpty() && adapter.getItemCount() - 1 != 0);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri filePath = result.getData().getData();
                    String imagePath = GetPathFromUri.getPath(CreateAlbumActivity.this, filePath);

                    if (replaceImage) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imagePath", filePath);
                        map.put("isLocked", false);
                        adapter.replaceImage(map, replaceAt);
                    } else {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imagePath", filePath);
                        map.put("isLocked", false);
                        adapter.addImage(map);
                        validate();
                    }

                    replaceImage = false;
                }

                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    replaceImage = false;
                }
            });
}
