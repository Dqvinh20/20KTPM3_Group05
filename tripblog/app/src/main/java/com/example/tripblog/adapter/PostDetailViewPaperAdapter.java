package com.example.tripblog.adapter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripblog.ui.fragments.OverviewFragment;
import com.example.tripblog.ui.fragments.ScheduleFragment;

public class PostDetailViewPaperAdapter extends FragmentStateAdapter {
    private final String TAG = PostDetailViewPaperAdapter.class.getSimpleName();
    private OverviewFragment overviewFragment = new OverviewFragment();
    private ScheduleFragment scheduleFragment = new ScheduleFragment();
    private boolean isEditable = false;

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        overviewFragment.setEditable(isEditable);
        scheduleFragment.setEditable(isEditable);
    }

    public PostDetailViewPaperAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return scheduleFragment;
        }

        return overviewFragment;
    }

    public void refreshFragmentData(int position, Bundle args) {
        Log.e(TAG + "2 " + position, String.valueOf(isEditable));


        if (position == 1) {
            scheduleFragment.setArguments(args);
            scheduleFragment.refresh();
        }
        else {
            overviewFragment.setArguments(args);
            overviewFragment.refresh();
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public Fragment get(int position) {
        if (position == 1) {
            return scheduleFragment;
        }
        return overviewFragment;
    }
}
