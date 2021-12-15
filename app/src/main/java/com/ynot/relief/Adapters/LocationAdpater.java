package com.ynot.relief.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ynot.relief.Models.AvailableLocationModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class LocationAdpater extends RecyclerView.Adapter<LocationAdpater.ViewHolder> {
    Context context;
    ArrayList<AvailableLocationModel> model;
    Click listener;

    public LocationAdpater(Context context, ArrayList<AvailableLocationModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AvailableLocationModel list = model.get(position);
        holder.name.setText(list.getLocation());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.item_click(list);
            }
        });
        Log.e("location", list.getLocation());
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
        void item_click(AvailableLocationModel model);
    }
}
