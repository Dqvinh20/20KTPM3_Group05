package com.example.tripblog.ui.tripPlan;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tripblog.TripShareApplication;
import com.example.tripblog.adapter.SearchResultAdapter;
import com.example.tripblog.api.services.SearchService;
import com.example.tripblog.databinding.BottomSheetSearchPlacesBinding;
import com.example.tripblog.model.Location;
import com.example.tripblog.model.response.SearchResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlaceBottomSheet extends BottomSheetDialogFragment
        implements TextWatcher, Callback<SearchResponse> {
    private final String TAG = BottomSheetDialog.class.getSimpleName();
    BottomSheetSearchPlacesBinding binding;
    private final SearchService searchService = TripShareApplication.createService(SearchService.class);
    private Long lastRequest = 0L;
    SearchResultAdapter adapter = new SearchResultAdapter();

    public AddPlaceBottomSheet() {
    }

    public void setOnLocationClickListener(SearchResultAdapter.IOnLocationClickListener onClick) {
        adapter.setOnLocationClickListener(onClick);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding = BottomSheetSearchPlacesBinding.inflate(inflater, container, false);
        binding.queryEditText.addTextChangedListener(this);
        binding.searchResults.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().isEmpty()) {
            adapter.setData(null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (SystemClock.elapsedRealtime() - lastRequest < 500){
            return;
        }
        lastRequest = SystemClock.elapsedRealtime();

        callSearchApi();
    }

    private void callSearchApi() {
        String query = binding.queryEditText.getText().toString();
        if (query.isEmpty()) {
            return;
        };

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.searchResults.setVisibility(View.GONE);
        binding.noResultTxt.setVisibility(View.GONE);

        searchService.searchPlaces(
                query,
                10
        ).enqueue(this);
    }

    @Override
    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
        binding.progressBar.setVisibility(View.GONE);
        if (!response.isSuccessful()) {
            return;
        }
        SearchResponse searchResponse = response.body();
        List<Location> locations = searchResponse.getLocations();
        boolean hasResult = locations != null && !locations.isEmpty();

        binding.searchResults.setVisibility(hasResult ? View.VISIBLE : View.GONE);
        binding.noResultTxt.setVisibility(hasResult ? View.GONE : View.VISIBLE);

        try {
            adapter.setData(locations);
        } catch (Exception err) {
            err.printStackTrace();
            Log.e(TAG, err.getMessage());
        }

    }

    @Override
    public void onFailure(Call<SearchResponse> call, Throwable t) {
        callSearchApi();
    }
}
