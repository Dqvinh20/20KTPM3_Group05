package com.example.tripshare.api.services;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RatingService {

    @GET("rating/get-all-rating/{postId}")
    Call<JsonObject> getAllPostRatings(
            @Path("postId")Integer postId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @FormUrlEncoded
    @POST("rating/create")
    Call<JsonObject> writeRating(
            @Field("post_id")Integer postId,
            @Field("score") Integer score,
            @Field("content") String content
    );
}
