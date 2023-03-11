package com.example.tripblog.ui.component;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripblog.R;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.post.ViewPost;

public class PostnewsfeedAdapterRecycle extends RecyclerView.Adapter<PostnewsfeedAdapterRecycle.PostNewsFeedHolder>{
    private String[] avatars; String[] images; String[] name; String[] tilte; String[] brief_des;String[] views; String[] id;
    private Context context;
    private AdapterView.OnItemClickListener listener;
    private ItemClickListener itemClickListener;
    public void setDate(String[] id,
                        String[] name,
                        String[] tilte,
                        String[] brief_des,
                        String[] views,
                        String[] avatars,
                        String[] images){
        this.id= id;
        this.avatars = avatars;
        this.name = name;
        this.tilte = tilte;
        this.brief_des = brief_des;
        this.views = views;
        this.images = images;

        notifyDataSetChanged();
    }
    public  void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    public  void setContext(Context context){
        this.context =  context;
    }
    @NonNull
    @Override
    public PostNewsFeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_post_newsfeed,parent,false);

        return new PostNewsFeedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostNewsFeedHolder holder, int position) {
        if(name.length==0){
            return;
        }

        holder.namelb.setText(name[position]);
        holder.tiltelb.setText(tilte[position]);
        holder.briefDeslb.setText(brief_des[position]);
        holder.viewlb.setText(views[position]);
        holder.icon.setImageResource(R.drawable.app_logo_transparent);
        holder.imageView.setImageResource(R.drawable.japan);
        String urldisplay = avatars[position];
        holder.itemView.setOnClickListener(view -> {
            itemClickListener.onItemClick(id[position]);
        });
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
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

    }


    @Override
    public int getItemCount() {
        if(name != null) return name.length;
        return 0;
    }
    public interface ItemClickListener{
        void onItemClick(String postid);
    }
    public class PostNewsFeedHolder extends RecyclerView.ViewHolder{
        private TextView namelb ;
        private TextView tiltelb ;
        private TextView briefDeslb ;
        private TextView viewlb ;
        private ImageView icon ;
        private ImageView imageView ;
        public PostNewsFeedHolder(@NonNull View itemView) {

            super(itemView);
            namelb = (TextView) itemView.findViewById(R.id.nameTextView);
            tiltelb = (TextView) itemView.findViewById(R.id.TitleTextView);
            briefDeslb = (TextView) itemView.findViewById(R.id.briefDescription);
            viewlb = (TextView) itemView.findViewById(R.id.viewsTextView);
            icon = ( ImageView ) itemView.findViewById(R.id.avatar);
            imageView = ( ImageView ) itemView.findViewById(R.id.imageView);
        }
    }
}
