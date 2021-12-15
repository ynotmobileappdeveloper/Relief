package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class NurseBooking extends AppCompatActivity implements View.OnClickListener {
    String id;
    String gender = "male";
    TextInputEditText name, age, phone, address;
    TextView male, female;
    Button book;
    ACProgressFlower dialog;
    String type = "", data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_booking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Patient Details");
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        if (getIntent().hasExtra("data")) {
            data = getIntent().getStringExtra("data");
            if (data.equals("Services")) {
                type = "1";
            } else {
                type = "2";
            }
        }


        Log.e("type", type);

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        book = findViewById(R.id.book);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        male.setOnClickListener(this);
        female.setOnClickListener(this);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("Fill this field !!");
                    name.requestFocus();
                    return;
                }
                if (age.getText().toString().isEmpty()) {
                    age.setError("Fill this field !!");
                    age.requestFocus();
                    return;
                }
                if (phone.getText().toString().length() != 10) {
                    phone.setError("Enter a valid Phone number !!");
                    phone.requestFocus();
                    return;
                }
                if (address.getText().toString().isEmpty()) {
                    address.setError("Fill this field !!");
                    address.requestFocus();
                    return;
                }
                book_now();

            }
        });


    }

    private void book_now() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SERVICES_BOOKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(NurseBooking.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(NurseBooking.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("name", name.getText().toString());
                params.put("gender", gender);
                params.put("age", age.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("address", address.getText().toString());
                params.put("id", id);
                params.put("type", type);
                Log.e("params", String.valueOf(params));
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

    @Override
    public void onClick(View v) {
        if (v == male) {
            male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_enable, 0);
            female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_disable, 0);
            gender = "male";
        }
        if (v == female) {
            female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_enable, 0);
            male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_disable, 0);
            gender = "female";
        }

    }
}