package com.example.tripblog.ui.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.adapter.RatingItemAdapter;
import com.example.tripblog.api.services.TripPlanService;
import com.example.tripblog.api.services.RatingService;
import com.example.tripblog.databinding.FragmentOverviewBinding;
import com.example.tripblog.model.TripPlan;
import com.example.tripblog.model.Rating;
import com.example.tripblog.ui.dialog.RatePostDialog;
import com.example.tripblog.ui.tripPlan.EditableTripPlanDetailActivity;
import com.example.tripblog.utils.NumberUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverviewFragment extends Fragment {
    private static final String TAG = OverviewFragment.class.getSimpleName();
    private final short LIMIT_RATING_PER_REQ = 5;
    private final RatingItemAdapter ratingItemAdapter = new RatingItemAdapter();
    private final RatingService ratingService = TripBlogApplication.createService(RatingService.class);
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
        binding.ratingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isExpanded = !isExpanded;
                int icon = isExpanded ? R.drawable.ic_baseline_arrow_drop_down_24 : R.drawable.ic_baseline_arrow_left_24;
                binding.ratingTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, icon, 0);
                binding.reviewExpandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                Log.e(TAG, "ratingTitle");
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
        loadDataToView();
        if (isFirstLoad && postId != null) {
            fetchRatings();
            isFirstLoad = false;
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
                        currentPage++;
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
        currentPage += 1;
        if (currentPage > maxPage) {
            return;
        }

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
        ratingService.getAllPostRatings(postId, currentPage, (int) LIMIT_RATING_PER_REQ)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            List<Rating> ratings = parseResponseData(response);
                            ratingItemAdapter.addAll(ratings);
                            binding.divider.setVisibility(ratings != null && !ratings.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                        Snackbar
                                .make(binding.getRoot(), "Fail when load reviews", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    public void appendNewRating(Rating rating) {
        binding.divider.setVisibility(rating != null ? View.VISIBLE : View.GONE);
        ratingItemAdapter.addHead(rating);
    }

    private void loadDataToView() {
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
                            "based on %s reviews",
                            NumberUtil.formatShorter(avgCount)
                    )
            );
        }
    }

    private void updateBriefDescription() {
        TripPlanService tripPlanService = TripBlogApplication.createService(TripPlanService.class);
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