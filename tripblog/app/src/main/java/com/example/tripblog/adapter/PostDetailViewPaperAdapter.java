package com.example.tripblog.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripblog.model.Schedule;
import com.example.tripblog.ui.fragments.OverviewFragment;
import com.example.tripblog.ui.fragments.ScheduleFragment;

import java.io.Serializable;
import java.util.List;

public class PostDetailViewPaperAdapter extends FragmentStateAdapter {

    OverviewFragment overviewFragment;
    ScheduleFragment scheduleFragment;

    private boolean isEditable = false;

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public PostDetailViewPaperAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: {
                if (scheduleFragment == null) onCreateScheduleFragment(null);
                return scheduleFragment;
            }
            default: {
                if (overviewFragment == null) onCreateOverviewFragment(null);
                return overviewFragment;
            }
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public final void onCreateScheduleFragment(List<Schedule> scheduleList){
        if (scheduleList == null) return;

        Bundle args = new Bundle();
        args.putSerializable("schedules",(Serializable) scheduleList);
        args.putBoolean("isEditable", isEditable);
        scheduleFragment = ScheduleFragment.newInstance(args);
    }

    public final void onCreateOverviewFragment(Bundle args) {
        if (overviewFragment != null) return;
        overviewFragment = overviewFragment.newInstance();
    }
}
