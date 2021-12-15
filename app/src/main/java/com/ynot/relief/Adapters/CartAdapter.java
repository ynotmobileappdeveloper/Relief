package com.ynot.relief.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.relief.Models.CartModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    ArrayList<CartModel> model;
    Click listener;


    public CartAdapter(Context context, ArrayList<CartModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cart_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final CartModel list = model.get(position);
        Glide.with(context).load(list.getImage()).listener(new RequestListener<Drawable>() {
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
        holder.name.setText(list.getName());
        holder.price.setText("Rs. " + list.getPrice());
        holder.count.setText(list.getBrand());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClik(list);
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(list.getBrand());
                if (count > 0) {
                    listener.plus(list);
                }

            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(list.getBrand());
                if (count > 1) {
                    listener.minus(list);
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.remove(list, position);
            }
        });

        if (list.getOut_of_stock().equals("1")) {
            holder.out_stock.setVisibility(View.VISIBLE);
        } else {
            holder.out_stock.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, brand, count, minus, plus, out_stock;
        ImageView image, delete, nodata;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            brand = itemView.findViewById(R.id.brand);
            count = itemView.findViewById(R.id.count);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            delete = itemView.findViewById(R.id.delete);
            nodata = itemView.findViewById(R.id.nodata);
            out_stock = itemView.findViewById(R.id.out_stock);
        }
    }

    public interface Click {
        void ItemClik(CartModel list);

        void plus(CartModel list);

        void minus(CartModel list);

        void remove(CartModel list, int position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
