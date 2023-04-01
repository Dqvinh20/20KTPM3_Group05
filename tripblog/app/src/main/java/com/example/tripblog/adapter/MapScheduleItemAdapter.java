package com.example.tripblog.adapter;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.model.Location;
import com.example.tripblog.ui.dialog.ImagePreviewDialog;
import com.example.tripblog.utils.ColorUtil;

import java.util.Comparator;
import java.util.List;

public class MapScheduleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Location> locations;
    private int markerColor;

    private boolean isEditable = false;

    private void sortByPosition() {
        if (locations == null) return;
        locations.sort(
                new Comparator<Location>() {
                    @Override
                    public int compare(Location location, Location another) {
                        return location.getPosition() - another.getPosition();
                    }
                }
        );
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
        notifyDataSetChanged();
    }

    public MapScheduleItemAdapter(List<Location> locations, int markerColor) {
        this.setHasStableIds(true);

        this.locations = locations;
        this.markerColor = markerColor;
//        sortByPosition();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_item_layout, parent, false);
        if (isEditable){
            v.setBackground(null);
        }
        return new MapScheduleItemViewHolder(v);
    }

    @Override
    public long getItemId(int position) {
        if (locations != null) {
            return locations.get(position).getId();
        }
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Location currLocation = locations.get(position);
        Log.d("onBindViewHolder", String.valueOf(currLocation.getId()));
        MapScheduleItemViewHolder mapHolder = (MapScheduleItemViewHolder) holder;

        mapHolder.locationPosition.getBackground().setTint(markerColor);
//        mapHolder.locationPosition.setText(currLocation.getPosition().toString());

        mapHolder.locationName.setText(currLocation.getName());
        mapHolder.locationNote.setTextIsSelectable(isEditable && currLocation.isExpandable());
        mapHolder.locationNote.setFocusable(isEditable && currLocation.isExpandable());
        mapHolder.locationNote.setText(currLocation.getNote());

        if (currLocation.getPhoto() != null) {
            mapHolder.locationPhoto.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView)
                    .load(currLocation.getPhoto())
                    .error(mapHolder.itemView.getResources())
                    .centerCrop()
                    .into(mapHolder.locationPhoto);
        }
        else {
            mapHolder.locationPhoto.setVisibility(View.GONE);
        }

        int isExpandedVisibility = currLocation.isExpandable() ? View.VISIBLE : View.GONE;
        mapHolder.expandableLayout.setVisibility(isExpandedVisibility);

        if (isEditable) {
            mapHolder.itemView.setSelected(currLocation.isExpandable());
            mapHolder.locationNote.setHint(mapHolder.itemView.getResources().getString(R.string.add_note_hint));
            if (mapHolder.locationNote.getText().toString().isEmpty()) {
                mapHolder.locationNote.setVisibility(isExpandedVisibility);
            }
            else {
                mapHolder.locationNote.setVisibility(View.VISIBLE);
            }
        }
        else {
            mapHolder.locationNote.setHint("");
            mapHolder.locationNote.setVisibility(View.VISIBLE);
        }

        mapHolder.removeLocation.setVisibility(isEditable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        if (locations != null) {
            return locations.size();
        }
        return 0;
    }

    public class MapScheduleItemViewHolder extends RecyclerView.ViewHolder {
        TextView locationName, locationPosition;
        ImageView locationPhoto;
        EditText locationNote;
        Button viewOnMap, removeLocation;
        LinearLayout expandableLayout;
        public MapScheduleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.locationName);
            locationPosition = itemView.findViewById(R.id.locationPosition);
            locationPhoto = itemView.findViewById(R.id.locationPhoto);
            locationNote = itemView.findViewById(R.id.locationNote);
            viewOnMap = itemView.findViewById(R.id.viewOnMap);
            removeLocation = itemView.findViewById(R.id.removeLocation);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            itemView.setOnClickListener(v -> {
                Location location = locations.get(getBindingAdapterPosition());
                location.setExpandable(!location.isExpandable());
                notifyItemChanged(getBindingAdapterPosition());
            });

            locationNote.setOnClickListener(v -> {
                Location location = locations.get(getBindingAdapterPosition());
                location.setExpandable(!location.isExpandable());
                notifyItemChanged(getBindingAdapterPosition());
            });

            locationPhoto.setOnClickListener(v -> {
                Bitmap imgSrc = ((BitmapDrawable) locationPhoto.getDrawable()).getBitmap();
                final ImagePreviewDialog imagePreviewDialog = new ImagePreviewDialog(itemView.getContext());
                imagePreviewDialog.setImgPreview(imgSrc);
                imagePreviewDialog.show();
            });

            locationNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Log.d("Focus", String.valueOf(b));
                    Location location = locations.get(getBindingAdapterPosition());
                    String newNote = locationNote.getText().toString();
                    if (!b && !location.getNote().equals(newNote)) {
                        location.setNote(newNote);
                        notifyItemChanged(getBindingAdapterPosition());
                    }
                }
            });
        }
    }
}