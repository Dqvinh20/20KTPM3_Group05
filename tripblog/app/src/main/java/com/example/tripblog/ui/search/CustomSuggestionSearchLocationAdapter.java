package com.example.tripblog.ui.search;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.model.Location;
import com.example.tripblog.model.User;

import java.util.List;

public class CustomSuggestionSearchLocationAdapter extends ArrayAdapter<String> {
    Context context ; Suggest_Search_Object[] list_suggestion;
    List<Location> listLocation;

    public CustomSuggestionSearchLocationAdapter(@NonNull Context context, int layoutToBeInflated , List<Location> listLocation ) {
        super(context,R.layout.suggest_search_component,new String[listLocation.size()]);
        this.listLocation = listLocation;

        this.context = context;
    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.suggest_search_component,null);
        TextView titleTextView = (TextView) row.findViewById(R.id.titleTextView);
        TextView subTiltleTextView = (TextView) row.findViewById(R.id.subTiltleTextView);
        ImageView icon_suggestion_search_imageview = ( ImageView ) row.findViewById(R.id.icon_suggestion_search_imageview);

        titleTextView.setText(listLocation.get(position).getName());
        subTiltleTextView.setText(listLocation.get(position).getFormattedAddress());

        icon_suggestion_search_imageview.setImageResource(R.drawable.ic_baseline_location_on_24);
        return(row);
    }
}
