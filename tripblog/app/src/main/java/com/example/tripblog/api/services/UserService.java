package com.example.tripblog.api.services;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {
    @GET("user/{id}/followers")
    Call<JsonArray> getUserFollowers(@Path("id")Integer userId);

    @GET("user/{id}/followings")
    Call<JsonArray> getUserFollowing(@Path("id")Integer userId);

}
