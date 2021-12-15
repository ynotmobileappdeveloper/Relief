package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class DoctorRegistration extends AppCompatActivity {
    Button register;
    CheckBox checkBox;
    String checked_status = "0";
    EditText name, specilization, address, hospital, other,phone;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        register = findViewById(R.id.register);
        checkBox = findViewById(R.id.checkbox);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        specilization = findViewById(R.id.specilization);
        other = findViewById(R.id.other);
        hospital = findViewById(R.id.hospital);
        progressDialog = new ProgressDialog(DoctorRegistration.this);
        progressDialog.setMessage("Please Wait...");


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked_status = "1";
                    Log.e("checked_status", checked_status);
                } else {
                    checked_status = "0";
                    Log.e("checked_status", checked_status);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("Please fill this field !");
                    name.requestFocus();
                    return;
                }
                if (specilization.getText().toString().isEmpty()) {
                    specilization.setError("Please fill this field !");
                    specilization.requestFocus();
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
                if (hospital.getText().toString().isEmpty()) {
                    hospital.setError("Please fill this field !");
                    hospital.requestFocus();
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.doc_register,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("doc_resp",response);
                        try {
                            JSONObject ob=new JSONObject(response);
                            if (ob.getBoolean("status"))
                            {
                                Toast.makeText(DoctorRegistration.this, "Successfully Registered !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(DoctorRegistration.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("specialities", specilization.getText().toString());
                params.put("name", name.getText().toString());
                params.put("address", address.getText().toString());
                params.put("hospital_name", hospital.getText().toString());
                params.put("other_facility", other.getText().toString());
                params.put("booking_status", checked_status);
                params.put("phone", phone.getText().toString());
                Log.e("doc_parsms", String.valueOf(params));
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
