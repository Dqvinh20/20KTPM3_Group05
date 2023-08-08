package com.example.tripshare.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.tripshare.R;
import com.example.tripshare.TripShareApplication;
import com.example.tripshare.adapter.RatingItemAdapter;
import com.example.tripshare.api.services.TripPlanService;
import com.example.tripshare.api.services.RatingService;
import com.example.tripshare.databinding.FragmentOverviewBinding;
import com.example.tripshare.model.TripPlan;
import com.example.tripshare.model.Rating;
import com.example.tripshare.ui.dialog.RatePostDialog;
import com.example.tripshare.ui.tripPlan.EditableTripPlanDetailActivity;
import com.example.tripshare.utils.NumberUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewFragment extends Fragment {
    private static final String TAG = OverviewFragment.class.getSimpleName();
    private final short LIMIT_RATING_PER_REQ = 5;
    private final RatingItemAdapter ratingItemAdapter = new RatingItemAdapter();
    private final RatingService ratingService = TripShareApplication.createService(RatingService.class);
    FragmentOverviewBinding binding;
    private boolean isEditable = false;
    private Integer postId;
    private String briefDescription;
    private float avgPoint;
    private int avgCount;
    private boolean isExpanded = false;

    // Pagination ratings list
    private int currentPage = 1;
    private int maxPage = 1;
    private boolean isLoading = false;
    private boolean isFirstLoad = true;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance() {
        OverviewFragment fragment = new OverviewFragment();
        return fragment;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        binding.ratingList.setAdapter(ratingItemAdapter);
        binding.writeYourRatingBtn.setOnClickListener(view -> {
            RatePostDialog ratePostDialog = new RatePostDialog(postId);
            ratePostDialog.show(getChildFragmentManager(), RatePostDialog.class.getSimpleName());
        });
        binding.reviewExpandableLayout.setVisibility(View.GONE);

        binding.ratingTitle.setOnClickListener(view -> {
            try{
                isExpanded = !isExpanded;

                int icon = isExpanded ? R.drawable.ic_baseline_arrow_drop_down_24 : R.drawable.ic_baseline_arrow_left_24;
                binding.ratingTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, icon, 0);
                binding.reviewExpandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        binding.briefDescription.setTextIsSelectable(isEditable);
        binding.briefDescription.setFocusable(isEditable);
        binding.briefDescription.setCursorVisible(isEditable);
        return binding.getRoot();
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            postId = args.getInt("postId");
            briefDescription = args.getString("briefDescription");
            avgPoint = (float) args.getDouble("avgPoint");
            avgCount = args.getInt("avgCount");
            loadDataToView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initScrollListener();
        initEditableView();
    }

    public void showSnackbar(final String text) {
        Snackbar.make(binding.getRoot(), text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad && postId != null) {
            Log.e(TAG, "onResume " + isFirstLoad);
            fetchRatings();
            isFirstLoad = false;
        }
        else {
            loadDataToView();
        }
    }
    private void initScrollListener() {
        binding.ratingList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // Drag to bottom of rating list
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING &&
                        !binding.ratingList.canScrollVertically(1)) {
                    if (!isLoading) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void initEditableView() {
        binding.reviewLayout.setVisibility(isEditable ? View.GONE : View.VISIBLE);
        binding.briefDescription.setTextIsSelectable(isEditable);
        binding.briefDescription.setFocusableInTouchMode(isEditable);
        binding.briefDescription.setCursorVisible(isEditable);
        binding.briefDescription.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        String newDescription = binding.briefDescription.getText().toString();
                        if (!b && !briefDescription.equals(newDescription)) {
                            // Hide keyboard
                            InputMethodManager imm =  (InputMethodManager) binding.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            updateBriefDescription();
                        }
                    }
                }
        );
    }

    private List<Rating> parseResponseData(Response<JsonObject> response) {
        JsonObject body = response.body();
        JsonObject ratingsObj = body.getAsJsonObject("ratings");
        currentPage = ratingsObj.get("current_page").getAsInt();
        maxPage = ratingsObj.get("max_page").getAsInt();
        return new Gson().fromJson(ratingsObj.get("data"), new TypeToken<List<Rating>>() {}.getType());
    }

    private void loadMore() {
        Log.e(TAG, "loadMore");
        if (currentPage > maxPage) {
            return;
        }
        currentPage += 1;

        binding.ratingList.post(() -> {
            ratingItemAdapter.add(null);
        });

        ratingService.getAllPostRatings(postId, currentPage, (int) LIMIT_RATING_PER_REQ)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        if (response.isSuccessful()) {
                            try {
                                binding.ratingList.post(() -> {
                                    ratingItemAdapter.remove(ratingItemAdapter.getItemCount() - 1);
                                    ratingItemAdapter.addAll(parseResponseData(response));
                                    ratingItemAdapter.notifyDataSetChanged();
                                    isLoading = false;
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Snackbar
                                .make(binding.getRoot(), "Fail when load reviews", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void fetchRatings() {
        if (currentPage > maxPage) {
            return;
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Response<JsonObject> response = ratingService.getAllPostRatings(postId, currentPage, (int) LIMIT_RATING_PER_REQ).execute();
                if (response.isSuccessful()) {
                    List<Rating> ratings1 = parseResponseData(response);
                    binding.ratingList.post(() -> {
                        binding.divider.setVisibility(ratings1 != null && !ratings1.isEmpty() ? View.VISIBLE : View.GONE);
                        if (!ratings1.isEmpty()) {
                            ratingItemAdapter.setRatingList(ratings1);
                            loadDataToView();
                        }
                    });
                }
            } catch (IOException e) {
                Snackbar
                        .make(binding.getRoot(), "Fail when load reviews", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        executorService.shutdown();
    }

    public void appendNewRating(Rating rating) {
        binding.divider.setVisibility(rating != null ? View.VISIBLE : View.GONE);
        ratingItemAdapter.addHead(rating);
    }

    private void loadDataToView() {
        try {
        if (isResumed()) {
            if (!isEditable && (briefDescription == null || briefDescription.isEmpty())) {
                binding.briefDescription.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                binding.briefDescription.setText("No description about this trip.");
            }
            else {
                binding.briefDescription.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                binding.briefDescription.setText(briefDescription);
            }

            String formatted = String.format("%.1f", avgPoint);
            binding.avgPoint.setText(formatted);
            binding.avgRatingBar.setRating(avgPoint);
            binding.ratingCount.setText(
                    String.format(
                            "based on %s ratings",
                            NumberUtil.formatShorter(avgCount)
                    )
            );
        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateBriefDescription() {
        TripPlanService tripPlanService = TripShareApplication.createService(TripPlanService.class);
        RequestBody postIdBody = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                postId.toString());
        RequestBody briefDescriptionBody = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                binding.briefDescription.getText().toString());

        tripPlanService.updatePost(
                postIdBody,
                null,
                briefDescriptionBody,
                null,
                null
        ).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (!response.isSuccessful()) return;
                JsonArray body = response.body();
                // Update success
                if (body.get(0).getAsInt() == 1) {
                    TripPlan updatedTripPlan = new Gson().fromJson(body.get(1).getAsJsonObject(), TripPlan.class);
                    briefDescription = updatedTripPlan.getBriefDescription();
                    ((EditableTripPlanDetailActivity) getActivity()).getPostLiveData().getValue().setBriefDescription(updatedTripPlan.getBriefDescription());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                binding.briefDescription.setText(briefDescription);
                Snackbar
                        .make(binding.getRoot(), "Fail to change brief description", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }
}