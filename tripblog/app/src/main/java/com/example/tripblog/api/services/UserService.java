package com.example.tripblog.api.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @GET("user/{id}/followers")
    Call<JsonArray> getUserFollowers(@Path("id")Integer userId);

    @GET("user/{id}/followings")
    Call<JsonArray> getUserFollowing(@Path("id")Integer userId);

    @DELETE("user/{id}/unfollow")
    Call<JsonArray> unfollowUser(@Path("id")Integer userId);

    @POST("user/{id}/follow")
    Call<JsonObject> followUser(@Path("id")Integer userId);
}
