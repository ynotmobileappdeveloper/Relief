package com.ynot.relief.ui.CategoryFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ynot.relief.Adapters.HomeViewpagerAdapter;
import com.ynot.relief.Models.HomeSlider;
import com.ynot.relief.R;

import java.util.ArrayList;
import java.util.Timer;


public class CategoryFragment extends Fragment {

    ViewPager viewpager;
    HomeViewpagerAdapter viewpagerAdapter;
    ArrayList<HomeSlider> homeSlider = new ArrayList<>();
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 3000;
    int currentPage = 0;
    int NUM_PAGES = 2;
    Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        viewpager = root.findViewById(R.id.viewpager);

        homeSlider.add(new HomeSlider(R.drawable.medi));
        homeSlider.add(new HomeSlider(R.drawable.medi));
        viewpagerAdapter = new HomeViewpagerAdapter(getContext(), homeSlider);
        viewpager.setAdapter(viewpagerAdapter);
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewpager.setCurrentItem(currentPage++, true);

            }
        };
        timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);

        viewpager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        viewpager.setPadding(70, 0, 70, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        viewpager.setPageMargin(20);


        return root;
    }
}