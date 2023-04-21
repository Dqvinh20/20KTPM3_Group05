package com.example.tripblog.adapter.viewpaper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripblog.ui.fragments.FollowerFragment;
import com.example.tripblog.ui.fragments.FollowingFragment;


public class FollowViewPagerAdapter extends FragmentStateAdapter {

    private int currUserId;
    public FollowViewPagerAdapter(@NonNull Fragment fragment, int currUserId) {
        super(fragment);
        this.currUserId = currUserId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return FollowerFragment.newInstance(currUserId);
            case 1:
                return FollowingFragment.newInstance(currUserId);
            default:
                return FollowerFragment.newInstance(currUserId);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
