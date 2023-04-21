package com.example.tripblog.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tripblog.R;
import com.example.tripblog.TripShareApplication;
import com.example.tripblog.api.services.TripPlanService;
import com.example.tripblog.model.TripPlan;
import com.example.tripblog.ui.tripPlan.EditableTripPlanDetailActivity;
import com.example.tripblog.ui.tripPlan.TripPlanDetailActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanListAdapter extends ArrayAdapter<String> {
    Context context;
    List<TripPlan> list;
    private boolean isEditable;

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public PlanListAdapter(@NonNull Context context, int layoutToBeInflated, List<TripPlan> list, boolean isEditable) {
        super(context, layoutToBeInflated, new String[list.size()]);
        this.context = context;
        this.list = list;
        this.isEditable = isEditable;
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
        ImageView moreBtn = row.findViewById(R.id.more);

        nameTxt.setText(list.get(position).getTitle() );
        likeCount.setText(list.get(position).getLikeCount().toString());
        viewCount.setText(list.get(position).getViewCount().toString());
        Glide.with(row)
                .load(list.get(position).getCoverImg())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(img);
        if(isEditable)
            moreBtn.setVisibility(View.VISIBLE);
        else
            moreBtn.setVisibility(View.GONE);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class postDetailClass = isEditable ? EditableTripPlanDetailActivity.class : TripPlanDetailActivity.class;
                Intent postDetail = new Intent(context, postDetailClass);
                Bundle data = new Bundle();
                data.putInt("postId", list.get(position).getId());
                postDetail.putExtras(data);
                context.startActivity(postDetail);
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = inflater.inflate(R.layout.setting_post_layout, null);
                BottomSheetDialog moreDialog = new BottomSheetDialog(getContext());
                moreDialog.setContentView(v);
                moreDialog.show();
                Button delete = v.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moreDialog.dismiss();
                        AlertDialog.Builder dialog =new AlertDialog.Builder(getContext());
                        dialog.setTitle("Delete" + " \"\"" + list.get(position).getTitle() + "\"\"")
                                .setMessage("Are you sure you want to delete this post?")
                                .setNegativeButton("No, don't delete it", null)
                                .setPositiveButton("Yes, delete it", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        TripPlanService tripPlanService = TripShareApplication.createService(TripPlanService.class);
                                        tripPlanService.delete(list.get(position).getId()).enqueue(new Callback<Integer>() {
                                            @Override
                                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                                if(response.isSuccessful()) {
                                                    list.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                                else {
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Integer> call, Throwable t) {
                                            }
                                        });
                                    }
                                })
                                .show();

                    }
                });

                Button editBtn = v.findViewById(R.id.edit);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moreDialog.dismiss();
                        row.performClick();
                    }
                });
                Button shareBtn = v.findViewById(R.id.share);
                shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moreDialog.dismiss();
                        String link = "https://tripblog.com?postId=" + list.get(position).getId();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                        sendIntent.putExtra(Intent.EXTRA_TITLE, "Share this trip");
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        context.startActivity(shareIntent);
                    }
                });


            }
        });

        return (row);
    }

    public void updateData(List<TripPlan> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

}
