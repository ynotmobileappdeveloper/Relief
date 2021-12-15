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

public class DistributorsRegisteration extends AppCompatActivity {
    Button register;
    EditText name,address,area,chemist,comapany,turnover,mnc,diabetics,phone;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributors_registeration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name=findViewById(R.id.name);
        register=findViewById(R.id.register);
        address=findViewById(R.id.address);
        area=findViewById(R.id.area);
        chemist=findViewById(R.id.chemist);
        comapany=findViewById(R.id.company);
        turnover=findViewById(R.id.turnover);
        phone=findViewById(R.id.phone);
        mnc=findViewById(R.id.mnc);
        diabetics=findViewById(R.id.diabiatics);
        progressDialog = new ProgressDialog(DistributorsRegisteration.this);
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
                if (area.getText().toString().isEmpty()) {
                    area.setError("Please fill this field !");
                    area.requestFocus();
                    return;
                }
                if (chemist.getText().toString().isEmpty()) {
                    chemist.setError("Please fill this field !");
                    chemist.requestFocus();
                    return;
                }
                if (comapany.getText().toString().isEmpty()) {
                    comapany.setError("Please fill this field !");
                    comapany.requestFocus();
                    return;
                }
                if (turnover.getText().toString().isEmpty()) {
                    turnover.setError("Please fill this field !");
                    turnover.requestFocus();
                    return;
                }
                if (mnc.getText().toString().isEmpty()) {
                    mnc.setError("Please fill this field !");
                    mnc.requestFocus();
                    return;
                }
                if (diabetics.getText().toString().isEmpty()) {
                    diabetics.setError("Please fill this field !");
                    diabetics.requestFocus();
                    return;
                }

                registration();

            }
        });
    }

    private void registration() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.distributor_registration,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("dist_res",response);
                        try {
                            JSONObject ob=new JSONObject(response);
                            if (ob.getBoolean("status"))
                            {
                                Toast.makeText(DistributorsRegisteration.this, "Successfully Registered !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(DistributorsRegisteration.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("area_covered", area.getText().toString());
                params.put("name", name.getText().toString());
                params.put("address", address.getText().toString());
                params.put("total_chemist_shops", chemist.getText().toString());
                params.put("total_companies", comapany.getText().toString());
                params.put("turnover_per_month", turnover.getText().toString());
                params.put("total_mnc", mnc.getText().toString());
                params.put("diabetes_cardiac_companies", diabetics.getText().toString());
                params.put("phone", phone.getText().toString());
                Log.e("dist_params", String.valueOf(params));
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
