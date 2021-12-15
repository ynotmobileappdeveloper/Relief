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
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.ynot.relief.Adapters.LabAdapter;
import com.ynot.relief.Adapters.PListAdpater;
import com.ynot.relief.Adapters.StoreAdapter;
import com.ynot.relief.Models.CityModel;
import com.ynot.relief.Models.LabModel;
import com.ynot.relief.Models.TypeModel;
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

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class PrescriptionListPage extends AppCompatActivity {
    String id;
    PListAdpater adpater;
    ArrayList<TypeModel> model;
    RecyclerView rec, lab_rec;
    LabAdapter lab_adapter;
    ArrayList<LabModel> labmodel = new ArrayList<>();
    Dialog share;
    Spinner city;
    RecyclerView stores_rec;
    ArrayList<CityModel> city_model = new ArrayList<>();
    ArrayList<CityModel> subcity_model = new ArrayList<>();
    ArrayList<CityModel> store_model = new ArrayList<>();
    List<String> city_name = new ArrayList<>();
    List<String> subcity_name = new ArrayList<>();
    String city_id = "", subcity_id = "", store_id = "", disable = "";
    StoreAdapter storeAdapter;
    Button share_btn, send;
    TextView name, gender, age, date, lab_test;
    ProgressDialog progressDialog;
    LinearLayout medicine_layout, layout, name_layout;

    ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_list_page);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
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

        rec = findViewById(R.id.rec);
        lab_rec = findViewById(R.id.lab_rec);
        share_btn = findViewById(R.id.share);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        date = findViewById(R.id.date);
        layout = findViewById(R.id.layout);
        medicine_layout = findViewById(R.id.medicine_layout);
        lab_test = findViewById(R.id.lab_test);
        name_layout = findViewById(R.id.name_layout);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lab_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lab_rec.setNestedScrollingEnabled(false);
        rec.setNestedScrollingEnabled(false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Prescription & Lab Tests");
        id = getIntent().getStringExtra("id");
        name.setText(getIntent().getStringExtra("name"));
        gender.setText(getIntent().getStringExtra("gender"));
        age.setText(getIntent().getStringExtra("age"));
        date.setText(getIntent().getStringExtra("date"));
        if (getIntent().hasExtra("disable")) {
            disable = getIntent().getStringExtra("disable");
        }
        if (name.getText().toString().isEmpty()) {
            name_layout.setVisibility(View.GONE);
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
                    Toast.makeText(PrescriptionListPage.this, "Please Choose a Store ", Toast.LENGTH_SHORT).show();
                    return;
                }
                send_details();
            }
        });

        if (!disable.isEmpty()) {
            disable_notification();
        }


    }

    private void disable_notification() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.DISABLE_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("disable", response);
                        try {
                            JSONObject ob = new JSONObject(response);
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
                params.put("id", id);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void send_details() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SEND_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                share.dismiss();
                                Toast.makeText(PrescriptionListPage.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PrescriptionListPage.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("store_id", store_id);
                params.put("id", id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("send_data", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void get_all_stores(final String subcity_id) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_STORES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                JSONArray array = ob.getJSONArray("stores");
                                store_model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    store_model.add(new CityModel(obb.getString("store_id"), obb.getString("name")));
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

    private void get_data() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRESCRIPTION_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("data", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("medicine_data");
                                if (array.length() != 0) {
                                    medicine_layout.setVisibility(View.VISIBLE);
                                }
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new TypeModel(obb.getString("sl_no"), obb.getString("name"), obb.getString("qty"), obb.getString("day ")));
                                }
                                adpater = new PListAdpater(getApplicationContext(), model);
                                rec.setAdapter(adpater);

                                JSONArray test = ob.getJSONArray("lab_test");
                                if (test.length() != 0) {
                                    lab_test.setVisibility(View.VISIBLE);
                                }
                                labmodel = new ArrayList<>();
                                for (int j = 0; j < test.length(); j++) {
                                    JSONObject oob = test.getJSONObject(j);
                                    labmodel.add(new LabModel(oob.getString("sl_no"), oob.getString("name")));
                                }
                                lab_adapter = new LabAdapter(getApplicationContext(), labmodel);
                                lab_rec.setAdapter(lab_adapter);
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

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("id", id);
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