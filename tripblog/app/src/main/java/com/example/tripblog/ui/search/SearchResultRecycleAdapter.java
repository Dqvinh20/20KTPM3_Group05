package com.example.tripblog.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.model.Location;
import com.example.tripblog.model.User;

import java.io.Serializable;
import java.util.List;

public class SearchResultRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int LOCATION_TYPE = 0;
    private int USER_TYPE = 1;
    IOnLocationItemClickListener onLocationItemClickListener;
    IOnUserItemClickListener onUserItemClickListener;
    public interface IOnLocationItemClickListener {
        void onItemClick(String name, Integer id);
    }

    public interface IOnUserItemClickListener {
        void onItemClick(Integer id);
    }

    List<Serializable> results;

    public void setOnLocationItemClickListener(IOnLocationItemClickListener onLocationItemClickListener) {
        this.onLocationItemClickListener = onLocationItemClickListener;
        notifyDataSetChanged();
    }

    public void setOnUserItemClickListener(IOnUserItemClickListener onUserItemClickListener) {
        this.onUserItemClickListener = onUserItemClickListener;
        notifyDataSetChanged();
    }

    public void setResults(List<Serializable> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public SearchResultRecycleAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_search_component, parent, false);
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Serializable currItem = results.get(position);
        ResultViewHolder resultViewHolder = (ResultViewHolder) holder;
        if (getItemViewType(position) == LOCATION_TYPE) {
            Location currLocation = (Location) currItem;
            resultViewHolder.titleTextView.setText(currLocation.getName());
            resultViewHolder.subTiltleTextView.setText(currLocation.getFormattedAddress());
            resultViewHolder.icon_suggestion_search_imageview.setImageResource(R.drawable.ic_baseline_location_on_24);
            resultViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onLocationItemClickListener.onItemClick(currLocation.getName(), currLocation.getId());
                }
            });
        }
        else {
            User currUser = (User) currItem;
            resultViewHolder.titleTextView.setText(currUser.getUserName());
            resultViewHolder.subTiltleTextView.setText(currUser.getUserNameNonAccent());
            Glide.with(resultViewHolder.itemView)
                    .load(currUser.getAvatar())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.avatar)
                    .into(resultViewHolder.icon_suggestion_search_imageview);
            resultViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onUserItemClickListener.onItemClick(currUser.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (results != null) {
            return results.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (results != null) {
            Serializable currItem = results.get(position);
            if (currItem instanceof Location) {
                return LOCATION_TYPE;
            }
            return USER_TYPE;
        }

        return super.getItemViewType(position);
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, subTiltleTextView;
        ImageView icon_suggestion_search_imageview;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            subTiltleTextView = itemView.findViewById(R.id.subTiltleTextView);
            icon_suggestion_search_imageview =itemView.findViewById(R.id.icon_suggestion_search_imageview);
        }
    }


}
