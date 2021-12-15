package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.PTestMode;
import com.ynot.relief.R;

import java.util.ArrayList;

public class PTestAdapter extends RecyclerView.Adapter<PTestAdapter.ViewHolder> {
    Context context;
    ArrayList<PTestMode> model;
    Click listener;

    public PTestAdapter(Context context, ArrayList<PTestMode> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ptest_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PTestMode list = model.get(position);

        holder.name.setText("Dr." + list.getName());
        holder.medicine.setText(list.getMedicine());
        holder.lab.setText(list.getTest());
        holder.date.setText(list.getDate());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.ItemClick(list);
            }
        });


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, gender, age, title, medicine, lab, date;
        Button view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.doc_name);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            title = itemView.findViewById(R.id.title);
            medicine = itemView.findViewById(R.id.medicine);
            lab = itemView.findViewById(R.id.lab);
            date = itemView.findViewById(R.id.date);
            view = itemView.findViewById(R.id.view);
        }
    }

    public interface Click {
        void ItemClick(PTestMode list);
    }
}
