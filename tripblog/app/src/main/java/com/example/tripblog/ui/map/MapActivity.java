package com.example.tripblog.ui.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.databinding.ActivityMapBinding;
import com.example.tripblog.model.Location;
import com.example.tripblog.model.Schedule;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener
{
    GoogleMap googleMap;
    private boolean isFullScreen = false;
    private Schedule currSchedule;
    private List<Marker> markerList = new ArrayList<>();
    private Marker currActiveMarker = null;
    private int markerColor;

    // UI
    ActivityMapBinding binding;
    SupportMapFragment supportMapFragment;
    TextView locationName, locationPosition, locationNote, formattedAddress;
    ImageView locationPhoto;
    ConstraintLayout bottomSheetLayout;
    BottomSheetBehavior sheetBehavior;
    Button directions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        bottomSheetLayout = binding.bottomSheetLayout.getRoot();
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        sheetBehavior.setDraggable(true);
        bottomSheetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        locationPosition = findViewById(R.id.locationPosition);
        locationNote = findViewById(R.id.locationNote);
        locationName = findViewById(R.id.locationName);
        locationPhoto = findViewById(R.id.locationPhoto);
        formattedAddress = findViewById(R.id.formattedAddress);
        directions = findViewById(R.id.directions);

        Bundle data = getIntent().getExtras();
        if (data != null){
            String tripTitle = data.getString("tripTitle");
            Log.d("test", tripTitle);
            getSupportActionBar().setTitle(tripTitle);
            currSchedule = (Schedule) data.getSerializable("schedule");
            markerColor = currSchedule.getMarkerColor();
        }

        supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map, supportMapFragment)
                .commit();

        supportMapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configMap() {
        if (this.googleMap == null) return;
        UiSettings uiSettings = this.googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        configMap();
        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.setOnMapClickListener(this);

        List<Location> locationList = currSchedule.getLocations();
        if (locationList != null) {
            locationList.forEach(location -> {
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                Bitmap bitmap = getBitmapMarker(location.getPosition().toString(), 300, 300);
                MarkerOptions markerOpts = new MarkerOptions()
                        .position(pos)
                        .title(location.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                Marker marker = this.googleMap.addMarker(markerOpts);
                marker.setTag(location);
                this.markerList.add(marker);
            });
            loadLocationDetail(markerList.get(0));
            activeMarker(markerList.get(0));
            if (locationList.size() > 1) {
                seeAllMarker();
            }
            else {
                goToMarker(markerList.get(0));
            }
        }
    }

    public void goToMarker(Marker marker) {
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10));
    }

    public Bitmap getBitmapMarker(String mText, Integer width, Integer height)
    {
        try
        {
            View markerLayout = getLayoutInflater().inflate(R.layout.custom_marker, null);
            markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

            TextView locationPosition = (TextView) markerLayout.findViewById(R.id.locationPosition);
            locationPosition.getBackground().setTint(markerColor);
            locationPosition.setText(mText);

            final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            markerLayout.draw(canvas);

            return scaleMarkerIcon(bitmap, width, width);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public Bitmap scaleMarkerIcon(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    @Override
    public void onCameraMove() {
    }

    public void loadLocationDetail(@NonNull Marker marker) {
        Location location = (Location) marker.getTag();
        locationPosition.setText(location.getPosition().toString());
        locationPosition.getBackground().setTint(markerColor);
        locationName.setText(location.getName());
        formattedAddress.setText(location.getFormattedAddress());
        if (location.getNote() == null || location.getNote().isEmpty()) {
            locationNote.setText("");
            locationNote.setVisibility(View.GONE);
        }
        else {
            locationNote.setText(location.getNote());
            locationNote.setVisibility(View.VISIBLE);
        }

        directions.setOnClickListener(view -> {
            openGgMap();
        });
        // Load place avatar
        Glide.with(this).load(location.getPhoto()).into(locationPhoto);
    }

    public void openGgMap() {
        List<Location> locationList = currSchedule.getLocations();
        if (locationList != null) {
            int n = locationList.size();
            Location lastLocation = locationList.get(n - 1);
            LatLng pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            String query = Uri.encode(pos.latitude + "," + pos.longitude);
            String urlPattern = "google.navigation:q=%s";
            String url = String.format(urlPattern, query);

            String waypoints = "";
            if (n > 1) {
                waypoints = locationList.stream().limit(n - 1).map(location -> {
                    LatLng pos1 = new LatLng(location.getLatitude(), location.getLongitude());
                    return pos1.latitude + "," + pos1.longitude;
                }).collect(Collectors.joining("|"));
                waypoints = Uri.encode(waypoints);
                url += "&waypoints=" + waypoints;
            }

            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse(url);

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
            else {
                Snackbar.make(binding.getRoot(), "Google Map was not installed", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void seeAllMarker() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 500; // offset from edges of the map in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        this.googleMap.moveCamera(cameraUpdate);
    }

    public void activeMarker(Marker newActiveMarker) {
        if (currActiveMarker != null) {
            Location location = (Location) this.currActiveMarker.getTag();
            this.currActiveMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    getBitmapMarker(location.getPosition().toString(), 300, 300)
            ));
        }

        this.currActiveMarker = newActiveMarker;
        Location location = (Location) this.currActiveMarker.getTag();
        this.currActiveMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                getBitmapMarker(location.getPosition().toString(), 400, 400)
        ));
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        loadLocationDetail(marker);
        activeMarker(marker);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(marker.getPosition());
        this.googleMap.animateCamera(cameraUpdate);
        return true;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        isFullScreen = !isFullScreen;
        if (isFullScreen) {
            sheetBehavior.setHideable(true);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        else {
            sheetBehavior.setHideable(false);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}