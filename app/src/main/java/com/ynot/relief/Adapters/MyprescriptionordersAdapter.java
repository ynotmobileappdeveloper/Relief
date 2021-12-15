package com.ynot.relief.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.relief.Models.PrescriptionModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class MyprescriptionordersAdapter extends RecyclerView.Adapter<MyprescriptionordersAdapter.ViewHolder> {

    Context context;
    ArrayList<PrescriptionModel> model;
    Click listener;


    public MyprescriptionordersAdapter(Context context, ArrayList<PrescriptionModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.pres_order_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PrescriptionModel list = model.get(position);
        Glide.with(context).load(list.getImage()).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.nodata.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.nodata.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.image);
        holder.name.setText(list.getStore_name());
        holder.p_date.setText(list.getPdate());
        if (list.getStatus().equals("Prescription Billed")) {
            holder.status.setBackgroundResource(R.drawable.viewbill_bg);
            holder.status.setText("View Bill");
            holder.status.setTextColor(Color.WHITE);
        } else {
            holder.status.setText(list.getStatus());
        }
        if (list.getStatus().equals("Delivered")) {
            holder.status.setBackgroundResource(R.drawable.delivered_bg);
            holder.status.setTextColor(Color.WHITE);
        }
        if (list.getStatus().equals("Confirmed")) {
            holder.extra_bill.setVisibility(View.VISIBLE);
        } else {
            holder.extra_bill.setVisibility(View.GONE);
        }


        if (!list.getStore_place().isEmpty()) {
            holder.order_no.setVisibility(View.VISIBLE);
            holder.order_no.setText(list.getStore_place());
        } else {
            holder.order_no.setVisibility(View.GONE);
        }

        holder.mob.setText("Contact : +91 " + list.getStore_mob());
        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.getStatus().equals("Prescription Billed") || list.getStatus().equals("Delivered")) {
                    listener.ItemClik(list);
                }
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.call(list);
            }
        });
        holder.extra_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClik(list);
            }
        });

        if (!list.getNote().isEmpty()) {
            holder.note_txt.setVisibility(View.VISIBLE);
            holder.note_layout.setVisibility(View.VISIBLE);
            holder.note.setText(list.getNote());

        } else {
            holder.note_txt.setVisibility(View.GONE);
            holder.note_layout.setVisibility(View.GONE);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.Image_click(list);
            }
        });
        if (!list.getOrder_no().isEmpty()) {
            holder.order.setText(list.getOrder_no());
            holder.order.setVisibility(View.VISIBLE);
        } else {
            holder.order.setVisibility(View.GONE);
        }

        if (list.getStatus().equalsIgnoreCase("Confirmed")) {
            holder.repurchase.setVisibility(View.VISIBLE);
        } else {
            holder.repurchase.setVisibility(View.GONE);
        }
        holder.repurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.Repurchase(list.getId());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.next_page(list);
            }
        });
        holder.payment_mode.setText("Payment Mode : " + list.getPayment_mode());
        holder.delivery_mode.setText("Delivery Mode : " + list.getDelivery_mode());
        holder.order_type.setText("Order Type : " + list.getOrder_type());


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, order_no, p_date, status, mob, extra_bill,
                note, note_txt, order, payment_mode, delivery_mode, order_type;
        ImageView image, nodata;
        RelativeLayout call;
        LinearLayout note_layout;
        Button repurchase;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            order_no = itemView.findViewById(R.id.order_no);
            p_date = itemView.findViewById(R.id.p_date);
            status = itemView.findViewById(R.id.status);
            mob = itemView.findViewById(R.id.mob);
            call = itemView.findViewById(R.id.call);
            extra_bill = itemView.findViewById(R.id.extra_bill);
            note = itemView.findViewById(R.id.note);
            note_layout = itemView.findViewById(R.id.note_layout);
            note_txt = itemView.findViewById(R.id.note_txt);
            repurchase = itemView.findViewById(R.id.repurchase);
            nodata = itemView.findViewById(R.id.nodata);
            order = itemView.findViewById(R.id.order);
            payment_mode = itemView.findViewById(R.id.payment_mode);
            delivery_mode = itemView.findViewById(R.id.delivery_mode);
            order_type = itemView.findViewById(R.id.order_type);
        }
    }

    public interface Click {
        void ItemClik(PrescriptionModel list);

        void call(PrescriptionModel list);

        void Image_click(PrescriptionModel list);

        void Repurchase(String id);

        void next_page(PrescriptionModel list);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
