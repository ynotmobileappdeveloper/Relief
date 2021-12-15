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

public class LaboratoryRegistration extends AppCompatActivity {
    Button register;
    EditText name,address,location,special,patient,other,phone;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratory_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        location = findViewById(R.id.location);
        other = findViewById(R.id.other);
        special = findViewById(R.id.special);
        phone = findViewById(R.id.phone);
        patient = findViewById(R.id.patient);
        register = findViewById(R.id.register);
        progressDialog = new ProgressDialog(LaboratoryRegistration.this);
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
                if (phone.getText().toString().length()!=10)
                {
                    phone.setError("Enter a valid Mobile Number !");
                    phone.requestFocus();
                    return;
                }
                if (location.getText().toString().isEmpty()) {
                    location.setError("Please fill this field !");
                    location.requestFocus();
                    return;
                }
                if (special.getText().toString().isEmpty()) {
                    special.setError("Please fill this field !");
                    special.requestFocus();
                    return;
                }
                if (patient.getText().toString().isEmpty()) {
                    patient.setError("Please fill this field !");
                    patient.requestFocus();
                    return;
                }
                if (other.getText().toString().isEmpty()) {
                    other.setError("Please fill this field !");
                    other.requestFocus();
                    return;
                }
                registration();
            }
        });
    }

    private void registration() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.lab_register,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("lab_res",response);
                        try {
                            JSONObject ob=new JSONObject(response);
                            if (ob.getBoolean("status"))
                            {
                                Toast.makeText(LaboratoryRegistration.this, "Successfully Registered !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LaboratoryRegistration.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("specialities", special.getText().toString());
                params.put("name", name.getText().toString());
                params.put("address", address.getText().toString());
                params.put("patient_per_month", patient.getText().toString());
                params.put("other_facility", other.getText().toString());
                params.put("location", location.getText().toString());
                params.put("phone", phone.getText().toString());
                Log.e("lab_params", String.valueOf(params));
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
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
