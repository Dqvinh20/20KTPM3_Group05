package com.example.tripblog.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.databinding.ActivitySplashBinding;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.login.LoginActivity;

import java.io.IOException;

import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private Boolean isAlive = true;

    private int countTry = 0;

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
                    sleep(2000);
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
            TripBlogApplication.updateToken(sharedPreferences.getString("token", ""));
            AuthService authService = TripBlogApplication.createService(AuthService.class);

            while (countTry < 4) {
                try {
                    Log.d("TOKEN", sharedPreferences.getString("token", ""));
                    Response<User> response = authService.retrieveLoggedUserInfo().execute();
                    TripBlogApplication.getInstance().setLoggedUser(response.body());
                    break;
                } catch (IOException err) {
                    Log.e("Connect err", err.getMessage());
                    countTry++;
                }
            }

            if (countTry == 4) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                return;
            }

            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);
        }
        else {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }
    }
}