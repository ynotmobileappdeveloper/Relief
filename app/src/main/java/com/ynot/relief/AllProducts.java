package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import com.ynot.relief.Adapters.ProductAdapter;
import com.ynot.relief.Adapters.TabAdapter;
import com.ynot.relief.CartLayout.NotificationCountSetClass;
import com.ynot.relief.Models.MedicineModel;
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

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AllProducts extends AppCompatActivity {
    RecyclerView rec;
    String id = "";
    ProductAdapter adapter;
    ArrayList<MedicineModel> model = new ArrayList<>();
    ACProgressFlower dialog;
    ImageView nodata;
    int cart_count;
    ViewPager rec_viewPager;
    TabLayout tabLayout;
    TabAdapter tabAdapter;
    ArrayList<SubcategoryModel> subCategoryModel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        rec = findViewById(R.id.rec);
        nodata = findViewById(R.id.nodata);
        rec_viewPager = findViewById(R.id.rec_viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Products");
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
        }
        get_subcategory();

    }

    public void get_cart_count() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CART_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {

                                cart_count = Integer.parseInt(ob.getString("cart_count"));
                                invalidateOptionsMenu();

                            } else {
                                cart_count = 0;
                                invalidateOptionsMenu();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    private void get_subcategory() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SUBCATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("sub_category", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                subCategoryModel = new ArrayList<>();
                                JSONArray array = ob.getJSONArray("sub_category");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    subCategoryModel.add(new SubcategoryModel(obb.getString("id"), obb.getString("name")));
                                    tabLayout.addTab(tabLayout.newTab().setText(obb.getString("name")));
                                }
                                tabAdapter = new TabAdapter
                                        (getSupportFragmentManager(), tabLayout.getTabCount(), subCategoryModel, id);

                                rec_viewPager.setAdapter(tabAdapter);
                                rec_viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                                rec_viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                int betweenSpace = 10;
                                ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);

                                for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
                                    View v1 = slidingTabStrip.getChildAt(i);
                                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v1.getLayoutParams();
                                    params.rightMargin = betweenSpace;
                                }
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
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("category_id", id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("inputs", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void get_products(final String id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("products", response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("products");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    String amount = String.format("%.2f", obj.getDouble("price"));
                                    model.add(new MedicineModel(obj.getString("p_id"), obj.getString("image"), "", obj.getString("name"), amount, ""));
                                }
                                adapter = new ProductAdapter(getApplicationContext(), model, new ProductAdapter.Click() {
                                    @Override
                                    public void ItemClick(MedicineModel model) {
                                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                                            Intent i = new Intent(getApplicationContext(), ProductDetail.class);
                                            i.putExtra("id", model.getId());
                                            i.putExtra("name", model.getName());
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                        }
                                    }
                                },"");
                                rec.setAdapter(adapter);
                            } else {
                                nodata.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (id.isEmpty()) {
                    params.put("sub_id", "0");
                } else {
                    params.put("sub_id", id);
                }
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_cart);
        NotificationCountSetClass.setAddToCart(AllProducts.this, item, cart_count);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_cart:
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_cart_count();
    }
}