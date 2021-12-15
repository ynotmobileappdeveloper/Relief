package com.ynot.relief.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ynot.relief.Models.DepartmentModel;
import com.ynot.relief.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocCategoryAdapter extends RecyclerView.Adapter<DocCategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<DepartmentModel> model;
    ArrayList<DepartmentModel> orig;
    Click listener;
    SpannableStringBuilder sb;
    String SearchText = "";

    public DocCategoryAdapter(Context context, ArrayList<DepartmentModel> model, Click listener) {
        this.context = context;
        this.model = model;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DepartmentModel list = model.get(position);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClick(list, position);
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

        ImageView image, nodata;
        TextView name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            nodata = itemView.findViewById(R.id.nodata);
        }
    }

    public interface Click {
        void ItemClick(DepartmentModel model, int position);

    }
    public Filter getfilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<DepartmentModel> results = new ArrayList<>();
                Log.e("sear", "yes");
                SearchText = charSequence.toString();
                if (orig == null)
                    orig = model;
                if (charSequence != null) {
                    if (orig != null & orig.size() > 0) {

                        for (final DepartmentModel g : orig) {
                            if (g.getName().toLowerCase().contains(charSequence.toString()))
                                results.add(g);
                        }

                    }
                    oReturn.values = results;

                }

                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                model = (ArrayList<DepartmentModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };


    }
}
