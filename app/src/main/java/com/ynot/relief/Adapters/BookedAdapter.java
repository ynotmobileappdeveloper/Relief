package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.BookedModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class BookedAdapter extends RecyclerView.Adapter<BookedAdapter.ViewHolder> {

    Context context;
    ArrayList<BookedModel> model;

    public BookedAdapter(Context context, ArrayList<BookedModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookedModel list = model.get(position);
        holder.service_name.setText(list.getService_name());
        holder.name.setText(list.getName());
        holder.gender.setText(list.getGender());
        holder.age.setText(list.getAge());
        holder.status.setText(list.getStatus());
        holder.phone.setText("+91 " + list.getPhone());
        holder.price.setText(list.getPrice());
        holder.date.setText(list.getDate());
        holder.lab_name.setText(list.getLab_name());
        holder.type.setText(list.getType());


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView service_name, name, gender, age, phone, status, price, date, lab_name, type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            service_name = itemView.findViewById(R.id.service_name);
            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            phone = itemView.findViewById(R.id.phone);
            status = itemView.findViewById(R.id.status);
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
            lab_name = itemView.findViewById(R.id.lab_name);
            type = itemView.findViewById(R.id.type);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
