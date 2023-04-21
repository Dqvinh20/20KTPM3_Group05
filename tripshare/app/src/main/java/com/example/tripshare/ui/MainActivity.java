package com.example.tripshare.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tripshare.R;
import com.example.tripshare.TripShareApplication;
import com.example.tripshare.databinding.ActivityMainBinding;
import com.example.tripshare.ui.fragments.HomeFragment;
import com.example.tripshare.ui.fragments.CreateFragment;
import com.example.tripshare.ui.fragments.ProfileFragment;
import com.example.tripshare.ui.interfaces.MainCallbacks;
import com.example.tripshare.ui.search.Search;
import com.google.android.material.snackbar.Snackbar;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionDeniedResponse;
//import com.karumi.dexter.listener.PermissionGrantedResponse;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    public static int TRIP_PLAN_REQ_CODE = 1;
    public static int SEARCH_REQ_CODE = 2;

    private String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding binding;
    private long lastClickTime = 0;
    private NotificationManagerCompat notificationManagerCompat;
    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = ProfileFragment.newInstance(TripShareApplication.getInstance().getLoggedUser().getId(), true);

    ActivityResultLauncher<Intent> activityResultLauncher;
    public ActivityResultLauncher<Intent> getActivityResultLauncher() {
        return activityResultLauncher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getData() != null) {
                            if (result.getResultCode() == TRIP_PLAN_REQ_CODE) {
                                Bundle data = result.getData().getExtras();
                                Integer userId = data.getInt("userId");
                                openUserProfile(userId);
                            }
                            else if (result.getResultCode() == SEARCH_REQ_CODE) {
                                Bundle data = result.getData().getExtras();
                                Integer userId = data.getInt("userId");
                                openUserProfile(userId);
                            }
                        }
                    }
                }
        );

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(homeFragment, homeFragment.TAG);
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(profileFragment, profileFragment.TAG);
            }
            return true;
        });
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        binding.create.setOnClickListener((v) -> {
            // Prevent double click
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();
            // Open create post dialog
            displayCreateTripPlanDialogFragment();
        });

        notificationManagerCompat = NotificationManagerCompat.from(this);

//        Intent Serviceintent = new Intent(this, MainService.class);
//        if (!isMyServiceRunning(MainService.class)) {
//            Log.i("MainActivity","Start service");
//            startService(Serviceintent);
//        }
    }

    public void showSnackBarInfo(String text) {
        Snackbar.make(binding.getRoot(), text, Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.create)
                .show();
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

    private void displayCreateTripPlanDialogFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CreateFragment createFragment = CreateFragment.newInstance();
        createFragment.show(fragmentTransaction, CreateFragment.class.getSimpleName());
    }

    public void onCreateDismis() {
        if (profileFragment.isAdded()) {
            profileFragment.onResume();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent=new Intent(MainActivity.this, Search.class);
                intent.setAction(Intent.ACTION_VIEW);
                activityResultLauncher.launch(intent);
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
        getMenuInflater().inflate(R.menu.search_trip_and_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void replaceFragment(Fragment fragment, String name) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();

        if (name == HomeFragment.TAG) {
            showAppBar();
        }
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if(sender.equals("CREATE_TRIP_BTN")){
            displayCreateTripPlanDialogFragment();
        }
    }
    public void openUserProfile(Integer userId) {
        Fragment fragment = ProfileFragment.newInstance(userId, false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout, fragment);
        transaction.addToBackStack(ProfileFragment.TAG);
        transaction.commit();
    }

    public void showAppBar() {
        getSupportActionBar().setShowHideAnimationEnabled(false);
        getSupportActionBar().show();
    }

    public void hideAppBar() {
        getSupportActionBar().hide();
    }
}