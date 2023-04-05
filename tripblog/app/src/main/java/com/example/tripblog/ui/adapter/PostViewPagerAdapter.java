package com.example.tripblog.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripblog.ui.fragments.PrivatePlanFragment;
import com.example.tripblog.ui.fragments.PublicPlanFragment;


public class PostViewPagerAdapter extends FragmentStateAdapter {


    public PostViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new PublicPlanFragment();
            case 1:
                return new PrivatePlanFragment();
            default:
                return new PublicPlanFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
