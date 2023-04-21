package com.example.tripshare.adapter.viewpaper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripshare.ui.fragments.FollowerFragment;
import com.example.tripshare.ui.fragments.FollowingFragment;


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
