package com.example.tripblog.api;

//import androidx.viewbinding.BuildConfig;

import com.example.tripblog.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit insRetrofit = null;
    private RetrofitClient() {}
    public synchronized static Retrofit getInstance() {
        if (insRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .build();
            insRetrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return insRetrofit;
    }
}
