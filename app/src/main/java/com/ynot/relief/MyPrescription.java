package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.ynot.relief.Adapters.MyprescriptionordersAdapter;
import com.ynot.relief.Models.PrescriptionModel;
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

public class MyPrescription extends AppCompatActivity {
    ACProgressFlower dialog;
    ArrayList<PrescriptionModel> model = new ArrayList<>();
    MyprescriptionordersAdapter adapter;
    RecyclerView rec;
    ImageView nodata;
    String status = "";
    Dialog image_view;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_prescription);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Prescription Orders");
        rec = findViewById(R.id.rec);
        nodata = findViewById(R.id.nodata);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        image_view = new Dialog(MyPrescription.this);
        image_view.setContentView(R.layout.image_view_layout);
        image = image_view.findViewById(R.id.image);
        Window window = image_view.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        image_view.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (getIntent().hasExtra("success")) {
            status = getIntent().getStringExtra("success");
        }

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();


        nodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

    private void get_orders() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MY_DOCUMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("orders");
                                model = new ArrayList<>();

                                for (int i = 0; i < array.length(); i++) {
                                    PrescriptionModel list = new PrescriptionModel();
                                    JSONObject obb = array.getJSONObject(i);
                                    list.setId(obb.getString("order_id"));
                                    list.setImage(obb.getString("image"));
                                    list.setStore_name(obb.getString("store_name"));
                                    list.setPdate(obb.getString("purchase_date"));
                                    list.setStatus(obb.getString("order_status"));
                                    list.setStore_mob(obb.getString("store_mob"));
                                    list.setStore_place(obb.getString("store_place"));
                                    list.setPayment_mode(obb.getString("payment_mode"));
                                    list.setNote(obb.getString("note"));
                                    list.setDelivery_mode(obb.getString("delivery_mode"));
                                    if (obb.has("prescription_no")) {
                                        list.setOrder_no(obb.getString("prescription_no"));
                                    } else {
                                        list.setOrder_no("");
                                    }
                                    if (obb.has("order_type")) {
                                        list.setOrder_type(obb.getString("order_type"));
                                    } else {
                                        list.setOrder_type("");
                                    }
                                    model.add(list);
                                }

                                adapter = new MyprescriptionordersAdapter(getApplicationContext(), model, new MyprescriptionordersAdapter.Click() {
                                    @Override
                                    public void ItemClik(PrescriptionModel list) {
                                        Intent i = new Intent(getApplicationContext(), BillView.class);
                                        i.putExtra("id", list.getId());
                                        startActivity(i);
                                    }

                                    @Override
                                    public void call(PrescriptionModel list) {
                                        if (checkpermission()) {
                                            String number = list.getStore_mob();
                                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                            callIntent.setData(Uri.parse("tel:" + number));
                                            startActivity(callIntent);
                                        }
                                    }

                                    @Override
                                    public void Image_click(PrescriptionModel list) {
                                        Glide.with(getApplicationContext()).load(list.getImage()).into(image);
                                        image_view.show();
                                    }

                                    @Override
                                    public void Repurchase(String id) {
                                        reorder(id);
                                    }

                                    @Override
                                    public void next_page(PrescriptionModel list) {
                                        Intent i = new Intent(getApplicationContext(), PrescriptionDetails.class);
                                        i.putExtra("id", list.getId());
                                        startActivity(i);
                                    }
                                });
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
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("input", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void reorder(final String id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REPURCHASE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(MyPrescription.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyPrescription.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("order_id", id);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (status.isEmpty()) {
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(MyPrescription.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MyPrescription.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_orders();
    }

    @Override
    public void onBackPressed() {
        if (status.isEmpty()) {
            super.onBackPressed();
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finishAffinity();
        }

    }
}