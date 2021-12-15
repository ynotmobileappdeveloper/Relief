package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.MymedicineModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class MyMedicineAdapter extends RecyclerView.Adapter<MyMedicineAdapter.ViewHolder> {

    Context context;
    ArrayList<MymedicineModel> model;
    ItemClick listener;

    public MyMedicineAdapter(Context context, ArrayList<MymedicineModel> model, ItemClick listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MymedicineModel list = model.get(position);
        holder.name.setText(list.getName());
        holder.date.setText(list.getDate());
        holder.address.setText(list.getAddress());
        holder.mob.setText("+91 " + list.getPhone());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.Click(list);
            }
        });

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button view;
        TextView name, date, address, mob;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            address = itemView.findViewById(R.id.address);
            mob = itemView.findViewById(R.id.mob);
            view = itemView.findViewById(R.id.view);
        }
    }

    public interface ItemClick {
        void Click(MymedicineModel list);
    }
}
