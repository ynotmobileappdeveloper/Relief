package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.BillModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    Context context;
    ArrayList<BillModel> model;
    String view;
    Click listener;

    public BillAdapter(Context context, ArrayList<BillModel> model, String view, Click listener) {
        this.context = context;
        this.model = model;
        this.view = view;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final BillModel list = model.get(position);
        holder.products.setText(list.getProducts());
        holder.rate.setText("\u20B9" + list.getRate());
        holder.qty.setText(list.getQty());
        holder.disc.setText("\u20B9" + list.getDiscounts());

        if (view.equals("yes")) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.Delete(position, model);
            }
        });

        if (view.equals("yes")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.ItemClick(list, position);
                }
            });
        }


    }


    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView items, products, qty, rate, disc;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            products = itemView.findViewById(R.id.products);
            qty = itemView.findViewById(R.id.qty);
            rate = itemView.findViewById(R.id.rate);
            disc = itemView.findViewById(R.id.disc);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public void view_delete() {

        view = "yes";
        notifyDataSetChanged();

    }

    public interface Click {
        void Delete(int position, ArrayList<BillModel> list);

        void ItemClick(BillModel model, int poition);
    }

}

