package com.ynot.relief.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.ynot.relief.Models.Slidermodel;
import com.ynot.relief.R;

import java.util.ArrayList;

public class ViewPager_Adapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Slidermodel> images;

    public ViewPager_Adapter(Context activity, ArrayList<Slidermodel> images) {

        context = activity;
        this.images = images;


    }


    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_pager, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.img);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(images.get(position).getTitle());
        Glide.with(context).load(images.get(position).getImages()).into(imageView);
       /* final RotateLoading loading=(RotateLoading)view.findViewById(R.id.rotateloading);
        loading.setVisibility(View.VISIBLE);
        loading.start();*/
       /* Glide.with(context).load(images.get(position)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                loading.setVisibility(View.GONE);
                loading.stop();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                loading.setVisibility(View.GONE);
                loading.stop();
                return false;
            }
        }).into(imageView);*/

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
