package com.example.tripblog.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.UserService;
import com.example.tripblog.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFollowAdapter extends RecyclerView.Adapter<UserFollowAdapter.UserFollowViewHolder> {

    private List<User> userList;
    private boolean isFollowerFragment;

    public UserFollowAdapter(List<User> userList, boolean isFollowerFragment) {
        this.userList = userList;
        this.isFollowerFragment = isFollowerFragment;
    }

    @NonNull
    @Override
    public UserFollowAdapter.UserFollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserFollowViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_follow_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserFollowAdapter.UserFollowViewHolder holder, int position) {
        User currUser = userList.get(position);

        Glide.with(holder.itemView)
                .load(currUser.getAvatar())
                .placeholder(R.drawable.da_lat)
                .error(R.drawable.avatar)
                .into(holder.avatar);
        holder.name.setText(currUser.getUserName());
        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            UserService userService = TripBlogApplication.createService(UserService.class);
            @Override
            public void onClick(View view) {
                if(holder.followBtn.getText().toString().equals("Unfollow")){
                    view.setBackgroundColor(Color.RED);
                    holder.followBtn.setText("Follow");
                    holder.followBtn.setTextColor(Color.WHITE);

                    userService.unfollowUser(currUser.getId()).enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(view.getContext(), "Unfollow", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(view.getContext(), " Not success", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonArray> call, Throwable t) {
                            Toast.makeText(view.getContext(), "Fail", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
                }
                else{
                    view.setBackgroundColor(Color.WHITE);
                    holder.followBtn.setTextColor(Color.RED);
                    holder.followBtn.setText("Unfollow");
                    userService.followUser(currUser.getId()).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(view.getContext(), "Follow", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(view.getContext(), " Not success", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(view.getContext(), "Fail", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });

                }
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if(userList != null)
        {
            return userList.size();
        }
        return 0;
    }

    public class UserFollowViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private Button followBtn;
        public UserFollowViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.usernameTxt);
            followBtn = itemView.findViewById(R.id.followBtn);
        }
    }
}
