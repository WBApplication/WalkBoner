package com.fusoft.walkboner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fusoft.walkboner.MainActivity;
import com.fusoft.walkboner.R;
import com.fusoft.walkboner.adapters.recyclerview.PostsAdapter;
import com.fusoft.walkboner.auth.Authentication;
import com.fusoft.walkboner.auth.UserInfoListener;
import com.fusoft.walkboner.database.funcions.GetPosts;
import com.fusoft.walkboner.utils.StartSnap;
import com.fusoft.walkboner.models.Post;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.settings.Settings;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.SnapHelper;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {

    View mRootView;

    private RecyclerView postsRecyclerView;
    private SwipeRefreshLayout homeSwipeRefreshLayout;

    private PostsAdapter adapter;

    private MainActivity mainActivity;

    private Authentication authentication;

    private Settings settings;

    @Override
    public void onDestroyView() {
        authentication = null;
        settings = null;
        mainActivity = null;
        adapter = null;
        mRootView = null;

        super.onDestroyView();
    }

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
        postsRecyclerView = (RecyclerView) mRootView.findViewById(R.id.posts_recycler_view);
        homeSwipeRefreshLayout = mRootView.findViewById(R.id.home_swipe_refresh_layout);
        settings = new Settings(getActivity());

        authentication = new Authentication(null);
        authentication.getUserData(new UserInfoListener() {
            @Override
            public void OnUserDataReceived(User user) {
                if (user.isShowFirstTimeTip()) {
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postsRecyclerView.setLayoutManager(layoutManager);
        postsRecyclerView.setNestedScrollingEnabled(true);
        postsRecyclerView.seslSetGoToTopEnabled(true);
        postsRecyclerView.seslShowGoToTopEdge(400);

        if (settings.shouldSnapPosts()) {
            SnapHelper linearSnapHelper = new StartSnap();
            linearSnapHelper.attachToRecyclerView(postsRecyclerView);
        }

        loadPosts();
        loadPopularPosts();

        homeSwipeRefreshLayout.setOnRefreshListener(() -> {
            loadPosts();
            loadPopularPosts();
        });

//        myProfileButton.setOnClickListener(v -> {
//            mainActivity.openActivity(UserProfileActivity.class, false);
//        });
//
//        celebrityButton.setOnClickListener(v -> {
//            mainActivity.changePage(1);
//        });
//
//        createPostButton.setOnClickListener(view -> {
//            if (settings.showCreatingPostDisclaimer()) {
//                new DisclaimerDialog().Dialog(getActivity(), "Tworz Posty!", "Dziel sie z uzytkownikami swoimi zdjeciami influencerek lub innych osob!\nPamietaj jednak o przestrzeganiu zasad, poniewaz nie zastosowanie sie do nich moze zakonczyc sie banem!", new DisclaimerDialog.DisclaimerDialogInterface() {
//                    @Override
//                    public void OnDismiss() {
//                        settings.disableCreatingPostDisclaimer();
//                        mainActivity.openActivity(CreatePostActivity.class, false);
//                    }
//                });
//            } else {
//                mainActivity.openActivity(CreatePostActivity.class, false);
//            }
//        });
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

    }
}
