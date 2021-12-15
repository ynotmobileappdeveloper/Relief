package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.HomeCategoryModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<HomeCategoryModel> model;
    Click listener;

    public CategoryAdapter(Context context, ArrayList<HomeCategoryModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final HomeCategoryModel list = model.get(position);
        holder.image.setImageResource(list.getImage());
        holder.name.setText(list.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == 0) {
                    listener.ItemClick(list, position);
                }

                if (position == 2) {

                    listener.ItemClick(list, position);
                }
                if (position == 1) {

                    listener.ItemClick(list, position);
                }
                if (position == 3) {
                    listener.ItemClick(list, position);
                }
                if (position == 4) {

                    listener.ItemClick(list, position);
                }
                if (position == 5) {

                    listener.ItemClick(list, position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
        }
    }

    public interface Click {
        void ItemClick(HomeCategoryModel model, int position);

    }
}
