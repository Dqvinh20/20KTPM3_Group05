package com.example.tripblog.api.services;

import com.example.tripblog.model.Post;
import com.example.tripblog.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface UserService {
    @Multipart
    @PATCH("user/update")
    Call<JsonArray> updateUser(
            @Part("user_name") RequestBody userName,
            @Part MultipartBody.Part avatar_img
    );


    @GET("user/{user_id}")
    Call<User> getUserById(@Path("user_id") Integer userid);

}
