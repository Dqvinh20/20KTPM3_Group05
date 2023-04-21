package com.example.tripblog.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tripblog.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CustomPostNewsfeedAdapter extends  ArrayAdapter<String> {
    Context context ; String[] avatars; String[] images; String[] name; String[] tilte; String[] brief_des;String[] views;
    public CustomPostNewsfeedAdapter(@NonNull Context context,
                                     int layoutToBeInflated ,
                                     String[] name,
                                     String[] tilte,
                                     String[] brief_des,
                                     String[] views,
                                     String[] avatars,
                                     String[] images) {
        super(context, R.layout.component_post_newsfeed,name);
        this.context = context;
        this.avatars = avatars;
        this.name = name;
        this.tilte = tilte;
        this.brief_des = brief_des;
        this.views = views;
        this.images = images;
    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.component_post_newsfeed,null);
        TextView namelb = (TextView) row.findViewById(R.id.nameTextView);
        TextView tiltelb = (TextView) row.findViewById(R.id.TitleTextView);
        TextView briefDeslb = (TextView) row.findViewById(R.id.briefDescription);
        TextView viewlb = (TextView) row.findViewById(R.id.viewsTextView);

        ImageView icon = ( ImageView ) row.findViewById(R.id.avatar);
        ImageView imageView = ( ImageView ) row.findViewById(R.id.imageView);

        namelb.setText(name[position]);
        tiltelb.setText(tilte[position]);
        briefDeslb.setText(brief_des[position]);
        viewlb.setText(views[position]);
//        new Thread() {
//            URL url;
//            Bitmap image;
//            public void run() {
//                try {
//                    url = new URL(avatars[position]);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    image = BitmapFactory.decodeStream(url.openStream());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                icon.setImageBitmap(image);
//
//                try {
//                    url = new URL(images[position]);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    image = BitmapFactory.decodeStream(url.openStream());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageView.setImageBitmap(image);
//            }
//        }.start();

        return(row);
    }
}
