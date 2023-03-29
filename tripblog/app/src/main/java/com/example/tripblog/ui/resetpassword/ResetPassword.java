package com.example.tripblog.ui.resetpassword;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
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
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.databinding.ActivityLoginBinding;
import com.example.tripblog.databinding.ActivityResetPasswordBinding;
import com.example.tripblog.model.AuthResponse;
import com.example.tripblog.ui.login.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity implements  View.OnClickListener   {
    MaterialAlertDialogBuilder loading = null;

    ActivityResetPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setTitle(null);

        binding.resetBtn.setOnClickListener(this);
        binding.editEmail.addTextChangedListener(new ValidationTextWatcher(binding.editEmail));
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void showDialogMessage(String title, String message, Runnable runnable)
    {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        runnable.run();
                    }
                })
                .show();

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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onClick(View view) {
        String email = binding.editEmail.getText().toString().trim();
        AuthService authService = TripBlogApplication.createService(AuthService.class);


        if (loading == null) {
            loading = new MaterialAlertDialogBuilder(ResetPassword.this);
            loading.setView(R.layout.loading);
            loading.setBackground(getDrawable(android.R.color.transparent));
            loading.setCancelable(false);
        }
        AlertDialog loadingDialog = loading.show();

        authService.resetPassword(email).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                loadingDialog.dismiss();
                AuthResponse authResponse = response.body();

                if (authResponse.getStatus() == "success"){
                    showDialogMessage(getString(R.string.success), getString(R.string.success_message), () -> {
                        finish();
                    });
                }
                else {
                    showDialogMessage(getString(R.string.failure), getString(R.string.failure_message), () -> {
                    });
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                loadingDialog.dismiss();

                Snackbar
                        .make(binding.getRoot(), "Can't connect to server!", Snackbar.LENGTH_LONG)
                        .setAction("Retry", view -> {
                            onClick(view);
                        })
                        .show();
            }
        });
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;
        private ValidationTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {

                case R.id.editEmail:
                    binding.editEmailLayout.setError(null);
                    break;
            }
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.editEmail:
                    validateEmail();
                    break;
            }
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
    private void requestFocus(View v) {
        if (v.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}