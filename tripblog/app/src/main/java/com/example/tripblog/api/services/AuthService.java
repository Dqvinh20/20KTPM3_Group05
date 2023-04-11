package com.example.tripblog.api.services;

import com.example.tripblog.model.response.AuthResponse;
import com.example.tripblog.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {
    @FormUrlEncoded
    @POST("auth/login")
    Call<AuthResponse> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/signup")
    Call<AuthResponse> signup(@Field("email") String email, @Field("password") String password);

    @GET("auth/logged-user")
    Call<User> retrieveLoggedUserInfo();

    @FormUrlEncoded
    @POST("auth/reset-password")
    Call<AuthResponse> resetPassword(@Field("email") String email);
}

