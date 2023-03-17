package com.example.tripblog.ui.post;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.tripblog.R;
import com.example.tripblog.adapter.PostDetailViewPaperAdapter;
import com.example.tripblog.databinding.ActivityPostDetailBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostDetailActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private MaterialDatePicker tripDates;
    ActivityPostDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int collapseToolbarHeight = binding.collapseToolbarLayout.getHeight();
            if (collapseToolbarHeight + verticalOffset < (2 * ViewCompat.getMinimumHeight(binding.collapseToolbarLayout))) {
                binding.toolbar.getNavigationIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                binding.toolbar.getMenu().getItem(0).setVisible(false);
            }
            else {
                binding.toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                binding.toolbar.getMenu().getItem(0).setVisible(true);
            }
        });

        PostDetailViewPaperAdapter contentViewPaperAdapter = new PostDetailViewPaperAdapter(this);
        binding.contentViewPaper.setAdapter(contentViewPaperAdapter);
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

        binding.tripDates.setOnClickListener((view) -> {
            editTripDates();
        });
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

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK  && result.getData() != null) {
                        Log.d("IMG", String.valueOf(result.getData().getData()));
                        try {
                            final Uri imageUri = result.getData().getData();
                            // Cach 1
                            // final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            // Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                            // Cach 2
                            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            binding.appBarImage.setImageBitmap(selectedImage);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.post_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i("MENU", String.valueOf(item.getItemId()));
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.coverImgChooser:
                onClickRequestPermission();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editTripDates() {
        if (tripDates == null) {
            tripDates = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Trip dates")
                    .build();

            tripDates.addOnPositiveButtonClickListener((selection -> {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                Pair<Long, Long> dates = (Pair<Long, Long>) selection;
                cal.setTimeInMillis(dates.first);
                cal.setTimeInMillis(dates.second);
            }));
        }

        tripDates.show(getSupportFragmentManager(), "TRIP-DATES-PICKER");
    }
    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

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

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
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