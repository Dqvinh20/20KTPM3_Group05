package com.example.tripblog.api.services;

import com.example.tripblog.model.Post;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostService {
//    @GET("post")
//    Call<JsonArray> getAllPost(@Query("page")Integer page, @Query("limit")Integer limit);
    @GET("post/")
    Call<JsonObject> getAllPost(@Query("page")Integer page);

    @GET("post/of-user/{id}")
    Call<JsonArray> getPostByUserId(@Path("id")Integer userId, @Query("is_public")Boolean isPublic);

    @FormUrlEncoded
    @POST("post/create")
    Call<Post> createNewPost(@Field("title") String title,
                             @Field("start_date")Date startDate,
                             @Field("end_date") Date endDate,
                             @Field("is_public") Boolean isPublic,
                             @Field("created_by") Integer userId
                             );

    @FormUrlEncoded
    @DELETE("post/delete")
    Call<Boolean> delete(@Field("post_id")Integer postId);
}
