package com.example.tripblog.ui.tripPlan;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;

import com.example.tripblog.R;
import com.example.tripblog.model.Location;
import com.example.tripblog.model.TripPlan;
import com.example.tripblog.model.Schedule;
import com.example.tripblog.ui.dialog.SimpleLoadingDialog;
import com.example.tripblog.utils.PathUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditableTripPlanDetailActivity extends TripPlanDetailActivity {
    protected final int REQUEST_CODE = 1;
    private static final String TAG = EditableTripPlanDetailActivity.class.getSimpleName();
    private MaterialDatePicker<Pair<Long, Long>> tripDatesRangePicker;
    private MaterialDatePicker.Builder<Pair<Long, Long>> builder;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isEditable = true;
        super.onCreate(savedInstanceState);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK  && result.getData() != null) {

                            final Uri imageUri = result.getData().getData();
                            updateCoverImg(imageUri);
                        }
                    }
                }
        );

        currPostLiveData.observe(this, new Observer<TripPlan>() {
            @Override
            public void onChanged(TripPlan tripPlan) {
                // First load only
                initDateRangePicker();
                currPostLiveData.removeObservers(EditableTripPlanDetailActivity.this);
                // Auto reload date on update currPostLiveData value
                currPostLiveData.observeForever(post1 -> {
                    loadData();
                });
            }
        });

        // Top app bar
        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int collapseToolbarHeight = binding.collapseToolbarLayout.getHeight();
            if (collapseToolbarHeight + verticalOffset < (2 * ViewCompat.getMinimumHeight(binding.collapseToolbarLayout))) {
                binding.toolbar.getNavigationIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                binding.toolbar.getMenu().getItem(0).setVisible(false);
                binding.toolbar.getMenu().getItem(1).setVisible(false);
                binding.toolbar.getMenu().getItem(2).setVisible(false);
            }
            else {
                binding.toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                binding.toolbar.getMenu().getItem(0).setVisible(true);
                binding.toolbar.getMenu().getItem(1).setVisible(true);
                binding.toolbar.getMenu().getItem(2).setVisible(true);
            }
        });
    }

    @Override
    protected void loadData() {
        super.loadData();
        Drawable drawable = AppCompatResources.getDrawable(this, currPostLiveData.getValue().getPublic() ? R.drawable.ic_baseline_public_24 : R.drawable.ic_baseline_lock_24);
        if(drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getColor(R.color.md_theme_light_background), PorterDuff.Mode.SRC_ATOP);
        }
        binding.toolbar.getMenu().getItem(1).setIcon(drawable);
    }

    private void initDateRangePicker() {
        if (tripDatesRangePicker != null) return;

        Date startDate = currPostLiveData.getValue().getStartDate();
        Date endDate = currPostLiveData.getValue().getEndDate();

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

        // Get start and end time from post
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, 1);
        long startTime = calendar.getTime().getTime();

        calendar.setTime(endDate);
        calendar.add(Calendar.DATE, 1);
        long endTime = calendar.getTimeInMillis();

        constraintBuilder.setOpenAt(startTime);
        builder = MaterialDatePicker.Builder
                .dateRangePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintBuilder.build())
                .setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_MaterialCalendar)
                .setTitleText("Trip dates")
                .setSelection(
                        Pair.create(
                                startTime,
                                endTime
                        )
                );

        tripDatesRangePicker = builder.build();
        tripDatesRangePicker.addOnPositiveButtonClickListener(onEditTripDates());
    }

    private MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> onEditTripDates() {
        return selection -> {
            Calendar calendar = Calendar.getInstance();

            Pair<Long, Long> dates = selection;
            calendar.setTimeInMillis(dates.first);
            Date newStartDate = calendar.getTime();

            calendar.setTimeInMillis(dates.second);
            Date newEndDate = calendar.getTime();
            askForChangeTripDate(newStartDate, newEndDate);
        };
    }

    @Override
    protected void reloadEditableView() {
        super.reloadEditableView();
        // Edit date
        binding.tripDates.setOnClickListener((view) -> {
            if (!tripDatesRangePicker.isVisible()) {
                tripDatesRangePicker.show(getSupportFragmentManager(), "TRIP-DATES-PICKER");
            }
        });

        // Edit title on un-focus event
        binding.tripTitle.setTextIsSelectable(isEditable);
        binding.tripTitle.setFocusable(isEditable);
        binding.tripTitle.setOnFocusChangeListener(onUpdateTripTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.post_detail_menu, menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getColor(R.color.md_theme_light_background), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.coverImgChooser:
                onClickRequestPermission();
                break;
            case R.id.deletePost:
                onDeletePost();
                break;
            case R.id.privacySetting:
                onChangePrivacy();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onChangePrivacy() {
        boolean isPublic = currPostLiveData.getValue().getPublic();
        String message = String.format(
                "%s %s (%s)",

                "Are you sure you want to change trip plan privacy to",
                getString(isPublic ? R.string.private_txt : R.string.public_txt),
                getString(isPublic ? R.string.private_scope : R.string.public_scope)
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.privacy_setting)
                .setMessage(message)
                .setNegativeButton(
                        "No",
                        (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        }
                )
                .setPositiveButton(
                        "Yes",
                        (dialogInterface, i) -> {
                            Properties data = new Properties();
                            RequestBody isPublicBody = RequestBody.create(MediaType.parse("multipart/form-data"), isPublic ? "false" : "true");
                            data.put("is_public", isPublicBody);
                            callUpdateApi(data, "Update privacy successfully.", "Fail when update privacy!");
                            dialogInterface.dismiss();
                        }
                )
                .show();
    }

    private void askForChangeTripDate(Date newStartDate, Date newEndDate) {
        TripPlan tripPlan = currPostLiveData.getValue();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        // Check if no change date
        if (fmt.format(newStartDate).equals(fmt.format(tripPlan.getStartDate()))
            && fmt.format(newEndDate).equals(fmt.format(tripPlan.getEndDate()))){
            return;
        }

        List<String> deletedScheduleItem = tripPlan.getSchedules().parallelStream().filter(schedule -> {
            Date scheduleDate = schedule.getDate();
            List<Location> locationList = schedule.getLocations();
            boolean hasLocation = locationList != null
                                        && !locationList.isEmpty();

            return hasLocation
                    && (scheduleDate.before(newStartDate) || scheduleDate.after(newEndDate))
                    && !fmt.format(scheduleDate).equals(fmt.format(newStartDate));
        }).map(Schedule::getDate).sorted()
                .map(date -> new SimpleDateFormat("MMM d", Locale.US).format(date))
                .collect(Collectors.toList());

        if (deletedScheduleItem.isEmpty()) {
            updateTripDate(tripPlan.getId(), newStartDate, newEndDate);
        }
        else {
            StringBuilder stringBuilder = new StringBuilder();
            if (deletedScheduleItem.size() > 3) {
                stringBuilder.append(
                        String.join(", ", deletedScheduleItem.subList(0, 2))
                );
                stringBuilder.append(",... ");
                stringBuilder.append(deletedScheduleItem.get(deletedScheduleItem.size() - 1));
            }
            else if (deletedScheduleItem.size() > 1) {
                int lastIndex = deletedScheduleItem.size() - 1;
                stringBuilder.append(String.join(", ",
                        deletedScheduleItem.subList(0, lastIndex)));
                stringBuilder.append(" and ");
                stringBuilder.append(deletedScheduleItem.get(lastIndex));
            }
            else {
                stringBuilder.append(deletedScheduleItem.get(0));
            }

            String message =
                    String.format(
                            "To change the dates, we need to delete places " +
                                    "you'd added to %d days in your schedule (%s)." +
                                    "Are you sure you want to delete them?",
                            deletedScheduleItem.size(),
                            stringBuilder
                    );
            new MaterialAlertDialogBuilder(this)
                .setTitle("Delete places and notes")
                .setMessage(message)
                .setNegativeButton(
                        "No",
                        (dialogInterface, i) -> {
                            // Restore date range picker
                            tripDatesRangePicker = builder.build();
                            tripDatesRangePicker.addOnPositiveButtonClickListener(onEditTripDates());
                            dialogInterface.dismiss();
                        }
                )
                .setPositiveButton(
                        "Yes",
                        (dialogInterface, i) -> {
                            // Update trip dates
                            updateTripDate(tripPlan.getId(), newStartDate, newEndDate);
                            dialogInterface.dismiss();
                        }
                )
                .show();
        }
    }

    private void updateTripDate(Integer postId, Date startDate, Date endDate) {
        SimpleLoadingDialog loadingDialog = new SimpleLoadingDialog(this);
        loadingDialog.show();
        tripPlanService.changeTripDates(postId, startDate, endDate)
                .enqueue(
                        new Callback<JsonArray>() {
                            @Override
                            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                loadingDialog.dismiss();

                                JsonArray body = response.body();
                                // body = [isSuccess, post]
                                if (body.isJsonNull() || body.get(0).getAsInt() == 0) {
                                    return;
                                }

                                TripPlan tripPlan = new Gson().fromJson(body.get(1).getAsJsonObject(), TripPlan.class);
                                currPostLiveData.postValue(tripPlan);
                                Snackbar
                                        .make(binding.getRoot(), "Updated trip dates.", Snackbar.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onFailure(Call<JsonArray> call, Throwable t) {
                                loadingDialog.dismiss();

                                Snackbar
                                        .make(binding.getRoot(), "Fail when update trip dates!", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }
                );
    }

    private View.OnFocusChangeListener onUpdateTripTitle() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) return;
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String titleTxt = binding.tripTitle.getText().toString().trim();
                RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), titleTxt);
                Properties data = new Properties();
                data.put("title", title);
                callUpdateApi(data, null, null);
            }
        };
    }

    private void onDeletePost() {
        String postTitle = currPostLiveData.getValue().getTitle();

        String title = "";
        if (postTitle.length() <= 25) {
            title = String.join(" ",
                    getString(R.string.delete_post_title),
                    postTitle
            );
        }
        else {
            title = String.join(" ",
                    getString(R.string.delete_post_title),
                    postTitle.substring(0, 25)
            );
            title += "...";
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(getString(R.string.delete_post_message))
                .setNegativeButton(
                        "No, don't delete",
                        (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        }
                )
                .setPositiveButton(
                        "Yes, delete it",
                        (dialogInterface, i) -> {
                            deletePost();
                            dialogInterface.dismiss();
                        }
                )
                .show();
    }

    private void deletePost() {
        tripPlanService.delete(currPostLiveData.getValue().getId()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Snackbar
                        .make(binding.getRoot(), "Can't delete post", Snackbar.LENGTH_SHORT)
                        .setAction(
                                "Retry",
                                v -> {
                                    deletePost();
                                }
                        ).show();
            }
        });
    }

    private void updateCoverImg(Uri imageUri) {
        try {
            SimpleLoadingDialog loadingDialog = new SimpleLoadingDialog(this);
            loadingDialog.show();

            String imageRealPath = PathUtil.getRealPath(this, imageUri);
            File coverImgFile = new File(imageRealPath);
            RequestBody coverImg = RequestBody.create(MediaType.parse("multipart/form-data"), coverImgFile);
            MultipartBody.Part multipartBodyCoverImg = MultipartBody.Part.createFormData("cover_img", coverImgFile.getName(), coverImg);
            Properties data = new Properties();
            data.put("cover_img", multipartBodyCoverImg);
            data.put("imageRealPath", imageRealPath);
            data.put("loading", loadingDialog);
            callUpdateApi(data,"Upload cover image successfully.", "Can't upload cover image!");
        } catch (Exception err) {
            Log.e(TAG, err.toString());
        }
    }

    private void callUpdateApi(Properties properties, String successMsg, String failureMsg) {
        RequestBody postId = RequestBody.create(MediaType.parse("multipart/form-data"), currPostLiveData.getValue().getId().toString());
        tripPlanService.updatePost(
                        postId,
                        (RequestBody) properties.get("title"),
                        (RequestBody) properties.get("brief_description"),
                        (RequestBody) properties.get("is_public"),
                        (MultipartBody.Part) properties.get("cover_img")
                )
                .enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        if (properties.containsKey("imageRealPath")) {
                            ((SimpleLoadingDialog) properties.get("loading")).dismiss();
                            PathUtil.deleteTempFile(EditableTripPlanDetailActivity.this, (String) properties.get("imageRealPath"));
                        }

                        if (!response.isSuccessful()) return;
                        JsonArray body = response.body();
                        // Update success
                        if (body.get(0).getAsInt() == 1) {
                            TripPlan updatedTripPlan = new Gson().fromJson(body.get(1).getAsJsonObject(), TripPlan.class);
                            currPostLiveData.postValue(updatedTripPlan);

                            if (successMsg != null) {
                                Snackbar
                                    .make(binding.getRoot(), successMsg, Snackbar.LENGTH_SHORT)
                                    .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        if (properties.containsKey("imageRealPath")) {
                            ((SimpleLoadingDialog) properties.get("loading")).dismiss();
                            PathUtil.deleteTempFile(EditableTripPlanDetailActivity.this, (String) properties.get("imageRealPath"));
                        }
                        if (failureMsg != null) {
                            Snackbar
                                    .make(binding.getRoot(), failureMsg, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    // Image chooser
    protected void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
    private void onClickRequestPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
        else {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            requestPermissions(permissions, REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }
}
