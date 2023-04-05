package com.example.tripblog.ui.signup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.databinding.ActivitySignupBinding;
import com.example.tripblog.model.response.AuthResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = SignupActivity.class.getSimpleName();
    MaterialAlertDialogBuilder loading = null;
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setTitle(null);

        binding.signupBtn.setOnClickListener(this);

        binding.editEmail.addTextChangedListener(new ValidationTextWatcher(binding.editEmail));
        binding.editPassword.addTextChangedListener(new ValidationTextWatcher(binding.editPassword));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void signup() {
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
        if (loading == null) {
            loading = new MaterialAlertDialogBuilder(SignupActivity.this);
            loading.setView(R.layout.loading);
            loading.setBackground(getDrawable(android.R.color.transparent));
            loading.setCancelable(false);
        }
        AlertDialog loadingDialog = loading.show();
        AuthService authService = TripBlogApplication.createService(AuthService.class);

        Log.d(TAG, "OnSignupBtn Press");
        Log.d(TAG, "Waiting response");
        authService.signup(email, password).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                AuthResponse body = response.body();
                Log.d(TAG, "Received response successfully");

                if (body.getStatus().equals( "failure")){
                    binding.editEmailLayout.setError(body.getError().getAsString());
                    loadingDialog.dismiss();
                    return;
                }
                else if (body.getStatus().equals( "error")) {
                    binding.editEmailLayout.setError("Unexpected error occur ! Try again !!!");
                    loadingDialog.dismiss();
                    Snackbar
                            .make(binding.getRoot(), "Unexpected error occur!", Snackbar.LENGTH_LONG)
                            .setAction("Retry", view -> {
                                signup();
                            })
                            .show();
                    return;
                }

                loadingDialog.dismiss();
                Snackbar
                        .make(binding.getRoot(), "Signup successfully", Snackbar.LENGTH_SHORT)
                        .setDuration(200)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                finishAfterTransition();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Snackbar
                        .make(binding.getRoot(), "Can't connect to server!", Snackbar.LENGTH_LONG)
                        .setAction("Retry", view -> {
                            signup();
                        })
                        .show();
                loadingDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!validateEmail()) return;
        if (!validatePassword()) return;
        if (!binding.editConfirmPassword.getText().toString()
                .equals(binding.editPassword.getText().toString())) {
            binding.editConfirmPasswordLayout.setError(getString(R.string.invalid_confirm_pass_err));
            return;
        }
        signup();
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

}
