package com.example.tripblog.ui.tripPlan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.TripPlanService;
import com.example.tripblog.databinding.ActivityViewSearchListBinding;
import com.example.tripblog.model.TripPlan;
import com.example.tripblog.ui.search.Search;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewSearchList extends AppCompatActivity {
    ActivityViewSearchListBinding binding;
    private static final String TAG = ViewSearchList.class.getSimpleName();

    TextView hint_search;
    List<TripPlan> listpost;
    CustomResultSearchAdapter postAdapter;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewSearchListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent currIntent = getIntent();
        Bundle currBundle = currIntent.getExtras();;
        Integer locationId = currBundle.getInt("LocationId");
        Log.d(TAG,locationId.toString());
        TripPlanService tripPlanService = TripBlogApplication.createService(TripPlanService.class);
        tripPlanService.getTripPlanByLocation(locationId).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JsonArray list = response.body();
                Log.d(TAG,list.toString());
                listpost = new Gson().fromJson(list, new TypeToken<List<TripPlan>>(){}.getType());
                Log.d(TAG,listpost.toString());
                postAdapter = new CustomResultSearchAdapter(ViewSearchList.this, R.layout.post_search_list_component
                        ,listpost);
                binding.listResultSearch.setAdapter(postAdapter);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
        binding.listResultSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewSearchList.this, TripPlanDetailActivity.class);
                intent.putExtra("postId", listpost.get(position).getId());
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_result_search_activity, menu);
        MenuItem menuItem = menu.findItem(R.id.hint_search_activity);
        SpannableString s = new SpannableString(menuItem.getTitle());

        s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);

        menuItem.setTitle(s);
        Log.i("expand",menuItem.isActionViewExpanded()?"Yes":"No");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem searchitem = menu.findItem(R.id.hint_search_activity);
        LinearLayout rootView = (LinearLayout) searchitem.getActionView();
        hint_search= rootView.findViewById(R.id.hint_search);
        Intent currIntent = getIntent();
        Bundle currBundle = currIntent.getExtras();;
        hint_search.setText(currBundle.getString("title"));

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleX(0.9f).setDuration(100).start();
                    v.animate().scaleY(0.9f).setDuration(100).start();
                    return false;
                } else if (action == MotionEvent.ACTION_UP) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(200).start();
                    v.animate().scaleY(1f).setDuration(200).start();
                    return false;
                }else if (action == MotionEvent.ACTION_CANCEL) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(200).start();
                    v.animate().scaleY(1f).setDuration(200).start();
                    return true;
                }
                return false;
            }
        });
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewSearchList.this, Search.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intent, 1122);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.hint_search_activity:

                Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.back_arrow_menu_item:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}