package com.fusoft.walkboner.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.fusoft.walkboner.AddInfluencerActivity;
import com.fusoft.walkboner.MainActivity;
import com.fusoft.walkboner.PersonAlbumsActivity;
import com.fusoft.walkboner.R;
import com.fusoft.walkboner.adapters.recyclerview.InfluencersAdapter;
import com.fusoft.walkboner.database.funcions.GetInfluencers;
import com.fusoft.walkboner.database.funcions.InfluencersListener;
import com.fusoft.walkboner.models.Influencer;
import com.fusoft.walkboner.utils.AnimateChanges;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.List;

import de.dlyt.yanndroid.oneui.sesl.recyclerview.GridLayoutManager;
import de.dlyt.yanndroid.oneui.sesl.recyclerview.LinearLayoutManager;
import de.dlyt.yanndroid.oneui.view.RecyclerView;
import de.dlyt.yanndroid.oneui.view.Toast;
import de.dlyt.yanndroid.oneui.widget.ProgressBar;
import de.dlyt.yanndroid.oneui.widget.RoundLinearLayout;
import de.dlyt.yanndroid.oneui.widget.SwipeRefreshLayout;

public class CelebritiesFragment extends Fragment {

    View mRootView;
    private RecyclerView celebritiesRecyclerView;
    private LinearLayout influencersLinear;
    private LottieAnimationView lottieAnim;
    private InfluencersAdapter adapter;
    private RoundLinearLayout addInfluencerButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdView adView;

    private MainActivity activity;
    private LinearLayout mainLinear, adLinear;
    private ProgressBar loadingProgressBar;

    @Override
    public void onDestroyView() {
        activity = null;
        loadingProgressBar = null;
        mRootView = null;
        adapter = null;

        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_celebrities, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        setup();
    }

    private void initView() {
        celebritiesRecyclerView = (RecyclerView) mRootView.findViewById(R.id.celebrities_recycler_view);
        influencersLinear = (LinearLayout) mRootView.findViewById(R.id.influencers_linear);
        lottieAnim = (LottieAnimationView) mRootView.findViewById(R.id.lottie_anim);
        addInfluencerButton = (RoundLinearLayout) mRootView.findViewById(R.id.add_influencer_button);
        mainLinear = (LinearLayout) mRootView.findViewById(R.id.main_linear);
        AnimateChanges.forLinear(mainLinear);
        loadingProgressBar = (ProgressBar) mRootView.findViewById(R.id.loading_progress_bar);
        swipeRefreshLayout = mRootView.findViewById(R.id.influencers_swipe_refresh);
        adLinear = mRootView.findViewById(R.id.ad_linear);

        adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-5168974918674183/1577192356");
        adView.setVisibility(View.GONE);
        adLinear.addView(adView);

        AdRequest adReq = new AdRequest.Builder().build();
        adView.loadAd(adReq);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        celebritiesRecyclerView.setLayoutManager(layoutManager);

        loadData();

        swipeRefreshLayout.setOnRefreshListener(() -> loadData());

        addInfluencerButton.setOnClickListener(v -> openActivity());
    }

    private void loadData() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        celebritiesRecyclerView.setVisibility(View.GONE);
        addInfluencerButton.setVisibility(View.GONE);
        celebritiesRecyclerView.setAdapter(null);

        GetInfluencers getter = new GetInfluencers(GetInfluencers.ORDER_MOST_VIEWED, new InfluencersListener() {
            @Override
            public void OnDataReceived(List<Influencer> influencers) {
                Log.e("CelebritiesFrag", "Data Received\nInfluencers: " + influencers.size());
                adapter = new InfluencersAdapter(getActivity(), influencers);
                celebritiesRecyclerView.setAdapter(adapter);
                loadingProgressBar.setVisibility(View.GONE);
                celebritiesRecyclerView.setVisibility(View.VISIBLE);
                addInfluencerButton.setVisibility(View.VISIBLE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                adapter.setClickListener(new InfluencersAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(Influencer influencer, int position) {
                        Intent intent = new Intent(getActivity(), PersonAlbumsActivity.class);
                        intent.putExtra("influencerNick", influencer.getInfluencerNickName());
                        intent.putExtra("influencerFullName", influencer.getInfluencerFirstName() + " " + influencer.getInfluencerLastName());
                        intent.putExtra("influencerAvatar", influencer.getInfluencerAvatar());
                        intent.putExtra("influencerUid", influencer.getInfluencerUid());
                        intent.putExtra("influencerYouTube", influencer.getInfluencerYouTubeLink());
                        intent.putExtra("influencerInstagram", influencer.getInfluencerInstagramLink());
                        startActivity(intent);
                        intent = null;
                    }
                });
            }

            @Override
            public void OnError(String reason) {

            }
        });
    }

    private void openActivity() {
        ((MainActivity) getActivity()).openActivity(AddInfluencerActivity.class, false);
    }

    private void setup() {
        animateIntro();
    }

    private void animateIntro() {
        influencersLinear.animate().alpha(1.0f).translationY(0.0f).scaleY(1.0f).scaleX(1.0f).setDuration(1500).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                lottieAnim.animate().alpha(0.0f).setDuration(1500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                influencersLinear.animate().alpha(0.0f).setDuration(1500).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        celebritiesRecyclerView.setVisibility(View.VISIBLE);
                        celebritiesRecyclerView.animate().alpha(1.0f).setDuration(600).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    }
                }).start();
            }
        }).start();
    }
}
