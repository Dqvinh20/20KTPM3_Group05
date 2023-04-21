package com.example.tripshare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripshare.R;
import com.example.tripshare.model.Rating;
import com.example.tripshare.model.User;

import java.util.ArrayList;
import java.util.List;

public class RatingItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<Rating> ratingList = new ArrayList<>();

    public void setRatingList(List<Rating> ratingList) {
        this.ratingList = ratingList;
        notifyDataSetChanged();
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void addAll(List<Rating> ratingList) {
        int lastIndex = this.ratingList.size();
        this.ratingList.addAll(ratingList);
        notifyItemInserted(lastIndex);
    }

    public void add(Rating rating) {
        this.ratingList.add(rating);
        notifyItemInserted(ratingList.size() - 1);
    }

    public void addHead(Rating rating) {
        this.ratingList.add(0, rating);
        notifyItemInserted(0);
    }

    public void remove(int position) {
        this.ratingList.remove(position);
        notifyItemRemoved(this.ratingList.size());
    }

    public RatingItemAdapter() {
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_item_layout, parent, false);
            return new RatingItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_item_loading_layout, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ratingList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RatingItemViewHolder) {
            populateItemRows((RatingItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {

    }

    private void populateItemRows(RatingItemViewHolder holder, int position) {
        Rating rating = ratingList.get(position);
        if (rating == null) return;
        User author = rating.getAuthor();

        Glide.with(holder.itemView)
                .load(author.getAvatar())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(holder.avatar);

        holder.usernameTxt.setText(author.getName());
        holder.point.setRating(rating.getScore());
        holder.contentTxt.setText(rating.getContent());
    }

    @Override
    public long getItemId(int position) {
        if (ratingList == null || ratingList.get(position) == null) return super.getItemId(position);
        return ratingList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        if (ratingList != null) {
            return ratingList.size();
        }
        return 0;
    }



    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public class RatingItemViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView usernameTxt;
        RatingBar point;
        TextView contentTxt;

        public RatingItemViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.avatar);
            usernameTxt = itemView.findViewById(R.id.usernameTxt);
            point = itemView.findViewById(R.id.point);
            contentTxt = itemView.findViewById(R.id.contentTxt);
        }
    }
}
