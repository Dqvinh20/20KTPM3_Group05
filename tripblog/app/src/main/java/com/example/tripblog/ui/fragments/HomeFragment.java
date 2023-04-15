package com.example.tripblog.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.TripPlanService;
import com.example.tripblog.model.TripPlan;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.component.HomePostAdapter;
import com.example.tripblog.ui.tripPlan.TripPlanDetailActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment
        implements HomePostAdapter.ItemClickListener {
    public static final String TAG = HomeFragment.class.getSimpleName();
    private final Integer LIMIT_PER_PAGE = 4;
    ImageNewsFeedFragment imageNewsFeedFragment;
    FragmentTransaction ft;
    private boolean isLoading = false;
    private boolean isLoaded = false;
    private int nextPage, maxPage, currPage;
    private HomePostAdapter popularPostsAdapter = new HomePostAdapter(RecyclerView.HORIZONTAL);
    private HomePostAdapter latestPostAdapter = new HomePostAdapter(RecyclerView.VERTICAL);
    private RecyclerView popularPosts;
    private RecyclerView lastedPosts;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ft = getChildFragmentManager().beginTransaction();
        imageNewsFeedFragment = ImageNewsFeedFragment.newInstance("Image Infor");
        ft.replace(R.id.infornewsfeed, imageNewsFeedFragment);
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.fragment_home, container, false);
        popularPosts = scrollView.findViewById(R.id.listPostnewsFeed);
        lastedPosts = scrollView.findViewById(R.id.listPostnewest);
        popularPostsAdapter.setItemClickListener(this);
        latestPostAdapter.setItemClickListener(this);
        popularPosts.setAdapter(popularPostsAdapter);
        lastedPosts.setAdapter(latestPostAdapter);
        TripPlanService tripPlanService = TripBlogApplication.createService(TripPlanService.class);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!scrollView.canScrollVertically(1)) {
                    Log.d(TAG, "Load More...");
                    if (!isLoading) {
                        if (currPage != maxPage) {
                            isLoading = true;
                            Log.v("...", "Last Item Wow !");

                            // Do pagination.. i.e. fetch new data
                            tripPlanService.getNewestTripPlans(nextPage, LIMIT_PER_PAGE).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    JsonObject data = response.body();
                                    nextPage = data.get("nextPage").getAsInt();
                                    currPage = data.get("page").getAsInt();
                                    maxPage = data.get("maxPage").getAsInt();

                                    JsonArray list = data.getAsJsonArray("data");
                                    List<TripPlan> listpost = new Gson().fromJson(list, new TypeToken<List<TripPlan>>(){}.getType());
                                    latestPostAdapter.appendList(listpost);
                                    isLoading = false;
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Snackbar.make(scrollView, "Fail to connect to server", Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    }
                }
            }
        });
        return scrollView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TripPlanService tripPlanService = TripBlogApplication.createService(TripPlanService.class);
        tripPlanService.getPopularTripPlans(
                1,10
        ).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray list = response.body();
                    List<TripPlan> listpost = new Gson().fromJson(list, new TypeToken<List<TripPlan>>(){}.getType());
                    popularPostsAdapter.setListPost(listpost);//1
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Snackbar.make(getView(), "Fail to connect to server", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        tripPlanService.getNewestTripPlans(1,4).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject data = response.body();
                nextPage = data.get("nextPage").getAsInt();
                currPage = data.get("page").getAsInt();
                maxPage = data.get("maxPage").getAsInt();

                JsonArray list = data.getAsJsonArray("data");
                List<TripPlan> listpost = new Gson().fromJson(list, new TypeToken<List<TripPlan>>(){}.getType());
                latestPostAdapter.setListPost(listpost);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Snackbar.make(getView(), "Fail to connect to server", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void showToast(String msg){
        Toast.makeText((MainActivity)getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Integer postId) {
        Intent intent = new Intent(getActivity(), TripPlanDetailActivity.class);
        intent.putExtra("postId", postId);
        ((MainActivity) getActivity()).getActivityResultLauncher().launch(intent);
    }
}