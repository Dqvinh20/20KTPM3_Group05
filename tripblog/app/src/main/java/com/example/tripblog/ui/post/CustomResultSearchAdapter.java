package com.example.tripblog.ui.post;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tripblog.R;

public class CustomResultSearchAdapter  extends ArrayAdapter<String> {
    Context context ; String[] urlImgSrc; String[] title; String[] avatarSrc;  String[] name;  String[] like_count; String[] view_count;
    public CustomResultSearchAdapter(@NonNull Context context, int layoutToBeInflated , String[] urlImgSrc,
                                     String[] title, String[] avatarSrc,  String[] name,  String[] like_count, String[] view_count ) {
        super(context, R.layout.post_search_list_component,title);
        this.urlImgSrc = urlImgSrc;
        this.title = title;
        this.avatarSrc = avatarSrc;
        this.name = name;
        this.like_count = like_count;
        this.view_count = view_count;

        this.context = context;
    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.post_search_list_component,null);

        ImageView image_result_search_imageview = ( ImageView ) row.findViewById(R.id.image_result_search_imageview);
        ImageView avatar_result_search = ( ImageView ) row.findViewById(R.id.avatar_result_search);
        TextView titleTextView = (TextView) row.findViewById(R.id.titleTextView);
        TextView nameTextView_result_search = (TextView) row.findViewById(R.id.nameTextView_result_search);
        ImageView fav_icon = ( ImageView ) row.findViewById(R.id.fav_icon);
        TextView fav_count_textView = (TextView) row.findViewById(R.id.fav_count_textView);
        TextView viewTextView_result_search = (TextView) row.findViewById(R.id.viewTextView_result_search);
        Log.i("count",like_count[position]);
        titleTextView.setText(title[position]);
        nameTextView_result_search.setText(name[position]);
        fav_count_textView.setText(like_count[position]);
        viewTextView_result_search.setText(view_count[position]);
        avatar_result_search.setImageResource(R.drawable.app_logo_transparent);
        image_result_search_imageview.setImageResource(R.drawable.japan);
        fav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fav_icon.getTag() == "liked"){
                    fav_icon.setTag("unliked");
                    fav_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.favarite_icon));
                }
                else{
                    fav_icon.setTag("liked");
                    fav_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.favcorite_icon_red));
                }
            }
        });
        return(row);
    }
}
