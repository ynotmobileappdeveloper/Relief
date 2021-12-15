package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.LabModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class LabAdapter extends RecyclerView.Adapter<LabAdapter.ViewHolder> {
    Context context;
    ArrayList<LabModel> model;

    public LabAdapter(Context context, ArrayList<LabModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lab_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.slno.setText(model.get(position).getSlno());
        holder.name.setText(model.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView slno, name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slno = itemView.findViewById(R.id.slno);
            name = itemView.findViewById(R.id.name);
        }
    }
}
