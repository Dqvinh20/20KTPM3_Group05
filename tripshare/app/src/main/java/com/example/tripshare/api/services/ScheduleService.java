package com.example.tripshare.api.services;

import com.example.tripshare.model.Location;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HTTP;
import retrofit2.http.POST;

public interface ScheduleService {
    @FormUrlEncoded
    @POST("schedule/add-location")
    Call<Location> addLocation(
            @Field("schedule_id")Integer scheduleId,
            @Field("location_id")Integer locationId
    );

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "schedule/remove-location", hasBody = true)
    Call<Integer> removeLocation(
            @Field("schedule_id")Integer scheduleId,
            @Field("location_pos")Integer locationPos
    );

    @FormUrlEncoded
    @HTTP(method = "PATCH", path = "schedule/edit-location-note", hasBody = true)
    Call<JsonObject> editLocationNote(
            @Field("schedule_id")Integer scheduleId,
            @Field("location_id")Integer locationId,
            @Field("location_pos") Integer locationPos,
            @Field("note")String note
    );

}
