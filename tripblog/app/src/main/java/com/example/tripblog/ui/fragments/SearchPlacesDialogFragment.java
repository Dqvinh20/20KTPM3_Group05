package com.example.tripblog.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

//import java.util.Arrays;
//implements
//        View.OnClickListener, TextWatcher
public class SearchPlacesDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "ADDRESS_AUTOCOMPLETE";
    private Long lastRequest = Long.valueOf(0);
//    FragmentSearchPlacesDialogBinding binding;
//    FindAutocompletePredictionsRequest.Builder reqBuilder = FindAutocompletePredictionsRequest.builder();

    public SearchPlacesDialogFragment() {
        // Required empty public constructor
    }

    public static SearchPlacesDialogFragment newInstance() {
        SearchPlacesDialogFragment fragment = new SearchPlacesDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        binding = FragmentSearchPlacesDialogBinding.inflate(inflater, container, false);
//        binding.close.setOnClickListener(this);
//        binding.clearText.setOnClickListener(this);
//        binding.editQuery.addTextChangedListener(this);
//        return binding.getRoot();
        return null;
    }

    @Override
    public void onClick(View view) {
//        if (view == binding.clearText) {
//            binding.editQuery.setText(null);
//        }
//        else if (view == binding.close) {
//            this.dismiss();
//        }
    }

//    @Override
//    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable editable) {
//        if (SystemClock.elapsedRealtime() - lastRequest < 1000){
//            return;
//        }
//        lastRequest = SystemClock.elapsedRealtime();
////        String query = binding.editQuery.getText().toString();
////        if (query.isEmpty()) return;
////
////        binding.loading.setVisibility(View.VISIBLE);
//
//        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
//        FindAutocompletePredictionsRequest request = reqBuilder
//                .setCountry("VN")
//                .setTypesFilter(Arrays.asList("locality", "administrative_area_level_3"))
//                .setSessionToken(token)
////                .setQuery(query)
//                .build();
//
//        PlacesClient placesClient = Places.createClient(getContext());
//        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
////            binding.loading.setVisibility(View.GONE);
//            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                Log.i(TAG, prediction.getPrimaryText(null).toString());
////                Log.i(TAG, prediction.getPlaceTypes().toString());
//            }
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                ApiException apiException = (ApiException) exception;
//                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//            }
//        });
//    }
}