package com.example.tripshare.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tripshare.R;
import com.example.tripshare.TripShareApplication;
import com.example.tripshare.api.services.UserService;
import com.example.tripshare.model.User;
import com.example.tripshare.adapter.UserFollowAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowerFragment extends Fragment {
    private ProgressBar loadingProgressBar;
    private static final String ARG_PARAM1 = "param1";
    private int currUserId;
    public FollowerFragment() {
        // Required empty public constructor
    }


    public static FollowerFragment newInstance(int currUserId) {
        FollowerFragment fragment = new FollowerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, currUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private UserFollowAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_follower, container, false);
        RecyclerView recyclerList = rootView.findViewById(R.id.followerRecycler);
        recyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar);

        if (getArguments() != null) {
            currUserId = getArguments().getInt(ARG_PARAM1);
        }
        loadingProgressBar.setVisibility(View.VISIBLE);
        recyclerList.setVisibility(View.GONE);
        UserService userService = TripShareApplication.createService(UserService.class);
        userService.getUserFollowers(currUserId).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                loadingProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    JsonArray rawData = response.body().getAsJsonArray();
                    JsonObject jsonObject = (JsonObject) rawData.get(0);
                    JsonArray followerJsonArray = jsonObject.getAsJsonArray("followers");
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<List<User>>() {}.getType();
                    List<User> followerList = gson.fromJson(followerJsonArray, userListType);

                    TextView message = rootView.findViewById(R.id.msgTxt);
                    message.setVisibility(followerList.size() == 0 ? View.VISIBLE : View.GONE);
                    message.setText("No one has followed this user yet.");

                    recyclerList.setVisibility(followerList.size() != 0 ? View.VISIBLE : View.GONE);
                    adapter = new UserFollowAdapter(followerList, true, new UserFollowAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(int userId) {
                            Fragment fragment = ProfileFragment.newInstance(userId, false);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.add(R.id.frameLayout, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                    recyclerList.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                recyclerList.setVisibility(View.GONE);
                TextView message = rootView.findViewById(R.id.msgTxt);
                message.setVisibility(View.VISIBLE);
                message.setText("Error occurs when loading followers!!!");
                t.printStackTrace();
            }
        });

        return rootView;
    }
}