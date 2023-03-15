package com.example.tripblog.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.tripblog.R;
import com.example.tripblog.databinding.ActivityMainBinding;
import com.example.tripblog.ui.fragments.HomeFragment;
import com.example.tripblog.ui.fragments.CreateFragment;
import com.example.tripblog.ui.fragments.ProfileFragment;
import com.example.tripblog.ui.post.ViewPost;
import com.example.tripblog.ui.search.Search;

public class MainActivity extends AppCompatActivity implements MainCallbacks{

    ActivityMainBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.create) {
                replaceFragment(new CreateFragment());
            }
            return true;
        });


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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if(sender.equals("CREATE_TRIP_BTN")){
            replaceFragment(new CreateFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.create);
        }
    }
}