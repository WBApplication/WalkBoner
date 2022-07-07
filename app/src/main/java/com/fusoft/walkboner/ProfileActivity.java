package com.fusoft.walkboner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.Profile;
import com.fusoft.walkboner.views.dialogs.ErrorDialog;
import com.google.android.material.textview.MaterialTextView;

import de.dlyt.yanndroid.oneui.dialog.ProgressDialog;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.sesl.tabs.SamsungTabLayout;
import de.dlyt.yanndroid.oneui.view.ViewPager2;
import de.dlyt.yanndroid.oneui.widget.TabLayout;
import kr.co.prnd.readmore.ReadMoreTextView;

public class ProfileActivity extends AppCompatActivity {
    private ToolbarLayout profileToolbar;
    private ImageView profileAvatarImage;
    private MaterialTextView profileNicknameText;
    private ImageView verifiedImage;
    private ReadMoreTextView profileDescriptionText;
    private MaterialTextView bannedText;
    private ViewPager2 profileViewpager;
    private TabLayout profileTablayout;

    private ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        setup();
        setupTabLayout();
    }

    private void initView() {
        profileToolbar = (ToolbarLayout) findViewById(R.id.profileToolbar);
        profileAvatarImage = (ImageView) findViewById(R.id.profile_avatar_image);
        profileNicknameText = (MaterialTextView) findViewById(R.id.profile_nickname_text);
        verifiedImage = (ImageView) findViewById(R.id.verified_image);
        profileDescriptionText = (ReadMoreTextView) findViewById(R.id.profile_description_text);
        bannedText = (MaterialTextView) findViewById(R.id.banned_text);
        bannedText.setVisibility(View.GONE);
        profileViewpager = (ViewPager2) findViewById(R.id.profile_viewpager);
        profileTablayout = (TabLayout) findViewById(R.id.profile_tablayout);

        loading = new ProgressDialog(ProfileActivity.this);
        loading.setProgressStyle(ProgressDialog.STYLE_CIRCLE);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.show();
    }

    private void setup() {
        Profile profile = new Profile();
        Log.e("ProfileActivity", "UserUID: " + getIntent().getExtras().getString("userUid"));
        profile.GetProfileForUser(getIntent().getExtras().getString("userUid"), new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                Log.e("ProfileActivity", "DataReceived");
                loading.dismiss();

                if (!user.getUserAvatar().contentEquals("default")) {
                    Glide.with(ProfileActivity.this).load(user.getUserAvatar()).into(profileAvatarImage);
                }

                if (user.isUserBanned()) {
                    bannedText.setVisibility(View.VISIBLE);
                }

                profileNicknameText.setText(user.getUserName());
                profileDescriptionText.setText(user.getUserDescription());

            }

            @Override
            public void OnUserNotFinded() {

            }

            @Override
            public void OnError(String reason) {
                loading.dismiss();
                Log.e("ProfileActivity", "OnError");
                new ErrorDialog().ErrorDialog(ProfileActivity.this, reason, () -> finish());
            }
        });
    }

    private void setupTabLayout() {
        SamsungTabLayout.Tab posts = profileTablayout.newTab().setText("Posty").setId(0);
        SamsungTabLayout.Tab albums = profileTablayout.newTab().setText("Albumy").setId(1);

        profileTablayout.addTab(posts);
        profileTablayout.addTab(albums);
    }
}
