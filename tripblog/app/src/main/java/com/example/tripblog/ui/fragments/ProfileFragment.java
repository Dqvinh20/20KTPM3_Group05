package com.example.tripblog.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.databinding.FragmentProfileBinding;
import com.example.tripblog.ui.ViewPagerAdapter;
import com.example.tripblog.ui.login.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    FragmentProfileBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        binding.moreSettingProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getLayoutInflater().inflate(R.layout.setting_profile_layout,null);
                BottomSheetDialog popupSetting = new BottomSheetDialog(getContext());
                popupSetting.setContentView(v);
                popupSetting.show();

                Button logout = (Button) v.findViewById(R.id.logout);
                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Logout", 0).show();

                        TripBlogApplication.logout(getContext());
                        Intent loginPage = new Intent(getContext(), LoginActivity.class);
                        startActivity(loginPage);
                        getActivity().finish();
                    }
                });
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter(getActivity()));

        tabLayout = view.findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout,viewPager, (tab, position) -> {
            switch(position) {
                case 0:
                    tab.setText("Private");
                    break;
                case 1:
                    tab.setText("Public");
                    break;
            }
        }).attach();


//        ListView planList = (ListView) view.findViewById(R.id.userList);
//        planList.setAdapter(new PlanListAdapter(getContext(), R.layout.plan_item, name, img));

    }

    @Override
    public void onDestroy() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logout) {
        }
    }
}