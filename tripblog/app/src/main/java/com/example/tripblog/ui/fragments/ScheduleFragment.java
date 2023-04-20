package com.example.tripblog.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.adapter.ScheduleItemAdapter;
import com.example.tripblog.api.services.ScheduleService;
import com.example.tripblog.databinding.FragmentScheduleBinding;
import com.example.tripblog.model.Location;
import com.example.tripblog.model.Schedule;
import com.example.tripblog.ui.dialog.ImagePreviewDialog;
import com.example.tripblog.ui.interfaces.IOnClickListener;
import com.example.tripblog.ui.map.MapActivity;
import com.example.tripblog.ui.tripPlan.AddPlaceBottomSheet;
import com.example.tripblog.ui.tripPlan.TripPlanDetailActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleFragment extends Fragment implements IOnClickListener {
    private static final String TAG = ScheduleFragment.class.getSimpleName();
    private final ScheduleService scheduleService = TripBlogApplication.createService(ScheduleService.class);
    ScheduleItemAdapter adapter = new ScheduleItemAdapter(this);
    FragmentScheduleBinding binding;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(Bundle args) {
        ScheduleFragment fragment = new ScheduleFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        binding.contentRecyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    // Handle item click in adapter
    @Override
    public void onClick(String action, Bundle data) {
        switch (action) {
            case "open_add_place":
                final AddPlaceBottomSheet bottomSheet = new AddPlaceBottomSheet();
                FragmentManager fragmentManager =
                        getActivity().getSupportFragmentManager();

                bottomSheet.setOnLocationClickListener(locationId -> {
                    if (data != null) {
                        int schedulePos = data.getInt("schedulePos");
                        addNewLocation(schedulePos, locationId);
                    }

                    bottomSheet.dismiss();
                });

                bottomSheet.show(fragmentManager, AddPlaceBottomSheet.class.getSimpleName());
                break;
            case "remove_location":
                if (data != null) {
                    int locationBindingPos = data.getInt("locationBindingPos");
                    int locationPos = data.getInt("locationPos");
                    int schedulePos = data.getInt("schedulePos");
                    removeLocation(schedulePos, locationPos, locationBindingPos);
                }
                break;
            case "edit_note":
                if (data != null) {
                    int locationPos = data.getInt("locationPos");
                    int locationId = data.getInt("locationId");
                    int schedulePos = data.getInt("schedulePos");
                    int scheduleBindingPos = data.getInt("locationBindingPos");
                    String note = data.getString("note");
                    editNote(schedulePos, locationId, note, locationPos, scheduleBindingPos);
                }
                break;
            case "view_on_map":
                if (data != null) {
                    Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                    data.putSerializable("schedule", adapter.getScheduleList().get(data.getInt("schedulePos")));
                    data.putString("tripTitle",((TripPlanDetailActivity) getActivity()).getPostLiveData().getValue().getTitle());
                    mapIntent.putExtras(data);
                    startActivity(mapIntent);
                }
                break;
            case "show_img_preview":
                if (data != null) {
                    final ImagePreviewDialog imagePreviewDialog = new ImagePreviewDialog();
                    imagePreviewDialog.setArguments(data);
                    imagePreviewDialog.show(getChildFragmentManager(), ImagePreviewDialog.class.getSimpleName());
                }
                break;
            default:
                break;
        }
    }

    // Call api
    public void addNewLocation(int schedulePos, int locationId) {
        int scheduleId = (int) adapter.getItemId(schedulePos);

        scheduleService.addLocation(scheduleId, locationId)
                .enqueue(new Callback<Location>() {
                    @Override
                    public void onResponse(Call<Location> call, Response<Location> response) {
                        if (!response.isSuccessful()) {
                            Snackbar
                                    .make(binding.getRoot(), "Fail to add place", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        };
                        Location location = response.body();
                        adapter.addLocation(schedulePos, location);
                    }
                    @Override
                    public void onFailure(Call<Location> call, Throwable t) {
                        Snackbar
                                .make(binding.getRoot(), "Can't connect to server!", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
    }
    public void removeLocation(int schedulePos, int locationPos, int locationBindingPos) {
        int scheduleId = (int) adapter.getItemId(schedulePos);

        scheduleService.removeLocation(scheduleId, locationPos)
                .enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (!response.isSuccessful()) {
                            Snackbar
                                    .make(binding.getRoot(), "Fail to remove place", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        };
                        Integer isSuccess = response.body();
                        if (isSuccess != null && isSuccess == 1) {
                            adapter.removeLocation(schedulePos, locationBindingPos);
                        }
                        Snackbar
                                .make(binding.getRoot(), "Removed place", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        t.printStackTrace();
                        Snackbar
                                .make(binding.getRoot(), "Can't connect to server!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }
    private void editNote(int schedulePos, int locationId, String note, int locationPos, int locationBindingPos) {
        int scheduleId = (int) adapter.getItemId(schedulePos);

        scheduleService.editLocationNote(scheduleId, locationId, locationPos, note)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful()) {
                            Snackbar
                                    .make(binding.getRoot(), "Fail to edit note", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        };

                        JsonObject result = response.body();
                        if (result != null && result.has("note")) {
                            adapter.editNoteLocation(schedulePos, locationBindingPos, note);
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Snackbar
                                .make(binding.getRoot(), "Can't connect to server!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    public void setEditable(boolean editable) {
        adapter.setEditable(editable);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            List<Schedule> schedules = (List<Schedule>) args.getSerializable("schedules");
            adapter.setScheduleList(schedules);
        }
    }
}