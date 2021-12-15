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
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.relief.Adapters.ServiceAdapter;
import com.ynot.relief.Models.PackageListModel;
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

public class NurseDetailPage extends AppCompatActivity {
    Button book, pack_book;
    TextView type, name, details, original, offer, about;
    ImageView image, nodata;
    NestedScrollView layout;
    ACProgressFlower dialog;
    String id, type_data;
    RelativeLayout package_layout;
    LinearLayout original_layout, offer_layout, service_layout;


    RecyclerView rec;
    ServiceAdapter adapter;
    ArrayList<PackageListModel> model = new ArrayList<>();

    ImageView package_image, package_nodata;
    TextView title, description, from_date, todate, price, discount, offer_per;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_detail_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Booking Details");
        book = findViewById(R.id.book);
        type = findViewById(R.id.type);
        name = findViewById(R.id.name);
        details = findViewById(R.id.details);
        original = findViewById(R.id.original);
        discount = findViewById(R.id.discount);
        offer_layout = findViewById(R.id.offer_layout);
        original_layout = findViewById(R.id.original_layout);
        offer = findViewById(R.id.offer);
        about = findViewById(R.id.about);
        image = findViewById(R.id.service_image);
        nodata = findViewById(R.id.service_nodata);
        layout = findViewById(R.id.layout);
        package_layout = findViewById(R.id.package_layout);
        package_nodata = findViewById(R.id.package_nodata);
        package_image = findViewById(R.id.package_image);
        service_layout = findViewById(R.id.service_layout);
        pack_book = findViewById(R.id.pack_book);

        rec = findViewById(R.id.rec);
        title = findViewById(R.id.title);
        description = findViewById(R.id.textView29);
        from_date = findViewById(R.id.from_date);
        todate = findViewById(R.id.todate);
        price = findViewById(R.id.price);
        offer_per = findViewById(R.id.offer_per);
        layout = findViewById(R.id.layout);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        id = getIntent().getStringExtra("id");
        type_data = getIntent().getStringExtra("type");

        if (type_data.equals("1")) {
            service_details(id);
        } else {
            get_details(id);
        }
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                    Intent i = new Intent(getApplicationContext(), NurseBooking.class);
                    i.putExtra("id", id);
                    i.putExtra("type", type_data);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                }
            }
        });
        pack_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                    Intent i = new Intent(getApplicationContext(), NurseBooking.class);
                    i.putExtra("id", id);
                    i.putExtra("type", type_data);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                }
            }
        });

    }

    private void service_details(final String id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_SERVICES_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("detail", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                service_layout.setVisibility(View.VISIBLE);
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
                                name.setText(ob.getString("title"));
                                type.setText(ob.getString("type"));
                                about.setText("About " + ob.getString("title"));
                                details.setText(ob.getString("description"));

                                if (!ob.getString("offer_price").equals(ob.getString("actual_price"))) {
                                    original.setText(String.format("%.2f", ob.getDouble("actual_price")));
                                    original.setPaintFlags(original.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                } else {
                                    original_layout.setVisibility(View.GONE);
                                }
                                offer.setText(String.format("%.2f", ob.getDouble("offer_price")));
                            } else {
                                service_layout.setVisibility(View.GONE);
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
                params.put("id", id);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void get_details(String id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_PACKAGE_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("details", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                package_layout.setVisibility(View.VISIBLE);
                                title.setText(ob.getString("package_name"));
                                from_date.setText(ob.getString("from_date"));
                                todate.setText(ob.getString("to_date"));
                                discount.setText("Rs. " + String.format("%.2f", ob.getDouble("offer_price")));
                                price.setText("Rs. " + String.format("%.2f", ob.getDouble("original_price")));
                                offer_per.setText(String.format("%.0f", ob.getDouble("offer_percentage")) + " %Off");
                                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                Glide.with(getApplicationContext()).load(ob.getString("package_image")).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        package_nodata.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        package_nodata.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(package_image);
                                if (ob.getString("package_description").isEmpty()) {
                                    description.setVisibility(View.GONE);
                                } else {
                                    description.setVisibility(View.VISIBLE);
                                }
                                description.setText(ob.getString("package_description"));
                                JSONArray array = ob.getJSONArray("service_list");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject obb = array.getJSONObject(i);
                                    PackageListModel data = new PackageListModel();
                                    data.setImage(obb.getString("service_image"));
                                    data.setName(obb.getString("service_name"));
                                    data.setDiscription(String.format("%.2f", obb.getDouble("offer_price")));
                                    model.add(data);
                                }
                                adapter = new ServiceAdapter(getApplicationContext(), model);
                                rec.setAdapter(adapter);

                            } else {
                                package_layout.setVisibility(View.GONE);
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
                params.put("package_id", id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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