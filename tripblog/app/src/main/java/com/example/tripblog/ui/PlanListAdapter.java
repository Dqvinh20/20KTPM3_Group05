package com.example.tripblog.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tripblog.R;

public class PlanListAdapter extends ArrayAdapter<String> {
    Context context;
    String[] nameList;
    Integer[] picture;
    public PlanListAdapter(@NonNull Context context, int layoutToBeInflated, String[] nameList, Integer[] picture) {
        super(context, layoutToBeInflated, nameList);
        this.context = context;
        this.nameList = nameList;
        this.picture = picture;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.plan_item, null);
        TextView nameTxt = (TextView) row.findViewById(R.id.nameTxt);
        ImageView icon = (ImageView) row.findViewById(R.id.thumbnailImg);
        nameTxt.setText(nameList[position]);
        icon.setImageResource(picture[position]);
        return (row);
    }
}
