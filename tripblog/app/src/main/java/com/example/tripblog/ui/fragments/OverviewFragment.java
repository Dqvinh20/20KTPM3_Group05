package com.example.tripblog.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.tripblog.R;
import com.example.tripblog.adapter.PostItemAdapter;
import com.example.tripblog.databinding.FragmentOverviewBinding;
import com.example.tripblog.model.PostItem;

import java.util.ArrayList;
import java.util.List;

public class OverviewFragment extends Fragment {
    FragmentOverviewBinding binding;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance() {
        OverviewFragment fragment = new OverviewFragment();
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
        // Inflate the layout for this
        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        binding.contentRecyclerView.setHasFixedSize(true);
        binding.contentRecyclerView.setAdapter(initAdapter());
        return binding.getRoot();
    }

    private PostItemAdapter initAdapter() {
        return new PostItemAdapter(initData());
    }

    private List<PostItem> initData() {
        List<PostItem> postItemList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            postItemList.add(new PostItem(i, "Noi dung bai viet",
                    "Hello cac ban"));
        }

        return postItemList;
    }
}