package com.app.ozaicamp.apiservice;

import com.app.ozaicamp.ResponseModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    @GET("/saveToken/{sessionId}/{deviceId}")
    Call<String> postFCMToken(@Path("sessionId") String sessionId,
                                                             @Path("deviceId") String fcmToken);

}
