package com.example.tripblog.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.component.CustomPostNewsfeedAdapter;
import com.example.tripblog.ui.component.PostnewsfeedAdapterRecycle;
import com.example.tripblog.ui.post.ViewPost;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String [] id = {
        "1","2","3"
    };
    // Dữ liệu mẫu
    private String [] name = {
        "Mizhelle","Sebastiano","Pietro Mossali"
    };
    private String [] title = {
        "Best Cherry Blossom Spots in Tokyo","French Riviera - Cote d'Azur Gui9de","Milan Photo Touri"
    };
    private String [] briefDes = {
        "Tokyo resident since 2011.",
            "It's dice I was 5 years old that I enjoy doing trips to Milan with My Familly",
            "I was in New Tork for a holiday in 2019 and it was one of most place"
    };
    private String [] views = {
            "150 views","78 views","123 views"
    };
    private String [] images = {
            "https://www.state.gov/wp-content/uploads/2019/04/Japan-2107x1406.jpg",
            "https://www.state.gov/wp-content/uploads/2019/04/Japan-2107x1406.jpg",
            "https://www.state.gov/wp-content/uploads/2019/04/Japan-2107x1406.jpg"
    };
    private String [] avatars = {
            "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Flag_of_Japan.svg/2560px-Flag_of_Japan.svg.png",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Flag_of_Japan.svg/2560px-Flag_of_Japan.svg.png",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Flag_of_Japan.svg/2560px-Flag_of_Japan.svg.png"
    };

    private RecyclerView listPostnewsFeed;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_home, container, false);
        listPostnewsFeed = frameLayout.findViewById(R.id.listPostnewsFeed);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        listPostnewsFeed.setLayoutManager(linearLayoutManager);
        PostnewsfeedAdapterRecycle postnewfeed = new PostnewsfeedAdapterRecycle();
        postnewfeed.setDate(id,name,title,briefDes,views,avatars,images);
        postnewfeed.setContext((MainActivity) getContext());
        TextView home = frameLayout.findViewById(R.id.home);

        postnewfeed.setItemClickListener(new PostnewsfeedAdapterRecycle.ItemClickListener() {
            @Override
            public void onItemClick(String postid) {
                home.setText("1");
                Intent intent=new Intent(getContext(), ViewPost.class);
                Bundle bundle = new Bundle();
                bundle.putString("postid", postid);
                intent.putExtras(bundle);
                home.setText("Click");
                showToast(postid);
                startActivityForResult(intent, 1122);

            }
        });
        listPostnewsFeed.setAdapter(postnewfeed);

        return frameLayout;
    }
    private void showToast(String msg){
        Toast.makeText((MainActivity)getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}