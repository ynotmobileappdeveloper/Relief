package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class DocDetails extends AppCompatActivity {
    Button book;
    String doc_id = "";
    ACProgressFlower progress;
    ImageView image, nodata;
    TextView name, qualification, about, details, patients, exp, hospital_op, personal_op, clinic_op;
    RatingBar rating;
    NestedScrollView scroll;
    LinearLayout hosp_layout, person_layout, clinic_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_details);
        progress = new ACProgressFlower.Builder(DocDetails.this)
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
        getSupportActionBar().setTitle("Doctor Details");
        book = findViewById(R.id.book);
        scroll = findViewById(R.id.scroll);
        image = findViewById(R.id.image);
        nodata = findViewById(R.id.nodata);
        name = findViewById(R.id.name);
        qualification = findViewById(R.id.qualification);
        about = findViewById(R.id.about);
        details = findViewById(R.id.details);
        patients = findViewById(R.id.patients);
        exp = findViewById(R.id.exp);
        hospital_op = findViewById(R.id.hospital_op);
        personal_op = findViewById(R.id.personal_op);
        rating = findViewById(R.id.rating);
        hosp_layout = findViewById(R.id.hosp_layout);
        person_layout = findViewById(R.id.personal_layout);
        clinic_op = findViewById(R.id.clinic_op);
        clinic_layout = findViewById(R.id.clinic_layout);

        doc_id = getIntent().getStringExtra("id");

        get_details();


        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AppointmentBooking.class);
                i.putExtra("id", doc_id);
                startActivity(i);
            }
        });
    }

    private void get_details() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.DOC_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.e("details", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                scroll.setVisibility(View.VISIBLE);

                                name.setText(ob.getString("name"));
                                Glide.with(getApplicationContext()).load(ob.getString("image")).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        nodata.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        nodata.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(image);

                                if (!ob.getString("about").isEmpty()) {
                                    about.setVisibility(View.VISIBLE);
                                    details.setVisibility(View.VISIBLE);
                                    about.setText("About Dr." + ob.getString("name"));
                                    details.setText(ob.getString("about"));
                                }
                                rating.setRating(Float.parseFloat(ob.getString("rating")));
                                qualification.setText(ob.getString("qualification"));
                                patients.setText(ob.getString("patinets_no"));
                                exp.setText(ob.getString("experience"));
                                if (!ob.getString("hospital_optime").isEmpty()) {
                                    hosp_layout.setVisibility(View.VISIBLE);
                                    hospital_op.setText(ob.getString("hospital_optime"));
                                }
                                if (!ob.getString("personal_optime").isEmpty()) {
                                    person_layout.setVisibility(View.VISIBLE);
                                    personal_op.setText(ob.getString("personal_optime"));
                                }
                                if (!ob.getString("clinic_optime").isEmpty()) {
                                    clinic_layout.setVisibility(View.VISIBLE);
                                    clinic_op.setText(ob.getString("clinic_optime"));
                                }

                            } else {
                                scroll.setVisibility(View.GONE);
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
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                params.put("doc_id", doc_id);
                Log.e("doc_params", String.valueOf(params));
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
}