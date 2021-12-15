package com.ynot.relief.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.NotificationModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    private static int LAYOUT_ONE = 1;
    private static int LAYOUT_TWO = 2;
    Context context;
    ArrayList<NotificationModel> model;
    Click listener;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if (viewType == LAYOUT_ONE) {
            view = inflater.inflate(R.layout.notification_layout_one, parent, false);
        } else {
            view = inflater.inflate(R.layout.notification_layout_two, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NotificationModel list = model.get(position);

        Log.e("status", list.getStatus());

        if (list.getStatus().equals("Next Checkup Date")) {
            holder.date.setText(list.getDate());
            holder.op_date.setText(list.getOp_date());
            holder.op_time.setText(list.getOp_time());
        } else {
            holder.date.setText(list.getDate());
            holder.medicine.setText(list.getMedicine());
            holder.lab.setText(list.getLab_test());
            holder.doc_name.setText("Dr." + list.getDoc_name());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.ItemClick(list.getId());
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, op_date, op_time, medicine, lab, doc_name;
        Button view;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            op_date = itemView.findViewById(R.id.op_date);
            op_time = itemView.findViewById(R.id.op_time);
            medicine = itemView.findViewById(R.id.medicine);
            lab = itemView.findViewById(R.id.lab);
            view = itemView.findViewById(R.id.view);
            doc_name = itemView.findViewById(R.id.doc_name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (model.get(position).getStatus().equals("Next Checkup Date")) {
            return LAYOUT_ONE;
        } else {
            return LAYOUT_TWO;
        }
    }

    public interface Click {
        void ItemClick(String id);
    }
}
