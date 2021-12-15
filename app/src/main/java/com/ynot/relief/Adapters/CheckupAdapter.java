package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.CheckupModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class CheckupAdapter extends RecyclerView.Adapter<CheckupAdapter.ViewHolder> {
    Context context;
    ArrayList<CheckupModel> model;
    ItemClick listener;

    public CheckupAdapter(Context context, ArrayList<CheckupModel> model, ItemClick listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_book_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CheckupModel list = model.get(position);

        holder.name.setText(list.getName());
        holder.gender.setText(list.getGender());
        holder.age.setText(list.getAge());
        holder.date.setText(list.getDate());
        holder.time.setText(list.getTime());
        holder.location.setText(list.getPlace());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        TextView name, gender, age, date, time, location;
        Button viewall;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location);
        }
    }

    public interface ItemClick {
        void Click(CheckupModel model);
    }
}
