package com.example.tripblog.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.api.services.PostService;
import com.example.tripblog.model.Post;
import com.example.tripblog.ui.adapter.PlanListAdapter;
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
 * Use the {@link PublicPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicPlanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String ARG_OBJECT = "object";

    public PublicPlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrivatePlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublicPlanFragment newInstance(String param1, String param2) {
        PublicPlanFragment fragment = new PublicPlanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_public_plan, container, false);
    }
//    String[] name = {"Da Lat Plan", "Vung Tau Plan"};
//    Integer[] img = {R.drawable.da_lat, R.drawable.da_lat};

    PlanListAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView planList = view.findViewById(R.id.planList);

        PostService postService = TripBlogApplication.createService(PostService.class);
        Context fragmentContext = getContext();
        postService.getPostByUserId(1, false).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

                    JsonArray postJsonArray = response.body().getAsJsonArray();
//                    JsonObject rawData = response.body();
//                    JsonArray postJsonArray = rawData.getAsJsonArray("posts");
                    Gson gson = new Gson();
                    Type postListType = new TypeToken<List<Post>>(){}.getType();
                    List<Post> postList = gson.fromJson(postJsonArray, postListType);
                    Log.d("Data in", postList.toString());

//                    if(postList != null) Log.d("post list", getActivity().toString());
                    if(postList.size() != 0) {
                        adapter = new PlanListAdapter(getActivity(), R.layout.plan_item, postList);
                        planList.setAdapter(adapter);
                    }
                    else{
                        TextView noPlanTxt = view.findViewById(R.id.noPlanTxt);
                        noPlanTxt.setText("You haven't written any posts yet.");
                    }
//                    adapter.updateData(postList);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });


    }
}