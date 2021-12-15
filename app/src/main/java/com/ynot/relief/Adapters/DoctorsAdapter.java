package com.ynot.relief.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.relief.Models.DoctorsModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder> {

    Context context;
    ArrayList<DoctorsModel> model;
    Click listener;


    public DoctorsAdapter(Context context, ArrayList<DoctorsModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.doctors_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final DoctorsModel list = model.get(position);
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
        holder.qualification.setText(list.getQualification());
        holder.rating.setRating(Float.parseFloat(list.getRating()));
        holder.place.setText(list.getLocation());
        holder.time.setText(list.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClick(list);
            }
        });

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView hospital, place, name, qualification, time;
        ImageView image, nodata;
        RatingBar rating;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            hospital = itemView.findViewById(R.id.hospital);
            place = itemView.findViewById(R.id.place);
            name = itemView.findViewById(R.id.name);
            qualification = itemView.findViewById(R.id.qualification);
            rating = itemView.findViewById(R.id.rating);
            time = itemView.findViewById(R.id.time);
            nodata = itemView.findViewById(R.id.nodata);

        }
    }

    public interface Click {
        void ItemClick(DoctorsModel model);
    }
}
