package com.example.tripblog.api;

import android.text.TextUtils;

import com.example.tripblog.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS);
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL_LOCAL)
                    .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit insRetrofit = null;
    private RetrofitClient() {}
    public synchronized static Retrofit getInstance() {
        if (insRetrofit == null) {
            insRetrofit = builder
                    .client(httpClient.build())
                    .build();
        }
        return insRetrofit;
    }

    public synchronized static Retrofit setNewAuth(String token) {
        if (TextUtils.isEmpty(token)) {
            return insRetrofit;
        }

        AuthenticationInterceptor authInterceptor = new AuthenticationInterceptor(token);
        if(!httpClient.interceptors().contains(authInterceptor)) {
            httpClient.addInterceptor(authInterceptor);
            insRetrofit = builder.client(httpClient.build()).build();
        }

        return insRetrofit;
    }
}
