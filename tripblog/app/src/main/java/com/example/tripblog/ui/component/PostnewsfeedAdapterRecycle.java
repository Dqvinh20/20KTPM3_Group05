package com.example.tripblog.ui.component;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripblog.R;
import com.example.tripblog.model.Post;
import com.example.tripblog.ui.MainActivity;
import com.example.tripblog.ui.post.ViewPost;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostnewsfeedAdapterRecycle extends RecyclerView.Adapter<PostnewsfeedAdapterRecycle.PostNewsFeedHolder>{
    private List<Post> listPost;
    private Context context;
    private AdapterView.OnItemClickListener listener;
    private ItemClickListener itemClickListener;
    public void setDate(List<Post> listPost){
        this.listPost = listPost;
        notifyDataSetChanged();
    }

    public void appendList(List<Post> listPost) {
        if (listPost != null) {
            int lastPos = this.listPost.size() - 1;
            this.listPost.addAll(listPost);
            notifyItemChanged(lastPos);
        }
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
        if(listPost.size()==0){
            return;
        }

        holder.namelb.setText(listPost.get(position).getAuthor().getUserName());
        holder.tiltelb.setText(listPost.get(position).getTitle());
        holder.briefDeslb.setText(listPost.get(position).getBriefDescription());
        holder.viewlb.setText(listPost.get(position).getViewCount().toString()+" views");

        Glide.with(holder.itemView)
                .load(listPost.get(position).getCoverImg())
                .placeholder(R.drawable.japan)
                .error(R.drawable.japan)
                .into(holder.imageView);
        Glide.with(holder.itemView)
                .load(listPost.get(position).getAuthor().getAvatar())
                .placeholder(R.drawable.app_logo_transparent)
                .error(R.drawable.app_logo_transparent)
                .into(holder.icon);
//        URL url = null;
//        try {
//            Log.i("url",listPost.get(position).getCoverImg());
//            url = new URL(listPost.get(position).getCoverImg());
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            holder.imageView.setImageBitmap(bmp);
//        } catch (MalformedURLException e) {
//            holder.imageView.setImageResource(R.drawable.japan);
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            holder.imageView.setImageResource(R.drawable.japan);
//            throw new RuntimeException(e);
//        }

        holder.itemView.setOnClickListener(view -> {
            itemClickListener.onItemClick(listPost.get(position).getId());
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
        if(listPost!=null && !listPost.isEmpty()) return listPost.size();
        return 0;
    }
    public interface ItemClickListener{
        void onItemClick(Integer postid);
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
