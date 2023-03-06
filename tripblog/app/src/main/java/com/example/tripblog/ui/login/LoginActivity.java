package com.example.tripblog.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.api.RetrofitClient;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.model.AuthResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;

import org.json.JSONStringer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout editEmailLayout, editPasswordLayout;
    TextInputEditText editEmail, editPassword;
    Button loginBtn;
    TextView forgotPasswordTxt, signupTxt;

    private final Retrofit retrofitClient = RetrofitClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        setContentView(R.layout.activity_login);

        editEmailLayout = (TextInputLayout) findViewById(R.id.editEmailLayout);
        editEmail = (TextInputEditText) findViewById(R.id.editEmail);

        editPasswordLayout = (TextInputLayout) findViewById(R.id.editPasswordLayout);
        editPassword = (TextInputEditText) findViewById(R.id.editPassword);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(LoginActivity.this);

        forgotPasswordTxt = (TextView) findViewById(R.id.forgotPasswordTxt);
        forgotPasswordTxt.setOnClickListener(LoginActivity.this);
        signupTxt = (TextView) findViewById(R.id.signupTxt);
        signupTxt.setOnClickListener(LoginActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signupTxt) {
            goToSignup();
        }
        else if (view.getId() == R.id.forgotPasswordTxt) {
            goToForgotPassword();
        }
        else if (view.getId() == R.id.loginBtn) {
            login();
        }
    }

    private void login() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        AuthService authService = retrofitClient.create(AuthService.class);
        authService.login(email, password).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                // TODO: Need to validate data before send req
                AuthResponse body = response.body();
                if (body.getStatus() == "failure" || body.getStatus() == "error"){
                    if (!(body.getError() instanceof JsonArray)){
                        editEmailLayout.setError(body.getError().getAsString());
                        editEmailLayout.invalidate();
                    }
                }
                Log.d("LOGIN", body.toString());
                Toast.makeText(LoginActivity.this, body.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN", t.toString());
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goToForgotPassword() {
        // TODO: Implement go to forgot password activity
    }

    private void goToSignup() {
        // TODO: Implement go to signup activity
    }
}