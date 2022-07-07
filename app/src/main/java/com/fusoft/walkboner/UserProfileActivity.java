package com.fusoft.walkboner;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusoft.walkboner.adapters.recyclerview.PostsAdapter;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.funcions.GetPosts;
import com.fusoft.walkboner.database.funcions.userProfile.GetUserLikedPosts;
import com.fusoft.walkboner.database.funcions.userProfile.GetUserPosts;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

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
    private ImageView image;
    private LinearLayout loadingContentLinear;
    private LinearLayout contentLinear;
    private RecyclerView profileRecyclerView;
    private TabLayout profileTablayout;

    private static final int LIKED_POSTS = 0;
    private static final int YOUR_POSTS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initView();
        setup();
    }

    private void initView() {
        profileDetailsLoadingLinear = (LinearLayout) findViewById(R.id.profile_details_loading_linear);
        profileDetailsLinear = (LinearLayout) findViewById(R.id.profile_details_linear);
        userNameText = (MaterialTextView) findViewById(R.id.user_name_text);
        readMoreDescriptionText = (ReadMoreTextView) findViewById(R.id.read_more_description_text);
        image = (ImageView) findViewById(R.id.image);
        loadingContentLinear = (LinearLayout) findViewById(R.id.loading_content_linear);
        contentLinear = (LinearLayout) findViewById(R.id.content_linear);
        profileRecyclerView = (RecyclerView) findViewById(R.id.profile_recycler_view);
        profileTablayout = (TabLayout) findViewById(R.id.profile_tablayout);

        Authentication authentication = new Authentication(null);
        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                changeProfileDataLoadingState(false);
                userNameText.setText(user.getUserName());
                readMoreDescriptionText.setText(user.getUserDescription());
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

        profileTablayout.addTab(likedPostsTab);
        profileTablayout.addTab(yourPostsTab);

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
    }

    private void changeContent(int id) {
        changeContentLoadingState(true);
        switch (id) {
            case LIKED_POSTS:
                new GetUserLikedPosts().get(UserProfileActivity.this, new GetUserLikedPosts.UserLikedPostsListener() {
                    @Override
                    public void OnLoaded(List<Post> posts) {
                        changeContentLoadingState(false);
                        PostsAdapter adapter = new PostsAdapter(UserProfileActivity.this, posts);
                        profileRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void OnError(String error) {
                        Toast.makeText(UserProfileActivity.this, error, de.dlyt.yanndroid.oneui.view.Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case YOUR_POSTS:
                new GetUserPosts().get(new GetPosts.PostsListener() {
                    @Override
                    public void OnLoaded(List<Post> posts) {
                        changeContentLoadingState(false);
                        PostsAdapter adapter = new PostsAdapter(UserProfileActivity.this, posts);
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
