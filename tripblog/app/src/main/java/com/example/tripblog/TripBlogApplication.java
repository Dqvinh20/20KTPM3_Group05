package com.example.tripblog;

import android.app.Application;

import com.example.tripblog.api.RetrofitClient;
import com.example.tripblog.model.User;

import retrofit2.Retrofit;

public class TripBlogApplication extends Application {

    public static final String TAG = TripBlogApplication.class.getSimpleName();
    private static TripBlogApplication ins;
    private static Retrofit retrofitClient;
    private User loggedUser = null;

    @Override
    public void onCreate() {
        super.onCreate();
        ins = this;
        retrofitClient = RetrofitClient.getInstance();
    }

    public static synchronized TripBlogApplication getInstance() {
        return ins;
    }
    public static void updateToken(String authToken) {
        retrofitClient = RetrofitClient.setNewAuth(authToken);
    }
    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(
            Class<S> serviceClass, final String authToken) {
        updateToken(authToken);
        return retrofitClient.create(serviceClass);
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }
}
