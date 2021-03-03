package com.example.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "http://192.168.0.104:8000/todo/";

    @GET("tasks/")
    Call<JsonArray> getData();

    @POST("create/")
    @FormUrlEncoded
    Call<JSONObject> addTask(@Field("task_name") String task_name);

    @POST("update/{tid}")
    @FormUrlEncoded
    Call<JSONObject> updateTask(@Path(value = "tid", encoded = true) String task_id, @Field("task_name") String task);

    @GET("delete")
    Call<JSONObject> deleteTask(@Query("tid") String tid);

}
