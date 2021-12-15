package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.CityModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    Context context;
    ArrayList<CityModel> model;
    Click listener;
    int selected_position = -1;

    public StoreAdapter(Context context, ArrayList<CityModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.name.setText(model.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.ItemClick(model.get(position));
                selected_position = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        if (position == selected_position) {
            holder.name.setBackgroundResource(R.drawable.selectable);
            holder.name.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.name.setBackgroundResource(R.drawable.nonselectable);
            holder.name.setTextColor(context.getResources().getColor(android.R.color.black));
        }

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
        void ItemClick(CityModel list);
    }
}
