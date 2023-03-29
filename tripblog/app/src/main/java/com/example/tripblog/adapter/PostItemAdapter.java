package com.example.tripblog.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripblog.R;
import com.example.tripblog.model.PostItem;

import java.util.List;

public class PostItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PostItem> postItemList;
    public PostItemAdapter(List<PostItem> postItemList) {
        this.setHasStableIds(true);
        this.postItemList = postItemList;
    }

    @Override
    public long getItemId(int position) {
        if (postItemList != null) {
            return postItemList.get(position).getId();
        }
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_post_item_layout, parent, false);
        return new PostItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostItem postItem = postItemList.get(position);
        Log.d("Render: ", String.valueOf(getItemId(position)));
        if (holder instanceof PostItemViewHolder) {
            PostItemViewHolder viewHolder = (PostItemViewHolder) holder;
            viewHolder.title.setText(postItem.getTitle());
            viewHolder.contentEdit.setText(postItem.getContent());
            boolean isExpandable = postItem.isExpandable();
            viewHolder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (postItemList != null) {
            return postItemList.size();
        }
        return 0;
    }

    public class PostItemViewHolder extends RecyclerView.ViewHolder {
        EditText contentEdit;
        TextView title;
        LinearLayout titleLayout;
        ConstraintLayout expandableLayout;

        public PostItemViewHolder(@NonNull View itemView) {
            super(itemView);

            titleLayout = itemView.findViewById(R.id.titleLayout);
            title = itemView.findViewById(R.id.title);
            contentEdit = itemView.findViewById(R.id.contentEdit);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            titleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PostItem postItem = postItemList.get(getBindingAdapterPosition());
                    postItem.setExpandable(!postItem.isExpandable());
                    notifyItemChanged(getBindingAdapterPosition());
                }
            });

            contentEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    PostItem postItem = postItemList.get(getBindingAdapterPosition());
                    String newValue = contentEdit.getText().toString();
                    postItem.setContent(newValue);
                }
            });
        }
    }
}
