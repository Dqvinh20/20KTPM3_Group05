package com.example.tripshare.ui.tripPlan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.tripshare.R;
import com.example.tripshare.TripShareApplication;
import com.example.tripshare.adapter.viewpaper.PostDetailViewPaperAdapter;
import com.example.tripshare.api.services.TripPlanService;
import com.example.tripshare.api.services.UserService;
import com.example.tripshare.databinding.ActivityTripPlanDetailBinding;
import com.example.tripshare.model.TripPlan;
import com.example.tripshare.model.User;
import com.example.tripshare.ui.MainActivity;
import com.example.tripshare.utils.NumberUtil;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class TripPlanDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = TripPlanDetailActivity.class.getSimpleName();
    protected final String DATE_PATTERN = "MMM d"; // "d/M/yy"
    protected ActivityTripPlanDetailBinding binding;
    protected MutableLiveData<TripPlan> currPostLiveData = new MutableLiveData<>();
    protected PostDetailViewPaperAdapter contentViewPaperAdapter;
    protected final TripPlanService tripPlanService = TripShareApplication.createService(TripPlanService.class);
    protected boolean isEditable = false;
    private boolean isLoading = false;

    public MutableLiveData<TripPlan> getPostLiveData() {
        return currPostLiveData;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");

        binding = ActivityTripPlanDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Top app bar
        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int collapseToolbarHeight = binding.collapseToolbarLayout.getHeight();
            if (collapseToolbarHeight + verticalOffset < (2 * ViewCompat.getMinimumHeight(binding.collapseToolbarLayout))) {
                binding.toolbar.getNavigationIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            }
            else {
                binding.toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        });

        binding.likeBtn.setOnClickListener(this);
        binding.shareBtn.setOnClickListener(this);
        binding.authorAvatar.setOnClickListener(this);

        // Auto reload view
        currPostLiveData.observe(this, new Observer<TripPlan>() {
            @Override
            public void onChanged(TripPlan tripPlan) {
                loadData();
            }
        });

        currPostLiveData.observe(this, new Observer<TripPlan>() {
            @Override
            public void onChanged(TripPlan tripPlan) {
                Bundle data = new Bundle();
                data.putString("briefDescription", tripPlan.getBriefDescription());
                data.putInt("postId", tripPlan.getId());
                data.putDouble("avgPoint", tripPlan.getAvgRating());
                data.putInt("avgCount", tripPlan.getRatingCount());
                contentViewPaperAdapter.setFragmentData(0, data);
            }
        });

        fetchData();
        reloadEditableView();
    }
    protected void reloadEditableView() {
        // View pager
        contentViewPaperAdapter = new PostDetailViewPaperAdapter(TripPlanDetailActivity.this);
        contentViewPaperAdapter.setEditable(isEditable);

        binding.contentViewPaper.setAdapter(contentViewPaperAdapter);
        if (isEditable) {
            binding.contentViewPaper.setPadding(0,0,0, 0);
        }
        binding.tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        boolean isOverviewFragment = tab.getText().toString().equals("Overview");
                        binding.appbar.setExpanded(isOverviewFragment);
                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                }
        );

        new TabLayoutMediator(binding.tabLayout, binding.contentViewPaper, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Overview");
                    break;
                case 1:
                    tab.setText("Schedule");
                    break;
                default:
                    break;
            }
        }).attach();
        binding.tripTitle.setTextIsSelectable(isEditable);
        binding.tripTitle.setFocusable(isEditable);
        binding.bottomAppBar.setVisibility(isEditable ? View.GONE : View.VISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void setTripDatesText(Date startDate, Date endDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern == null ? "dd/MM/yy" : pattern, Locale.US);
        Long today = MaterialDatePicker.todayInUtcMilliseconds();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(today);

        int currYear = calendar.get(Calendar.YEAR);

        calendar.setTime(startDate);
        int startYear = calendar.get(Calendar.YEAR);
        sdf.applyPattern(currYear == startYear ? pattern : pattern + ",yyyy");
        String formatedStartDate = sdf.format(startDate);

        calendar.setTime(endDate);
        int endYear = calendar.get(Calendar.YEAR);
        sdf.applyPattern(currYear == endYear ? pattern : pattern + ",yyyy");
        String formatedEndDate = sdf.format(endDate);

        String tripDatesTxt = String.format("%s - %s",
                formatedStartDate,
                formatedEndDate
        );

        binding.tripDates.setText(tripDatesTxt);
    }
    public void fetchData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null && bundle.containsKey("post")) {
            // Load from bundle
            currPostLiveData.postValue((TripPlan) bundle.getSerializable("post"));
        }
        else {
            // Load data from internet
            Integer postId = bundle.getInt("postId");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    if (!isEditable) {
                        Response<TripPlan> response = tripPlanService.increaseView(postId).execute();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Increased view");
                        }
                    }

                    Response<TripPlan> tripPlanResponse = tripPlanService.getTripPlanById(postId).execute();
                    if (tripPlanResponse.isSuccessful()) {
                        TripPlan tripPlan = tripPlanResponse.body();
                        Log.d(TAG, "Get data successfully!");
                        if (tripPlan.getId() == null) return;
                        runOnUiThread(() -> {
                            currPostLiveData.postValue(tripPlan);
                        });
                    }
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        Snackbar
                                .make(binding.getRoot(), "Fetch data error!", Snackbar.LENGTH_SHORT)
                                .show();
                    });
                }
            });
            executorService.shutdown();
        }
    }
    protected void loadData() {
        TripPlan currTripPlan = currPostLiveData.getValue();
        if (currTripPlan == null) return;

        if (currTripPlan.getSchedules() != null) {
            Bundle args = new Bundle();
            args.putSerializable("schedules",(Serializable) currTripPlan.getSchedules());
            contentViewPaperAdapter.setFragmentData(1, args);
        }

        binding.tripTitle.setText(currTripPlan.getTitle());
        binding.collapseToolbarLayout.setTitle(currTripPlan.getTitle());
        setTripDatesText(currTripPlan.getStartDate(), currTripPlan.getEndDate(), DATE_PATTERN);

        String formattedViewCount = NumberUtil.formatShorter(currTripPlan.getViewCount());
        binding.viewCountTxt.setText(String.join(" ",formattedViewCount , getString(R.string.view_txt)));

        String formattedLikeCount = NumberUtil.formatShorter(currTripPlan.getLikeCount());
        binding.likeBtn.setText(String.join(" ", formattedLikeCount, getString(R.string.like_btn_txt)));
        binding.likeBtn.setChecked(currTripPlan.isLikedByYou());

        // Load cover img
        Glide.with(binding.getRoot())
                .load(currTripPlan.getCoverImg())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.coverImg);

        // Load author avatar
        Glide.with(binding.getRoot())
                .load(currTripPlan.getAuthor().getAvatar())
                .centerCrop()
                .into(binding.authorAvatar);

        binding.contentViewPaper.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.likeBtn) {
            if (isLoading) return;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            UserService userService = TripShareApplication.createService(UserService.class);
            TripPlan tripPlan = currPostLiveData.getValue();
            int likedCount = tripPlan.getLikeCount();
            boolean isLiked = tripPlan.isLikedByYou();
            executorService.execute(() -> {
                try {
                    if (isLiked) {
                        userService.unlikePost(tripPlan.getId()).execute();
                    }
                    else {
                        userService.likePost(tripPlan.getId()).execute();
                    }
                }
                catch (IOException e) {}
                finally {
                    isLoading = false;
                    runOnUiThread(() -> {
                        tripPlan.setLikedByYou(!isLiked);
                        tripPlan.setLikeCount(likedCount + (!isLiked ? 1 : -1));

                        // Update like button text
                        String formattedLikeCount = NumberUtil.formatShorter(tripPlan.getLikeCount());
                        binding.likeBtn.setText(String.join(" ", formattedLikeCount, getString(R.string.like_btn_txt)));
                    });
                }
            });
            executorService.shutdown();
        }
        else if (view.getId() == R.id.shareBtn) {
            String link = "https://tripblog.com?postId=" + currPostLiveData.getValue().getId();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, link);
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Share this trip");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
        else if (view.getId() == R.id.authorAvatar) {
            User author = currPostLiveData.getValue().getAuthor();
            if (author == null || author.getId() == TripShareApplication.getInstance().getLoggedUser().getId()) {
                return;
            }

            Intent result = new Intent();
            Bundle data = new Bundle();
            data.putInt("userId", author.getId());
            result.putExtras(data);
            setResult(MainActivity.TRIP_PLAN_REQ_CODE, result);
            finishAfterTransition();
        }
    }
}