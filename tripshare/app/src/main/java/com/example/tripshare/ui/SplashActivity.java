package com.example.tripshare.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.tripshare.R;
import com.example.tripshare.TripShareApplication;
import com.example.tripshare.api.services.AuthService;
import com.example.tripshare.databinding.ActivitySplashBinding;
import com.example.tripshare.model.User;
import com.example.tripshare.ui.login.LoginActivity;
import com.example.tripshare.utils.NetworkUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private boolean isAlive = true;
    ActivitySplashBinding binding;

    Runnable checkCredentialInBackground = () -> {
        try{
            Thread.sleep(500);
            if (isAlive) {
                checkCredential();
                finish();
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAlive = true;

        if (!NetworkUtil.isNetworkAvailable(this)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.error_title))
                    .setMessage(getString(R.string.no_internet_description))
                    .setPositiveButton(getString(R.string.try_again), (dialogInterface, i) -> {
                        onStart();
                    })
                    .setNegativeButton(getString(R.string.exit), (dialogInterface, i) -> finish())
                    .show();
            return;
        }

        Thread thread = new Thread(checkCredentialInBackground);
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
        String token = sharedPreferences.getString("token", "");

        if (!token.isEmpty()) {
            TripShareApplication.updateToken(token);
            AuthService authService = TripShareApplication.createService(AuthService.class);
            Arrays.stream(authService.getClass().getInterfaces()).forEach(aClass -> {
                Log.d(SplashActivity.class.getSimpleName(), aClass.getSimpleName());
            });
            char countTry = 0;
            while (countTry < 4) {
                try {
                    Response<User> response = authService.retrieveLoggedUserInfo().execute();

                    if (response.isSuccessful()) {
                        // Save logged user info
                        TripShareApplication.getInstance().setLoggedUser(response.body());
                        goToMainActivity();
                    }
                    else {
                        goToLoginActivity();
                    }

                    return;
                } catch (IOException err) {
                    countTry++;
                }
            }
        }

        goToLoginActivity();
        sharedPreferences.edit().putString("token", "");
    }

    private void goToMainActivity() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }

    private void goToLoginActivity() {
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(login);
    }
}