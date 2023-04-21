package com.example.tripblog.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.util.Pair;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.example.tripblog.R;
import com.example.tripblog.TripShareApplication;
import com.example.tripblog.api.services.TripPlanService;
import com.example.tripblog.databinding.FragmentCreateBinding;
import com.example.tripblog.model.TripPlan;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.dialog.SimpleLoadingDialog;
import com.example.tripblog.ui.tripPlan.EditableTripPlanDetailActivity;
import com.example.tripblog.utils.PathUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class CreateFragment extends DialogFragment
    implements View.OnClickListener, TextWatcher
{
    private static final String TAG = CreateFragment.class.getSimpleName();
    private final String DATE_PATTERN = "MMM d, yyyy";
    private final String SUBMIT_DATE_PATTERN = "yyyy-MM-dd";
    FragmentCreateBinding binding;
    private MaterialDatePicker tripDates = null;
    private String title;
    private Date startDate = null;
    private Date endDate = null;
    private boolean isPublic = true;
    private Uri coverImgUri = null;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

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
        dialog.getWindow().getAttributes().windowAnimations = R.style.Theme_Tripshare_DialogFragmentAnim;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            openGallery();
                        }
                    }
                }
        );

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK  && result.getData() != null) {
                            final Uri imageUri = result.getData().getData();
                            coverImgUri = imageUri;
                            binding.coverImg.setImageURI(imageUri);
                            binding.coverImg.setVisibility(View.VISIBLE);
                            toggleCreateBtnOnValidInput();
                        }
                    }
                }
        );

        setStyle(STYLE_NO_TITLE, R.style.Theme_Tripshare_NoActionBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateBinding.inflate(inflater, container,false);
        binding.close.setOnClickListener(this);
        binding.tripDatesLayout.setOnClickListener(this);
        binding.privacySetting.setOnClickListener(this);
        binding.imgChooser.setOnClickListener(this);

        binding.createTripPlanBtn.setOnClickListener(this);
        binding.createTripPlanBtn.setEnabled(false);

        binding.editTripTitle.addTextChangedListener(this);

        // Hide keyboard when click outside edittext
        binding.container.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(view);
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coverImg:
            case R.id.imgChooser:
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.close:
                dismiss();
                break;
            case R.id.createTripPlanBtn:
                hideKeyboard(view);
                createTripPlan();
                break;
            case R.id.tripDatesLayout:
                onTripDatesPicker();
                break;
            case R.id.privacySetting:
                openPrivacySettingBottomSheet();
                break;
            default:
                break;
        }
    }

    private void hideKeyboard(View v) {
        binding.editTripTitle.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
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
                binding.startDate.setText(formatDate(cal.getTime(), DATE_PATTERN));

                // Get end date
                cal.setTimeInMillis(dates.second);
                endDate = cal.getTime();
                binding.endDate.setText(formatDate(cal.getTime(), DATE_PATTERN));

                toggleCreateBtnOnValidInput();
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
            return false;
        }

        if (startDate == null || endDate == null) {
            return false;
        }

        if (coverImgUri == null) {
            return false;
        }

        return true;
    }

    private void toggleCreateBtnOnValidInput() {
        binding.createTripPlanBtn.setEnabled(validateInput());
    }

    private void createTripPlan() {
        SimpleLoadingDialog simpleLoadingDialog = new SimpleLoadingDialog(getContext());
        simpleLoadingDialog.show();
        String tripTitle = binding.editTripTitle.getText().toString();
        String briefDescription = binding.editTripBriefDescription.getText().toString();
        String startDateStr = formatDate(startDate, SUBMIT_DATE_PATTERN);
        String endDateStr = formatDate(endDate, SUBMIT_DATE_PATTERN);
        String createdBy = TripShareApplication.getInstance().getLoggedUser().getId().toString();

        RequestBody tripTitleBody = RequestBody.create(MediaType.parse("multipart/form-data"), tripTitle);
        RequestBody briefDescriptionBody = RequestBody.create(MediaType.parse("multipart/form-data"), briefDescription);
        RequestBody startDateBody = RequestBody.create(MediaType.parse("multipart/form-data"), startDateStr);
        RequestBody endDateBody = RequestBody.create(MediaType.parse("multipart/form-data"), endDateStr);
        RequestBody isPublicBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(isPublic));
        RequestBody createdByBody = RequestBody.create(MediaType.parse("multipart/form-data"), createdBy);

        String imageRealPath = PathUtil.getRealPath(getContext(), coverImgUri);
        File coverImgFile = new File(imageRealPath);
        RequestBody coverImg = RequestBody.create(MediaType.parse("multipart/form-data"), coverImgFile);
        MultipartBody.Part coverImgBody = MultipartBody.Part.createFormData("cover_img", coverImgFile.getName(), coverImg);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TripPlanService tripPlanService = TripShareApplication.createService(TripPlanService.class);
                    Call<TripPlan> createNewTripCall = tripPlanService.createNewTripPlan(
                            tripTitleBody,
                            startDateBody,
                            endDateBody,
                            isPublicBody,
                            createdByBody,
                            briefDescriptionBody,
                            coverImgBody
                    );
                    Response<TripPlan> response = createNewTripCall.execute();
                    simpleLoadingDialog.dismiss();
                    if (response.isSuccessful()) {
                        TripPlan newTripPlan = response.body();
                        openEditPostDetail(newTripPlan.getId());
                    }
                } catch (IOException e) {
                    Snackbar
                        .make(binding.getRoot(), "Fail to connect to server", Snackbar.LENGTH_SHORT)
                        .setAction("Retry", view -> {
                            createTripPlan();
                        })
                        .show();
                    simpleLoadingDialog.dismiss();
                }
            }
        });

        executorService.shutdown();
    }

    // Image chooser
    protected void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        toggleCreateBtnOnValidInput();
    }


}