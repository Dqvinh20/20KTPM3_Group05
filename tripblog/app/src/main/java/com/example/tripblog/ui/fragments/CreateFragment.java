package com.example.tripblog.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tripblog.R;
import com.example.tripblog.databinding.FragmentCreateBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateFragment extends Fragment {
    FragmentCreateBinding binding;
    private MaterialDatePicker tripDates = null;

    private Long startDate = null;
    private Long endDate = null;

    public CreateFragment() {
        // Required empty public constructor
    }

    public static CreateFragment newInstance() {
        CreateFragment fragment = new CreateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateBinding.inflate(inflater,container,false);
        binding.tripDates.setOnClickListener(view -> onTripDatesPicker());
        binding.states.setOnClickListener(view -> openPrivacySettingBottomSheet());

        return binding.getRoot();
    }

    private void onTripDatesPicker() {
        if (tripDates == null) {
            tripDates = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Trip dates")
                    .build();

            tripDates.addOnPositiveButtonClickListener((selection -> {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                Pair<Long, Long> dates = (Pair<Long, Long>) selection;
                cal.setTimeInMillis(dates.first);
                binding.startDate.setText(sdf.format(cal.getTime()));
                cal.setTimeInMillis(dates.second);
                binding.endDate.setText(sdf.format(cal.getTime()));
            }));
        }

        tripDates.show(getActivity().getSupportFragmentManager(), "TRIP-DATES-PICKER");
    }

    private void openPrivacySettingBottomSheet() {
        View v = getLayoutInflater().inflate(R.layout.state_choose_bottom_sheet, null);
        BottomSheetDialog popupMenu = new BottomSheetDialog(this.getContext());
        popupMenu.setContentView(v);
        popupMenu.show();

        LinearLayout publicLayout = (LinearLayout) v.findViewById(R.id.publicChoose);
        publicLayout.setOnClickListener(view -> {
            binding.states.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_public_24, 0, R.drawable.ic_baseline_arrow_drop_down_24, 0);
            binding.states.setText("Public");
            popupMenu.dismiss();
        });
        LinearLayout privateLayout = (LinearLayout) v.findViewById(R.id.privateChoose);
        privateLayout.setOnClickListener(view -> {
            binding.states.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_arrow_drop_down_24, 0);
            binding.states.setText("Private");
            popupMenu.dismiss();
        });
    }
}