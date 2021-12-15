package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleyMultipartRequest;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class CheckoutPage extends AppCompatActivity implements View.OnClickListener {
    CardView cod, gpay, net;
    Button address, proceed;
    TextView address_name, landmark, pincode, nodata, result;
    LinearLayout address_layout, delivery;
    ACProgressFlower dialog;
    String upload = "";
    byte[] byteArray;
    String store_id = "", payment_status = "1", add_id = "", delivery_status = "";
    NestedScrollView layout;
    String note = "", order_type = "";
    String type = "", type_data = "", latitiude, longitude;
    RadioGroup radiogroup, delivery_group;
    RadioButton cod_radio, net_banking_radio, gpay_radio, home, pick;
    double total_amount;
    ProgressDialog progressDialog;
    String my_order_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        if (getIntent().hasExtra("total")) {
            total_amount = getIntent().getDoubleExtra("total", 0);
            order_type = getIntent().getStringExtra("order");
        }

        if (getIntent().hasExtra("upload")) {
            upload = getIntent().getStringExtra("upload");
            note = getIntent().getStringExtra("note");
            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
            String previouslyEncodedImage = shre.getString("image_data", "");
            //store_id = shre.getString("store_id", "");

            if (!previouslyEncodedImage.equalsIgnoreCase("")) {

                byte[] b = Base64.decode(previouslyEncodedImage.getBytes(), Base64.DEFAULT);
                byteArray = b;

            }
        }
        if (getIntent().hasExtra("type")) {
            type_data = getIntent().getStringExtra("type");
            my_order_type = getIntent().getStringExtra("order_type");

            SharedPreferences preferences = getSharedPreferences("store", Context.MODE_PRIVATE);
            // store_id = preferences.getString("store", "");
        }


        cod = findViewById(R.id.cod);
        delivery_group = findViewById(R.id.delivery_group);
        delivery = findViewById(R.id.delivery);
        result = findViewById(R.id.result);
        address = findViewById(R.id.address);
        layout = findViewById(R.id.layout);
        proceed = findViewById(R.id.proceed);
        gpay = findViewById(R.id.gpay);
        net = findViewById(R.id.net);
        address_name = findViewById(R.id.address_name);
        landmark = findViewById(R.id.landmark);
        pincode = findViewById(R.id.pincode);
        nodata = findViewById(R.id.nodata);
        address_layout = findViewById(R.id.address_layout);
        radiogroup = findViewById(R.id.radiogroup);
        cod_radio = findViewById(R.id.cod_radio);
        home = findViewById(R.id.home);
        pick = findViewById(R.id.pick);
        net_banking_radio = findViewById(R.id.net_banking_radio);
        gpay_radio = findViewById(R.id.gpay_radio);


        cod.setOnClickListener(this);
        gpay.setOnClickListener(this);
        net.setOnClickListener(this);

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddressPage.class);
                i.putExtra("set_default", "set_default");
                if (!upload.isEmpty()) {
                    i.putExtra("upload", upload);
                    i.putExtra("note", note);
                }
                if (!type_data.isEmpty()) {
                    i.putExtra("type", type_data);
                }
                startActivity(i);
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (add_id.isEmpty()) {
                    Toast.makeText(CheckoutPage.this, "Please choose address !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (delivery_status.isEmpty()) {
                    Toast.makeText(CheckoutPage.this, "Please Choose Delivery Method !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!upload.isEmpty()) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
                    upload_data(byteArray, "IMG_" + timeStamp + ".jpg");
                } else if (!type_data.isEmpty()) {
                    set_type_order(type_data);
                } else {
                    place_order();
                }

            }
        });

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cod_radio:
                        cod_radio.setChecked(true);
                        net_banking_radio.setChecked(false);
                        gpay_radio.setChecked(false);
                        payment_status = "1";
                        break;
                    case R.id.net_banking_radio:
                        cod_radio.setChecked(false);
                        net_banking_radio.setChecked(true);
                        gpay_radio.setChecked(false);
                        payment_status = "2";
                        break;
                    case R.id.gpay_radio:
                        cod_radio.setChecked(false);
                        net_banking_radio.setChecked(false);
                        gpay_radio.setChecked(true);
                        payment_status = "3";
                        break;
                }
            }
        });
        delivery_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.home:
                        home.setChecked(true);
                        pick.setChecked(false);
                        delivery_status = "1";
                        break;
                    case R.id.pick:
                        home.setChecked(false);
                        pick.setChecked(true);
                        delivery_status = "2";
                        break;
                }
            }
        });


     /*   if (total_amount > 300) {
            home.setEnabled(true);
        } else {
            home.setEnabled(false);
        }*/


    }

    private void place_order() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PLACE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Intent i = new Intent(getApplicationContext(), OrderPageSuccess.class);
                                i.putExtra("order", "order");
                                i.putExtra("order_id", ob.getString("order_id"));
                                i.putExtra("total_price", ob.getString("total_price"));
                                startActivity(i);
                                finish();
                                Toast.makeText(CheckoutPage.this, "Order placed successfully !", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(CheckoutPage.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("error", new String(error.networkResponse.data));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("add_id", add_id);
                params.put("payment_mode", payment_status);
                params.put("delivery_mode", delivery_status);
                Log.e("params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(80 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void set_type_order(final String data) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.TYPE_MEDICINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("type", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                Intent i = new Intent(getApplicationContext(), OrderPageSuccess.class);
                                i.putExtra("type", "type");
                                startActivity(i);
                                finish();
                                Toast.makeText(CheckoutPage.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CheckoutPage.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("payment_mode", payment_status);
                params.put("delivery_mode", delivery_status);
                params.put("add_id", add_id);
                params.put("data", data);
                params.put("store_id", store_id);
                params.put("order_type", my_order_type);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("type_params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(80 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void upload_data(final byte[] byteArray, final String name) {
        dialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLs.UPLOAD_PRESCRIPTION,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        dialog.dismiss();
                        Log.e("res", String.valueOf(response));
                        try {
                            JSONObject ob = new JSONObject(new String(response.data));
                            Log.e("res", String.valueOf(ob));
                            if (ob.getBoolean("status")) {
                                Intent i = new Intent(getApplicationContext(), OrderPageSuccess.class);
                                i.putExtra("upload", "upload");
                                i.putExtra("order_id", ob.getString("prescription_no"));
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(CheckoutPage.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("error", new String(error.networkResponse.data));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("store_id", store_id);
                params.put("payment_mode", payment_status);
                params.put("delivery_mode", delivery_status);
                params.put("add_id", add_id);
                params.put("note", note);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("upload_params", String.valueOf(params));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(name, byteArray));
                Log.e("image_data", String.valueOf(params));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(80 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void get_details() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_OUT_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("res", response);

                            if (ob.getBoolean("status")) {

                                JSONObject obb = ob.getJSONObject("address");

                                if (!obb.getString("add_id").isEmpty()) {
                                    add_id = obb.getString("add_id");
                                    store_id = obb.getString("store_id");
                                    nodata.setVisibility(View.GONE);
                                    address_layout.setVisibility(View.VISIBLE);
                                    address_name.setText(obb.getString("address"));
                                    landmark.setText(obb.getString("landmark"));
                                    pincode.setText("Pin :" + obb.getString("pincode"));
                                    address.setText("Change/Set Default");
                                    latitiude = obb.getString("latitude");
                                    longitude = obb.getString("longitude");

                                    if (!longitude.isEmpty()) {
                                        check_location();
                                    }


                                } else {
                                    nodata.setVisibility(View.VISIBLE);
                                    address_layout.setVisibility(View.INVISIBLE);
                                }

                            } else {
                                nodata.setVisibility(View.VISIBLE);
                                address_layout.setVisibility(View.INVISIBLE);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                if (upload.equals("upload")) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    finish();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (upload.equals("upload")) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View v) {

        if (v == cod) {
            cod.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            gpay.setCardBackgroundColor(Color.WHITE);
            net.setCardBackgroundColor(Color.WHITE);

        }
        if (v == gpay) {
            gpay.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            cod.setCardBackgroundColor(Color.WHITE);
            net.setCardBackgroundColor(Color.WHITE);

        }
        if (v == net) {
            net.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            gpay.setCardBackgroundColor(Color.WHITE);
            cod.setCardBackgroundColor(Color.WHITE);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        get_details();
    }

    private void check_location() {
        progressDialog.show();
        delivery.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        delivery.setVisibility(View.VISIBLE);
                        Log.e("check", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                if (!order_type.isEmpty()) {
                                    if (total_amount > 300) {
                                        result.setVisibility(View.GONE);
                                        home.setEnabled(true);
                                    } else {
                                        result.setText("*Minimum purchase amount should be Rs. 300 for home delivery.");
                                        result.setVisibility(View.VISIBLE);
                                        home.setEnabled(false);
                                        home.setChecked(false);
                                        pick.setChecked(true);
                                    }
                                } else {
                                    result.setVisibility(View.GONE);
                                    home.setEnabled(true);
                                }

                            } else {
                                result.setText("*Home delivery not available to this location.");
                                result.setVisibility(View.VISIBLE);
                                home.setEnabled(false);
                                pick.setChecked(true);
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
                Map<String, String> param = new HashMap<>();
                param.put("latitude", latitiude);
                param.put("longitude", longitude);
                Log.e("check_params", String.valueOf(param));
                return param;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

}