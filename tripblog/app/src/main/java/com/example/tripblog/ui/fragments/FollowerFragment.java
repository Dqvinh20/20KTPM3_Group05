package com.example.tripblog.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.UserService;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.adapter.UserFollowAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowerFragment extends Fragment {



    public FollowerFragment() {
        // Required empty public constructor
    }


    public static FollowerFragment newInstance() {
        FollowerFragment fragment = new FollowerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    private UserFollowAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_follower, container, false);
        RecyclerView recyclerList = rootView.findViewById(R.id.followerRecycler);
        recyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        UserService userService = TripBlogApplication.createService(UserService.class);
        userService.getUserFollowers(TripBlogApplication.getInstance().getLoggedUser().getId()).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {

                    JsonArray rawData = response.body().getAsJsonArray();
                    JsonObject jsonObject = (JsonObject) rawData.get(0);
                    JsonArray followerJsonArray = jsonObject.getAsJsonArray("followers");
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<List<User>>() {}.getType();
                    List<User> followerList = gson.fromJson(followerJsonArray, userListType);
                    Log.d("Data in", followerList.toString());
                    if (followerList.size() != 0) {
                        adapter = new UserFollowAdapter(followerList, true);
                        recyclerList.setAdapter(adapter);
                    }
                    else {
                        TextView message = rootView.findViewById(R.id.msgTxt);
                        message.setText("No one has followed this user yet.");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return rootView;
    }
}