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

import com.example.tripblog.R;
import com.example.tripblog.model.User;

import java.util.List;

public class CustomSuggestionSearchLocationAdapter extends ArrayAdapter<String> {
    Context context ; Suggest_Search_Object[] list_suggestion;
    List<User> userListBelow;

    public CustomSuggestionSearchLocationAdapter(@NonNull Context context, int layoutToBeInflated , List<User> userListBelow ) {
        super(context,R.layout.suggest_search_component,new String[userListBelow.size()]);
        this.userListBelow = userListBelow;

        this.context = context;
    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.suggest_search_component,null);
        TextView titleTextView = (TextView) row.findViewById(R.id.titleTextView);
        TextView subTiltleTextView = (TextView) row.findViewById(R.id.subTiltleTextView);
        ImageView icon_suggestion_search_imageview = ( ImageView ) row.findViewById(R.id.icon_suggestion_search_imageview);

        titleTextView.setText(userListBelow.get(position).getUserName());
        subTiltleTextView.setText(userListBelow.get(position).getUserNameNonAccent());

//        Toast.makeText(context, "Hi", Toast.LENGTH_LONG).show();
        System.out.println(list_suggestion[position]);
        if(list_suggestion[position].getType() == "Location"){
            icon_suggestion_search_imageview.setImageResource(R.drawable.market_location);
        }
        else {
            icon_suggestion_search_imageview.setImageResource(R.drawable.logo_user);
        }
//        Log.i("size",Integer.toString(list_suggestion.length));
//        for(int i = 0;i<list_suggestion.length;i++)
//            Log.i("data",list_suggestion[i].toString());

        return(row);
    }
}
