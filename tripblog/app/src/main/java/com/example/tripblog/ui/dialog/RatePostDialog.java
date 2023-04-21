package com.example.tripblog.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tripblog.R;
import com.example.tripblog.TripShareApplication;
import com.example.tripblog.api.services.RatingService;
import com.example.tripblog.model.Rating;
import com.example.tripblog.ui.fragments.OverviewFragment;
import com.example.tripblog.ui.tripPlan.TripPlanDetailActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class RatePostDialog extends DialogFragment implements View.OnClickListener {
    RatingBar ratingBar;
    EditText contentEdit;

    private int postId;

    public RatePostDialog(int postId) {
        this.postId = postId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rate_post_layout, container, false);
        ratingBar = v.findViewById(R.id.ratingBar);
        contentEdit = v.findViewById(R.id.contentEdit);

        contentEdit.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (!hasFocus) {
                            InputMethodManager inputMethodManager =(InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
        );

        v.findViewById(R.id.cancelButton).setOnClickListener(this);
        v.findViewById(R.id.submitButton).setOnClickListener(this);
        return v;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        contentEdit.clearFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onDismiss(dialog);
    }

    private boolean validateForm() {
        float rating = ratingBar.getRating();
        String content = contentEdit.getText().toString();

        if (rating == 0 || content.isEmpty()) {
            return false;
        }
        return true;
    }

    public void submitRating() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(sendData(executorService));
    }

    public Runnable fetchNewRating = new Runnable() {
        @Override
        public void run() {
            RatingService ratingService = TripShareApplication.createService(RatingService.class);

            try {
                Response<JsonObject> req = ratingService.getAllPostRatings(postId, 1, 1).execute();
                JsonObject ratingsObj = req.body().getAsJsonObject("ratings");
                List<Rating> ratings = new Gson().fromJson(ratingsObj.get("data"), new TypeToken<List<Rating>>() {}.getType());
                getActivity().runOnUiThread(() -> {
                    ((TripPlanDetailActivity) getActivity()).fetchData();
                    ((OverviewFragment) getParentFragment()).appendNewRating(ratings.get(0));
                });
                ((OverviewFragment) getParentFragment()).showSnackbar("Rate successfully");
            } catch (IOException e) {
                ((OverviewFragment) getParentFragment()).showSnackbar("Error occur when rate the trip!");
            } finally {
                dismiss();
            }
        }
    };

    private Runnable sendData(ExecutorService executorService) {
        return new Runnable() {
            @Override
            public void run() {
                RatingService ratingService = TripShareApplication.createService(RatingService.class);
                try {
                    Response<JsonObject> response = ratingService
                                                                .writeRating(
                                                                    postId,
                                                                    (int) ratingBar.getRating(),
                                                                    contentEdit.getText().toString())
                                                                .execute();
                    if (response.isSuccessful()) {
                        if (response.body().get("success").getAsInt() == 1) {
                            executorService.execute(fetchNewRating);
                            executorService.shutdown();
                            return;
                        }
                        else {
                            dismiss();
                            ((OverviewFragment) getParentFragment()).showSnackbar("You already rate this trip!");
                        }
                    }
                } catch (IOException e) {
                    dismiss();
                    ((OverviewFragment) getParentFragment()).showSnackbar("Error occur when rate the trip!");
                }
            }
        };
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cancelButton) {
            dismiss();
        }
        else if (validateForm()) {
            submitRating();
        }
        else {
            dismiss();
        }
    }
}
