package com.example.tripblog.ui.updateProfile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.PostService;
import com.example.tripblog.api.services.UserService;
import com.example.tripblog.databinding.ActivityPostDetailBinding;
import com.example.tripblog.databinding.ActivityUpdateProfileBinding;
import com.example.tripblog.model.Post;
import com.example.tripblog.model.User;
import com.example.tripblog.ui.SimpleLoadingDialog;
import com.example.tripblog.ui.fragments.CreateFragment;
import com.example.tripblog.ui.post.EditablePostDetailActivity;
import com.example.tripblog.utils.PathUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.Properties;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class updateProfile extends AppCompatActivity {
    private static final String TAG = updateProfile.class.getSimpleName();
    protected final UserService userService = TripBlogApplication.createService(UserService.class);
    protected ActivityUpdateProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        binding= ActivityUpdateProfileBinding.inflate(getLayoutInflater());

    }
    User currUser ;

    private void fetchData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("user")) {
            // Load from bundle
            currUser = (User) bundle.getSerializable("user");
//            loadData();
        }
        else {
            // Load data from internet
            Integer userid = bundle.getInt("user_id");
            UserService.getUserById(userid).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    currUser = response.body();
//                    loadData();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Snackbar
                            .make(binding.getRoot(), "Can't connect to server", Snackbar.LENGTH_SHORT)
                            .show();
                }
            });
        }
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
            MultipartBody.Part multipartBodyCoverImg = MultipartBody.Part.createFormData("cover_img", coverImgFile.getName(), coverImg);
            Properties data = new Properties();
            data.put("cover_img", multipartBodyCoverImg);
            data.put("imageRealPath", imageRealPath);
            data.put("loading", loadingDialog);
            callUpdateApi(data);
        } catch (Exception err) {
            Log.e(TAG, err.toString());
        }
    };

    private void callUpdateApi(Properties properties) {
//        RequestBody postId = RequestBody.create(MediaType.parse("multipart/form-data"), currPost.getId().toString());

        userService.updateUser(
                        (RequestBody) properties.get("user_name"),
                        (MultipartBody.Part) properties.get("avatar_img")
                )
                .enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        if (properties.containsKey("imageRealPath")) {
                            ((SimpleLoadingDialog) properties.get("loading")).dismiss();
                            PathUtil.deleteTempFile(updateProfile.this, (String) properties.get("imageRealPath"));
                        }

                        if (!response.isSuccessful()) return;
                        JsonArray body = response.body();
                        // Update success
                        if (body.get(0).getAsInt() == 1) {
                            User updateUser = new Gson().fromJson(body.get(1).getAsJsonObject(), User.class);
                            currUser.setUserName(updateUser.getUserName());
//                            loadData();
                            Snackbar
                                    .make(binding.getRoot(), "Upload cover image successfully.", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        if (properties.containsKey("imageRealPath")) {
                            ((SimpleLoadingDialog) properties.get("loading")).dismiss();
                            PathUtil.deleteTempFile(updateProfile.this, (String) properties.get("imageRealPath"));
                        }

                        Snackbar
                                .make(binding.getRoot(), "Can't upload cover image!", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

}