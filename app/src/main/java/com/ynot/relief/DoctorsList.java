package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.DoctorsAdapter;
import com.ynot.relief.Adapters.HomeDoctorsAdapter;
import com.ynot.relief.Adapters.HomeViewpagerAdapter;
import com.ynot.relief.Models.DoctorsModel;
import com.ynot.relief.Models.HomeDoctorsModel;
import com.ynot.relief.Models.HomeSlider;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class DoctorsList extends AppCompatActivity {
    ViewPager viewpager;
    HomeViewpagerAdapter viewpagerAdapter;
    ArrayList<HomeSlider> homeSlider = new ArrayList<>();
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 3000;
    int currentPage = 0;
    int NUM_PAGES = 3;
    Timer timer;
    TextView name, namesecond;
    RecyclerView doc_rec, doc_avail;
    DoctorsAdapter adapter;
    ArrayList<DoctorsModel> model = new ArrayList<>();
    HomeDoctorsAdapter doc_adapter;
    ArrayList<HomeDoctorsModel> docmodel = new ArrayList<>();
    String depart_id = "";
    ACProgressFlower progress;
    LinearLayout doc_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        progress = new ACProgressFlower.Builder(DoctorsList.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        viewpager = findViewById(R.id.viewpager);
        doc_rec = findViewById(R.id.doc);
        doc_avail = findViewById(R.id.doc_rec);
        doc_layout = findViewById(R.id.doc_layout);
        doc_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        doc_avail.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        name = findViewById(R.id.name);
        namesecond = findViewById(R.id.namesecond);

        name.setText(getIntent().getStringExtra("name"));
        namesecond.setText(getIntent().getStringExtra("name"));
        depart_id = getIntent().getStringExtra("id");


        viewpagerAdapter = new HomeViewpagerAdapter(getApplicationContext(), homeSlider);
        viewpager.setAdapter(viewpagerAdapter);
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewpager.setCurrentItem(currentPage++, true);

            }
        };
        timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);

        viewpager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        viewpager.setPadding(70, 0, 70, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        viewpager.setPageMargin(20);


        get_doctors();
        get_available_doc();


    }

    private void get_available_doc() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.AVAILABLE_DOC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("avail_doc", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                doc_layout.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("doc");
                                docmodel = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    docmodel.add(new HomeDoctorsModel(obb.getString("doc_id"), obb.getString("name"), obb.getString("image"), obb.getString("qualification"), obb.getString("patients_count"), obb.getString("experience"), obb.getString("rating")));
                                }
                                doc_adapter = new HomeDoctorsAdapter(getApplicationContext(), docmodel, new HomeDoctorsAdapter.Click() {
                                    @Override
                                    public void ItemClick(HomeDoctorsModel model) {
                                        Intent i = new Intent(getApplicationContext(), DocDetails.class);
                                        i.putExtra("id", model.getId());
                                        startActivity(i);
                                    }
                                });
                                doc_avail.setAdapter(doc_adapter);


                            } else {
                                doc_layout.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void get_doctors() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_DOC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.e("get_resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                model = new ArrayList<>();
                                JSONArray array = ob.getJSONArray("doc");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new DoctorsModel(obb.getString("doc_id"), obb.getString("image"), obb.getString("name"), obb.getString("qualification"), obb.getString("op_time"), obb.getString("location"), obb.getString("rating")));

                                }
                                adapter = new DoctorsAdapter(getApplicationContext(), model, new DoctorsAdapter.Click() {
                                    @Override
                                    public void ItemClick(DoctorsModel model) {
                                        Intent i = new Intent(getApplicationContext(), DocDetails.class);
                                        i.putExtra("id", model.getId());
                                        startActivity(i);
                                    }
                                });
                                doc_rec.setAdapter(adapter);


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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("department_id", depart_id);
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