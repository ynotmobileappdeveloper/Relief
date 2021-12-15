package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.ynot.relief.Adapters.BillAdapter;
import com.ynot.relief.Models.BillModel;
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

public class BillView extends AppCompatActivity {
    RecyclerView rec;
    BillAdapter adapter;
    ArrayList<BillModel> model = new ArrayList<>();
    ArrayList<BillModel> save_model = new ArrayList<>();
    Button approve, edit, save;
    String id;
    TextView name, address, mob, amount, tax, disc, total, edit_name, days, qty, delivery_amount;
    NestedScrollView layout;
    ACProgressFlower dialog, dialo;
    ImageView delete;
    Dialog edit_view;
    String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_view);

        edit_view = new Dialog(BillView.this);
        edit_view.setContentView(R.layout.edit_layout);
        edit_view.setCanceledOnTouchOutside(true);
        save = edit_view.findViewById(R.id.save);
        edit_name = edit_view.findViewById(R.id.name);
        days = edit_view.findViewById(R.id.days);
        qty = edit_view.findViewById(R.id.qty);
        Window window = edit_view.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        edit_view.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rec = findViewById(R.id.rec);
        approve = findViewById(R.id.approve);
        delivery_amount = findViewById(R.id.delivery_amount);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        mob = findViewById(R.id.mob);
        amount = findViewById(R.id.amount);
        tax = findViewById(R.id.tax_amount);
        disc = findViewById(R.id.disc);
        total = findViewById(R.id.total);
        layout = findViewById(R.id.layout);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        dialo = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();


        id = getIntent().getStringExtra("id");

        get_bill_data();

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approve_bill();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.view_delete();
                delete.setVisibility(View.INVISIBLE);


            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void get_bill_data() {
        dialo.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.BILL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        dialo.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                delete.setVisibility(View.GONE);
                                layout.setVisibility(View.VISIBLE);
                                name.setText(ob.getString("store_name"));
                                address.setText(ob.getString("store_address"));
                                mob.setText("Contact : +91 " + ob.getString("store_mob"));
                                amount.setText("\u20B9" + String.format("%.2f", ob.getDouble("amount")));
                                tax.setText("\u20B9" + String.format("%.2f", ob.getDouble("tax")));
                                disc.setText("\u20B9" + String.format("%.2f", ob.getDouble("discount")));
                                if (ob.has("delivery_charge")) {
                                    delivery_amount.setText("\u20B9" + String.format("%.2f", ob.getDouble("delivery_charge")));
                                } else {
                                    delivery_amount.setText("\u20B9" + "0.00");
                                }

                                total.setText("Total Amount " + "\u20B9" + String.format("%.2f", ob.getDouble("total_amount")));
                                if (ob.getString("approve_status").equals("1")) {
                                    approve.setVisibility(View.GONE);
                                    edit.setVisibility(View.GONE);
                                } else {
                                    approve.setVisibility(View.GONE);
                                    edit.setVisibility(View.GONE);
                                }
                                JSONArray array = ob.getJSONArray("bill ");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new BillModel(obb.getString("sl_no"), obb.getString("product"), obb.getString("qty"), String.format("%.2f", obb.getDouble("rate")), String.format("%.2f", obb.getDouble("discount")), obb.getString("item_id"), "0"));
                                }
                                adapter = new BillAdapter(getApplicationContext(), model, "no", new BillAdapter.Click() {
                                    @Override
                                    public void Delete(int position, ArrayList<BillModel> list) {
                                        save_model = list;
                                        list.get(position).setDelete("1");
                                        Gson gson = new Gson();
                                        data = gson.toJson(model);
                                        Log.e("data", data);
                                       /* model.remove(position);
                                        adapter.notifyDataSetChanged();*/
                                        edit_bill(data, "0");
                                    }

                                    @Override
                                    public void ItemClick(final BillModel list, int poition) {
                                        edit_name.setText(list.getProducts());
                                        qty.setText(list.getQty());
                                        edit_view.show();

                                        save.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                list.setQty(qty.getText().toString());
                                                adapter.notifyDataSetChanged();
                                                edit_view.dismiss();
                                                Gson gson = new Gson();
                                                data = gson.toJson(model);
                                                Log.e("data", data);
                                                edit_bill(data, "0");
                                            }
                                        });

                                    }
                                });
                                rec.setAdapter(adapter);

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
                params.put("order_id", id);
                Log.e("bill_params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);


    }

    private void remove_bill(final String data) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REMOVE_BILL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                get_bill_data();
                            } else {
                                Toast.makeText(BillView.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("data", data);
                Log.e("input", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void edit_bill(final String data, final String delete_status) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.EDIT_BILL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                get_bill_data();
                            } else {
                                Toast.makeText(BillView.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("data", data);
                Log.e("input", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void approve_bill() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.APPROVE_BILL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(BillView.this, "Successfully Confirmed !!", Toast.LENGTH_SHORT).show();
                                approve.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(BillView.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("order_id", id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);
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