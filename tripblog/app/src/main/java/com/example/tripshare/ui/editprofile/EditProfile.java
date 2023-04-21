package com.example.tripshare.ui.editprofile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.tripshare.R;
import com.example.tripshare.TripShareApplication;
import com.example.tripshare.api.services.UserService;
import com.example.tripshare.databinding.ActivityUpdateProfileBinding;
import com.example.tripshare.model.User;
import com.example.tripshare.ui.dialog.SimpleLoadingDialog;
import com.example.tripshare.utils.PathUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = EditProfile.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    private final int MAX_USERNAME_LENGTH = 15;
    private final String USERNAME_PATTERN = "^[a-zA-Z]([_]?[a-zA-Z0-9]+)*$";

    // Source pattern https://stackoverflow.com/a/5963425
    private final String NAME_PATTERN = "^(?:[\\p{L}\\p{Mn}\\p{Pd}\\'\\x{2019}]+\\s[\\p{L}\\p{Mn}\\p{Pd}\\'\\x{2019}]+\\s?)+$";
    private final UserService userService = TripShareApplication.createService(UserService.class);
    private ActivityUpdateProfileBinding binding;
    private User currUser;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currUser = TripShareApplication.getInstance().getLoggedUser();
        Log.e(TAG, String.valueOf(currUser));

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK  && result.getData() != null) {
                            final Uri imageUri = result.getData().getData();
                            updateCoverImg(imageUri);
                        }
                    }
                }
        );

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setTitle(null);

        binding.saveButton.setOnClickListener(view -> {
            save();
        });

        binding.editAvatarButton.setOnClickListener(view -> {
            onClickRequestPermission();
        });

        binding.nameEditText.addTextChangedListener(new ValidationTextWatcher(binding.nameEditText));
        binding.usernameEditText.addTextChangedListener(new ValidationTextWatcher(binding.usernameEditText));

        loadData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent result = new Intent();
        Bundle data = new Bundle();
        setResult(1, result);
        finish();
        return true;
    }

    protected void loadData() {
        if (currUser == null) return;

        // Load author avatar
        Glide.with(binding.getRoot())
                .load(currUser.getAvatar())
                .centerCrop()
                .into(binding.userAvatar);
        binding.emailEdit.setText(currUser.getEmail());
        binding.usernameEditText.setText(currUser.getUserName());
        binding.nameEditText.setText(currUser.getName());
    }

    private boolean preValidate() {
        String usernameEditText = binding.usernameEditText.getText().toString().trim();
        String nameEditText = binding.nameEditText.getText().toString().trim();

        if (nameEditText.isEmpty()) {
            binding.nameEditLayout.setError("Please enter your name.");
            return false;
        }

        if (!nameEditText.matches(NAME_PATTERN)) {
            binding.nameEditLayout.setError("Your name should contain letters only.");
            return false;
        }
        if (usernameEditText.isEmpty()) {
            binding.usernameEditLayout.setError("Please enter a username.");
            return false;
        }

        if (usernameEditText.length() > MAX_USERNAME_LENGTH) {
            binding.usernameEditLayout.setError(String.format("Username must be less than %d characters long.",
                    MAX_USERNAME_LENGTH)
            );
            return false;
        }

        if (!usernameEditText.matches(USERNAME_PATTERN)) {
            binding.usernameEditLayout.setError("Your username can only contain letters, numbers, underscores and start with a letter");
            return false;
        }
        return true;
    }

    private void save()
    {
        if (!preValidate()) return;

        SimpleLoadingDialog loadingDialog = new SimpleLoadingDialog(this);
        loadingDialog.show();

        String usernameEditText = binding.usernameEditText.getText().toString().trim();
        String nameEditText = binding.nameEditText.getText().toString().trim();

        RequestBody username = RequestBody.create(MediaType.parse("multipart/form-data"),usernameEditText);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"),nameEditText);

        Properties data = new Properties();
        if (!usernameEditText.equals(currUser.getUserName())) {
            data.put("user_name", username);
        }

        data.put("name",name);
        data.put("loading", loadingDialog);
        callUpdateApi(data);
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;
        private ValidationTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.usernameEditText:
                    binding.usernameEditLayout.setError(null);
                    break;
                case R.id.nameEditText:
                    binding.nameEditLayout.setError(null);
                    break;
            }
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {

        }
    }

    private void updateCoverImg(Uri imageUri) {
        try {
            SimpleLoadingDialog loadingDialog = new SimpleLoadingDialog(this);
            loadingDialog.show();

            String imageRealPath = PathUtil.getRealPath(this, imageUri);
            File coverImgFile = new File(imageRealPath);
            RequestBody coverImg = RequestBody.create(MediaType.parse("multipart/form-data"), coverImgFile);
            MultipartBody.Part multipartBodyCoverImg = MultipartBody.Part.createFormData("avatar_img", coverImgFile.getName(), coverImg);
            Properties data = new Properties();
            data.put("avatar_img", multipartBodyCoverImg);
            data.put("imageRealPath", imageRealPath);
            data.put("loading", loadingDialog);
            callUpdateApi(data);
        } catch (Exception err) {
            Log.e(TAG, err.toString());
        }
    };
    private void callUpdateApi(Properties properties) {
        userService.updateUser(
                        (RequestBody) properties.get("user_name"),
                        (RequestBody) properties.get("name"),
                        (MultipartBody.Part) properties.get("avatar_img")
                )
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        ((SimpleLoadingDialog) properties.get("loading")).dismiss();

                        if (properties.containsKey("imageRealPath")) {
                            PathUtil.deleteTempFile(EditProfile.this, (String) properties.get("imageRealPath"));
                        }

                        if (!response.isSuccessful()) {
                            if (properties.get("avatar_img") != null) {
                                Snackbar
                                    .make(binding.getRoot(), "Upload image unsuccessfully!", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            try {
                                String responseData = response.errorBody().string();
                                JsonObject errorBody = new Gson().fromJson(responseData, JsonObject.class);
                                JsonArray errors = errorBody.getAsJsonArray("errors");
                                if (errors.size() > 0) {
                                    JsonObject error = errors.get(0).getAsJsonObject();
                                    if (error.get("param").getAsString().equals("user_name")) {
                                        binding.usernameEditLayout.setError(error.get("msg").getAsString());
                                    }
                                }
                            } catch (IOException e) {}
                            return;
                        }

                        JsonArray body = (JsonArray) response.body();
                        // Update success
                        if (body.get(0).getAsInt() == 1) {
                            User updateUser = new Gson().fromJson(body.get(1).getAsJsonObject(), User.class);
                            TripShareApplication.getInstance().setLoggedUser(updateUser);
                            currUser = TripShareApplication.getInstance().getLoggedUser();
                            loadData();
                            Snackbar
                                    .make(binding.getRoot(), "Update successfully!", Snackbar.LENGTH_SHORT).show();
                            if (properties.get("name") != null) {
                                onSupportNavigateUp();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        ((SimpleLoadingDialog) properties.get("loading")).dismiss();

                        if (properties.containsKey("imageRealPath")) {
                            PathUtil.deleteTempFile(EditProfile.this, (String) properties.get("imageRealPath"));
                        }

                        Snackbar
                                .make(binding.getRoot(), "Fail when updating!", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    protected void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
    private void onClickRequestPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
        else {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            requestPermissions(permissions, REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }
}