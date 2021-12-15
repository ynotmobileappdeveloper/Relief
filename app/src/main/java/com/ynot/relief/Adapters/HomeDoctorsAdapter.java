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
import com.ynot.relief.Models.HomeDoctorsModel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class HomeDoctorsAdapter extends RecyclerView.Adapter<HomeDoctorsAdapter.ViewHolder> {

    Context context;
    ArrayList<HomeDoctorsModel> model;
    Click listener;


    public HomeDoctorsAdapter(Context context, ArrayList<HomeDoctorsModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_doc_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HomeDoctorsModel list = model.get(position);

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
        holder.role.setText(list.getRole());
        holder.patient.setText(list.getPatient());
        holder.exp.setText(list.getExp());
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

        ImageView image, nodata;
        TextView name, role, exp, patient;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            nodata = itemView.findViewById(R.id.nodata);
            name = itemView.findViewById(R.id.name);
            role = itemView.findViewById(R.id.role);
            exp = itemView.findViewById(R.id.exp);
            patient = itemView.findViewById(R.id.patient);
        }
    }

    public interface Click {
        void ItemClick(HomeDoctorsModel model);
    }
}
