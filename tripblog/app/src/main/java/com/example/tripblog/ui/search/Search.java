package com.example.tripblog.ui.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.widget.SearchView;

import com.example.tripblog.R;
import com.example.tripblog.TripBlogApplication;
import com.example.tripblog.api.services.SearchService;
import com.example.tripblog.databinding.ActivitySearchBinding;
import com.example.tripblog.model.Location;
import com.example.tripblog.model.Post;
import com.example.tripblog.model.User;
import com.example.tripblog.model.response.SearchResponse;
import com.example.tripblog.ui.post.ViewSearchList;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {

    ActivitySearchBinding binding ;
    List<User> userList;
    List<Location> arrlocation;
    private static final String TAG = Search.class.getSimpleName();
    CustomSuggestionSearchLocationAdapter arrayLocationSuggest;
    CustomSuggestionSearchUserAdapter arrayUserAdapterSuggest;
    String [] model_data_search = {"China", "Wales", "Belgium", "Japan", "France", "America",
            "Germany", "Canada", "Spain", "Brazil", "South Africa", "Belgium", "India"};
    Suggest_Search_Object[] result = new Suggest_Search_Object[]{};
    Suggest_Search_Object[] listsuggest = new Suggest_Search_Object[]{
            new Suggest_Search_Object("Ca Mau","Viet Nam","Location"),
            new Suggest_Search_Object("HCM","Viet Nam","Location"),
            new Suggest_Search_Object("Ha Noi","Viet Nam","Location"),
            new Suggest_Search_Object("Viet Hoang","viethoandid","User"),
            new Suggest_Search_Object("Viet Dung","vietdungid","User")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        System.out.println("listsuggest");
//        arrayAdapterSuggest = new CustomSuggestionSearchAdapter(Search.this,R.layout.suggest_search_component,listsuggest);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_activity, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_activity);
        menuItem.expandActionView();

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                finish();
                return false;
            }
        });
        SearchView searchView =(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        binding.suggestSearchLocation.setAdapter(arrayLocationSuggest);
        binding.suggestSearchLocation.setBackgroundColor(Color.WHITE);

       binding.suggestSearchLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(Search.this, ViewSearchList.class);
                intent.putExtra("title", arrlocation.get(position).getName());
                intent.putExtra("LocationId", arrlocation.get(position).getId());
                intent.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intent, 1122);
                searchView.setQuery(arrlocation.get(position).getName(),true);

            }
        });
       binding.suggestSearchUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Log.i("data" ,userList.get(position).getId().toString());
           }
       });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) return false;
                SearchService searchService = TripBlogApplication.createService(SearchService.class);
                searchService.getUserFromText(newText).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            JsonObject data = response.body();
                            JsonArray list = data.getAsJsonArray("users");
                            userList = new Gson().fromJson(list, new TypeToken<List<User>>(){}.getType());

                            Log.d("Data",userList.toString());
                            arrayUserAdapterSuggest = new CustomSuggestionSearchUserAdapter(Search.this,
                                    R.layout.suggest_search_component,
                                    userList);
                            binding.suggestSearchUser.setAdapter(arrayUserAdapterSuggest);
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        binding.notificationNoResult.setText("We coundn't find any users or places matching "+"'"+searchView.getQuery()+"'");
                        t.printStackTrace();
                        Log.d("Data","false");
                        Log.e(TAG, t.toString());
                        Snackbar.make(binding.getRoot(), "Fail to connect to server", Snackbar.LENGTH_LONG)
                                .setAction("Retry", view -> {
//                            createPost();
                                })
                                .show();
                    }
                });
                searchService.searchPlaces(newText,5).enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                        arrlocation = response.body().getLocations();
                        Log.d("Data",arrlocation.toString());
                        arrayLocationSuggest = new CustomSuggestionSearchLocationAdapter(Search.this,
                                R.layout.suggest_search_component,
                                arrlocation);
                        binding.suggestSearchLocation.setAdapter(arrayLocationSuggest);
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        t.printStackTrace();
                        Log.d("Data","false");
                        Log.e(TAG, t.toString());
                        Snackbar.make(binding.getRoot(), "Fail to connect to server", Snackbar.LENGTH_LONG)
                                .setAction("Retry", view -> {
//                            createPost();
                                })
                                .show();
                    }
                });
                if(userList!= null&& arrlocation!=null && userList.size()==0 && arrlocation.size()==0){
                    binding.notificationNoResult.setText("We coundn't find any users or places  matching "+"'"+searchView.getQuery()+"'");
                }
                else {
                    binding.notificationNoResult.setText(null);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}