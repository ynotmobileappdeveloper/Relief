package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.MyappointmentAdapter;
import com.ynot.relief.Models.MyAppoinmentsModel;
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

public class MyAppoinments extends AppCompatActivity {
    RecyclerView rec;
    MyappointmentAdapter adapter;
    ArrayList<MyAppoinmentsModel> models = new ArrayList<>();
    ACProgressFlower dialog;
    ImageView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appoinments);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Appointments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rec = findViewById(R.id.rec);
        nodata = findViewById(R.id.nodata);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    private void get_apponments() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MY_APPOINMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("data");
                                models = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    models.add(new MyAppoinmentsModel(obb.getString("id"), obb.getString("doc_name"), obb.getString("department"), obb.getString("name"), obb.getString("gender"), obb.getString("age"),
                                            obb.getString("op_type"), obb.getString("time"), obb.getString("date"), obb.getString("op_status"), obb.getString("doc_address"), obb.getString("doc_phone")));
                                }
                                adapter = new MyappointmentAdapter(getApplicationContext(), models, new MyappointmentAdapter.Click() {
                                    @Override
                                    public void ItemClick(MyAppoinmentsModel model) {
                                        Intent i = new Intent(getApplicationContext(), MyAppoinmentsDetails.class);
                                        i.putExtra("model", model);
                                        startActivity(i);
                                    }

                                    @Override
                                    public void Cancel(final MyAppoinmentsModel model) {

                                        AlertDialog.Builder alert = new AlertDialog.Builder(MyAppoinments.this);
                                        alert.setTitle("Confirmation !!");
                                        alert.setIcon(R.drawable.ic_stop);
                                        alert.setMessage("Do you want cancel this appointment ?");
                                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                cancel_app(model.getId());
                                            }
                                        });
                                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alert.show();
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
                Log.e("pa", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void cancel_app(final String id) {
        dialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CANCEL_APPOINMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(MyAppoinments.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(MyAppoinments.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("id", id);
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

    @Override
    protected void onResume() {
        super.onResume();
        get_apponments();
    }
}