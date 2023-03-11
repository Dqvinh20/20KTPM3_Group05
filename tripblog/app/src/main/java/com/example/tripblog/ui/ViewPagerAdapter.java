package com.example.tripblog.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripblog.ui.fragments.PlanObjectFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new PlanObjectFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(PlanObjectFragment.ARG_OBJECT, position + 1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
