package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.MyordersModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class MyordersAdapter extends RecyclerView.Adapter<MyordersAdapter.ViewHolder> {

    Context context;
    ArrayList<MyordersModel> model;
    Click listener;


    public MyordersAdapter(Context context, ArrayList<MyordersModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.order_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MyordersModel list = model.get(position);
        //Glide.with(context).load(list.getImage()).into(holder.image);
        holder.name.setText("Order ID : " + list.getName());
        holder.price.setText("Rs. " + list.getPrice());
        holder.status.setText(list.getOrder_status());
        holder.p_date.setText("Purchase Date : " + list.getPdate());
        if (!list.getDdate().isEmpty()) {
            holder.d_date.setVisibility(View.VISIBLE);
            holder.d_date.setText("Delivery Date : " + list.getDdate());
        } else {
            holder.d_date.setVisibility(View.GONE);
        }

        holder.order_no.setText("Order ID : " + list.getOrder());
        holder.qty.setText("No. of Items : " + list.getQty());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClik(list);
            }
        });

        if (list.getOrder_status().equalsIgnoreCase("Delivered")) {
            holder.repurchase.setVisibility(View.GONE);
        } else {
            holder.repurchase.setVisibility(View.GONE);
        }


        holder.bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ViewBill(list);
            }
        });
        holder.payment_mode.setText("Payment Mode : " + list.getPayment_mode());
        holder.delivery_mode.setText("Delivery Mode : " + list.getDelivery_mode());

        holder.repurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.repurchase(list.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, order_no, qty, p_date, d_date, price,
                status, click, payment_mode, delivery_mode,order_type;
        ImageView image;
        Button repurchase, bill;
        CardView card;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            order_no = itemView.findViewById(R.id.order_no);
            qty = itemView.findViewById(R.id.qty);
            p_date = itemView.findViewById(R.id.p_date);
            d_date = itemView.findViewById(R.id.d_date);
            status = itemView.findViewById(R.id.status);
            click = itemView.findViewById(R.id.click);
            repurchase = itemView.findViewById(R.id.repurchase);
            card = itemView.findViewById(R.id.card);
            payment_mode = itemView.findViewById(R.id.payment_mode);
            delivery_mode = itemView.findViewById(R.id.delivery_mode);
            bill = itemView.findViewById(R.id.bill);
        }
    }

    public interface Click {
        void ItemClik(MyordersModel list);

        void repurchase(String id);

        void ViewBill(MyordersModel list);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
