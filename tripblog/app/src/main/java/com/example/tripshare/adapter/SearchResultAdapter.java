package com.example.tripshare.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripshare.R;
import com.example.tripshare.model.Location;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Location> locationList;

    private IOnLocationClickListener onLocationClickListener;

    public interface IOnLocationClickListener {
        void onClick(Integer locationId);
    }

    public void setOnLocationClickListener(IOnLocationClickListener onLocationClickListener) {
        this.onLocationClickListener = onLocationClickListener;
        notifyDataSetChanged();
    }

    public void setData(List<Location> objectList) {
        this.locationList = objectList;
        notifyDataSetChanged();
    }

    public SearchResultAdapter() {}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result, parent, false);
        return new SearchResultViewHolder(v);
    }

    @Override
    public long getItemId(int position) {
        Location location = locationList.get(position);
        return location.getId();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Location location = locationList.get(position);
        if (location == null) return;
        SearchResultViewHolder viewHolder = (SearchResultViewHolder) holder;

        Location currLocation = (Location) location;
        viewHolder.title.setText(currLocation.getName());
        viewHolder.subTitle.setText(currLocation.getFormattedAddress());
        viewHolder.icon.setImageResource(R.drawable.ic_baseline_location_on_24);

        viewHolder.itemView.setOnClickListener(view -> {
            onLocationClickListener.onClick(location.getId());
        });
    }

    @Override
    public int getItemCount() {
        if (locationList != null) return locationList.size();
        return 0;
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, subTitle;
        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
        }
    }
}
