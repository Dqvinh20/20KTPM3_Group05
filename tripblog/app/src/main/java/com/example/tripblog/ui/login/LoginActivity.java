package com.example.tripblog.ui.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.tripblog.R;
import com.example.tripblog.TripShareApplication;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.databinding.ActivityLoginBinding;
import com.example.tripblog.model.response.AuthResponse;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.dialog.SimpleLoadingDialog;
import com.example.tripblog.ui.resetpassword.ResetPassword;
import com.example.tripblog.ui.signup.SignupActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = LoginActivity.class.getSimpleName();
    ActivityLoginBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            Bundle data = result.getData().getExtras();
                            String email = data.getString("email");
                            binding.editEmail.setText(email);
                        }
                    }
                }
        );

        binding.loginBtn.setOnClickListener(this);
        binding.forgotPasswordBtn.setOnClickListener(this);
        binding.signupBtn.setOnClickListener(this);

        binding.editEmail.addTextChangedListener(new ValidationTextWatcher(binding.editEmail));
        binding.editPassword.addTextChangedListener(new ValidationTextWatcher(binding.editPassword));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signupBtn) {
            goToSignup();
        }
        else if (view.getId() == R.id.forgotPasswordBtn) {
            goToForgotPassword();
        }
        else if (view.getId() == R.id.loginBtn) {
            if (!validateEmail()) return;
            if (!validatePassword()) return;
            login();
        }
    }
    private void requestFocus(View v) {
        if  (v.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateEmail() {
        String email = binding.editEmail.getText().toString();
        if (email.trim().isEmpty()) {
            binding.editEmailLayout.setError(null);
        }
        else {
            boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (isValidEmail) {
                binding.editEmailLayout.setError(null);
            }
            else {
                binding.editEmailLayout.setError(getString(R.string.invalid_email_err));
                requestFocus(binding.editEmail);
                return false;
            }
        }
        return true;
    }

    private boolean validatePassword() {
        String password = binding.editPassword.getText().toString();
        if (password.trim().isEmpty()) {
            binding.editPasswordLayout.setError(null);
        }else if(password.length() < 6){
            binding.editPasswordLayout.setError(getString(R.string.invalid_pass_length_err));
            requestFocus(binding.editPassword);
            return false;
        }
        else {
            binding.editPasswordLayout.setError(null);
        }
        return true;
    }

    private void login() {
        String email = binding.editEmail.getText().toString();
        String password = binding.editPassword.getText().toString();
        if (email.isEmpty()) {
            binding.editEmailLayout.setError(getString(R.string.email_required_err));
            requestFocus(binding.editEmail);
            return;
        }

        if (password.isEmpty()) {
            binding.editPasswordLayout.setError(getString(R.string.pass_required_err));
            requestFocus(binding.editPassword);
            return;
        }

        SimpleLoadingDialog loadingDialog = new SimpleLoadingDialog(this);

        loadingDialog.show();

        AuthService authService = TripShareApplication.createService(AuthService.class);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Response<AuthResponse> response = authService.login(email, password).execute();
                AuthResponse body = response.body();
                runOnUiThread(() -> {
                    if (body.getStatus().equals( "failure")){
                        binding.editEmailLayout.setError(body.getError().getAsString());
                        loadingDialog.dismiss();
                        return;
                    }
                    else if (body.getStatus().equals( "error")) {
                        loadingDialog.dismiss();
                        Snackbar
                                .make(binding.getRoot(), "Unexpected error occur!", Snackbar.LENGTH_LONG)
                                .setAction("Retry", view -> {
                                    login();
                                })
                                .show();
                        return;
                    }

                    // Save logged user
                    JsonElement userJson = body.getData().getAsJsonObject().get("user");
                    User loggedUser = new Gson().fromJson(userJson, User.class);
                    TripShareApplication.getInstance().setLoggedUser(loggedUser);

                    String token = body.getData().getAsJsonObject().get("token").getAsString();
                    SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                    sharedPreferences.edit().putString("token", token).commit();
                    TripShareApplication.updateToken(token); // Save token for next req

                    // Go to main
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    loadingDialog.dismiss();
                    startActivity(intent);
                    finishAfterTransition();
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Snackbar
                            .make(binding.getRoot(), "Can't connect to server!", Snackbar.LENGTH_LONG)
                            .setAction("Retry", view -> {
                                login();
                            })
                            .show();
                });

            }
        });
        executorService.shutdown();
    }

    private void goToForgotPassword() {
        Intent reset = new Intent(LoginActivity.this, ResetPassword.class);
        startActivity(reset);
    }

    private void goToSignup() {
        Intent signup = new Intent(LoginActivity.this, SignupActivity.class);
        activityResultLauncher.launch(signup);
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;
        private ValidationTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.editPassword:
                    binding.editPasswordLayout.setError(null);
                    break;
                case R.id.editEmail:
                    binding.editEmailLayout.setError(null);
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
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}