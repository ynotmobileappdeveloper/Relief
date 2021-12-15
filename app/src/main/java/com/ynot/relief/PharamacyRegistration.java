package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PharamacyRegistration extends AppCompatActivity {
    EditText name, address, licence, gst, location, owner, pharm, staffs, stock, diabetis, cardiac, business, stockist, phone;
    Button register;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharamacy_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        licence = findViewById(R.id.license);
        gst = findViewById(R.id.gst);
        location = findViewById(R.id.location);
        owner = findViewById(R.id.owner);
        pharm = findViewById(R.id.pharma);
        staffs = findViewById(R.id.staffs);
        phone = findViewById(R.id.phone);
        stock = findViewById(R.id.stock);
        diabetis = findViewById(R.id.diabiatics);
        cardiac = findViewById(R.id.cardiac);
        business = findViewById(R.id.business);
        stockist = findViewById(R.id.stokiest);
        register = findViewById(R.id.register);
        progressDialog = new ProgressDialog(PharamacyRegistration.this);
        progressDialog.setMessage("Please Wait...");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("Please fill this field !");
                    name.requestFocus();
                    return;
                }
                if (address.getText().toString().isEmpty()) {
                    address.setError("Please fill this field !");
                    address.requestFocus();
                    return;
                }
                if (phone.getText().toString().length() != 10) {
                    phone.setError("Enter a valid Mobile Number !");
                    phone.requestFocus();
                    return;
                }
                if (licence.getText().toString().isEmpty()) {
                    licence.setError("Please fill this field !");
                    licence.requestFocus();
                    return;
                }
                if (gst.getText().toString().isEmpty()) {
                    gst.setError("Please fill this field !");
                    gst.requestFocus();
                    return;
                }
                if (location.getText().toString().isEmpty()) {
                    location.setError("Please fill this field !");
                    location.requestFocus();
                    return;
                }
                if (owner.getText().toString().isEmpty()) {
                    owner.setError("Please fill this field !");
                    owner.requestFocus();
                    return;
                }
                if (pharm.getText().toString().isEmpty()) {
                    pharm.setError("Please fill this field !");
                    pharm.requestFocus();
                    return;
                }
                if (staffs.getText().toString().isEmpty()) {
                    staffs.setError("Please fill this field !");
                    staffs.requestFocus();
                    return;
                }
                if (stock.getText().toString().isEmpty()) {
                    stock.setError("Please fill this field !");
                    stock.requestFocus();
                    return;
                }
                if (diabetis.getText().toString().isEmpty()) {
                    diabetis.setError("Please fill this field !");
                    diabetis.requestFocus();
                    return;
                }
                if (cardiac.getText().toString().isEmpty()) {
                    cardiac.setError("Please fill this field !");
                    cardiac.requestFocus();
                    return;
                }
                if (business.getText().toString().isEmpty()) {
                    business.setError("Please fill this field !");
                    business.requestFocus();
                    return;
                }
                if (stockist.getText().toString().isEmpty()) {
                    stockist.setError("Please fill this field !");
                    stockist.requestFocus();
                    return;
                }
                registration();


            }
        });


    }

    private void registration() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.pharmacy_registration,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("phar_resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(PharamacyRegistration.this, "Successfully Registered !!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(PharamacyRegistration.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("name", name.getText().toString());
                params.put("licence", licence.getText().toString());
                params.put("address", address.getText().toString());
                params.put("gst", gst.getText().toString());
                params.put("location", location.getText().toString());
                params.put("owners_name", owner.getText().toString());
                params.put("pharmacicest", pharm.getText().toString());
                params.put("total_staffs", staffs.getText().toString());
                params.put("stock_value", stock.getText().toString());
                params.put("total_daibetic_patients", diabetis.getText().toString());
                params.put("total_cardiac_patients", cardiac.getText().toString());
                params.put("business_per_month", business.getText().toString());
                params.put("name_stockists", stockist.getText().toString());
                params.put("phone", phone.getText().toString());
                Log.e("phar_params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);

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
