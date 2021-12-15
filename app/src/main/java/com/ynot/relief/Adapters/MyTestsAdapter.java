package com.ynot.relief.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.ynot.relief.Models.MyTestsModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class MyTestsAdapter extends RecyclerView.Adapter<MyTestsAdapter.ViewHolder> {

    Context context;
    ArrayList<MyTestsModel> model;
    Click listener;
    String status;

    public MyTestsAdapter(Context context, ArrayList<MyTestsModel> model, Click listener, String status) {
        this.context = context;
        this.model = model;
        this.listener = listener;
        this.status = status;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_tests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyTestsModel list = model.get(position);
        holder.date.setText(list.getDate());
        holder.name.setText(list.getName());
        holder.gender.setText(list.getGender());
        holder.tests.setText(list.getTests());
        holder.age.setText(list.getAge());
        holder.see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClick(list);
            }
        });
        if (status.equals("2")) {
            holder.title.setText("Results :");
        }

        if (list.getLab_status().equals("1")) {
            holder.lab_status.setVisibility(View.VISIBLE);
        } else {
            holder.lab_status.setVisibility(View.GONE);
        }
        holder.lab_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.lab_status(list);
            }
        });

        if (!list.getImage().isEmpty()) {
            holder.test_layout.setVisibility(View.GONE);
            holder.card.setVisibility(View.VISIBLE);
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
        } else {
            holder.card.setVisibility(View.GONE);
            holder.test_layout.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, name, gender, age, tests, see, title;
        ImageView image, nodata;
        MaterialCardView card;
        LinearLayout test_layout;
        Button lab_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            age = itemView.findViewById(R.id.age);
            tests = itemView.findViewById(R.id.tests);
            see = itemView.findViewById(R.id.see);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            card = itemView.findViewById(R.id.card);
            nodata = itemView.findViewById(R.id.nodata);
            test_layout = itemView.findViewById(R.id.test_layout);
            lab_status = itemView.findViewById(R.id.lab_status);
        }
    }

    public interface Click {
        void ItemClick(MyTestsModel list);

        void lab_status(MyTestsModel list);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
