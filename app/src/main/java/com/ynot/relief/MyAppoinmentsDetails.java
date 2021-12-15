package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.ynot.relief.Models.MyAppoinmentsModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class MyAppoinmentsDetails extends AppCompatActivity {

    MyAppoinmentsModel model;
    TextView drname, department, name, gender, age, location, address, phone, date, time, status;
    ImageView call;
    Button cancel;
    String mob = "";
    ACProgressFlower dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appoinments_details);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drname = findViewById(R.id.drname);
        department = findViewById(R.id.department);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        location = findViewById(R.id.location);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        status = findViewById(R.id.status);
        call = findViewById(R.id.call);
        cancel = findViewById(R.id.cancel);


        model = (MyAppoinmentsModel) getIntent().getExtras().getSerializable("model");

        drname.setText("Dr. " + model.getDoc_name());
        name.setText(model.getName());
        department.setText(model.getDepartment());
        age.setText(model.getAge());
        gender.setText(model.getGender());
        address.setText(model.getDoc_address());
        phone.setText(model.getDoc_phone());
        date.setText(model.getDate());
        time.setText(model.getTime());
        mob = model.getDoc_phone();
        location.setText(model.getOp_type()+" OP");
        getSupportActionBar().setTitle(model.getName());

        if (model.getOp_status().equals("0")) {
            cancel.setVisibility(View.VISIBLE);
        } else {
            cancel.setVisibility(View.GONE);
        }
        if (model.getOp_status().equals("1")) {
            status.setText("Approved");
        } else if (model.getOp_status().equals("2")) {
            status.setText("Rejected");
        } else {
            status.setText("Pending");
        }


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mob.isEmpty()) {
                    if (checkpermission()) {
                        String number = mob;
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + number));
                        startActivity(callIntent);
                    }

                } else {
                    Toast.makeText(MyAppoinmentsDetails.this, "No Mobile number Found !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MyAppoinmentsDetails.this);
                alert.setTitle("Confirmation !!");
                alert.setIcon(R.drawable.ic_stop);
                alert.setMessage("Do you want cancel this appointment ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancel_appoinment();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });


    }

    private void cancel_appoinment() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CANCEL_APPOINMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(MyAppoinmentsDetails.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(MyAppoinmentsDetails.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("id", model.getId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MyAppoinmentsDetails.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return false;
        } else {
            return true;
        }
    }
}