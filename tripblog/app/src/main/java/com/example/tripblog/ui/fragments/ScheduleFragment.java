package com.example.tripblog.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tripblog.R;
import com.example.tripblog.adapter.PostItemAdapter;
import com.example.tripblog.adapter.ScheduleItemAdapter;
import com.example.tripblog.databinding.FragmentScheduleBinding;
import com.example.tripblog.model.PostItem;
import com.example.tripblog.model.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {
    FragmentScheduleBinding binding;
    ScheduleItemAdapter adapter = null;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(Bundle args) {
        ScheduleFragment fragment = new ScheduleFragment();
        if (args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args =  getArguments();
            boolean isEditable = args.getBoolean("isEditable");
            List<Schedule> schedules = (List<Schedule>) args.getSerializable("schedules");
            adapter = new ScheduleItemAdapter(schedules);
            adapter.setEditable(isEditable);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        if (adapter != null)
            binding.contentRecyclerView.setAdapter(adapter);
        return binding.getRoot();
    }
}