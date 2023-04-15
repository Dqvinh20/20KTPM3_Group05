package com.example.tripblog.deeplinking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tripblog.ui.SplashActivity;
import com.example.tripblog.ui.tripPlan.TripPlanDetailActivity;

public class DeepLinking extends Activity {
    protected static final String TAG = DeepLinking.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();
        try {
            String data1 = data.getQueryParameter("postId");
            Integer postid =  Integer.valueOf(data1);
            Log.d(TAG,postid.toString());
            Intent postintent = new Intent(DeepLinking.this, TripPlanDetailActivity.class);
            postintent.putExtra("postId",postid);
            startActivityForResult(postintent,1122);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent postintent = new Intent(DeepLinking.this, SplashActivity.class);
        startActivityForResult(postintent,1122);
        finish();
    }

}
