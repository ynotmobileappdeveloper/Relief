package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.ynot.relief.Adapters.LocationAdpater;
import com.ynot.relief.Models.AvailableLocationModel;
import com.ynot.relief.Models.LocationModel;
import com.ynot.relief.Models.ShopsModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LocationPage extends AppCompatActivity {

    LocationAdpater adapter;
    ArrayList<ShopsModel> loc_model = new ArrayList<>();
    RecyclerView shops_rec, location_rec;
    String lat, lon;
    String name = "";
    ProgressDialog progress;
    RelativeLayout result;
    Button check;
    TextView skip,availablehead;
    String apiKey;
    String map_address = "";
    CardView locations, stores;
    ArrayList<AvailableLocationModel> location_model = new ArrayList<>();
    ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_page);
        availablehead=findViewById(R.id.availaa);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Location Change");
        progress = new ProgressDialog(LocationPage.this);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        dialog = new ACProgressFlower.Builder(LocationPage.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);

        shops_rec = findViewById(R.id.shops_rec);
        location_rec = findViewById(R.id.location_rec);
        skip = findViewById(R.id.skip);
        check = findViewById(R.id.check);
        result = findViewById(R.id.result);
        locations = findViewById(R.id.locations);
        stores = findViewById(R.id.stores);
        shops_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        shops_rec.addItemDecoration(new DividerItemDecoration(shops_rec.getContext(), DividerItemDecoration.VERTICAL));
        location_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        location_rec.addItemDecoration(new DividerItemDecoration(shops_rec.getContext(), DividerItemDecoration.VERTICAL));

        get_api();
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.equals("")) {
                    Toast.makeText(LocationPage.this, "Please select a location !!", Toast.LENGTH_LONG).show();

                } else {
                    check_location();
                }
            }
        });

    }


    private void common_method() {
        Places.initialize(getApplicationContext(), apiKey);

        final PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.sv_location);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {

                    String location = place.toString();
                    name = place.getName();
                    map_address = place.getAddress();
                    Log.e("place", location);

                    lat = String.valueOf(place.getLatLng().latitude);
                    lon = String.valueOf(place.getLatLng().longitude);
                    List<Address> addressList = null;
                    if (location != null || !location.equals("")) {
                        Geocoder geocoder = new Geocoder(LocationPage.this);
                        try {
                            addressList = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                            Address address = addressList.get(0);
                            String city = address.getLocality();
                            String district = address.getSubAdminArea();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }

                @Override
                public void onError(Status status) {
                    // txtVw.setText(status.toString());
                    Log.e("error", String.valueOf(status));
                }
            });
        }


    }

    private void get_api() {
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("api key", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                apiKey = ob.getString("api_key");
                                common_method();
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
                return params;
            }
        };
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);

    }

    private void check_location() {
        dialog.show();
        if(location_model.size()>0){
            location_model.clear();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("check", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                result.setVisibility(View.GONE);
                                locations.setVisibility(View.GONE);
                                stores.setVisibility(View.VISIBLE);
                                LocationModel location = new LocationModel(ob.getString("store_id"), ob.getString("location"), ob.getString("latitude"), ob.getString("longitude"));
                                SharedPrefManager.getInstatnce(getApplicationContext()).ShopDetails(location);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();

                            } else {
                               // result.setVisibility(View.VISIBLE);
                                stores.setVisibility(View.VISIBLE);
                               locations.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("available_stores");
                                location_model = new ArrayList<>();
                                loc_model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject o = array.getJSONObject(i);

                                    // loc_model.add(new ShopsModel("", "", o.getString("location"), "", ""));
                                    location_model.add(new AvailableLocationModel(o.getString("store_id"), o.getString("location"), o.getString("latitude"), o.getString("longitude")));
                                }
                                adapter = new LocationAdpater(getApplicationContext(), location_model, new LocationAdpater.Click() {
                                    @Override
                                    public void item_click(AvailableLocationModel model) {
                                        LocationModel location = new LocationModel(model.getStore_id(), model.getLocation(), model.getLat(), model.getLon());
                                        SharedPrefManager.getInstatnce(getApplicationContext()).ShopDetails(location);
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finishAffinity();
                                    }
                                });
                                location_rec.setAdapter(adapter);
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
                Map<String, String> param = new HashMap<>();
                param.put("loc_name", name);
                param.put("latitude", lat);
                param.put("longitude", lon);
                Log.e("params", String.valueOf(param));
                return param;
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
}