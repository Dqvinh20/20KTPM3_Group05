package com.example.tripblog.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.model.User;

import java.util.List;

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
//        holder.followBtn.setText("Follow");
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
