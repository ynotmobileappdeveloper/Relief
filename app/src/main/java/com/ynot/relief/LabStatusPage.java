package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.TestAdapter;
import com.ynot.relief.Models.SlotModel;
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

public class LabStatusPage extends AppCompatActivity {
    ArrayList<SlotModel> model = new ArrayList<>();
    TestAdapter adapter;
    RecyclerView rec, stores_rec;
    Dialog share;
    Spinner city;
    Button share_btn, send;
    TextView symptoms, lab_name, name, gender, age, phone, address;
    ACProgressFlower dialog;
    String id, mob;
    LinearLayout layout;
    ImageView call, map;
    double la, lo;
    Button approve_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_status_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lab Status");
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        rec = findViewById(R.id.rec);
        symptoms = findViewById(R.id.symptoms);
        lab_name = findViewById(R.id.lab_name);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        layout = findViewById(R.id.layout);
        age = findViewById(R.id.age);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        call = findViewById(R.id.call);
        approve_status = findViewById(R.id.approve_status);
        map = findViewById(R.id.map);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        get_data(getIntent().getStringExtra("id"));

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mob.isEmpty()) {
                    Log.e("call", "yes");
                    if (checkpermission()) {
                        Log.e("call", "inside");
                        String number = mob;
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + number));
                        startActivity(callIntent);
                    }

                }
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (la != 0 && lo != 0) {
                    String url = "http://maps.google.com/maps?saddr=&daddr=" + la + "," + lo;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                } else {
                    Toast.makeText(LabStatusPage.this, "No Location Found !!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void get_data(String id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.LAB_STATUS_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                lab_name.setText(ob.getString("lab_name"));
                                name.setText(ob.getString("name"));
                                gender.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                address.setText(ob.getString("address"));
                                phone.setText(ob.getString("lab_phone"));
                                mob = ob.getString("lab_phone");

                                if (ob.getString("approve_status").equals("0")) {
                                    approve_status.setText("Pending");
                                } else {
                                    approve_status.setText("Approved");
                                }
                                la = Double.parseDouble(ob.getString("latitude"));
                                lo = Double.parseDouble(ob.getString("longitude"));

                                symptoms.setText(ob.getString("symptoms"));

                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new SlotModel("", obb.getString("tests")));
                                    adapter = new TestAdapter(getApplicationContext(), model);
                                    rec.setAdapter(adapter);

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
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("id", id);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(LabStatusPage.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return false;
        } else {
            return true;
        }
    }

}