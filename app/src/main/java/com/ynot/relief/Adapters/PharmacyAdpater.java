package com.ynot.relief.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ynot.relief.Models.PharmacyModel;
import com.ynot.relief.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PharmacyAdpater extends RecyclerView.Adapter<PharmacyAdpater.ViewHolder> {

    Context context;
    ArrayList<PharmacyModel> model;
    ArrayList<PharmacyModel> orig;
    String SearchText = "";
    SpannableStringBuilder sb;
    Click listener;

    public PharmacyAdpater(Context context, ArrayList<PharmacyModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pharamcy_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PharmacyModel list = model.get(position);
        String tit = list.getName();
        holder.name.setText(list.getName());
        holder.address.setText(list.getAddress());
        holder.image.setBackgroundResource(list.getImage());
        if (list.getTime().equals("null")) {
            holder.time.setVisibility(View.INVISIBLE);
        } else {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(list.getTime());
        }

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.call(list);
            }
        });
        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.whatsapp(list);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClick(list);
            }
        });

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
        TextView time, name, address;
        ImageView image;
        RelativeLayout call, whatsapp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            image = itemView.findViewById(R.id.image);
            call = itemView.findViewById(R.id.call);
            whatsapp = itemView.findViewById(R.id.whatsapp);
        }
    }

    public interface Click {
        void itemClick(PharmacyModel model);

        void call(PharmacyModel model);

        void whatsapp(PharmacyModel model);
    }

    public Filter getfilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<PharmacyModel> results = new ArrayList<>();
                Log.e("sear", "yes");
                SearchText = charSequence.toString();
                if (orig == null)
                    orig = model;
                if (charSequence != null) {
                    if (orig != null & orig.size() > 0) {

                        for (final PharmacyModel g : orig) {
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
                model = (ArrayList<PharmacyModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };


    }
}
