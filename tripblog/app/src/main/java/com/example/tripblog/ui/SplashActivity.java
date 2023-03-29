package com.example.tripblog.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.databinding.ActivitySplashBinding;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.login.LoginActivity;
import com.example.tripblog.utils.NetworkUtil;

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
        if (NetworkUtil.isNetworkAvailable(this)) {
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
        else {
            new AlertDialog.Builder(this)
                    .setTitle("No internet")
                    .setMessage("You don't have internet connection !")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
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
                    Response<User> response = authService.retrieveLoggedUserInfo().execute();
                    if (response.code() == 200)
                        TripBlogApplication.getInstance().setLoggedUser(response.body());
                    else if (response.code() == 401) {
                        goToLoginActivity();
                        return;
                    }
                    break;
                } catch (IOException err) {
                    Log.e("Connect err", err.getMessage());
                    countTry++;
                }
            }

            if (countTry == 4) {
                goToLoginActivity();
                return;
            }
            goToMainActivity();
        }
        else {
            goToLoginActivity();
        }
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