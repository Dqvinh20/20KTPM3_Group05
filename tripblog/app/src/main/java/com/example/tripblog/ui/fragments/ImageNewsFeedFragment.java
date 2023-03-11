package com.example.tripblog.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.tripblog.R;
import com.example.tripblog.ui.MainActivity;


public class ImageNewsFeedFragment extends Fragment {
    HomeFragment homeFragment;
    MainActivity main;
    Button createtripButton;
    public static ImageNewsFeedFragment newInstance(String Arg){
        ImageNewsFeedFragment fragment = new ImageNewsFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1",Arg);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        homeFragment = (HomeFragment) getParentFragment();
        main = (MainActivity) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout relativeLayout =(RelativeLayout) inflater.inflate(R.layout.imagehone_infornews, container, false);
        createtripButton = relativeLayout.findViewById(R.id.createtripButton);
        createtripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.onMsgFromFragToMain("CREATE_TRIP_BTN","");
            }
        });
        return relativeLayout;
    }
}
