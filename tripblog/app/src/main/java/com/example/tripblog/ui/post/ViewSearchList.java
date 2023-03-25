package com.example.tripblog.ui.post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.tripblog.R;
import com.example.tripblog.databinding.ActivitySearchBinding;
import com.example.tripblog.databinding.ActivityViewSearchListBinding;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.search.Search;

public class ViewSearchList extends AppCompatActivity {
    ActivityViewSearchListBinding binding;
    TextView hint_search;
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
        binding.listResultSearch.setAdapter(new CustomResultSearchAdapter(this,R.layout.post_search_list_component,
                images,title,avatars,name,views,views));
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}