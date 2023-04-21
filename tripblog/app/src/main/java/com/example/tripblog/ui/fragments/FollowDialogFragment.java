package com.example.tripblog.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tripblog.databinding.DialogFollowBinding;
import com.example.tripblog.adapter.viewpaper.FollowViewPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class FollowDialogFragment extends DialogFragment {
    private static final String TAG = FollowDialogFragment.class.getSimpleName();
    DialogFollowBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private int tabPosition;
    private int currUserId;
    private String currUserName;

    public FollowDialogFragment() {}

    public FollowDialogFragment(String currUserName, int tabPosition) {
        // Required empty public constructor
        this.tabPosition = tabPosition;
        this.currUserName = currUserName;
    }

    public static FollowDialogFragment newInstance(int currUserId, String currUserName, int tabPosition) {
        FollowDialogFragment fragment = new FollowDialogFragment(currUserName, tabPosition);
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, currUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            currUserId = getArguments().getInt(ARG_PARAM1);
        }
        binding = DialogFollowBinding.inflate(inflater, container, false);
        binding.pager.setAdapter(new FollowViewPagerAdapter(this, currUserId));

        if(tabPosition == 1){
            binding.pager.setCurrentItem(tabPosition);
        }

        new TabLayoutMediator(binding.tabLayout, binding.pager, true, (tab, position) -> {
            switch(position) {
                case 0:
                    tab.setText("Followers");
                    break;
                case 1:
                    tab.setText("Following");
                    break;
            }
        }).attach();
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        binding.nameTxt.setText(currUserName);
        return binding.getRoot();
    }
}