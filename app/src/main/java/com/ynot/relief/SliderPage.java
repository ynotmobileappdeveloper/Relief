package com.ynot.relief;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.ynot.relief.Adapters.ViewPager_Adapter;
import com.ynot.relief.Models.Slidermodel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SliderPage extends AppCompatActivity {
    ViewPager viewpager;
    ArrayList<Slidermodel> model = new ArrayList<>();
    ViewPager_Adapter adapter;
    WormDotsIndicator worm_dots_indicator;
    TextView next;
    TextView skip;
    int position = 0;
    ImageView dot1, dot2;
    int delay;
    int status;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_page);
        viewpager = findViewById(R.id.viewpager);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);

        delay = 2000;
        model.add(new Slidermodel(R.drawable.slide_front, ""));
        model.add(new Slidermodel(R.drawable.slide_sec, ""));
        adapter = new ViewPager_Adapter(getApplicationContext(), model);
        viewpager.setAdapter(adapter);
        NUM_PAGES = 2;
        // worm_dots_indicator.setViewPager(viewpager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                viewpager.setCurrentItem(currentPage++, true);
                if (currentPage == NUM_PAGES) {
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                    finishAffinity();

                }
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, delay, 2000);

        SharedPreferences prefs = getSharedPreferences("slider", MODE_PRIVATE);
        String name = prefs.getString("slider", "");//"No name defined" is the default value.

      /*  if (!name.isEmpty()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finishAffinity();
        } else {
            startActivity(new Intent(getApplicationContext(), RegistrationPage.class));
        }*/


       /* next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = viewpager.getCurrentItem();
                if (position < model.size()) {
                    position++;
                    viewpager.setCurrentItem(position);
                }
                if(position==2){
                    SharedPreferences.Editor editor = getSharedPreferences("slider", MODE_PRIVATE).edit();
                    editor.putString("slider", "yes");
                    editor.apply();
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(), RegistrationPage.class));
                }
                if (position == model.size()) {
                    position = 0;
                    viewpager.setCurrentItem(position);
                }
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("slider", MODE_PRIVATE).edit();
                editor.putString("slider", "yes");
                editor.apply();
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), RegistrationPage.class));
            }
        });*/

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                Log.e("current_page", String.valueOf(currentPage));
                Log.e("position", String.valueOf(position));
                if (currentPage == 0) {
                    dot2.setImageResource(R.drawable.bgbutton4);
                    dot1.setImageResource(R.drawable.bgbutton3);
                }
                if (currentPage == 1) {
                    dot1.setImageResource(R.drawable.bgbutton4);
                    dot2.setImageResource(R.drawable.bgbutton3);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
