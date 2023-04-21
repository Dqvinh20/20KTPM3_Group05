package com.example.tripshare.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    private String token;

    public AuthenticationInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request origin = chain.request();
        Request newRequest = origin.newBuilder()
                .addHeader("Authorization", "Bearer " + this.token)
                .build();
        return chain.proceed(newRequest);
    }
}
