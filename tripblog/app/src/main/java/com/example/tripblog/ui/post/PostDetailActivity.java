package com.example.tripblog.ui.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
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
import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.adapter.PostDetailViewPaperAdapter;
import com.example.tripblog.api.services.PostService;
import com.example.tripblog.api.services.UserService;
import com.example.tripblog.databinding.ActivityPostDetailBinding;
import com.example.tripblog.model.Post;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.fragments.ProfileFragment;
import com.example.tripblog.ui.map.MapActivity;
import com.example.tripblog.utils.NumberUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PostDetailActivity.class.getSimpleName();
    protected final String DATE_PATTERN = "MMM d"; // "d/M/yy"
    protected ActivityPostDetailBinding binding;
    protected MutableLiveData<Post> currPostLiveData = new MutableLiveData<>();
    protected PostDetailViewPaperAdapter contentViewPaperAdapter;
    protected final PostService postService = TripBlogApplication.createService(PostService.class);
    protected boolean isEditable = false;
    private boolean isLoading = false;

    public MutableLiveData<Post> getPostLiveData() {
        return currPostLiveData;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
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
        currPostLiveData.observe(this, new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                loadData();
            }
        });

        currPostLiveData.observe(this, new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                Bundle data = new Bundle();
                data.putString("briefDescription", post.getBriefDescription());
                data.putInt("postId", post.getId());
                data.putDouble("avgPoint", post.getAvgRating());
                data.putInt("avgCount", post.getRatingCount());
                contentViewPaperAdapter.refreshFragmentData(0, data);
            }
        });

        fetchData();
        reloadEditableView();
    }
    public void onFragmentLoaded() {
        contentViewPaperAdapter.setEditable(isEditable);
    }
    protected void reloadEditableView() {
        // View pager
        contentViewPaperAdapter = new PostDetailViewPaperAdapter(PostDetailActivity.this);
        binding.contentViewPaper.setAdapter(contentViewPaperAdapter);
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
            currPostLiveData.postValue((Post) bundle.getSerializable("post"));
        }
        else {
            // Load data from internet
            Integer postId = bundle.getInt("postId");

            // Increase view
            if (!isEditable) {
                // View only
                postService.increaseView(postId).enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        if (response.isSuccessful()) {
                            currPostLiveData.postValue(response.body());
                        }
                    }
                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {}
                });
            }

            postService.getPostById(postId).enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    Post post = response.body();
                    if (post.getId() == null) return;
                    currPostLiveData.postValue(post);
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Snackbar
                        .make(binding.getRoot(), "Can't connect to server", Snackbar.LENGTH_SHORT)
                        .show();
                }
            });
        }
    }
    protected void loadData() {
        Post currPost = currPostLiveData.getValue();
        if (currPost == null) return;

        if (currPost.getSchedules() != null) {
            Bundle args = new Bundle();
            args.putSerializable("schedules",(Serializable) currPost.getSchedules());
            contentViewPaperAdapter.refreshFragmentData(1, args);
        }

        binding.tripTitle.setText(currPost.getTitle());
        binding.collapseToolbarLayout.setTitle(currPost.getTitle());
        setTripDatesText(currPost.getStartDate(), currPost.getEndDate(), DATE_PATTERN);

        String formattedViewCount = NumberUtil.formatShorter(currPost.getViewCount());
        binding.viewCountTxt.setText(String.join(" ",formattedViewCount , getString(R.string.view_txt)));

        String formattedLikeCount = NumberUtil.formatShorter(currPost.getLikeCount());
        binding.likeBtn.setText(String.join(" ", formattedLikeCount, getString(R.string.like_btn_txt)));
        binding.likeBtn.setChecked(currPost.isLikedByYou());

        // Load cover img
        Glide.with(binding.getRoot())
                .load(currPost.getCoverImg())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.coverImg);
        
        // Load author avatar
        Glide.with(binding.getRoot())
                .load(currPost.getAuthor().getAvatar())
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
            UserService userService = TripBlogApplication.createService(UserService.class);
            Post post = currPostLiveData.getValue();
            int likedCount = post.getLikeCount();
            boolean isLiked = post.isLikedByYou();

            if (isLiked) {
                executorService.execute(() -> {
                    try {
                        userService.unlikePost(post.getId()).execute();
                        isLoading = false;
                    } catch (IOException e) {}
                });
            }
            else {
                executorService.execute(() -> {
                    try {
                        userService.likePost(post.getId()).execute();
                        isLoading = false;
                    } catch (IOException e) {}
                });
            }
            executorService.shutdown();
            post.setLikedByYou(!isLiked);
            post.setLikeCount(likedCount + (!isLiked ? 1 : -1));

            // Update like button text
            String formattedLikeCount = NumberUtil.formatShorter(post.getLikeCount());
            binding.likeBtn.setText(String.join(" ", formattedLikeCount, getString(R.string.like_btn_txt)));
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
            User loggedUser = TripBlogApplication.getInstance().getLoggedUser();
            if (!author.getId().equals(loggedUser.getId())) {
                // TODO: Open author profile
                Intent intent = new Intent(PostDetailActivity.this, ProfileFragment.class);
//                startActivity(in);
            }
        }
    }
}