package com.example.tripblog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripblog.R;
import com.example.tripblog.model.Schedule;
import com.example.tripblog.utils.ColorUtil;
import com.google.android.material.divider.MaterialDivider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ScheduleItemAdapter extends RecyclerView.Adapter<ScheduleItemAdapter.ScheduleItemViewHolder> {
    private List<Schedule> scheduleList;

    private boolean isEditable = false;

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        notifyDataSetChanged();
    }

    public ScheduleItemAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
        loadScheduleColors();
    }

    private void loadScheduleColors() {
        if (this.scheduleList == null) return;
        this.scheduleList.forEach(schedule -> schedule.setMarkerColor(
                ColorUtil.randomHexColor(schedule.getDate().toString()
                )));
    }

    public ScheduleItemAdapter(List<Schedule> scheduleList, boolean isEditable) {
        this.scheduleList = scheduleList;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public ScheduleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item_layout, parent, false);
        return new ScheduleItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleItemViewHolder holder, int position) {
        Schedule currSchedule = scheduleList.get(position);
        holder.title.setText(currSchedule.getTitle());
        holder.dateText.setText(formatDate(currSchedule.getDate()));
        holder.countPlaces.setText(currSchedule.getLocationCount() + " " + holder.itemView.getResources().getString(R.string.count_places));

        int visibility = currSchedule.isExpandable() ? View.VISIBLE : View.GONE;
        holder.expandableLayout.setVisibility(visibility);

        int iconSrc = currSchedule.isExpandable() ?  R.drawable.ic_baseline_expand_more_24 : R.drawable.ic_baseline_chevron_left_24;
        holder.expandableIcon.setImageResource(iconSrc);

        int isEditVisibility = isEditable ? View.VISIBLE : View.GONE;
        holder.mapRecycleView.setVisibility(currSchedule.getLocationCount() == 0 ? View.GONE : View.VISIBLE);

        MapScheduleItemAdapter mapItemAdapter = new MapScheduleItemAdapter(currSchedule.getLocations(), currSchedule.getMarkerColor());
        mapItemAdapter.setEditable(isEditable);
        holder.mapRecycleView.setAdapter(mapItemAdapter);

        if (currSchedule.getLocations() == null || currSchedule.getLocations().isEmpty()) {
            holder.divider.setVisibility(View.GONE);
        }
        else {
            holder.divider.setVisibility(isEditVisibility);
        }
        holder.addPlace.setVisibility(isEditVisibility);

        holder.titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Schedule schedule =  scheduleList.get(holder.getBindingAdapterPosition());
                schedule.setExpandable(!schedule.isExpandable());
                notifyItemChanged(holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (scheduleList != null) {
            return scheduleList.size();
        }
        return 0;
    }

    public String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public class ScheduleItemViewHolder extends RecyclerView.ViewHolder {
        ImageView expandableIcon;
        MaterialDivider divider;
        TextView title, dateText, countPlaces, addPlace;
        RelativeLayout titleLayout;
        ConstraintLayout expandableLayout;

        RecyclerView mapRecycleView;

        public ScheduleItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            dateText = itemView.findViewById(R.id.dateText);
            countPlaces = itemView.findViewById(R.id.countPlaces);
            addPlace = itemView.findViewById(R.id.addPlace);
            titleLayout = itemView.findViewById(R.id.titleLayout);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            expandableIcon = itemView.findViewById(R.id.expandableIcon);
            divider = itemView.findViewById(R.id.divider);
            mapRecycleView = itemView.findViewById(R.id.mapRecycleView);
        }
    }
}
