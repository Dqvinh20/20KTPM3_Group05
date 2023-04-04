package com.example.tripblog.ui.adapter;

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

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tripblog.R;
import com.example.tripblog.model.Post;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.fragments.PublicPlanFragment;

import java.util.List;

public class PlanListAdapter extends ArrayAdapter<String> {
    Context context;
    String[] nameList;
    Integer[] picture;
    List<Post> list;
    public PlanListAdapter(@NonNull Context context, int layoutToBeInflated, List<Post> list) {
        super(context, layoutToBeInflated, new String[list.size()]);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.plan_item, null);
        TextView nameTxt = (TextView) row.findViewById(R.id.postNameTxt);
        ImageView img = (ImageView) row.findViewById(R.id.imgView);
        TextView likeCount = (TextView) row.findViewById(R.id.likeCountTxt);
        TextView viewCount = (TextView) row.findViewById(R.id.viewCountTxt);

        nameTxt.setText(list.get(position).getTitle() );
        likeCount.setText(list.get(position).getLikeCount().toString());
        viewCount.setText(list.get(position).getViewCount().toString());
        Glide.with(row)
                .load(list.get(position).getCoverImg())
                .placeholder(R.drawable.da_lat)
                .error(R.drawable.da_lat)
                .into(img);


        return (row);
    }
//    public PlanListAdapter(@NonNull Context context, int layoutToBeInflated, String[] nameList, Integer[] picture) {
//        super(context, layoutToBeInflated, nameList);
//        this.context = context;
//        this.nameList = nameList;
//        this.picture = picture;
//    }
    public void updateData(List<Post> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//        View row = inflater.inflate(R.layout.plan_item, null);
//        TextView nameTxt = (TextView) row.findViewById(R.id.postNameTxt);
//        ImageView icon = (ImageView) row.findViewById(R.id.imgView);
//        nameTxt.setText(nameList[position]);
//        icon.setImageResource(picture[position]);
//        return (row);
//    }
}
