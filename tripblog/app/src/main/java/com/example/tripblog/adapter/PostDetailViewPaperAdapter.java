package com.example.tripblog.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripblog.ui.fragments.OverviewFragment;
import com.example.tripblog.ui.fragments.ScheduleFragment;

public class PostDetailViewPaperAdapter extends FragmentStateAdapter {
    public PostDetailViewPaperAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new OverviewFragment();
            case 1: return new ScheduleFragment();

            default: return new OverviewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
