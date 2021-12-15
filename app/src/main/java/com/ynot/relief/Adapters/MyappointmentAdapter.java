package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.MyAppoinmentsModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class MyappointmentAdapter extends RecyclerView.Adapter<MyappointmentAdapter.ViewHolder> {

    Context context;
    ArrayList<MyAppoinmentsModel> model;
    Click listener;

    public MyappointmentAdapter(Context context, ArrayList<MyAppoinmentsModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myapp_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MyAppoinmentsModel list = model.get(position);
        holder.name.setText(list.getName());
        holder.gender.setText(list.getGender());
        holder.age.setText(list.getAge());
        holder.date.setText(list.getDate());
        holder.time.setText(list.getTime());
        holder.location.setText(list.getOp_type()+" OP");
        holder.type.setText(list.getDepartment());
        holder.drname.setText("Dr. " + list.getDoc_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.ItemClick(list);
            }
        });

        if (list.getOp_status().equals("0")) {
            holder.cancel.setVisibility(View.VISIBLE);
        } else {
            holder.cancel.setVisibility(View.GONE);
        }


        if (list.getOp_status().equals("1")) {
            holder.status.setText("Approved");
        } else if (list.getOp_status().equals("2")) {
            holder.status.setText("Rejected");
        } else {
            holder.status.setText("Pending");
        }
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.Cancel(list);
            }
        });

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, gender, age, date, time, location, status, drname,type;
        Button cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location);
            cancel = itemView.findViewById(R.id.cancel);
            status = itemView.findViewById(R.id.status);
            drname = itemView.findViewById(R.id.drname);
            type = itemView.findViewById(R.id.type);
        }
    }

    public interface Click {
        void ItemClick(MyAppoinmentsModel model);

        void Cancel(MyAppoinmentsModel model);
    }
}
