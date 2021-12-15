package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import com.ynot.relief.Adapters.HomeViewpagerAdapter;
import com.ynot.relief.Adapters.ShopAdapter;
import com.ynot.relief.Adapters.TabAdapter;
import com.ynot.relief.Models.HomeSlider;
import com.ynot.relief.Models.PharmacyModel;
import com.ynot.relief.Models.SubcategoryModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ProductPage extends AppCompatActivity {

    ViewPager viewpager, rec_viewPager;
    HomeViewpagerAdapter viewpagerAdapter;
    ArrayList<HomeSlider> homeSlider = new ArrayList<>();
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 3000;
    int currentPage = 0;
    int NUM_PAGES = 3;
    Timer timer;
    TabLayout tabLayout;
    TabAdapter tabAdapter;
    RecyclerView shop_rec;
    ArrayList<PharmacyModel> model = new ArrayList<>();
    ArrayList<SubcategoryModel> subCategoryModel = new ArrayList<>();
    ShopAdapter adapter;
    String id;
    ACProgressFlower dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);

        id = getIntent().getStringExtra("id");
        viewpager = findViewById(R.id.viewpager);
        shop_rec = findViewById(R.id.shop_rec);
        rec_viewPager = findViewById(R.id.rec_viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        shop_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));



        viewpagerAdapter = new HomeViewpagerAdapter(getApplicationContext(), homeSlider);
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
        get_subcategory();


    }

    private void get_subcategory() {
        dialog1.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SUBCATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("sub_category", response);
                        dialog1.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                subCategoryModel = new ArrayList<>();
                                JSONArray array = ob.getJSONArray("sub_category");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    subCategoryModel.add(new SubcategoryModel(obb.getString("sub_id"), obb.getString("sub_name")));
                                    tabLayout.addTab(tabLayout.newTab().setText(obb.getString("sub_name")));
                                }
                                tabAdapter = new TabAdapter
                                        (getSupportFragmentManager(), tabLayout.getTabCount(), subCategoryModel,"0");

                                rec_viewPager.setAdapter(tabAdapter);
                                rec_viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                                rec_viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        rec_viewPager.setCurrentItem(tab.getPosition());
                                    }

                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {

                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {

                                    }

                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cat_id", id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("inputs", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}