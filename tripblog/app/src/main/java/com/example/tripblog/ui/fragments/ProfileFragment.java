package com.example.tripblog.ui.fragments;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;

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

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.adapter.PostViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PostViewPagerAdapter postViewPagerAdapter;
    private User loggedUser = TripBlogApplication.getInstance().getLoggedUser();
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView username = view.findViewById(R.id.usernameTxt);
        username.setText(loggedUser.getUserName());
        ImageView avatar = view.findViewById(R.id.avatar);
//        Glide.with()
//                .load(list.get(position).getCoverImg())
//                .placeholder(R.drawable.da_lat)
//                .error(R.drawable.da_lat)
//                .into(img);
        Glide.with(view)
                .load(loggedUser.getAvatar())
                .placeholder(R.drawable.da_lat)
                .error(R.drawable.da_lat)
                .into(avatar);

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

    public void updateUserData(User newUserData) {
        this.loggedUser = newUserData;
    }
}