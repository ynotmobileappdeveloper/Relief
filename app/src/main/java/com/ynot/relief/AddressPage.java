package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.AddressAdapter;
import com.ynot.relief.Models.AddressModel;
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

public class AddressPage extends AppCompatActivity {
    RecyclerView rec;
    ArrayList<AddressModel> model = new ArrayList<>();
    AddressAdapter adapter;
    Button add;
    ACProgressFlower dialog;
    String set_default = "", upload = "", type = "", note = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_page);
        rec = findViewById(R.id.rec);
        add = findViewById(R.id.add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        if (getIntent().hasExtra("set_default")) {
            set_default = getIntent().getStringExtra("set_default");
        }
        if (getIntent().hasExtra("upload")) {
            upload = getIntent().getStringExtra("upload");
            note = getIntent().getStringExtra("note");
        }
        if (getIntent().hasExtra("type")) {
            type = getIntent().getStringExtra("type");
        }


        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddAddress.class);
                i.putExtra("set_default", set_default);
                if (!upload.isEmpty()) {
                    i.putExtra("upload", upload);
                    i.putExtra("note", note);
                }
                if (!type.isEmpty()) {
                    i.putExtra("type", type);
                }
                startActivity(i);
                finish();

            }
        });


    }

    private void get_address() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("address", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("addresslist");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    model.add(new AddressModel(obj.getString("add_id"), obj.getString("address"), obj.getString("landmark"), obj.getString("pincode"), obj.getString("latitude"), obj.getString("longitude"), obj.getString("address_status "), obj.getString("location"),obj.getString("store_id")));
                                }
                                adapter = new AddressAdapter(getApplicationContext(), model, new AddressAdapter.Click() {
                                    @Override
                                    public void ItemClick(AddressModel model) {

                                    }

                                    @Override
                                    public void edit_address(AddressModel model) {
                                        Intent i = new Intent(getApplicationContext(), AddAddress.class);
                                        i.putExtra("address", model.getHouse());
                                        i.putExtra("location", model.getAddress());
                                        i.putExtra("google", model.getGoogle_location());
                                        i.putExtra("latitude", model.getLat());
                                        i.putExtra("logitude", model.getLon());
                                        i.putExtra("status", model.getStatus());
                                        i.putExtra("edit", "edit");
                                        i.putExtra("id", model.getId());
                                        i.putExtra("pincode", model.getPin());
                                        startActivity(i);
                                    }

                                    @Override
                                    public void delete(AddressModel model, int position) {

                                        remove_address(model.getId(), position);

                                    }

                                    @Override
                                    public void set_default(AddressModel model) {
                                        set_default_address(model.getId());
                                    }
                                });
                                rec.setAdapter(adapter);
                                adapter.set_default(set_default);


                            } else {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
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

    private void remove_address(final String id, final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REMOVE_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                model.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, model.size());

                            } else {
                                Toast.makeText(AddressPage.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("add_id", id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    @Override
    protected void onResume() {
        super.onResume();
        get_address();
    }

    private void set_default_address(final String add_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SET_DEFAULT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("def", response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(AddressPage.this, "Successfully Set Default ", Toast.LENGTH_SHORT).show();
                                if (set_default.equals("set_default")) {
                                    finish();
                                } else {
                                    get_address();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("add_id", add_id);
                Log.e("def_params", String.valueOf(params));
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
}