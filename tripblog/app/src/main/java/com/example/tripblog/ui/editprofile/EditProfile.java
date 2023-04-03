package com.example.tripblog.ui.editprofile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.api.services.UserService;
import com.example.tripblog.databinding.ActivityUpdateProfileBinding;
import com.example.tripblog.model.AuthResponse;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.SimpleLoadingDialog;
import com.example.tripblog.ui.login.LoginActivity;
import com.example.tripblog.utils.PathUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.File;
import java.util.Properties;

import javax.xml.validation.Validator;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {

    MaterialAlertDialogBuilder loading = null;
    private static final String TAG = EditProfile.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    protected final UserService userService = TripBlogApplication.createService(UserService.class);

    protected ActivityUpdateProfileBinding binding;
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                }
        );
        currUser = TripBlogApplication.getInstance().getLoggedUser();
        loadData();

        binding.nameEditText.addTextChangedListener(new ValidationTextWatcher(binding.nameEditText));
        binding.usernameEditText.addTextChangedListener(new ValidationTextWatcher(binding.usernameEditText));
    }


    private void save()
    {
        String usernameEditText = binding.usernameEditText.getText().toString();
        String nameEditText = binding.nameEditText.getText().toString();

        if (loading == null) {
            loading = new MaterialAlertDialogBuilder(EditProfile.this);
            loading.setView(R.layout.loading);
            loading.setBackground(getDrawable(android.R.color.transparent));
            loading.setCancelable(false);
        }
//        String imageRealPath = PathUtil.getRealPath(this, imageUri);
//        File coverImgFile = new File(imageRealPath);
//        RequestBody coverImg = RequestBody.create(MediaType.parse("multipart/form-data"), coverImgFile);
//        MultipartBody.Part multipartBodyCoverImg = MultipartBody.Part.createFormData("avatar_img", coverImgFile.getName(), coverImg);

        RequestBody username= RequestBody.create(MediaType.parse("multipart/form-data"),usernameEditText);
        RequestBody name= RequestBody.create(MediaType.parse("multipart/form-data"),nameEditText);

        Properties data = new Properties();
        data.put("user_name",username);
        data.put("name",name);
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
                    binding.usernameEditText.setError(null);
                    break;
                case R.id.nameEditText:
                    binding.nameEditText.setError(null);
                    break;
            }
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {

        }
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchData() {

    }

    protected void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
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
                .enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        if (properties.containsKey("imageRealPath")) {
                            ((SimpleLoadingDialog) properties.get("loading")).dismiss();
                            PathUtil.deleteTempFile(EditProfile.this, (String) properties.get("imageRealPath"));
                        }

                        if (!response.isSuccessful()) {
                            Snackbar
                                    .make(binding.getRoot(), "Upload cover image unsuccessfully. - " + response.code(), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        JsonArray body = response.body();
                        // Update success
                        if (body.get(0).getAsInt() == 1) {
                            User updateUser = new Gson().fromJson(body.get(1).getAsJsonObject(), User.class);
                            currUser.setName(updateUser.getName());
                            currUser.setUserName(updateUser.getUserName());
                            currUser.setAvatar(updateUser.getAvatar());
                            loadData();
                            Snackbar
                                    .make(binding.getRoot(), "Upload cover image successfully.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        if (properties.containsKey("imageRealPath")) {
                            ((SimpleLoadingDialog) properties.get("loading")).dismiss();
                            PathUtil.deleteTempFile(EditProfile.this, (String) properties.get("imageRealPath"));
                        }

                        Snackbar
                                .make(binding.getRoot(), "Can't upload cover image!", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }


    protected void loadData() {
        if (currUser == null) return;

        // Load author avatar
        Glide.with(binding.getRoot())
                .load(currUser.getAvatar())
                .centerCrop()
                .into(binding.userAvatar);

        Log.d("debug", String.valueOf(currUser));
        binding.emailText.setText(currUser.getEmail());
        binding.usernameEditText.setText(currUser.getUserName());
        binding.nameEditText.setText(currUser.getName());
    }

}