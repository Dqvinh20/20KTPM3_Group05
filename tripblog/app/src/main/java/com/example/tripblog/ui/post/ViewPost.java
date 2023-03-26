package com.example.tripblog.ui.post;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tripblog.R;
import com.example.tripblog.databinding.ActivityMainBinding;
import com.example.tripblog.databinding.ActivityViewPostBinding;

public class ViewPost extends AppCompatActivity {
    ActivityViewPostBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        Bundle currBundle = currIntent.getExtras();

        binding.textView2.setText(currBundle.getString("postid"));
        // ToDo: Check Postid null!
        // Todo: call api view Post
    }
}