package com.example.tripblog.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
//import com.example.tripblog.ui.component.PostnewsfeedAdapterRecycle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFollowAdapter extends RecyclerView.Adapter<UserFollowAdapter.UserFollowViewHolder> {

    private List<User> userList;
    private boolean isFollowerFragment;
    private ItemClickListener listener;
    private List<User> followingList = null;
    UserService userService = TripBlogApplication.createService(UserService.class);
    private final int loggedUserId = TripBlogApplication.getInstance().getLoggedUser().getId();


    public UserFollowAdapter(List<User> userList, boolean isFollowerFragment, ItemClickListener listener) {
        this.userList = userList;
        this.isFollowerFragment = isFollowerFragment;
        this.listener = listener;

        loadFollowingList();
    }

    private void loadFollowingList() {
        userService.getUserFollowing(TripBlogApplication.getInstance().getLoggedUser().getId()).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if(response.isSuccessful()){
                    JsonArray rawData = response.body().getAsJsonArray();
                    JsonObject jsonObject = (JsonObject) rawData.get(0);
                    JsonArray followingJsonArray = jsonObject.getAsJsonArray("followings");
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<List<User>>() {}.getType();
                    followingList = gson.fromJson(followingJsonArray, userListType);
                    notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
            }
        });
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
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.avatar)
                .into(holder.avatar);
        holder.name.setText(currUser.getUserName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(currUser.getId());
            }
        });
        holder.followBtn.addOnCheckedChangeListener((button, isChecked) -> {
            // Followed
            if (isChecked) {
                button.setTextColor(Color.RED);
                button.setText("Unfollow");
            }
            // Not follow
            else {
                button.setTextColor(Color.WHITE);
                button.setText("Follow");
            }
        });
        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.followBtn.isChecked()) {
                    userService.followUser(currUser.getId()).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {}
                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
                else {
                    userService.unfollowUser(currUser.getId()).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {}

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });

        if (followingList == null) return;
        boolean isFollowing = followingList.parallelStream().anyMatch(user -> user.getId() == currUser.getId());
        holder.followBtn.setChecked(isFollowing);

        if (userList == null) return;
        boolean isLoggedUser = userList.parallelStream().anyMatch(user -> user.getId() == loggedUserId);
        holder.followBtn.setVisibility(isLoggedUser ? View.GONE : View.VISIBLE);
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
    public interface ItemClickListener {
        void onItemClick(int userId);
    }
    public class UserFollowViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private MaterialButton followBtn;
        public UserFollowViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.usernameTxt);
            followBtn = itemView.findViewById(R.id.followBtn);
        }
    }
}
