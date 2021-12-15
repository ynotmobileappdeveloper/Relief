package com.ynot.relief;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ynot.relief.Adapters.TypeAdpater;
import com.ynot.relief.Models.MediProduct;
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

public class SearchTypeAMedicine extends AppCompatActivity {
    RecyclerView rec;
    Button add, order;
    ArrayList<TypeModel> model = new ArrayList<>();
    TypeAdpater adpater;
    ACProgressFlower progress;
    ProgressDialog progressDialog;
    Dialog dialog;
    Button save;
    EditText days, qty;
    AutoCompleteTextView name;
    int count = 0;
    String latitude = "", longitude = "", store_id = "", medi_id = "";
    List<String> medicines = new ArrayList<>();
    ArrayList<MediProduct> medi_model = new ArrayList<>();
    ArrayAdapter<String> adapter;
    LinearLayout top_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_type_a_medicine);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progress = new ACProgressFlower.Builder(SearchTypeAMedicine.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        rec = findViewById(R.id.rec);
        add = findViewById(R.id.add);
        order = findViewById(R.id.order);
        top_layout = findViewById(R.id.top_layout);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        dialog = new Dialog(SearchTypeAMedicine.this);
        dialog.setContentView(R.layout.dialog_type);
        dialog.setCanceledOnTouchOutside(true);
        save = dialog.findViewById(R.id.save);
        name = dialog.findViewById(R.id.name);
        days = dialog.findViewById(R.id.days);
        qty = dialog.findViewById(R.id.qty);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getName() != null) {
            latitude = SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getLat();
            longitude = SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getLon();
        } else {
            SharedPreferences preferences = getSharedPreferences("location", Context.MODE_PRIVATE);
            latitude = preferences.getString("latitude", "");
            longitude = preferences.getString("longitude", "");
        }

        get_store_location_based();
        //get_products();


        if (model.size() > 0) {
            order.setEnabled(true);
        } else {
            order.setEnabled(false);
        }


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                name.setText("");
                days.setText("");
                qty.setText("");
                name.requestFocus();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("Please fill this field !!");
                    name.requestFocus();
                    return;
                }

                if (qty.getText().toString().isEmpty()) {
                    qty.setError("Please fill this field !!");
                    qty.requestFocus();
                    return;
                }

                dialog.dismiss();
                count++;
                model.add(new TypeModel(String.valueOf(count), name.getText().toString(), qty.getText().toString(), days.getText().toString()));
                adpater = new TypeAdpater(getApplicationContext(), model, new TypeAdpater.Click() {
                    @Override
                    public void delete(int position) {
                        model.remove(position);
                        adpater.notifyItemRemoved(position);
                        adpater.notifyItemRangeChanged(position, model.size());
                        if (model.size() > 0) {
                            order.setEnabled(true);
                            top_layout.setVisibility(View.VISIBLE);
                        } else {
                            count = 0;
                            order.setEnabled(false);
                            top_layout.setVisibility(View.GONE);
                        }
                    }
                });
                rec.setAdapter(adpater);
                if (model.size() > 0) {
                    order.setEnabled(true);
                    top_layout.setVisibility(View.VISIBLE);
                } else {
                    order.setEnabled(false);
                    top_layout.setVisibility(View.GONE);
                }
                medi_id = "";
            }
        });

        name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (medi_model.get(position).getNeed_pres().equals("0")) {
                    medi_id = medi_model.get(position).getId();
                } else {
                    Toast.makeText(getApplicationContext(), "Need Prescription for this Item !!", Toast.LENGTH_LONG).show();
                    name.getText().clear();
                }

            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("store", MODE_PRIVATE).edit();
                editor.putString("store", store_id);
                editor.apply();

                Gson gson = new Gson();
                String json = gson.toJson(model);
                Log.e("model", json);
                Intent i = new Intent(getApplicationContext(), CheckoutPage.class);
                i.putExtra("type", json);
                i.putExtra("order_type", "1");
                startActivity(i);

            }
        });

    }

    private void get_store_location_based() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                store_id = ob.getString("store_id");
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
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                Log.e("params_location", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void get_products() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MEDICAL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                JSONArray array = ob.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    medicines.add(obb.getString("medicine_name"));
                                    String pres;
                                    if (obb.has("prescription_status")) {
                                        pres = obb.getString("prescription_status");
                                    } else {
                                        pres = "0";
                                    }
                                    medi_model.add(new MediProduct(obb.getString("medicine_id"), obb.getString("medicine_name"), pres));
                                }
                                adapter = new ArrayAdapter<String>(SearchTypeAMedicine.this, android.R.layout.simple_spinner_dropdown_item, medicines);
                                name.setAdapter(adapter);

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
}