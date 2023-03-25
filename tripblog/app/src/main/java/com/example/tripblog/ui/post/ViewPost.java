package com.example.tripblog.ui.post;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tripblog.R;

public class ViewPost extends AppCompatActivity {
    TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        textView2 = findViewById(R.id.textView2);
        Intent currIntent = getIntent();
        Bundle currBundle = currIntent.getExtras();

        textView2.setText(currBundle.getString("postid"));
    }
}