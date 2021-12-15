package com.ynot.relief.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.relief.MedicineFragment;
import com.ynot.relief.Models.MedicineModel;
import com.ynot.relief.R;
import com.ynot.relief.Webservices.URLs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    Context context;
    ArrayList<MedicineModel> model;
    Click listener;
    ArrayList<MedicineModel> orig;
    SpannableStringBuilder sb;
    String SearchText = "", page = "";
    public boolean data = false;

    public ProductAdapter(Context context, ArrayList<MedicineModel> model, Click listener, String page) {
        this.context = context;
        this.model = model;
        this.listener = listener;
        this.page = page;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MedicineModel list = model.get(position);
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
        if (list.getCompany().isEmpty()) {
            holder.brand.setVisibility(View.GONE);
        } else {
            holder.brand.setVisibility(View.VISIBLE);
            holder.brand.setText(list.getCompany());
        }
        holder.price.setText("Rs." + list.getPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClick(list);
            }
        });

        if (SearchText.length() > 0) {
            sb = new SpannableStringBuilder(tit);
            Pattern p = Pattern.compile(SearchText, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(tit);
            while (m.find()) {
                data = false;
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

        ImageView image, nodata;
        TextView name, brand, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            brand = itemView.findViewById(R.id.brand);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);
            nodata = itemView.findViewById(R.id.nodata);
        }
    }

    public interface Click {
        void ItemClick(MedicineModel model);
    }

    public Filter getfilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<MedicineModel> results = new ArrayList<>();

                SearchText = charSequence.toString();
                if (orig == null)
                    orig = model;
                if (charSequence != null) {
                    if (orig != null & orig.size() > 0) {

                        for (final MedicineModel g : orig) {
                            if (g.getName().toUpperCase().contains(charSequence.toString().toUpperCase().trim()) || g.getName().toLowerCase().contains(charSequence.toString().toLowerCase().trim()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                if (page.equals("3")) {
                    if (results.size() == 0) {
                        data = true;
                    } else {
                        data = false;
                    }
                }
                Log.e("size", String.valueOf(results.size()));
                return oReturn;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                model = (ArrayList<MedicineModel>) filterResults.values;
                notifyDataSetChanged();
            }


        };


    }

    public boolean resultCheck() {
        if (data) {
            return true;
        } else {
            return false;
        }

    }
}
