package com.fusoft.walkboner.adapters.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.fusoft.walkboner.fragments.CelebritiesFragment;
import com.fusoft.walkboner.fragments.HomeFragment;

import de.dlyt.yanndroid.oneui.sesl.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class MainViewPager extends FragmentStateAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();


    public MainViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new CelebritiesFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
