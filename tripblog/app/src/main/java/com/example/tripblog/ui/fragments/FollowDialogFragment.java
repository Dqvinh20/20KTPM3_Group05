package com.example.tripblog.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.databinding.DialogFollowBinding;
import com.example.tripblog.ui.adapter.FollowViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FollowDialogFragment extends DialogFragment {
    DialogFollowBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FollowViewPagerAdapter followViewPagerAdapter;
    public FollowDialogFragment() {
        // Required empty public constructor
    }

    public static FollowDialogFragment newInstance() {
        FollowDialogFragment fragment = new FollowDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DialogFollowBinding.inflate(inflater, container, false);
        binding.pager.setAdapter(new FollowViewPagerAdapter(getActivity()));
        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            switch(position) {
                case 0:
                    tab.setText("Followers");
                    break;
                case 1:
                    tab.setText("Following");
                    break;
            }
        }).attach();

//        binding.tabLayout.getTabAt(1).select();
        binding.tabLayout.setScrollPosition(1, 0, true);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return binding.getRoot();
    }
}