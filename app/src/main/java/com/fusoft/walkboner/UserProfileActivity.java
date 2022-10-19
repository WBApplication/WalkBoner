package com.fusoft.walkboner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fusoft.walkboner.adapters.recyclerview.PostsAdapter;
import com.fusoft.walkboner.adapters.recyclerview.ProfilePostsAdapter;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.ImageUploadListener;
import com.fusoft.walkboner.database.StorageDirectory;
import com.fusoft.walkboner.database.UploadImage;
import com.fusoft.walkboner.database.funcions.GetPosts;
import com.fusoft.walkboner.database.funcions.userProfile.GetUserLikedPosts;
import com.fusoft.walkboner.database.funcions.userProfile.GetUserPosts;
import com.fusoft.walkboner.database.funcions.userProfile.SetDescription;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.GetPathFromUri;
import com.fusoft.walkboner.views.Avatar;
import com.fusoft.walkboner.views.dialogs.ChangeProfileDescriptionDialog;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.sesl.tabs.SamsungTabLayout;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.widget.TabLayout;
import kr.co.prnd.readmore.ReadMoreTextView;

public class UserProfileActivity extends AppCompatActivity {
    private LinearLayout profileDetailsLoadingLinear;
    private LinearLayout profileDetailsLinear;
    private MaterialTextView userNameText;
    private ReadMoreTextView readMoreDescriptionText;
    private Avatar image;
    private LinearLayout loadingContentLinear;
    private LinearLayout contentLinear;
    private RecyclerView profileRecyclerView;
    private TabLayout profileTablayout;
    private MaterialButton editDescriptionButton;

    private static final int LIKED_POSTS = 0;
    private static final int YOUR_POSTS = 1;

    private String imageStoragePath = "";

    private ProgressDialog loading;

    private Authentication authentication;
    private FirebaseFirestore firestore;
    private GetUserLikedPosts likedPostsGetter;
    private GetUserPosts userPostsGetter;

    private ProfilePostsAdapter adapter;

    @Override
    protected void onDestroy() {
        authentication = null;
        loading = null;
        firestore = null;
        likedPostsGetter = null;
        userPostsGetter = null;
        adapter = null;

        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initView();
        setup();
    }

    private void initView() {
        likedPostsGetter = new GetUserLikedPosts();
        userPostsGetter = new GetUserPosts();

        profileDetailsLoadingLinear = (LinearLayout) findViewById(R.id.profile_details_loading_linear);
        profileDetailsLinear = (LinearLayout) findViewById(R.id.profile_details_linear);
        userNameText = (MaterialTextView) findViewById(R.id.user_name_text);
        readMoreDescriptionText = (ReadMoreTextView) findViewById(R.id.read_more_description_text);
        image = (Avatar) findViewById(R.id.image);
        loadingContentLinear = (LinearLayout) findViewById(R.id.loading_content_linear);
        contentLinear = (LinearLayout) findViewById(R.id.content_linear);
        profileRecyclerView = (RecyclerView) findViewById(R.id.profile_recycler_view);
        profileTablayout = (TabLayout) findViewById(R.id.profile_tablayout);
        editDescriptionButton = findViewById(R.id.edit_description_button);

        loading = new ProgressDialog(UserProfileActivity.this);
        loading.setIndeterminate(true);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        authentication = new Authentication(null);
        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                changeProfileDataLoadingState(false);
                userNameText.setText(user.getUserName());
                readMoreDescriptionText.setText(user.getUserDescription());

                if (user.isUserAdmin()) {
                    image.setAvatarOwnerPrivileges(Avatar.ADMIN);
                } else if (user.isUserModerator()) {
                    image.setAvatarOwnerPrivileges(Avatar.MODERATOR);
                } else {
                    image.setAvatarOwnerPrivileges(Avatar.USER);
                }

                if (!user.getUserAvatar().contentEquals("default")) {
                    image.setImageFromUrl(user.getUserAvatar());
                }
            }

            @Override
            public void OnUserNotFinded() {

            }

            @Override
            public void OnError(String reason) {

            }
        });
    }

    private void setup() {
        SamsungTabLayout.Tab likedPostsTab = profileTablayout.newTab().setText("Polubione Posty").setId(LIKED_POSTS);
        SamsungTabLayout.Tab yourPostsTab = profileTablayout.newTab().setText("Twoje Posty").setId(YOUR_POSTS);

        LinearLayoutManager layoutManager = new LinearLayoutManager(UserProfileActivity.this);
        profileRecyclerView.setLayoutManager(layoutManager);

        profileTablayout.addTab(yourPostsTab);
        profileTablayout.addTab(likedPostsTab);

        changeContent(YOUR_POSTS);

        profileTablayout.addOnTabSelectedListener(new SamsungTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(SamsungTabLayout.Tab tab) {
                changeContent(tab.getId());
            }

            @Override
            public void onTabUnselected(SamsungTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(SamsungTabLayout.Tab tab) {

            }
        });

        image.setOnClickListener(v -> {
            imageChooser();
        });

        editDescriptionButton.setOnClickListener(v -> {
            ChangeProfileDescriptionDialog.Show(UserProfileActivity.this, new ChangeProfileDescriptionDialog.ProfileDescriptionDialogListener() {
                @Override
                public void OnChange(String newDescription) {
                    loading.show();

                    SetDescription.Set(authentication.getCurrentUserUid(), newDescription, new SetDescription.OnDescriptionChanged() {
                        @Override
                        public void OnChanged() {
                            loading.dismiss();
                            readMoreDescriptionText.setText(newDescription);
                        }

                        @Override
                        public void OnError(String reason) {
                            loading.dismiss();
                            Toast.makeText(UserProfileActivity.this, reason, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void OnDismiss() {

                }
            });
        });
    }

    private void imageChooser() {
        activityResultLauncher.launch(ImagePicker.with(UserProfileActivity.this)
                .crop(1f, 1f)
                .provider(ImageProvider.GALLERY)
                .createIntent()
        );
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loading.show();
                    imageStoragePath = GetPathFromUri.getPath(UserProfileActivity.this, result.getData().getData());
                    image.setImageFromURI(result.getData().getData());

                    new UploadImage(UserProfileActivity.this, StorageDirectory.AVATARS_PATH, result.getData().getData(), new ImageUploadListener() {
                        @Override
                        public void OnImageUploaded(String imageUrl) {
                            firestore = FirebaseFirestore.getInstance();
                            firestore.collection("users").whereEqualTo("userUid", authentication.getCurrentUserUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("avatar", imageUrl);
                                    queryDocumentSnapshots.getDocuments().get(0).getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            de.dlyt.yanndroid.oneui.view.Toast.makeText(UserProfileActivity.this, "Zmieniono!", de.dlyt.yanndroid.oneui.view.Toast.LENGTH_SHORT).show();
                                            loading.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            de.dlyt.yanndroid.oneui.view.Toast.makeText(UserProfileActivity.this, "Błąd:\n" + e.getMessage(), de.dlyt.yanndroid.oneui.view.Toast.LENGTH_LONG).show();
                                            loading.dismiss();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    de.dlyt.yanndroid.oneui.view.Toast.makeText(UserProfileActivity.this, "Błąd:\n" + e.getMessage(), de.dlyt.yanndroid.oneui.view.Toast.LENGTH_LONG).show();
                                    loading.dismiss();
                                }
                            });
                        }

                        @Override
                        public void Progress(int value) {

                        }

                        @Override
                        public void OnError(String reason) {
                            de.dlyt.yanndroid.oneui.view.Toast.makeText(UserProfileActivity.this, "Błąd:\n" + reason, de.dlyt.yanndroid.oneui.view.Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    });
                }
            });

    private void changeContent(int id) {
        changeContentLoadingState(true);
        switch (id) {
            case LIKED_POSTS:
                likedPostsGetter.get(UserProfileActivity.this, new GetUserLikedPosts.UserLikedPostsListener() {
                    @Override
                    public void OnLoaded(List<Post> posts) {
                        changeContentLoadingState(false);
                        adapter = new ProfilePostsAdapter(UserProfileActivity.this, posts);
                        adapter.setHeartClickListener(new ProfilePostsAdapter.HeartClickListener() {
                            @Override
                            public void onHeartClick(int position) {
                                adapter.removeFromList(position);
                            }
                        });
                        profileRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void OnError(String error) {
                        Toast.makeText(UserProfileActivity.this, error, de.dlyt.yanndroid.oneui.view.Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case YOUR_POSTS:

                userPostsGetter.get(new GetPosts.PostsListener() {
                    @Override
                    public void OnLoaded(List<Post> posts) {
                        changeContentLoadingState(false);
                        adapter = new ProfilePostsAdapter(UserProfileActivity.this, posts);
                        profileRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void OnError(String error) {
                        Toast.makeText(UserProfileActivity.this, error, de.dlyt.yanndroid.oneui.view.Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private void changeProfileDataLoadingState(boolean on) {
        if (on) {
            profileDetailsLoadingLinear.setVisibility(View.VISIBLE);
            profileDetailsLinear.setVisibility(View.GONE);
        } else {
            profileDetailsLoadingLinear.setVisibility(View.GONE);
            profileDetailsLinear.setVisibility(View.VISIBLE);
        }
    }

    private void changeContentLoadingState(boolean on) {
        if (on) {
            contentLinear.setVisibility(View.GONE);
            loadingContentLinear.setVisibility(View.VISIBLE);
        } else {
            contentLinear.setVisibility(View.VISIBLE);
            loadingContentLinear.setVisibility(View.GONE);
        }
    }
}
