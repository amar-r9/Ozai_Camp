package com.app.ozaicamp.apiservice;

import com.app.ozaicamp.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Api {


   // https://ozailiving.com/saveToken/%7BsessionId%7D/%7BdeviceId%7D
    public static APIService getClient() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ozailiving.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClientObject())
                .build();

        APIService service = retrofit.create(APIService.class);
        return service;
    }


    private static OkHttpClient getOkHttpClientObject() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.writeTimeout(60, TimeUnit.SECONDS);
        httpClient.callTimeout(60, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(interceptor);// <-- this is the important line!
        }
        return httpClient.build();
    }
}



