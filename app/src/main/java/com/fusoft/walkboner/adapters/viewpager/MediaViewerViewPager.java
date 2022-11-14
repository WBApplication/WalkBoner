package com.fusoft.walkboner.adapters.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.fusoft.walkboner.fragments.MediaFragment;
import com.fusoft.walkboner.models.album.AlbumImage;

import java.util.List;

import de.dlyt.yanndroid.oneui.sesl.viewpager2.adapter.FragmentStateAdapter;

public class MediaViewerViewPager extends FragmentStateAdapter {
    private List<AlbumImage> imagesUrls;

    public MediaViewerViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<AlbumImage> imagesUrls) {
        super(fragmentManager, lifecycle);
        this.imagesUrls = imagesUrls;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MediaFragment.newInstance(imagesUrls.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return imagesUrls.size();
    }
}