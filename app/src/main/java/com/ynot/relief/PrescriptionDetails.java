package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.google.android.material.card.MaterialCardView;
import com.ynot.relief.Adapters.PrescriptionAdpater;
import com.ynot.relief.Models.TypeModel;
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

public class PrescriptionDetails extends AppCompatActivity {

    ArrayList<TypeModel> model = new ArrayList<>();
    PrescriptionAdpater adapter;
    RecyclerView rec;
    ACProgressFlower progress;
    ImageView image, nodata;
    MaterialCardView card;
    LinearLayout header, layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details");
        progress = new ACProgressFlower.Builder(PrescriptionDetails.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        rec = findViewById(R.id.rec);
        image = findViewById(R.id.image);
        layout = findViewById(R.id.layout);
        nodata = findViewById(R.id.nodata);
        card = findViewById(R.id.card);
        header = findViewById(R.id.header);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        get_prescription_details(getIntent().getStringExtra("id"));


    }

    private void get_prescription_details(String id) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MY_DOCUMENT_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        progress.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("data");
                                if (array.length() > 0) {
                                    header.setVisibility(View.VISIBLE);
                                    card.setVisibility(View.GONE);
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obb = array.getJSONObject(i);
                                        model.add(new TypeModel(String.valueOf(i + 1), obb.getString("name"), obb.getString("qty"), obb.getString("days")));
                                    }
                                    adapter = new PrescriptionAdpater(getApplicationContext(), model, new PrescriptionAdpater.Click() {
                                        @Override
                                        public void delete(int position) {

                                        }
                                    });
                                    rec.setAdapter(adapter);
                                } else {
                                    header.setVisibility(View.GONE);
                                    card.setVisibility(View.VISIBLE);
                                    Glide.with(PrescriptionDetails.this).load(ob.getString("image")).addListener(new RequestListener<Drawable>() {
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

                                }
                            } else {
                                layout.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("id", id);
                Log.e("input", String.valueOf(params));
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}