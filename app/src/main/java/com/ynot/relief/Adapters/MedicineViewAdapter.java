package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.MedicineViewModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class MedicineViewAdapter extends RecyclerView.Adapter<MedicineViewAdapter.ViewHolder> {

    Context context;
    ArrayList<MedicineViewModel> model;
    Click listener;

    public MedicineViewAdapter(Context context, ArrayList<MedicineViewModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final MedicineViewModel list = model.get(position);
        holder.name.setText(list.getName());
        holder.qty.setText(list.getQty());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.ItemClick(list);
            }
        });
        holder.name.setSelected(true);
        holder.qty.setSelected(true);

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty;
        ImageView view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
            view = itemView.findViewById(R.id.view);
        }
    }

    public interface Click {
        void ItemClick(MedicineViewModel list);
    }
}
