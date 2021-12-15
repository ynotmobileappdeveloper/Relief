package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.MyMedicineAdapter;
import com.ynot.relief.Models.MymedicineModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyMedicine extends AppCompatActivity {
    RecyclerView rec;
    ArrayList<MymedicineModel> model = new ArrayList<>();
    MyMedicineAdapter adapter;
    ImageView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_medicine);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Medicine");
        rec = findViewById(R.id.rec);
        nodata = findViewById(R.id.nodata);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        get_data();

    }

    private void get_data() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MY_MEDICAL_LABS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("resp", response);
                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    MymedicineModel mo = new MymedicineModel();
                                    mo.setId(obb.getString("id"));
                                    mo.setDate(obb.getString("date"));
                                    mo.setAddress(obb.getString("address"));
                                    mo.setPhone(obb.getString("phone"));
                                    mo.setName(obb.getString("name"));
                                    model.add(mo);
                                }
                                adapter = new MyMedicineAdapter(getApplicationContext(), model, new MyMedicineAdapter.ItemClick() {
                                    @Override
                                    public void Click(MymedicineModel list) {

                                        Intent i = new Intent(getApplicationContext(), ViewMyMedicine.class);
                                        i.putExtra("name", list.getName());
                                        i.putExtra("id", list.getId());
                                        i.putExtra("address", list.getAddress());
                                        i.putExtra("mob", list.getPhone());
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}