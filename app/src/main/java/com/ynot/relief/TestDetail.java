package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ynot.relief.Adapters.LabAdapter;
import com.ynot.relief.Adapters.StoreAdapter;
import com.ynot.relief.Adapters.TestAdapter;
import com.ynot.relief.Models.CityModel;
import com.ynot.relief.Models.LabModel;
import com.ynot.relief.Models.SlotModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDetail extends AppCompatActivity {
    ArrayList<SlotModel> model = new ArrayList<>();
    TestAdapter adapter;

    ArrayList<CityModel> city_model = new ArrayList<>();
    ArrayList<CityModel> subcity_model = new ArrayList<>();
    ArrayList<CityModel> store_model = new ArrayList<>();
    List<String> city_name = new ArrayList<>();
    String city_id = "", subcity_id = "", store_id = "", id = "", status;

    RecyclerView rec, lab_rec, stores_rec;
    LabAdapter lab_adapter;
    ArrayList<LabModel> labmodel = new ArrayList<>();
    Dialog share;
    Spinner city;
    StoreAdapter storeAdapter;
    Button share_btn, send;
    ProgressDialog progressDialog;
    TextView date, name, gender, age, tests, title, labtest;
    LinearLayout layout, test_layout, titile_layout;
    ImageView imageView, nodata;
    MaterialCardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        rec = findViewById(R.id.rec);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        gender = findViewById(R.id.gender);
        tests = findViewById(R.id.tests);
        age = findViewById(R.id.age);
        rec = findViewById(R.id.rec);
        card = findViewById(R.id.card);
        layout = findViewById(R.id.layout);
        imageView = findViewById(R.id.imageView);
        test_layout = findViewById(R.id.test_layout);
        nodata = findViewById(R.id.nodata);
        labtest = findViewById(R.id.labtest);
        title = findViewById(R.id.title);
        titile_layout = findViewById(R.id.titile_layout);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");

        id = getIntent().getStringExtra("id");
        status = getIntent().getStringExtra("status");


        share = new Dialog(this);
        share.setContentView(R.layout.share_layout);
        Window window = share.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        share.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        city = share.findViewById(R.id.city);
        stores_rec = share.findViewById(R.id.stores_rec);
        send = share.findViewById(R.id.send);
        stores_rec.setNestedScrollingEnabled(false);
        stores_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        stores_rec.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        share_btn = findViewById(R.id.share);
        if (status.equals("2")) {
            share_btn.setVisibility(View.GONE);
        } else {
            share_btn.setVisibility(View.VISIBLE);
        }
        get_data();
        get_city();
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = view.findViewById(android.R.id.text1);
                if (i == 0) {
                    textView.setTextColor(Color.GRAY);
                    city_id = "";
                } else {
                    textView.setTextColor(Color.BLACK);
                    city_id = city_model.get(i - 1).getId();
                    get_all_stores(city_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share.show();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (store_id.isEmpty()) {
                    Toast.makeText(TestDetail.this, "Please Choose a Lab ", Toast.LENGTH_SHORT).show();
                    return;
                }
                send_details();
            }
        });

    }

    private void get_data() {
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.TEST_DETAILS
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject ob = new JSONObject(response);
                    Log.e("resp", response);
                    progressDialog.dismiss();

                    if (ob.getBoolean("status")) {

                        if (status.equals("1")) {
                            layout.setVisibility(View.VISIBLE);
                            name.setText(ob.getString("name"));
                            gender.setText(ob.getString("gender"));
                            age.setText(ob.getString("age"));
                            tests.setText(ob.getString("symptoms"));

                            if (ob.getString("lab_status").equals("1")) {
                                share_btn.setVisibility(View.GONE);
                            } else {
                                share_btn.setVisibility(View.VISIBLE);
                            }
                            if (!ob.getString("image").isEmpty()) {
                                titile_layout.setVisibility(View.GONE);
                                card.setVisibility(View.VISIBLE);
                                test_layout.setVisibility(View.GONE);
                                Glide.with(getApplicationContext()).load(ob.getString("image")).addListener(new RequestListener<Drawable>() {
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
                                }).into(imageView);
                            } else {
                                titile_layout.setVisibility(View.VISIBLE);
                                test_layout.setVisibility(View.VISIBLE);
                                card.setVisibility(View.GONE);
                            }


                            JSONArray array = ob.getJSONArray("data");
                            model = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obb = array.getJSONObject(i);
                                model.add(new SlotModel("", obb.getString("lab_tests")));
                            }
                            adapter = new TestAdapter(getApplicationContext(), model);
                            rec.setAdapter(adapter);
                        } else {
                            title.setText("Results :");
                            layout.setVisibility(View.VISIBLE);
                            labtest.setVisibility(View.GONE);
                            name.setText(ob.getString("name"));
                            gender.setText(ob.getString("gender"));
                            age.setText(ob.getString("age"));
                            tests.setText(ob.getString("tests"));

                            if (!ob.getString("image").isEmpty()) {
                                titile_layout.setVisibility(View.GONE);
                                test_layout.setVisibility(View.GONE);
                                card.setVisibility(View.VISIBLE);
                                test_layout.setVisibility(View.GONE);
                                Glide.with(getApplicationContext()).load(ob.getString("image")).addListener(new RequestListener<Drawable>() {
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
                                }).into(imageView);
                            } else {
                                titile_layout.setVisibility(View.VISIBLE);

                                card.setVisibility(View.GONE);
                            }


                            JSONArray array = ob.getJSONArray("data");
                            model = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obb = array.getJSONObject(i);
                                model.add(new SlotModel("", obb.getString("lab_tests")));
                            }
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
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("status", status);
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

    private void get_all_stores(final String subcity_id) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_LABS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                JSONArray array = ob.getJSONArray("labs");
                                store_model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    store_model.add(new CityModel(obb.getString("lab_id"), obb.getString("name")));
                                }
                                storeAdapter = new StoreAdapter(getApplicationContext(), store_model, new StoreAdapter.Click() {
                                    @Override
                                    public void ItemClick(CityModel list) {
                                        store_id = list.getId();
                                    }
                                });
                                stores_rec.setAdapter(storeAdapter);
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
                params.put("state_id", city_id);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    private void get_city() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("states", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                JSONArray array = ob.getJSONArray("states");
                                city_model = new ArrayList<>();
                                city_name = new ArrayList<>();
                                city_name.add("Choose State");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    city_model.add(new CityModel(obb.getString("id"), obb.getString("state")));
                                    city_name.add(obb.getString("state"));
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, city_name) {
                                    @Override
                                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                        View view = super.getDropDownView(position, convertView, parent);
                                        TextView tv = (TextView) view;
                                        if (position == 0) {
                                            tv.setTextColor(Color.GRAY);
                                        } else {
                                            tv.setTextColor(Color.BLACK);
                                        }
                                        return view;
                                    }
                                };
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                city.setAdapter(adapter);
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
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void send_details() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SEND_TO_LAB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                share.dismiss();
                                Toast.makeText(TestDetail.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(TestDetail.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("lab_id", store_id);
                params.put("id", id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("send_data", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}