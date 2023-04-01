package com.example.tripblog.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.tripblog.R;
import com.example.tripblog.databinding.ActivityMainBinding;
import com.example.tripblog.ui.fragments.HomeFragment;
import com.example.tripblog.ui.fragments.CreateFragment;
import com.example.tripblog.ui.fragments.ProfileFragment;
import com.example.tripblog.ui.post.EditablePostDetailActivity;
import com.example.tripblog.ui.search.Search;
import com.example.tripblog.ui.editprofile.EditProfile;

public class MainActivity extends AppCompatActivity implements MainCallbacks{
    private String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    private Integer currFragment = null;
    private long lastClickTime = 0;

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
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            // Open create post dialog
//                Intent intent = new Intent(MainActivity.this, EditablePostDetailActivity.class);
//                intent.putExtra("postId", 21);
//                startActivity(intent);
//            displayCreatePostDialog();
//            ImagePreviewDialog imagePreviewDialog = new ImagePreviewDialog(this);
//            imagePreviewDialog.show();

        });
        binding.create.performClick();
//        logout();
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