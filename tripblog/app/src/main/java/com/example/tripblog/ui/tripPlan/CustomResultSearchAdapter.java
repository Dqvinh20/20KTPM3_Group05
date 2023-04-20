package com.example.tripblog.ui.tripPlan;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.model.TripPlan;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CustomResultSearchAdapter  extends ArrayAdapter<String> {
    Context context ;
    List<TripPlan> tripPlanList;

    public CustomResultSearchAdapter(@NonNull Context context, int layoutToBeInflated ,List<TripPlan> tripPlanList) {
        super(context, R.layout.post_search_list_component,new String[tripPlanList.size()]);
        this.tripPlanList = tripPlanList;

        this.context = context;
    }
    @Override
    public View getView(int position, View convertView , ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.post_search_list_component,null);
        if (tripPlanList == null || tripPlanList.isEmpty()) return row;
        ImageView image_result_search_imageview = ( ImageView ) row.findViewById(R.id.image_result_search_imageview);
        ImageView avatar_result_search = ( ImageView ) row.findViewById(R.id.avatar_result_search);
        TextView titleTextView = (TextView) row.findViewById(R.id.titleTextView);
        TextView nameTextView_result_search = (TextView) row.findViewById(R.id.nameTextView_result_search);
        MaterialButton fav_icon = (MaterialButton) row.findViewById(R.id.fav_icon);
        TextView fav_count_textView = (TextView) row.findViewById(R.id.fav_count_textView);
        TextView viewTextView_result_search = (TextView) row.findViewById(R.id.viewTextView_result_search);
        ImageView share_btn = row.findViewById(R.id.share_btn);

        titleTextView.setText(tripPlanList.get(position).getTitle());
        nameTextView_result_search.setText(tripPlanList.get(position).getAuthor().getUserName());
        fav_count_textView.setText(tripPlanList.get(position).getLikeCount().toString());
        viewTextView_result_search.setText(tripPlanList.get(position).getViewCount().toString());

        Glide.with(row)
                .load(tripPlanList.get(position).getAuthor().getAvatar())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.avatar)
                .into(avatar_result_search);

        Glide.with(row)
                .load(tripPlanList.get(position).getCoverImg())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(image_result_search_imageview);

        fav_icon.setChecked(tripPlanList.get(position).isLikedByYou());

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", "https://tripblog.com?postId="+ tripPlanList.get(position).getId());
                clipboard.setPrimaryClip(clip);
                Snackbar.make(parent, "Copy to clipboard", Snackbar.LENGTH_SHORT)
                            .show();
            }
        });
        return(row);
    }
}
