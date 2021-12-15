package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.TestModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.ViewHolder> {

    Context context;
    ArrayList<TestModel> model;
    ItemClick listener;

    public TestResultAdapter(Context context, ArrayList<TestModel> model, ItemClick listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_result_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TestModel list = model.get(position);
        holder.service_name.setText(list.getBooked_name());
        holder.date.setText(list.getDate());
        holder.price.setText("Rs. " + list.getPrice());
        holder.lab_name.setText(list.getLab_name());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        TextView service_name, date, address, price, lab_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            service_name = itemView.findViewById(R.id.service_name);
            date = itemView.findViewById(R.id.date);
            address = itemView.findViewById(R.id.address);
            view = itemView.findViewById(R.id.view);
            price = itemView.findViewById(R.id.price);
            lab_name = itemView.findViewById(R.id.lab_name);
        }
    }

    public interface ItemClick {
        void Click(TestModel list);
    }
}
