package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.RecentModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    Context context;
    ArrayList<RecentModel> model;
    String page = "";


    public RecentAdapter(Context context, ArrayList<RecentModel> model, String page) {
        this.context = context;
        this.model = model;
        this.page = page;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recent_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentModel list = model.get(position);
        holder.name.setText(list.getName());
        holder.type.setText(list.getType());
        holder.calender.setText(list.getDate());


    }

    @Override
    public int getItemCount() {
        if (page.equals("profile")) {
            if (model.size() > 4) {
                return 4;
            } else {
                return model.size();
            }

        } else {
            return model.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, calender;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            calender = itemView.findViewById(R.id.calender);
        }
    }
}
