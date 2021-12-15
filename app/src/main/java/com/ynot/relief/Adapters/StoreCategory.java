package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.PharmCategoryModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class StoreCategory extends RecyclerView.Adapter<StoreCategory.ViewHolder> {

    Context context;
    ArrayList<PharmCategoryModel> model;
    Click listener;

    public StoreCategory(Context context, ArrayList<PharmCategoryModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pharm_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final PharmCategoryModel list=model.get(position);
        holder.name.setText(model.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClick(list);
            }
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }

    public interface Click {
        void ItemClick(PharmCategoryModel model);
    }
}
