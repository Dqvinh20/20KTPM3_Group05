package com.example.tripblog.ui.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.widget.SearchView;

import com.example.tripblog.R;
import com.example.tripblog.databinding.ActivitySearchBinding;
import com.example.tripblog.ui.post.ViewPost;
import com.example.tripblog.ui.post.ViewSearchList;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Search extends AppCompatActivity {

    ActivitySearchBinding binding ;
    CustomSuggestionSearchAdapter arrayAdapterSuggest;
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
        binding.suggestSearchLocation.setAdapter(arrayAdapterSuggest);
        binding.suggestSearchLocation.setBackgroundColor(Color.WHITE);
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

       binding.suggestSearchLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(result[position].getTitle(),true);

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // ToDo:query data search
                int randomNum = ThreadLocalRandom.current().nextInt(0, 1 + 1);
                if (listsuggest.length>0){
                    result = listsuggest;
                    binding.notificationNoResult.setText(null);
                }
                else{
                    result = new Suggest_Search_Object[]{};
                    binding.notificationNoResult.setText("We coundn't find any users or places matching "+"'"+searchView.getQuery()+"'");
                }
                arrayAdapterSuggest = new CustomSuggestionSearchAdapter(Search.this,R.layout.suggest_search_component,result);
                binding.suggestSearchLocation.setAdapter(arrayAdapterSuggest);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}