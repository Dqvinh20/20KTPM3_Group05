package com.example.tripblog.ui.signup;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tripblog.R;
import com.example.tripblog.api.RetrofitClient;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.model.AuthResponse;
import com.example.tripblog.ui.login.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "SIGNUP";
    TextInputLayout editEmailLayout, editPasswordLayout;
    TextInputEditText editEmail, editPassword;
    Button loginBtn;
    private final Retrofit retrofitClient = RetrofitClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setTitle(null);
        Log.d("Signup", "Created");

        editEmailLayout = (TextInputLayout) findViewById(R.id.editEmailLayout);
        editEmail = (TextInputEditText) findViewById(R.id.editEmail);

        editPasswordLayout = (TextInputLayout) findViewById(R.id.editPasswordLayout);
        editPassword = (TextInputEditText) findViewById(R.id.editPassword);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(SignupActivity.this);

        editEmail.addTextChangedListener(new ValidationTextWatcher(editEmail));
        editPassword.addTextChangedListener(new ValidationTextWatcher(editPassword));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        if (email.isEmpty()) {
            editEmailLayout.setError("Email is required");
            requestFocus(editEmail);
            return;
        }

        if (password.isEmpty()) {
            editPasswordLayout.setError("Password is required");
            requestFocus(editPassword);
            return;
        }

        AuthService authService = retrofitClient.create(AuthService.class);
        authService.signup(email, password).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                AuthResponse body = response.body();
                if (body.getStatus().equals( "failure")){
                    editEmailLayout.setError(body.getError().getAsString());
                    return;
                }
                else if (body.getStatus().equals( "error")) {
                    JsonArray errors = (JsonArray) body.getError();
                    Log.d(TAG, errors.toString());
                    JsonObject content = errors.get(0).getAsJsonObject();
                    if (content.get("param").getAsString().equals("email")) {
                        editEmailLayout.setError(content.get("msg").toString());
                    }
                }

                // TODO: Need to save jwt token
                Toast.makeText(SignupActivity.this, body.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }
    private void requestFocus(View v) {
        if  (v.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateEmail() {
        String email = editEmail.getText().toString();
        if (email.trim().isEmpty()) {
            editEmailLayout.setError(null);
        }
        else {
            boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (isValidEmail) {
                editEmailLayout.setError(null);
            }
            else {
                editEmailLayout.setError("Invalid E-mail address");
                requestFocus(editEmail);
                return false;
            }
        }

        return true;
    }

    private boolean validatePassword() {
        String password = editPassword.getText().toString();
        if (password.trim().isEmpty()) {
            editPasswordLayout.setError(null);
        }else if(password.length() < 6){
            editPasswordLayout.setError("Password can't be less than 6 characters");
            requestFocus(editPassword);
            return false;
        }
        else {
            editPasswordLayout.setError(null);
        }
        return true;
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;
        private ValidationTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.editPassword:
                    editPasswordLayout.setError(null);
                    break;
                case R.id.editEmail:
                    editEmailLayout.setError(null);
                    break;
            }
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editPassword:
                    validatePassword();
                    break;
                case R.id.editEmail:
                    validateEmail();
                    break;
            }
        }
    }

}
