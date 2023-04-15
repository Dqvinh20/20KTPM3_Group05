package com.example.tripblog.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.util.Pair;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.TripPlanService;
import com.example.tripblog.databinding.FragmentCreateBinding;
import com.example.tripblog.model.TripPlan;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.tripPlan.EditableTripPlanDetailActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateFragment extends DialogFragment {
    private static final String TAG = CreateFragment.class.getSimpleName();
    FragmentCreateBinding binding;
    private MaterialDatePicker tripDates = null;
    private String title;
    private Date startDate = null;
    private Date endDate = null;
    private boolean isPublic = true;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((MainActivity) getActivity()).onCreateDismis();
    }

    public static CreateFragment newInstance() {
        CreateFragment fragment = new CreateFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.Theme_Tripblog_DialogFragmentAnim;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateBinding.inflate(inflater, container,false);
        binding.close.setOnClickListener(view -> this.dismiss());
        binding.tripDatesLayout.setOnClickListener(view -> onTripDatesPicker());
        binding.privacySetting.setOnClickListener(view -> openPrivacySettingBottomSheet());
        binding.createPost.setOnClickListener(view -> {
            hideKeyboard(view);
            if (validateInput()) {
                createPost();
            }
        });

        if (savedInstanceState != null) {
            title = savedInstanceState.getString("title");
            startDate = (Date) savedInstanceState.getSerializable("start_date");
            endDate = (Date) savedInstanceState.getSerializable("end_date");
            binding.startDate.setText(formatDate(startDate));
            binding.endDate.setText(formatDate(endDate));
        }

        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard(v);
            }
            return false;
        });

        return binding.getRoot();
    }

    private void hideKeyboard(View v) {
        binding.editTripTitle.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        return sdf.format(date);
    }

    private void onTripDatesPicker() {
        if (tripDates == null) {
            CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
            constraintBuilder.setFirstDayOfWeek(Calendar.MONDAY);
            Long today = MaterialDatePicker.todayInUtcMilliseconds();

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.clear();
            calendar.setTimeInMillis(today);

            // Set calendar constraint to date range picker
            // Can only select +-1 year from now
            int currYear = calendar.get(Calendar.YEAR);
            calendar.set(Calendar.YEAR, currYear - 1);
            constraintBuilder.setStart(calendar.getTimeInMillis());

            calendar.set(Calendar.YEAR, currYear + 5);
            constraintBuilder.setEnd(calendar.getTimeInMillis());

            constraintBuilder.setOpenAt(today);
            tripDates = MaterialDatePicker.Builder
                    .dateRangePicker()
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_MaterialCalendar)
                    .setCalendarConstraints(constraintBuilder.build())
                    .setTitleText("Chose your trip dates")
                    .build();

            tripDates.addOnPositiveButtonClickListener((selection -> {
                Pair<Long, Long> dates = (Pair<Long, Long>) selection;
                Calendar cal = Calendar.getInstance();

                // Get start date
                cal.setTimeInMillis(dates.first);
                startDate = cal.getTime();
                binding.startDate.setText(formatDate(cal.getTime()));

                // Get end date
                cal.setTimeInMillis(dates.second);
                endDate = cal.getTime();
                binding.endDate.setText(formatDate(cal.getTime()));
            }));
        }

        tripDates.show(getActivity().getSupportFragmentManager(), "TRIP-DATES-PICKER");
    }

    private void openPrivacySettingBottomSheet() {
        View v = getLayoutInflater().inflate(R.layout.state_choose_bottom_sheet, null);
        BottomSheetDialog popupMenu = new BottomSheetDialog(this.getContext());
        popupMenu.setContentView(v);
        popupMenu.show();

        LinearLayout publicLayout = (LinearLayout) v.findViewById(R.id.publicChoose);
        publicLayout.setOnClickListener(view -> {
            binding.privacySetting.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_public_24, 0, R.drawable.ic_baseline_arrow_drop_down_24, 0);
            binding.privacySetting.setText(getString(R.string.public_txt));
            isPublic = true;
            popupMenu.dismiss();
        });

        LinearLayout privateLayout = (LinearLayout) v.findViewById(R.id.privateChoose);
        privateLayout.setOnClickListener(view -> {
            binding.privacySetting.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_arrow_drop_down_24, 0);
            binding.privacySetting.setText(getString(R.string.private_txt));
            isPublic = false;
            popupMenu.dismiss();
        });
    }

    private void openEditPostDetail(Integer postId) {
        Intent postDetail = new Intent(getActivity(), EditableTripPlanDetailActivity.class);
        postDetail.putExtra("postId", postId);
        startActivity(postDetail);
        dismiss();
    }

    private boolean validateInput() {
        if (binding.editTripTitle.getText().toString().isEmpty()) {
            binding.editTripTitle.requestFocus();
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            Snackbar
                    .make(binding.getRoot(), "You must enter trip title!", Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }

        if (startDate == null || endDate == null) {
            Snackbar
                    .make(binding.getRoot(), "You must choose dates for trip!", Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    private void createPost() {
        TripPlanService tripPlanService = TripBlogApplication.createService(TripPlanService.class);
        tripPlanService.createNewTripPlan(
                binding.editTripTitle.getText().toString(),
                startDate,
                endDate,
                isPublic,
                TripBlogApplication.getInstance().getLoggedUser().getId()
        ).enqueue(new Callback<TripPlan>() {
            @Override
            public void onResponse(Call<TripPlan> call, Response<TripPlan> response) {
                if (response.isSuccessful()) {
                    TripPlan newTripPlan = response.body();
                    Log.d(TAG, "Created post: " + newTripPlan.toString());
                    openEditPostDetail(newTripPlan.getId());
                }
            }
            @Override
            public void onFailure(Call<TripPlan> call, Throwable t) {
                Snackbar
                        .make(binding.getRoot(), "Fail to connect to server", Snackbar.LENGTH_LONG)
                        .setAction("Retry", view -> {
                            createPost();
                        })
                        .show();
            }
        });
    }
}