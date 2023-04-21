package com.example.tripshare.api.services;
//import com.example.tripblog.model.AuthResponse;
import com.example.tripshare.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface UserService {
    @GET("user/{id}/followers")
    Call<JsonArray> getUserFollowers(@Path("id")Integer userId);

    @GET("user/{id}/followings")
    Call<JsonArray> getUserFollowing(@Path("id")Integer userId);

    @DELETE("user/{id}/unfollow")
    Call<JsonObject> unfollowUser(@Path("id")Integer userId);

    @POST("user/{id}/follow")
    Call<JsonObject> followUser(@Path("id")Integer userId);

    @Multipart
    @PATCH("user/update")
    Call<JsonElement> updateUser(
            @Part("user_name") RequestBody userName,
            @Part("name")  RequestBody name,
            @Part MultipartBody.Part avatar_img
    );

    @Multipart
    @PATCH("user/update")
    Call<JsonArray> updateNameUser(
            @Part("user_name") RequestBody userName,
            @Part("name")  RequestBody name

    );


    @GET("user/{user_id}")
    Call<User> getUserById(@Path("user_id") Integer userid);

    @POST("user/like/{postId}")
    Call<JsonObject> likePost(@Path("postId") Integer postId);

    @DELETE("user/unlike/{postId}")
    Call<JsonObject> unlikePost(@Path("postId") Integer postId);
}
