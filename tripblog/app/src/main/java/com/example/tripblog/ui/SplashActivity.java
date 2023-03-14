package com.example.tripblog.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.example.tripblog.R;
import com.example.tripblog.databinding.ActivitySplashBinding;
import com.example.tripblog.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private Boolean isAlive = true;

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread thread = new Thread() {
            @Override
            public void run() {
                try{
                    sleep(5000);
                    if (isAlive) {
                        checkCredential();
                        finish();
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        isAlive = false;
        super.onDestroy();
    }

    private void checkCredential() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);

        Boolean isAuth = !sharedPreferences.getString("token", "").isEmpty();
        if (isAuth) {
            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);
        }
        else {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }
    }
}