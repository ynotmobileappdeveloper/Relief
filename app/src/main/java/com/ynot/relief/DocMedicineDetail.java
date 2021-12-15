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
import com.ynot.relief.Adapters.DocmedicineDetailAdpater;
import com.ynot.relief.Adapters.StoreAdapter;
import com.ynot.relief.Models.CityModel;
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

public class DocMedicineDetail extends AppCompatActivity {


    DocmedicineDetailAdpater adapter;
    ArrayList<TypeModel> model = new ArrayList<>();
    ArrayList<CityModel> city_model = new ArrayList<>();
    ArrayList<CityModel> subcity_model = new ArrayList<>();
    ArrayList<CityModel> store_model = new ArrayList<>();
    List<String> city_name = new ArrayList<>();
    String city_id = "", subcity_id = "", store_id = "", id = "", status;
    RecyclerView rec, stores_rec;
    TextView date, name, gender, age, symptoms;
    LinearLayout list_layout;
    ImageView image, nodata;
    MaterialCardView cardView;

    Button share_btn, send;
    Dialog share;
    Spinner city;
    StoreAdapter storeAdapter;
    ProgressDialog progressDialog;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_medicine_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        rec = findViewById(R.id.rec);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        name = findViewById(R.id.name);
        layout = findViewById(R.id.layout);
        date = findViewById(R.id.date);
        gender = findViewById(R.id.gender);
        symptoms = findViewById(R.id.tests);
        age = findViewById(R.id.age);
        cardView = findViewById(R.id.cardView);
        list_layout = findViewById(R.id.list_layout);
        image = findViewById(R.id.image);
        nodata = findViewById(R.id.nodata);

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


        id = getIntent().getStringExtra("id");


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
                    Toast.makeText(DocMedicineDetail.this, "Please Choose a Lab ", Toast.LENGTH_SHORT).show();
                    return;
                }
                send_details();
            }
        });

    }

    private void get_details() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MY_MEDICINE_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("data", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                name.setText(ob.getString("name"));
                                date.setText(ob.getString("date"));
                                gender.setText(ob.getString("gender"));
                                age.setText(ob.getString("age"));
                                symptoms.setText(ob.getString("symptoms"));

                                if (ob.getString("store_status").equals("1")) {
                                    share_btn.setVisibility(View.GONE);
                                } else {
                                    share_btn.setVisibility(View.VISIBLE);
                                }


                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new TypeModel(obb.getString("id"), obb.getString("name"), obb.getString("qty"), obb.getString("days")));

                                }
                                adapter = new DocmedicineDetailAdpater(getApplicationContext(), model);
                                rec.setAdapter(adapter);

                                if (model.size() == 0) {
                                    cardView.setVisibility(View.VISIBLE);
                                    list_layout.setVisibility(View.GONE);
                                    Glide.with(DocMedicineDetail.this).load(ob.getString("image")).addListener(new RequestListener<Drawable>() {
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
                                    }).into(image);

                                } else {
                                    cardView.setVisibility(View.GONE);
                                    list_layout.setVisibility(View.VISIBLE);
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

    private void send_details() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SEND_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("send", response);
                        progressDialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                share.dismiss();
                                Toast.makeText(DocMedicineDetail.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DocMedicineDetail.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        get_details();
    }
}