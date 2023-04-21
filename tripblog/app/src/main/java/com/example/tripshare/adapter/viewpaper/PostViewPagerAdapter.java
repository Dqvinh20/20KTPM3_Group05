package com.example.tripshare.adapter.viewpaper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripshare.ui.fragments.PrivatePlanFragment;
import com.example.tripshare.ui.fragments.PublicPlanFragment;


public class PostViewPagerAdapter extends FragmentStateAdapter {
    private int currUserId;

    public PostViewPagerAdapter(@NonNull Fragment fragment, int currUserId) {
        super(fragment);
        this.currUserId = currUserId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return PublicPlanFragment.newInstance(currUserId);
            case 1:
                return PrivatePlanFragment.newInstance(currUserId);
            default:
                return PublicPlanFragment.newInstance(currUserId);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
