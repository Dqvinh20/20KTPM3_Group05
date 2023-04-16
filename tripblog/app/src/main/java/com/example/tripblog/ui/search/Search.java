package com.example.tripblog.ui.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.example.tripblog.model.User;
import com.example.tripblog.model.response.SearchResponse;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.tripPlan.ViewSearchList;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    SearchResultRecycleAdapter searchResultRecycleAdapter = new SearchResultRecycleAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        arrayAdapterSuggest = new CustomSuggestionSearchAdapter(Search.this,R.layout.suggest_search_component,listsuggest);
        binding.searchResultsView.setAdapter(searchResultRecycleAdapter);
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

        searchResultRecycleAdapter.setOnLocationItemClickListener(new SearchResultRecycleAdapter.IOnLocationItemClickListener() {
            @Override
            public void onItemClick(String name, Integer id) {
                Intent intent=new Intent(Search.this, ViewSearchList.class);
                intent.putExtra("title", name);
                intent.putExtra("LocationId", id);
                intent.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intent, 1122);
                searchView.setQuery(name,true);
            }
        });
        searchResultRecycleAdapter.setOnUserItemClickListener(new SearchResultRecycleAdapter.IOnUserItemClickListener() {
            @Override
            public void onItemClick(Integer id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", id);
                intent.putExtras(bundle);
                setResult(MainActivity.SEARCH_REQ_CODE,intent);
                finish();
            }
        });

//        binding.suggestSearchLocation.setAdapter(arrayLocationSuggest);
//
//        binding.suggestSearchLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent intent=new Intent(Search.this, ViewSearchList.class);
//            intent.putExtra("title", arrlocation.get(position).getName());
//            intent.putExtra("LocationId", arrlocation.get(position).getId());
//            intent.setAction(Intent.ACTION_VIEW);
//            startActivityForResult(intent, 1122);
//            searchView.setQuery(arrlocation.get(position).getName(),true);
//        }
//        });
//        binding.suggestSearchUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//        @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//               Log.i("data" ,userList.get(position).getId().toString());
//               Intent intent = new Intent();
//               Bundle bundle = new Bundle();
//               bundle.putInt("userId", userList.get(position).getId());
//               intent.putExtras(bundle);
//               setResult(MainActivity.SEARCH_REQ_CODE,intent);
//               finish();
//            }
//            });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) return false;
                SearchService searchService = TripBlogApplication.createService(SearchService.class);
//                searchService.searchPlaces(newText,5).enqueue(new Callback<SearchResponse>() {
//                    @Override
//                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
//                        arrlocation = response.body().getLocations();
//                        Log.d("Data",arrlocation.toString());
//                        arrayLocationSuggest = new CustomSuggestionSearchLocationAdapter(Search.this,
//                                R.layout.suggest_search_component,
//                                arrlocation);
//                        binding.suggestSearchLocation.setAdapter(arrayLocationSuggest);
//                    }
//
//                    @Override
//                    public void onFailure(Call<SearchResponse> call, Throwable t) {
//                        t.printStackTrace();
//                        Log.d("Data","false");
//                        Log.e(TAG, t.toString());
//                        Snackbar.make(binding.getRoot(), "Fail to connect to server", Snackbar.LENGTH_LONG)
//                                .setAction("Retry", view -> {
//                                    //                            createPost();
//                                })
//                                .show();
//                    }
//                });
//                searchService.getUserFromText(newText).enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                        if (response.isSuccessful()) {
//                            JsonObject data = response.body();
//                            JsonArray list = data.getAsJsonArray("users");
//                            userList = new Gson().fromJson(list, new TypeToken<List<User>>(){}.getType());
//                            arrayUserAdapterSuggest = new CustomSuggestionSearchUserAdapter(Search.this,
//                                    R.layout.suggest_search_component,
//                                    userList);
//                            binding.suggestSearchUser.setAdapter(arrayUserAdapterSuggest);
//                        }
//                    }
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//            //                        binding.notificationNoResult.setText("We coundn't find any users or places matching "+"'"+searchView.getQuery()+"'");
//                        t.printStackTrace();
//                        Log.d("Data","false");
//                        Log.e(TAG, t.toString());
//                        Snackbar.make(binding.getRoot(), "Fail to connect to server", Snackbar.LENGTH_LONG)
//                                .setAction("Retry", view -> {
//            //                            createPost();
//                                })
//                                .show();
//                    }
//                });

                search(newText);

                if(userList!= null&& arrlocation!=null && userList.size()==0 && arrlocation.size()==0){
                    binding.notificationNoResult.setText("We coundn't find any users or places  matching "+"'"+searchView.getQuery()+"'");
                    binding.notificationNoResult.setVisibility(View.VISIBLE);
                }
                else {
                    binding.notificationNoResult.setText(null);
                    binding.notificationNoResult.setVisibility(View.GONE);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void search(String query) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Location> locations = null;
                List<User> users = null;
                try {
                    locations = searchLocation(query);
                    users = searchUser(query);
                    users.removeIf(user -> user.getId().equals(TripBlogApplication.getInstance().getLoggedUser().getId()));
                    List<Serializable> results = new ArrayList<>();
                    results.addAll(locations);
                    results.addAll(users);
                    runOnUiThread(() -> {
                        searchResultRecycleAdapter.setResults(results);
                    });
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        Snackbar.make(binding.getRoot(), "Fail to connect to server", Snackbar.LENGTH_LONG)
                                .show();
                    });
                }
            }
        });
        executorService.shutdown();
    }

    public List<Location> searchLocation(String query) throws IOException {
        SearchService searchService = TripBlogApplication.createService(SearchService.class);

        Response<SearchResponse> response = searchService.searchPlaces(query, 5).execute();
        List<Location> locations = response.body().getLocations();
        arrlocation = locations;
        return arrlocation;

    }

    public List<User> searchUser(String query) throws IOException {
        SearchService searchService = TripBlogApplication.createService(SearchService.class);
        Response<JsonObject> response = searchService.getUserFromText(query).execute();
        JsonObject data = response.body();
        JsonArray list = data.getAsJsonArray("users");
        List<User> users = new Gson().fromJson(list, new TypeToken<List<User>>(){}.getType());
        userList = users;
        return users;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}