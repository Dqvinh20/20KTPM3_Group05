package com.example.tripblog.ui.tripPlan;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
        ImageView fav_icon = ( ImageView ) row.findViewById(R.id.fav_icon);
        TextView fav_count_textView = (TextView) row.findViewById(R.id.fav_count_textView);
        TextView viewTextView_result_search = (TextView) row.findViewById(R.id.viewTextView_result_search);
        ImageView share_btn = row.findViewById(R.id.share_btn);

        Log.i("count", tripPlanList.get(position).getViewCount().toString());
        titleTextView.setText(tripPlanList.get(position).getTitle());
        nameTextView_result_search.setText(tripPlanList.get(position).getAuthor().getUserName());
        fav_count_textView.setText(tripPlanList.get(position).getLikeCount().toString());
        viewTextView_result_search.setText(tripPlanList.get(position).getViewCount().toString());
        Glide.with(row)
                .load(tripPlanList.get(position).getAuthor().getAvatar())
                .placeholder(R.drawable.app_logo_transparent)
                .error(R.drawable.app_logo_transparent)
                .into(avatar_result_search);
        Glide.with(row)
                .load(tripPlanList.get(position).getCoverImg())
                .placeholder(R.drawable.japan)
                .error(R.drawable.japan)
                .into(image_result_search_imageview);
        if(tripPlanList.get(position).isLikedByYou()){
            fav_icon.setTag("liked");
            fav_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.favcorite_icon_red));
        }else{
            fav_icon.setTag("unliked");
            fav_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.favarite_icon));
        }

//        fav_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(fav_icon.getTag() == "liked"){
//                    // ToDO: request unliked to db
//                    fav_icon.setTag("unliked");
//                    fav_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.favarite_icon));
//                }
//                else{
//                    // ToDO: request liked to db
//                    fav_icon.setTag("liked");
//                    fav_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.favcorite_icon_red));
//                }
//            }
//        });
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
