package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.SlotModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class NoonSlotAdpater extends RecyclerView.Adapter<NoonSlotAdpater.ViewHolder> {

    Context context;
    ArrayList<SlotModel> model;
    Select listener;
    private RadioButton selected = null;
    boolean clicked = false;

    public NoonSlotAdpater(Context context, ArrayList<SlotModel> model, Select listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SlotModel list = model.get(position);
        holder.slot.setText(list.getTime());
        holder.radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != null) {
                    selected.setChecked(false);

                }
                holder.radio.setChecked(true);
                selected = holder.radio;
                listener.Click("yes", list);
            }
        });
        if (clicked) {
            holder.radio.setChecked(false);
        }


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView slot;
        RadioButton radio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slot = itemView.findViewById(R.id.slot);
            radio = itemView.findViewById(R.id.radio);
        }
    }

    public interface Select {
        void Click(String status, SlotModel model);
    }

    public void disable() {
        clicked = true;
    }

}
