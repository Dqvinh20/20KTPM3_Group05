package com.example.tripblog.api.services;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {
    @GET("search")
    Call<JsonObject> getUserFromText(@Query("query")String text);
}
