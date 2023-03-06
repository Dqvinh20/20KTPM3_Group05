package com.example.tripblog.api.services;

import com.example.tripblog.model.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthService {
    @FormUrlEncoded
    @POST("auth/login")
    Call<AuthResponse> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/signup")
    Call<AuthResponse> signup(@Field("email") String email, @Field("password") String password);
}
