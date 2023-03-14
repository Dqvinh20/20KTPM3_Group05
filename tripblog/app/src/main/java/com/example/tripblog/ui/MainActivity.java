package com.example.tripblog.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.tripblog.R;
import com.example.tripblog.databinding.ActivityMainBinding;
import com.example.tripblog.ui.fragments.HomeFragment;
import com.example.tripblog.ui.fragments.CreateFragment;
import com.example.tripblog.ui.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements MainCallbacks{
    ActivityMainBinding binding;
    private Integer currFragment = null;
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
            if (currFragment != null && v.getId() == currFragment) {
                return;
            }
            currFragment = v.getId();
            replaceFragment(new CreateFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.places_holder);
        });

        binding.create.setOnClickListener((v) -> {
            if (currFragment != null && v.getId() == currFragment) {
                return;
            }
            currFragment = v.getId();
            replaceFragment(new CreateFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.places_holder);
        });
    }

    private void logout() {
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
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if(sender.equals("CREATE_TRIP_BTN")){
            replaceFragment(new CreateFragment());
            currFragment = binding.create.getId();
            binding.bottomNavigationView.setSelectedItemId(R.id.places_holder);
            currFragment = binding.create.getId();
            binding.bottomNavigationView.setSelectedItemId(R.id.places_holder);
        }
    }
}