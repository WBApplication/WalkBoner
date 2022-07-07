package com.fusoft.walkboner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fusoft.walkboner.adapters.recyclerview.PopularPostsAdapter;
import com.fusoft.walkboner.adapters.recyclerview.PostsAdapter;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.funcions.GetPopularPosts;
import com.fusoft.walkboner.database.funcions.GetPosts;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.utils.AnimateChanges;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.dlyt.yanndroid.oneui.sesl.recyclerview.GridLayoutManager;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.widget.RoundLinearLayout;
import de.dlyt.yanndroid.oneui.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {

    View mRootView;

    private ImageView hotPersonImage;
    private MaterialTextView hotPersonViewCountText;
    private MaterialTextView hotPersonNameText;
    private MaterialButton myProfileButton;
    private MaterialButton celebrityButton;
    private MaterialButton onlyfansLeaksButton;
    private MaterialButton hotTiktoksButton;
    private MaterialButton createPostButton;
    private RecyclerView postsRecyclerView, popularPostsRecyclerView;
    private SwipeRefreshLayout homeSwipeRefreshLayout;

    private PostsAdapter adapter;
    private PopularPostsAdapter popularPostsAdapter;

    private MainActivity mainActivity;

    private Authentication authentication;
    private RoundLinearLayout tipLinear;
    private MaterialTextView closeTipButton;
    private MaterialTextView openTipButton;
    private LinearLayout mainLinear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        setup();
    }

    private void initView() {
        hotPersonImage = (ImageView) mRootView.findViewById(R.id.hot_person_image);
        hotPersonViewCountText = (MaterialTextView) mRootView.findViewById(R.id.hot_person_view_count_text);
        hotPersonNameText = (MaterialTextView) mRootView.findViewById(R.id.hot_person_name_text);
        myProfileButton = (MaterialButton) mRootView.findViewById(R.id.my_profile_button);
        celebrityButton = (MaterialButton) mRootView.findViewById(R.id.celebrity_button);
        onlyfansLeaksButton = (MaterialButton) mRootView.findViewById(R.id.onlyfans_leaks_button);
        hotTiktoksButton = (MaterialButton) mRootView.findViewById(R.id.hot_tiktoks_button);
        postsRecyclerView = (RecyclerView) mRootView.findViewById(R.id.posts_recycler_view);
        createPostButton = mRootView.findViewById(R.id.create_post_button);
        homeSwipeRefreshLayout = mRootView.findViewById(R.id.home_swipe_refresh_layout);
        popularPostsRecyclerView = mRootView.findViewById(R.id.popular_posts_recycler_view);
        tipLinear = (RoundLinearLayout) mRootView.findViewById(R.id.tip_linear);
        closeTipButton = (MaterialTextView) mRootView.findViewById(R.id.close_tip_button);
        openTipButton = (MaterialTextView) mRootView.findViewById(R.id.open_tip_button);
        mainLinear = (LinearLayout) mRootView.findViewById(R.id.main_linear);
        AnimateChanges.forLinear(tipLinear);

        authentication = new Authentication(null);
        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                if (user.isShowFirstTimeTip()) {
                    tipLinear.setVisibility(View.VISIBLE);
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
        mainActivity = ((MainActivity) getActivity());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        popularPostsRecyclerView.setLayoutManager(gridLayoutManager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postsRecyclerView.setLayoutManager(layoutManager);

        loadPosts();
        loadPopularPosts();

        homeSwipeRefreshLayout.setOnRefreshListener(() -> {
            loadPosts();
            loadPopularPosts();
        });

        myProfileButton.setOnClickListener(v -> {
            mainActivity.openActivity(UserProfileActivity.class, false);
        });

        celebrityButton.setOnClickListener(v -> {
            mainActivity.changePage(1);
        });

        createPostButton.setOnClickListener(view -> {
            mainActivity.openActivity(CreatePostActivity.class, false);
        });
    }

    private void loadPosts() {
        GetPosts.get(new GetPosts.PostsListener() {
            @Override
            public void OnLoaded(List<Post> posts) {
                if (homeSwipeRefreshLayout.isRefreshing()) {
                    homeSwipeRefreshLayout.setRefreshing(false);
                }

                //Collections.reverse(posts);
                adapter = new PostsAdapter(getActivity(), posts);
                postsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void OnError(String error) {

            }
        });
    }

    private void loadPopularPosts() {
        GetPopularPosts.get(new GetPopularPosts.PostsListener() {
            @Override
            public void OnLoaded(List<Post> posts) {
                popularPostsAdapter = new PopularPostsAdapter(getActivity(), posts);
                popularPostsRecyclerView.setAdapter(popularPostsAdapter);
            }

            @Override
            public void OnError(String error) {

            }
        }, 3);
    }
}
