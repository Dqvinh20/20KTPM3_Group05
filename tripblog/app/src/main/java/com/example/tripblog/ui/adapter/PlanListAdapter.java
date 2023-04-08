package com.example.tripblog.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.PostService;
import com.example.tripblog.databinding.FragmentProfileBinding;
import com.example.tripblog.model.Post;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.fragments.PublicPlanFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanListAdapter extends ArrayAdapter<String> {
    Context context;
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
        ImageView more = row.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
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
                                        PostService postService = TripBlogApplication.createService(PostService.class);
                                        postService.delete(list.get(position).getId()).enqueue(new Callback<Boolean>() {
                                            @Override
                                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                                if(response.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(getContext(), "Not success", Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Boolean> call, Throwable t) {
                                                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                })
                                .show();

                    }
                });
            }
        });

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
