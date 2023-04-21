package com.example.tripblog.ui.fragments;

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

import com.example.tripblog.R;
import com.example.tripblog.TripShareApplication;
import com.example.tripblog.api.services.UserService;
import com.example.tripblog.model.User;
import com.example.tripblog.adapter.UserFollowAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingFragment extends Fragment {
    private static final String TAG = FollowDialogFragment.class.getSimpleName();
    private ProgressBar loadingProgressBar;
    private static final String ARG_PARAM1 = "param1";
    private int currUserId;
    public FollowingFragment() {
        // Required empty public constructor
    }
    public static FollowingFragment newInstance(int currUserId) {
        FollowingFragment fragment = new FollowingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_following, container, false);
        RecyclerView recyclerList = rootView.findViewById(R.id.followingRecycler);
        recyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar);

        if (getArguments() != null) {
            currUserId = getArguments().getInt(ARG_PARAM1);
        }
        loadingProgressBar.setVisibility(View.VISIBLE);
        recyclerList.setVisibility(View.GONE);
        UserService userService = TripShareApplication.createService(UserService.class);
        userService.getUserFollowing(currUserId).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                loadingProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    JsonArray rawData = response.body().getAsJsonArray();
                    JsonObject jsonObject = (JsonObject) rawData.get(0);
                    JsonArray followingJsonArray = jsonObject.getAsJsonArray("followings");
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<List<User>>() {}.getType();
                    List<User> followingList = gson.fromJson(followingJsonArray, userListType);

                    TextView message = rootView.findViewById(R.id.msgTxt);
                    message.setVisibility(followingList.size() == 0 ? View.VISIBLE : View.GONE);
                    message.setText("No one has followed this user yet.");

                    recyclerList.setVisibility(followingList.size() != 0 ? View.VISIBLE : View.GONE);
                    adapter = new UserFollowAdapter(followingList, false, new UserFollowAdapter.ItemClickListener() {
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
                message.setText("Error occurs when loading followings!!!");
            }
        });
        return rootView;
    }
}