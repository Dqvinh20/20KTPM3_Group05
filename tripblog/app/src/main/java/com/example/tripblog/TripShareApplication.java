package com.example.tripblog;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.content.Context;

import androidx.core.app.ActivityCompat;

import com.example.tripblog.api.RetrofitClient;
import com.example.tripblog.model.User;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import retrofit2.Retrofit;

public class TripShareApplication extends Application {
    public static final String TAG = TripShareApplication.class.getSimpleName();
    public static final String CHANNEL_1_ID = "channel1";
    private static TripShareApplication ins;
    private static Retrofit retrofitClient;
    private User loggedUser = null;

    @Override
    public void onCreate() {
        super.onCreate();
        ins = this;
        retrofitClient = RetrofitClient.getInstance();
        create_notification();
        checkPermission();


    }
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Dexter.withContext(this).withPermission(android.Manifest.permission.POST_NOTIFICATIONS).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                    Toast.makeText(MapsActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
                    Uri uri = Uri.fromParts("package",getPackageName(),"");
                    intent.setData(uri);
                    startActivities(new Intent[]{intent});
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
            return;
        }
    }
    private void create_notification() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"TripBlog", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is Channel 1");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    public static synchronized TripShareApplication getInstance() {
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

    public static void logout(Context context) {
        context.getSharedPreferences("auth", MODE_PRIVATE).edit().putString("token", "").commit();
        getInstance().setLoggedUser(null);
    }
}
