package com.example.tripblog.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.Image;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.databinding.FragmentProfileBinding;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.adapter.PostViewPagerAdapter;
import com.example.tripblog.ui.editprofile.EditProfile;
import com.example.tripblog.ui.login.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    FragmentProfileBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PostViewPagerAdapter postViewPagerAdapter;
    private User loggedUser;
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
    
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
                        popupSetting.dismiss();
                        getActivity().finish();
                    }
                });
                Button editProfile =(Button) v.findViewById(R.id.editProfile);

                editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupSetting.dismiss();

                        Intent intent = new Intent(getActivity(), EditProfile.class);
                        activityResultLauncher.launch(intent);
//                        startActivity(intent);
//                        finishAfterTransition();
                    }
                });


            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();

        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(new PostViewPagerAdapter(getActivity()));

        TextView followingTxt = view.findViewById(R.id.followingTxt);
        tabLayout = view.findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout,viewPager, (tab, position) -> {
            switch(position) {
                case 0:
                    tab.setText("Public");
                    break;
                case 1:
                    tab.setText("Private");
                    break;
            }
        }).attach();

//        TextView username = view.findViewById(R.id.usernameTxt);
//        ImageView avatar = view.findViewById(R.id.avatar);
//
//        username.setText(loggedUser.getUserName());
////        Glide.with()
////                .load(list.get(position).getCoverImg())
////                .placeholder(R.drawable.da_lat)
////                .error(R.drawable.da_lat)
////                .into(img);
//        Glide.with(view)
//                .load(loggedUser.getAvatar())
//                .placeholder(R.drawable.da_lat)
//                .error(R.drawable.da_lat)
//                .into(avatar);


        followingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowDialogFragment fragment = new FollowDialogFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment.show(fragmentTransaction, "Detail followers");
//                fragmentTransaction.replace(R.id.frameLayout, fragment);
//                fragmentTransaction.commit();
            }
        });
    }

    private void loadData() {
        loggedUser = TripBlogApplication.getInstance().getLoggedUser();
        binding.usernameTxt.setText(loggedUser.getUserName());
        Glide.with(binding.getRoot())
                .load(loggedUser.getAvatar())
                .placeholder(R.drawable.da_lat)
                .error(R.drawable.da_lat)
                .into(binding.avatar);
    }

    public void updateUserData(User newUserData) {
        this.loggedUser = newUserData;
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

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 1 && result.getData() != null) {
                        Bundle data = result.getData().getExtras();
                        User user = (User) data.getSerializable("user");
                        TripBlogApplication.getInstance().setLoggedUser(user);
                        loadData();
                    }
                }
            }
    );
}