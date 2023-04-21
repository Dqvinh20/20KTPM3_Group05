package com.example.tripblog.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.TripPlanService;
import com.example.tripblog.model.TripPlan;
import com.example.tripblog.model.User;
import com.example.tripblog.adapter.PlanListAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PrivatePlanFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private int currUserId;
    private boolean isEditable = true;
    private User loggedUser = TripBlogApplication.getInstance().getLoggedUser();
    ListView planList;
    private PlanListAdapter adapter = null;

    public PrivatePlanFragment() {
        // Required empty public constructor
    }

    public static PrivatePlanFragment newInstance(int currUserId) {
        PrivatePlanFragment fragment = new PrivatePlanFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, currUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_plan, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            currUserId = getArguments().getInt(ARG_PARAM1);
        }
        planList = view.findViewById(R.id.planList);
    }

    @Override
    public void onResume() {
        super.onResume();
        TripPlanService tripPlanService = TripBlogApplication.createService(TripPlanService.class);
        tripPlanService.getTripPlanByUserId(currUserId, false).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if(response.isSuccessful()) {
                    JsonArray postJsonArray = response.body().getAsJsonArray();
                    Gson gson = new Gson();
                    Type postListType = new TypeToken<List<TripPlan>>(){}.getType();
                    List<TripPlan> tripPlanList = gson.fromJson(postJsonArray, postListType);

                    if(tripPlanList.size() != 0) {
                        if(currUserId == loggedUser.getId())
                            isEditable = true;
                        else
                            isEditable = false;
                        adapter = new PlanListAdapter(getActivity(), R.layout.plan_item, tripPlanList, isEditable);
                        planList.setAdapter(adapter);
                    }
                    else{
                        TextView noPlanTxt = getView().findViewById(R.id.noPlanTxt);
                        noPlanTxt.setText("You haven't written any posts yet.");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });
    }
}