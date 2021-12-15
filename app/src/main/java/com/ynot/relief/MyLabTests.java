package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.MyTestsAdapter;
import com.ynot.relief.Models.MyTestsModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyLabTests extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rec;
    MyTestsAdapter adapter;
    ArrayList<MyTestsModel> model = new ArrayList<>();
    TextView lab, result;
    ProgressDialog progressDialog;
    String status = "1";
    ImageView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lab_tests);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Lab Test & Results");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        rec = findViewById(R.id.rec);
        lab = findViewById(R.id.lab);
        result = findViewById(R.id.result);
        nodata = findViewById(R.id.nodata);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lab.setOnClickListener(this);
        result.setOnClickListener(this);
        get_details(status);
    }

    private void get_details(String status) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MY_TEST_RESULTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("resp", response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                model = new ArrayList<>();
                                JSONArray array = ob.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new MyTestsModel(obb.getString("id"), obb.getString("date"), obb.getString("name"), obb.getString("gender"), obb.getString("age"), obb.getString("tests"), obb.getString("image"), obb.getString("lab_status")));
                                    adapter = new MyTestsAdapter(getApplicationContext(), model, new MyTestsAdapter.Click() {
                                        @Override
                                        public void ItemClick(MyTestsModel list) {
                                            Intent i = new Intent(getApplicationContext(), TestDetail.class);
                                            i.putExtra("id", list.getId());
                                            i.putExtra("status", status);
                                            startActivity(i);
                                        }

                                        @Override
                                        public void lab_status(MyTestsModel list) {
                                            Intent i = new Intent(getApplicationContext(), LabStatusPage.class);
                                            i.putExtra("id", list.getId());
                                            startActivity(i);
                                        }
                                    }, status);
                                    rec.setAdapter(adapter);
                                }
                            } else {
                                nodata.setVisibility(View.VISIBLE);
                                rec.setAdapter(null);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("status", status);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    @Override
    public void onClick(View v) {

        if (v == lab) {
            lab.setTextColor(getResources().getColor(android.R.color.white));
            lab.setBackgroundResource(R.drawable.tab_selected_bg);
            result.setBackgroundResource(R.drawable.tab_bg);
            result.setTextColor(getResources().getColor(android.R.color.black));
            get_details("1");
        }
        if (v == result) {
            result.setTextColor(getResources().getColor(android.R.color.white));
            result.setBackgroundResource(R.drawable.tab_selected_bg);
            lab.setBackgroundResource(R.drawable.tab_bg);
            lab.setTextColor(getResources().getColor(android.R.color.black));
            get_details("2");
        }

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