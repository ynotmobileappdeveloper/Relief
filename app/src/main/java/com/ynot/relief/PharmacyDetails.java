package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.HomeViewpagerAdapter;
import com.ynot.relief.Adapters.PharmCategory;
import com.ynot.relief.Models.HomeSlider;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class PharmacyDetails extends AppCompatActivity {
    ViewPager viewpager;
    HomeViewpagerAdapter viewpagerAdapter;
    ArrayList<HomeSlider> homeSlider = new ArrayList<>();
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 3000;
    int currentPage = 0;
    int NUM_PAGES = 3;
    Timer timer;
    RecyclerView rec;
    PharmCategory adapter;
    ArrayList<PharmCategoryModel> model = new ArrayList<>();
    ACProgressFlower dialog;
    TextView name, address, time;
    ImageView image;
    String store_id = "", phone;
    RelativeLayout whatsapp, call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Purchase Medicine");
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        time = findViewById(R.id.time);
        image = findViewById(R.id.image);

        dialog = new ACProgressFlower.Builder(PharmacyDetails.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        viewpager = findViewById(R.id.viewpager);
        whatsapp = findViewById(R.id.whatsapp);
        call = findViewById(R.id.call);
        rec = findViewById(R.id.rec);

        if (getIntent().hasExtra("id")) {
            store_id = getIntent().getStringExtra("id");
            phone = getIntent().getStringExtra("phone");
        }


        viewpagerAdapter = new HomeViewpagerAdapter(getApplicationContext(), homeSlider);
        viewpager.setAdapter(viewpagerAdapter);
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewpager.setCurrentItem(currentPage++, true);

            }
        };
        timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);

        viewpager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        viewpager.setPadding(70, 0, 70, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        viewpager.setPageMargin(20);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        get_category();
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkpermission()) {
                    String number = phone;
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                }

            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = phone; // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = getPackageManager();
                    Log.e("package", String.valueOf(PackageManager.GET_ACTIVITIES));
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    private void get_category() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.STORE_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("cat", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {

                                name.setText(ob.getString("store_name"));
                                address.setText(ob.getString("store_address"));
                                if (!ob.getString("store_time").equals("null")) {
                                    time.setText(ob.getString("store_time"));

                                } else {
                                    time.setVisibility(View.INVISIBLE);
                                }
                                JSONArray array = ob.getJSONArray("category");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    if (i != 0) {
                                        model.add(new PharmCategoryModel(obj.getString("cat_id"), obj.getString("cat_name")));
                                    }

                                }
                                adapter = new PharmCategory(getApplicationContext(), model, new PharmCategory.Click() {
                                    @Override
                                    public void ItemClick(PharmCategoryModel model) {
                                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                                            Intent i = new Intent(getApplicationContext(), ProductPage.class);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                            finishAffinity();
                                        }

                                    }
                                });
                                rec.setAdapter(adapter);

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
                params.put("store_id", store_id);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(PharmacyDetails.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(PharmacyDetails.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return false;
        } else {
            return true;
        }
    }
}