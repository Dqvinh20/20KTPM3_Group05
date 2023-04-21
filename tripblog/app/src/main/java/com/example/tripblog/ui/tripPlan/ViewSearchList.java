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

import com.example.tripblog.R;
import com.example.tripblog.TripShareApplication;
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
    List<TripPlan> tripPlansList;
    CustomResultSearchAdapter tripPlansAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewSearchListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle currBundle = getIntent().getExtras();;
        Integer locationId = currBundle.getInt("LocationId");

        TripPlanService tripPlanService = TripShareApplication.createService(TripPlanService.class);
        tripPlanService.getTripPlanByLocation(locationId).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JsonArray list = response.body();
                tripPlansList = new Gson().fromJson(list, new TypeToken<List<TripPlan>>(){}.getType());
                tripPlansList.sort((tripPlan, t1) -> Float.compare(t1.getAvgRating(), tripPlan.getAvgRating()));

                if (tripPlansList != null && tripPlansList.size() == 0) {
                    binding.noResultInfo.setVisibility(View.VISIBLE);
                }
                else {
                    binding.noResultInfo.setVisibility(View.GONE);
                }

                tripPlansAdapter = new CustomResultSearchAdapter(ViewSearchList.this, R.layout.post_search_list_component, tripPlansList);
                binding.listResultSearch.setAdapter(tripPlansAdapter);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });
        binding.listResultSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewSearchList.this, TripPlanDetailActivity.class);
                intent.putExtra("postId", tripPlansList.get(position).getId());
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.hint_search_activity:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}