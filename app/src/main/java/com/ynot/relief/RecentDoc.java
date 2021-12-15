package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.RecentAdapter;
import com.ynot.relief.Models.RecentModel;
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

public class RecentDoc extends AppCompatActivity {

    RecyclerView rec;
    RecentAdapter adapter;
    ArrayList<RecentModel> model = new ArrayList<>();
    ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_doc);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recent Doctors");
        dialog = new ACProgressFlower.Builder(RecentDoc.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        rec = findViewById(R.id.rec);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        get_recent_doc();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void get_recent_doc() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.RECENT_DOC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("recent_doc", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new RecentModel(obb.getString("name"), obb.getString("department"), obb.getString("consult_date")));
                                }
                                adapter = new RecentAdapter(getApplicationContext(), model, "page");
                                rec.setAdapter(adapter);
                            } else {
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", String.valueOf(error));

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
}