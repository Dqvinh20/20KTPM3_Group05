package com.example.tripblog.ui.search;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tripblog.R;

import java.util.Arrays;

public class CustomSuggestionSearchAdapter extends ArrayAdapter<String> {
    Context context ; Suggest_Search_Object[] list_suggestion;
    public CustomSuggestionSearchAdapter(@NonNull Context context, int layoutToBeInflated ,Suggest_Search_Object[] list_suggestion ) {
        super(context,R.layout.suggest_search_component,new String[list_suggestion.length]);
        this.list_suggestion = list_suggestion;

        this.context = context;
    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        Log.i("size",Integer.toString(list_suggestion.length));
        View row = inflater.inflate(R.layout.suggest_search_component,null);
        Log.i("size",Integer.toString(list_suggestion.length));
        TextView titleTextView = (TextView) row.findViewById(R.id.titleTextView);
        TextView subTiltleTextView = (TextView) row.findViewById(R.id.subTiltleTextView);
        ImageView icon_suggestion_search_imageview = ( ImageView ) row.findViewById(R.id.icon_suggestion_search_imageview);
        titleTextView.setText(list_suggestion[position].getTitle());
        subTiltleTextView.setText(list_suggestion[position].getSubtitle());
        Toast.makeText(context, "Hi", Toast.LENGTH_LONG).show();
        System.out.println(list_suggestion[position]);
        if(list_suggestion[position].getType() == "Location"){
            icon_suggestion_search_imageview.setImageResource(R.drawable.market_location);
        }
        else {
            icon_suggestion_search_imageview.setImageResource(R.drawable.logo_user);
        }
        Log.i("size",Integer.toString(list_suggestion.length));
        for(int i = 0;i<list_suggestion.length;i++)
            Log.i("data",list_suggestion[i].toString());

        return(row);
    }
}
