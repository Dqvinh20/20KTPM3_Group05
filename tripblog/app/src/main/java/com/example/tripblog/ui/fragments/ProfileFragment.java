package com.example.tripblog.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.UserService;
import com.example.tripblog.databinding.FragmentProfileBinding;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.adapter.PostViewPagerAdapter;
import com.example.tripblog.ui.editprofile.EditProfile;
import com.example.tripblog.ui.login.LoginActivity;
import com.example.tripblog.utils.NumberUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = ProfileFragment.class.getSimpleName();
    FragmentProfileBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private User currUser;
    private UserService userService;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int currUserId = 2;
    private List<User> followingList;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(int currUserId, boolean isMyProfile) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, currUserId);
        args.putBoolean(ARG_PARAM2, isMyProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            Bundle data = result.getData().getExtras();
                            User user = (User) data.getSerializable("user");
                            TripBlogApplication.getInstance().setLoggedUser(user);
                            updateUI();
                        }
                    }
                }
        );
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        userService = TripBlogApplication.createService(UserService.class);

        if (getArguments() != null) {
            currUserId = getArguments().getInt(ARG_PARAM1);
            Boolean isMyProfile = getArguments().getBoolean(ARG_PARAM2);
            binding.backBtn.setVisibility(isMyProfile != null && isMyProfile ? View.GONE : View.VISIBLE);
        }
        else {

            binding.backBtn.setVisibility(View.GONE);
        }

        binding.backBtn.setOnClickListener((view) -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        if(currUserId == TripBlogApplication.getInstance().getLoggedUser().getId()) {
            binding.followBtn.setVisibility(View.GONE);
            binding.moreSettingProfileBtn.setVisibility(View.VISIBLE);
        }
        else {
            binding.followBtn.setVisibility(View.VISIBLE);
            binding.moreSettingProfileBtn.setVisibility(View.GONE);
            userService.getUserFollowing(TripBlogApplication.getInstance().getLoggedUser().getId()).enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    if(response.isSuccessful()) {
                        JsonArray rawData = response.body().getAsJsonArray();
                        JsonObject jsonObject = (JsonObject) rawData.get(0);
                        JsonArray followingJsonArray = jsonObject.getAsJsonArray("followings");
                        Gson gson = new Gson();
                        Type userListType = new TypeToken<List<User>>() {}.getType();
                        followingList = gson.fromJson(followingJsonArray, userListType);

                        boolean isFollowing = followingList.parallelStream().anyMatch(user -> user.getId() == currUserId);
                        binding.followBtn.setChecked(isFollowing);
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        binding.followBtn.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                // Followed
                if (isChecked) {
                    button.setText("Unfollow");
                }
                // Not follow
                else {
                    button.setText("Follow");
                }
            }
        });
        binding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.followBtn.isChecked()) {
                    userService.followUser(currUserId).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {}

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } else {
                    userService.unfollowUser(currUserId).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });
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
                        TripBlogApplication.logout(getContext());
                        Intent loginPage = new Intent(getContext(), LoginActivity.class);
                        popupSetting.dismiss();
                        getActivity().finish();
                        startActivity(loginPage);
                    }
                });
                Button editProfile =(Button) v.findViewById(R.id.editProfile);
                editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupSetting.dismiss();
                        Intent intent = new Intent(getActivity(), EditProfile.class);
                        activityResultLauncher.launch(intent);
                    }
                });
            }
        });
        userService.getUserById(currUserId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    User user = response.body();
                    if (user == null) return;
                    loadData(user);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });

        binding.pager.setAdapter(new PostViewPagerAdapter(this, currUserId));
        binding.pager.setSaveEnabled(false);
        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            switch(position) {
                case 0:
                    tab.setText("Public");
                    break;
                case 1:
                    tab.setText("Private");
                    break;
            }
        }).attach();

        return binding.getRoot();
    }

    private void loadData(User user) {
        binding.usernameTxt.setText(user.getUserName());
        Glide.with(getContext())
                .load(user.getAvatar())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.avatar)
                .into(binding.avatar);

        binding.followerTxt.setText(NumberUtil.formatShorter(user.getFollowersCount()));
        binding.followingTxt.setText(NumberUtil.formatShorter(user.getFollowingsCount()));

        binding.followingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowDialogFragment fragment = FollowDialogFragment.newInstance(currUserId, user.getUserName(),1);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.frameLayout, fragment);
                fragmentTransaction.addToBackStack(FollowDialogFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });

        binding.followersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowDialogFragment fragment = FollowDialogFragment.newInstance(currUserId, user.getUserName(),0);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.frameLayout, fragment);
                fragmentTransaction.addToBackStack(FollowDialogFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });
    }

    private void updateUI() {
        currUser = TripBlogApplication.getInstance().getLoggedUser();
        binding.usernameTxt.setText(currUser.getUserName());
        Glide.with(binding.getRoot())
                .load(currUser.getAvatar())
                .placeholder(R.drawable.da_lat)
                .error(R.drawable.da_lat)
                .into(binding.avatar);
    }

    public void updateUserData(User newUserData) {
        this.currUser = newUserData;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount == 0) {
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments.size() == 1 && fragments.get(0) instanceof HomeFragment) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }
        }
//

    }

    @Override
    public void onClick(View view) {

    }
}