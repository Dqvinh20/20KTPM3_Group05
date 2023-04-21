package com.example.tripshare.api.services;

import com.example.tripshare.model.TripPlan;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TripPlanService {
    @GET("post/by-location/{id}")
    Call<JsonArray> getTripPlanByLocation(@Path("id")Integer loctionID);

    @GET("post/of-user/{id}")
    Call<JsonArray> getTripPlanByUserId(@Path("id")Integer userId, @Query("is_public")Boolean isPublic);

    @GET("post/{id}")
    Call<TripPlan> getTripPlanById(@Path("id")Integer id);

    @Multipart
    @POST("post/create")
    Call<TripPlan> createNewTripPlan(@Part("title") RequestBody title,
                                     @Part("start_date")RequestBody startDate,
                                     @Part("end_date") RequestBody endDate,
                                     @Part("is_public") RequestBody isPublic,
                                     @Part("created_by") RequestBody userId,
                                     @Part("brief_description") RequestBody brief_description,
                                     @Part MultipartBody.Part coverImg);
    @FormUrlEncoded
    @HTTP(method = "PATCH", path = "post/change-trip-dates/{id}", hasBody = true)
    Call<JsonArray> changeTripDates(
            @Path("id")Integer id,
            @Field("start_date") Date startDate,
            @Field("end_date") Date endDate
    );

    @Multipart
    @PATCH("post/update")
    Call<JsonArray> updatePost(
            @Part("post_id") RequestBody postId,
            @Part("title") RequestBody title,
            @Part("brief_description") RequestBody brief_description,
            @Part("is_public") RequestBody isPublic,
            @Part MultipartBody.Part coverImg
   );

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "post/delete", hasBody = true)
    Call<Integer> delete(@Field("post_id")Integer postId);
    
    @GET("home/newest")
    Call<JsonObject> getNewestTripPlans(@Query("page")Integer page, @Query("limit")Integer limit);
    @GET("home/popular")
    Call<JsonArray> getPopularTripPlans(@Query("page")Integer page, @Query("limit")Integer limit);

    @FormUrlEncoded
    @HTTP(method = "PATCH", path = "post/increase-view", hasBody = true)
    Call<TripPlan> increaseView(@Field("post_id") Integer postId);
}
