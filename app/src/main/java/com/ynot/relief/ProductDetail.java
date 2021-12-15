package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.relief.Adapters.FeaturesAdapter;
import com.ynot.relief.CartLayout.NotificationCountSetClass;
import com.ynot.relief.Models.FeaturesModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ProductDetail extends AppCompatActivity {
    RecyclerView rec;
    FeaturesAdapter adapter;
    ArrayList<FeaturesModel> model = new ArrayList<>();
    Button cart;
    TextView plus, minus, item_count, desc, feature;
    ImageView nodata, image;
    TextView description, name, price;
    String product_id = "";
    NestedScrollView layout;
    ACProgressFlower dialog;
    int cart_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Details");
        rec = findViewById(R.id.rec);
        cart = findViewById(R.id.cart);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        item_count = findViewById(R.id.item_count);
        nodata = findViewById(R.id.nodata);
        image = findViewById(R.id.image);
        description = findViewById(R.id.description);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        layout = findViewById(R.id.layout);
        desc = findViewById(R.id.desc);
        feature = findViewById(R.id.feature);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();


        product_id = getIntent().getStringExtra("id");
        name.setText(getIntent().getStringExtra("name"));
        if (getIntent().hasExtra("count")) {
            item_count.setText(getIntent().getStringExtra("count"));
        }

        get_data();
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.setNestedScrollingEnabled(false);
       /* model.add(new FeaturesModel("Robust Steel Framework."));
        model.add(new FeaturesModel("Foot Rest 150mm solid front"));
        model.add(new FeaturesModel("Castor’’s Rear solid Rubber tyres"));
        model.add(new FeaturesModel("Seat and Back of Tetron Fabric"));
        model.add(new FeaturesModel("Hand Brakes"));
        model.add(new FeaturesModel("Rust resistance"));
        model.add(new FeaturesModel("Sturdy construction"));
        model.add(new FeaturesModel("Low Maintenance"));
        adapter = new FeaturesAdapter(getApplicationContext(), model);
        rec.setAdapter(adapter);*/
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId().isEmpty()) {
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                } else {
                    add_to_cart();
                }
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(item_count.getText().toString());
                if (count > 0) {
                    int new_count = count + 1;
                    item_count.setText(new_count + "");
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(item_count.getText().toString());
                if (count > 1) {
                    int new_count = count - 1;
                    item_count.setText(new_count + "");
                }


            }
        });


    }

    private void add_to_cart() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("res", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                Toast.makeText(ProductDetail.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                get_cart_count();
                            } else {
                                Toast.makeText(ProductDetail.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("product_id", product_id);
                params.put("qty", item_count.getText().toString());
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void get_data() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRODUCTS_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("detail", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                Glide.with(getApplicationContext()).load(ob.getString("image")).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        nodata.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        nodata.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(image);
                                price.setText("Rs." + String.format("%.2f", ob.getDouble("selling_price")));
                                item_count.setText(ob.getString("cart_qty"));
                                if (!ob.getString("description").isEmpty()) {
                                    description.setText(ob.getString("description"));
                                    desc.setVisibility(View.VISIBLE);
                                    description.setVisibility(View.VISIBLE);
                                }

                            } else {
                                layout.setVisibility(View.GONE);
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
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("product_id", product_id);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_cart);
        NotificationCountSetClass.setAddToCart(ProductDetail.this, item, cart_count);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_cart_count();
    }
}