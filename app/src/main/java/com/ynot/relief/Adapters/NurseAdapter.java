package com.ynot.relief.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
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
import com.ynot.relief.Models.NurseModel;
import com.ynot.relief.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NurseAdapter extends RecyclerView.Adapter<NurseAdapter.ViewHolder> {

    Context context;
    ArrayList<NurseModel> model;
    ArrayList<NurseModel> orig;
    Click listener;
    SpannableStringBuilder sb;
    String SearchText = "";


    public NurseAdapter(Context context, ArrayList<NurseModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.nurse_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final NurseModel list = model.get(position);
        String tit = list.getName();
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
        holder.service.setText(list.getService());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClik(list);
            }
        });
        holder.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.booknow(list);
            }
        });

        if (!list.getOffer().equals(list.getPrice())) {
            holder.original.setText("\u20B9 " + list.getPrice());
            holder.original.setPaintFlags(holder.original.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.original.setVisibility(View.GONE);
        }

        holder.offer.setText("\u20B9 " + list.getOffer() + " Onwards");

        if (SearchText.length() > 0) {
            sb = new SpannableStringBuilder(tit);
            Pattern p = Pattern.compile(SearchText, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(tit);
            while (m.find()) {
                sb.setSpan(new BackgroundColorSpan(Color.YELLOW), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            holder.name.setText(sb);
        }


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, service, original, offer;
        ImageView image, nodata;
        Button book;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            service = itemView.findViewById(R.id.service);
            book = itemView.findViewById(R.id.book);
            nodata = itemView.findViewById(R.id.nodata);
            original = itemView.findViewById(R.id.original);
            offer = itemView.findViewById(R.id.offer);
        }
    }

    public interface Click {
        void ItemClik(NurseModel list);

        void booknow(NurseModel list);
    }

    public Filter getfilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<NurseModel> results = new ArrayList<>();
                Log.e("sear", "yes");
                SearchText = charSequence.toString();
                if (orig == null)
                    orig = model;
                if (charSequence != null) {
                    if (orig != null & orig.size() > 0) {

                        for (final NurseModel g : orig) {
                            if (g.getName().toLowerCase().contains(charSequence.toString().toLowerCase()) || g.getName().toUpperCase().contains(charSequence.toString().toLowerCase()))
                                results.add(g);
                        }

                    }
                    oReturn.values = results;

                }

                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                model = (ArrayList<NurseModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };


    }
}
