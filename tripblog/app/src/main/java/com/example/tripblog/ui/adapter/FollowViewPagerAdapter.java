package com.example.tripblog.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripblog.ui.fragments.FollowerFragment;
import com.example.tripblog.ui.fragments.FollowingFragment;


public class FollowViewPagerAdapter extends FragmentStateAdapter {

    public FollowViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new FollowerFragment();
            case 1:
                return new FollowingFragment();
            default:
                return new FollowerFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
