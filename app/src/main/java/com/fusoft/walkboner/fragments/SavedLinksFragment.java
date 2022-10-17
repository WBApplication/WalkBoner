package com.fusoft.walkboner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fusoft.walkboner.R;

import de.dlyt.yanndroid.oneui.view.RecyclerView;

public class SavedLinksFragment extends Fragment {

    private View mRootView;

    private RecyclerView savedLinksRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_saved_links, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        // setup();
    }

    private void initView() {
        savedLinksRecyclerView = (RecyclerView) mRootView.findViewById(R.id.saved_links_recycler_view);
    }
}
