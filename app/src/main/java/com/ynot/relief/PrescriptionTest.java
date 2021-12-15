package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.PTestAdapter;
import com.ynot.relief.Models.PTestMode;
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

public class PrescriptionTest extends AppCompatActivity {
    ACProgressFlower dialog;
    ArrayList<PTestMode> model = new ArrayList<>();
    PTestAdapter adapter;
    RecyclerView rec;
    TextView name, gender, age;
    LinearLayout top_layout;
    ImageView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_test);
        rec = findViewById(R.id.rec);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        nodata = findViewById(R.id.nodata);
        top_layout = findViewById(R.id.top_layout);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Prescription & Lab Tests");

        get_data();

    }

    private void get_data() {
        dialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRESCRIPTION_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                top_layout.setVisibility(View.VISIBLE);
                                name.setText(ob.getString("name"));
                                age.setText(ob.getString("age"));
                                gender.setText(ob.getString("gender"));
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new PTestMode(obb.getString("id"), obb.getString("doc_name"), "", "", ""
                                            , obb.getString("medicine"), obb.getString("lab_test"), obb.getString("date")));
                                }
                                adapter = new PTestAdapter(getApplicationContext(), model, new PTestAdapter.Click() {
                                    @Override
                                    public void ItemClick(PTestMode list) {
                                        Intent i = new Intent(getApplicationContext(), PrescriptionListPage.class);
                                        i.putExtra("id", list.getId());
                                        i.putExtra("name", name.getText().toString());
                                        i.putExtra("age", age.getText().toString());
                                        i.putExtra("gender", gender.getText().toString());
                                        i.putExtra("date", list.getDate());
                                        startActivity(i);
                                    }
                                });
                                rec.setAdapter(adapter);
                            } else {
                                top_layout.setVisibility(View.GONE);
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