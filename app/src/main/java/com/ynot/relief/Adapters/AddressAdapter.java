package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.AddressModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    Context context;
    ArrayList<AddressModel> model;
    Click listener;

    String set_default;

    public void set_default(String set_default) {
        this.set_default = set_default;
    }

    public AddressAdapter(Context context, ArrayList<AddressModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final AddressModel list = model.get(position);
        holder.name.setText(list.getHouse());
        holder.address.setText(list.getAddress());
        holder.house.setText(list.getHouse());
        holder.pin.setText("Pin :" + list.getPin());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.ItemClick(list);
            }
        });
        if (list.getStatus().equals("1")) {
            holder.check.setChecked(true);
        } else {
            holder.check.setChecked(false);
        }
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.edit_address(list);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete(list,position);
            }
        });
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.getStatus().equals("0"))
                {
                    listener.set_default(list);

                }else
                {
                    Toast.makeText(context, "Already Default !!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        if (set_default.equals("set_default")) {
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, house, address, pin;
        RadioButton check;
        ImageView edit,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            house = itemView.findViewById(R.id.house);
            address = itemView.findViewById(R.id.address);
            pin = itemView.findViewById(R.id.pin);
            check = itemView.findViewById(R.id.check);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

    public interface Click {
        void ItemClick(AddressModel model);
        void  edit_address(AddressModel model);
        void  delete(AddressModel model,int postition);
        void  set_default(AddressModel model);

    }
}
