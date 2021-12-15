package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.TypeModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class TypeAdpater extends RecyclerView.Adapter<TypeAdpater.ViewHolder> {

    Context context;
    ArrayList<TypeModel> model;
    Click listener;


    public TypeAdpater(Context context, ArrayList<TypeModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.type_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        TypeModel list=model.get(position);
        holder.name.setText(list.getName());
        holder.no.setText(list.getId());
        holder.days.setText(list.getDays());
        holder.qty.setText(list.getQty());
        holder.name.setSelected(true);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,no,days,qty;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            no=itemView.findViewById(R.id.no);
            days=itemView.findViewById(R.id.days);
            qty=itemView.findViewById(R.id.qty);
            delete=itemView.findViewById(R.id.delete);
        }
    }
    public  interface Click
    {
        void delete(int position);
    }
}
