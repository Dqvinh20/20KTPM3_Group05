package com.example.tripshare.api.services;

import com.example.tripshare.model.response.SearchResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {
    @GET("search")
    Call<JsonObject> getUserFromText(@Query("query")String text);

    @GET("search/location")
    Call<SearchResponse> searchPlaces(@Query("query")String text, @Query("limit")Integer limit);
}
