package com.example.tripblog.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.databinding.ActivityMainBinding;
import com.example.tripblog.services.MainService;
import com.example.tripblog.ui.dialog.ImagePreviewDialog;
import com.example.tripblog.ui.fragments.HomeFragment;
import com.example.tripblog.ui.fragments.CreateFragment;
import com.example.tripblog.ui.fragments.ProfileFragment;
import com.example.tripblog.ui.post.EditablePostDetailActivity;
import com.example.tripblog.ui.post.PostDetailActivity;
import com.example.tripblog.ui.search.Search;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity implements MainCallbacks{
    private String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    private Integer currFragment = null;
    private long lastClickTime = 0;
    private NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (currFragment != null && item.getItemId() == currFragment) {
                return false;
            }

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }

            currFragment = item.getItemId();
            return true;
        });

        binding.create.setOnClickListener((v) -> {
            // Prevent double click
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            // Open create post dialog
//                Intent intent = new Intent(MainActivity.this, PostDetailActivity.class);
//                intent.putExtra("postId", 21);
//                startActivity(intent);
//            displayCreatePostDialog();
//            ImagePreviewDialog imagePreviewDialog = new ImagePreviewDialog(this);
//            imagePreviewDialog.show();

        });

        binding.create.performClick();
        notificationManagerCompat = NotificationManagerCompat.from(this);
        Log.i("MainActivity","Check");
        Intent Serviceintent = new Intent(this, MainService.class);
        if (!isMyServiceRunning(MainService.class)) {
            Log.i("MainActivity","Start service");
            startService(Serviceintent);
        }

//        logout();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }



    private void displayCreatePostDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CreateFragment createFragment = CreateFragment.newInstance();
        createFragment.show(fragmentTransaction, "CREATE");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent=new Intent(MainActivity.this, Search.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intent, 1122);
                // User chose the "Settings" item, show the app settings UI...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_trip_and_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void logout() {
        // Use to logout when debug
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        sharedPreferences.edit().putString("token", "").commit();
    }

    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
        finish();
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if(sender.equals("CREATE_TRIP_BTN")){
            displayCreatePostDialog();
        }
    }
}