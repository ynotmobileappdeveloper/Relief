package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AddAddress extends AppCompatActivity implements OnMapReadyCallback {
    String apiKey;
    private GoogleMap map;
    SupportMapFragment mapFragment;
    String latitiude, longititude;
    Marker now;
    EditText edit_address, edit_landmark, pincode;
    ProgressDialog dialog;
    String google_location = "";

    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final int PERMISSION_ID = 102;
    private FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    private LocationCallback locationCallback;
    CheckBox check_address;
    String address_status = "0", add_id;
    Button save;
    TextView result;
    String set_default = "", upload = "", edit = "", type = "", note = "", store_id = "";
    boolean commom_check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        edit_address = findViewById(R.id.address);
        edit_landmark = findViewById(R.id.edit_landmark);
        check_address = findViewById(R.id.check_address);
        save = findViewById(R.id.create);
        pincode = findViewById(R.id.pincode);
        result = findViewById(R.id.result);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);

        if (getIntent().hasExtra("set_default")) {
            set_default = getIntent().getStringExtra("set_default");
        }
        if (getIntent().hasExtra("upload")) {
            upload = getIntent().getStringExtra("upload");
            note = getIntent().getStringExtra("note");
        }
        if (getIntent().hasExtra("type")) {
            type = getIntent().getStringExtra("type");
        }

        if (set_default.equals("set_default")) {
            check_address.setChecked(true);
            check_address.setEnabled(false);
            address_status = "1";
        }


        if (getIntent().hasExtra("edit")) {
            edit_address.setText(getIntent().getStringExtra("address"));
            edit_landmark.setText(getIntent().getStringExtra("location"));
            latitiude = getIntent().getStringExtra("latitude");
            longititude = getIntent().getStringExtra("logitude");
            google_location = getIntent().getStringExtra("google");
            address_status = getIntent().getStringExtra("status");
            if (address_status.equals("1")) {
                check_address.setChecked(true);
            } else {
                check_address.setChecked(false);
            }
            save.setText("Edit");
            add_id = getIntent().getStringExtra("id");
            edit = getIntent().getStringExtra("edit");
            pincode.setText(getIntent().getStringExtra("pincode"));

        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        get_api();

        //apiKey = "AIzaSyAT5JG_9ijuOfjvz9Ng9Wv1JEqRHQeELGg";
        // common_method();
        if (edit.isEmpty()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(AddAddress.this);
            createLocationRequest();
        }

        check_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    address_status = "1";
                } else {
                    address_status = "0";
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_address.getText().toString().isEmpty()) {
                    edit_address.setError("Please fill this field !!");
                    edit_address.requestFocus();
                    return;
                }
                if (edit_landmark.getText().toString().isEmpty()) {
                    edit_landmark.setError("Please fill this field !!");
                    edit_landmark.requestFocus();
                    return;
                }
                if (pincode.getText().toString().isEmpty()) {
                    pincode.setError("Please fill this field !!");
                    pincode.requestFocus();
                    return;
                }

                if (save.getText().toString().equals("Edit")) {
                    check_location();
                } else {
                    check_location();
                }

            }
        });


    }

    private void edit_address() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.EDIT_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("edit_add", response);

                            if (ob.getBoolean("status")) {
                                finish();
                                Toast.makeText(AddAddress.this, "Address Successfully Edited !!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddAddress.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("address", edit_address.getText().toString());
                params.put("landmark", edit_landmark.getText().toString());
                params.put("location", google_location);
                params.put("pincode", pincode.getText().toString());
                params.put("latitude", latitiude);
                params.put("longitude", longititude);
                params.put("address_status", address_status);
                params.put("add_id", add_id);
                params.put("store_id", store_id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("save_address", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void check_location() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("check", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                result.setVisibility(View.GONE);
                                store_id = ob.getString("store_id");
                                if (save.getText().toString().equals("Edit")) {
                                    edit_address();
                                } else {
                                    save_address();
                                }
                            } else {
                                result.setVisibility(View.GONE);
                                store_id = ob.getString("store_id");
                                if (save.getText().toString().equals("Edit")) {
                                    edit_address();
                                } else {
                                    save_address();
                                }
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
                param.put("loc_name", edit_landmark.getText().toString());
                param.put("latitude", latitiude);
                param.put("longitude", longititude);
                Log.e("check_params", String.valueOf(param));
                return param;
            }
        };
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    private void save_address() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SAVE_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                if (set_default.equals("set_default")) {
                                    if (!upload.isEmpty()) {
                                        Intent i = new Intent(getApplicationContext(), CheckoutPage.class);
                                        i.putExtra("upload", "upload");
                                        i.putExtra("note", note);
                                        startActivity(i);
                                    } else if (!type.isEmpty()) {
                                        Intent i = new Intent(getApplicationContext(), CheckoutPage.class);
                                        i.putExtra("type", type);
                                        startActivity(i);
                                    } else {
                                        startActivity(new Intent(getApplicationContext(), CheckoutPage.class));
                                    }
                                    finish();
                                    Toast.makeText(AddAddress.this, "Address Saved Successfully !!", Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), AddressPage.class));
                                    finish();
                                    Toast.makeText(AddAddress.this, "Address Saved Successfully !!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AddAddress.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("address", edit_address.getText().toString());
                params.put("landmark", edit_landmark.getText().toString());
                params.put("location", google_location);
                params.put("pincode", pincode.getText().toString());
                params.put("latitude", latitiude);
                params.put("longitude", longititude);
                params.put("address_status", address_status);
                params.put("store_id", store_id);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("save_address", String.valueOf(params));
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

    private void common_method() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        final PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.sv_location);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                commom_check = false;
                result.setVisibility(View.GONE);
                // txtVw.setText(place.getName()+place.getAddress());
                map.clear();
                //result.setVisibility(View.GONE);
               /* low.setVisibility(View.VISIBLE);
                //  map_image.setVisibility(View.VISIBLE);
                google_location.setVisibility(View.VISIBLE);
                place_add.setVisibility(View.VISIBLE);
                google_location.setText(place.getName());
                place_data = place.getName();
                place_add.setText(place.getAddress());*/
                if (place.getAddress() != null && !place.getAddress().isEmpty()) {
                    //edit_address.setText(city);
                    edit_landmark.setText(place.getAddress());
                } else {
                    edit_landmark.setText(place.getAddress());
                    //edit_landmark.setText(addresses.get(0).getSubLocality());
                }
                //  pincode.setText(place.get);
                google_location = place.getAddress();

                String location = place.toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(AddAddress.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Address address = addressList.get(0);
                    latitiude = String.valueOf(address.getLatitude());
                    longititude = String.valueOf(address.getLongitude());
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geo.getFromLocation(address.getLatitude(), address.getLongitude(), 1);
                        Address obj = addresses.get(0);


                        String add = obj.getAddressLine(0);
                        add = add + "\n" + obj.getCountryName();
                        add = add + "\n" + obj.getCountryCode();
                        add = add + "\n" + obj.getAdminArea();
                        add = add + "\n" + obj.getPostalCode();
                        add = add + "\n" + obj.getSubAdminArea();
                        add = add + "\n" + obj.getLocality();
                        add = add + "\n" + obj.getSubThoroughfare();
                        pincode.setText(obj.getPostalCode());
                        Log.e("address", add);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    map.addMarker(new MarkerOptions().position(latLng).title(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {

                            map.clear();
                            now = map.addMarker(new MarkerOptions().position(latLng));
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                            latitiude = String.valueOf(latLng.latitude);
                            longititude = String.valueOf(latLng.longitude);

                            LatLng pp = now.getPosition();
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(pp.latitude, pp.longitude, 1);
                                Address obj = addresses.get(0);

                                String add = obj.getAddressLine(0);
                                add = add + "\n" + obj.getCountryName();
                                add = add + "\n" + obj.getCountryCode();
                                add = add + "\n" + obj.getAdminArea();
                                add = add + "\n" + obj.getPostalCode();
                                add = add + "\n" + obj.getSubAdminArea();
                                add = add + "\n" + obj.getLocality();
                                add = add + "\n" + obj.getSubThoroughfare();
                                Log.e("address", add);
                                edit_landmark.setText(obj.getAddressLine(0) + "");
                                pincode.setText(obj.getPostalCode() + "");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }


            }

            @Override
            public void onError(Status status) {
                // txtVw.setText(status.toString());
                Log.e("error", String.valueOf(status));
            }
        });
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        if (edit.isEmpty()) {
            createLocationRequest();
            askLocationSettings();
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        //checkLocationAPI(location.getLatitude()+"",location.getLongitude()+"");
                        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AddAddress.this);
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // GPS location can be null if GPS is switched off
                                        if (location != null) {
                                            map.clear();
                                            //lastLocation=location;
                                            try {
                                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                                if (addresses != null && addresses.size() > 0) {
                                                    String address = addresses.get(0).getAddressLine(0);
                                                    String city = addresses.get(0).getLocality();


                                                    if (city != null && !city.isEmpty()) {
                                                        //edit_address.setText(city);
                                                        edit_landmark.setText(addresses.get(0).getAddressLine(0));
                                                    } else {
                                                        //edit_address.setText(address);
                                                        edit_landmark.setText(addresses.get(0).getAddressLine(0));
                                                    }
                                                    google_location = addresses.get(0).getAddressLine(0);
                                                    pincode.setText(addresses.get(0).getPostalCode());

                                                    latitiude = String.valueOf(location.getLatitude());
                                                    longititude = String.valueOf(location.getLongitude());
                                                    LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                                                    map.addMarker(new MarkerOptions().position(sydney).title("My Current location"));
                                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
                                                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                                        @Override
                                                        public void onMapClick(LatLng latLng) {
                                                            map.clear();
                                                            now = map.addMarker(new MarkerOptions().position(latLng));
                                                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                                            Log.e("CLick", "click");
                                                            LatLng pp = now.getPosition();
                                                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                                            try {
                                                                List<Address> addresses = geocoder.getFromLocation(pp.latitude, pp.longitude, 1);
                                                                Address obj = addresses.get(0);

                                                                String add = obj.getAddressLine(0);
                                                                add = add + "\n" + obj.getCountryName();
                                                                add = add + "\n" + obj.getCountryCode();
                                                                add = add + "\n" + obj.getAdminArea();
                                                                add = add + "\n" + obj.getPostalCode();
                                                                add = add + "\n" + obj.getSubAdminArea();
                                                                add = add + "\n" + obj.getLocality();
                                                                add = add + "\n" + obj.getSubThoroughfare();
                                                                Log.e("address", add);
                                                                edit_landmark.setText(obj.getAddressLine(0) + "");
                                                                pincode.setText(obj.getPostalCode() + "");

                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    });

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            return;
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                    }

                }
            };
        } else {
            location_set();
        }

    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void askLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(AddAddress.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(AddAddress.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLastLocation();
            }
        });

        task.addOnFailureListener(AddAddress.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(AddAddress.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void getLastLocation() {
        map.clear();
        if (checkPermissions()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(AddAddress.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                //checkLocationAPI(location.getLatitude()+"",location.getLongitude()+"");
                                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AddAddress.this);
                                mFusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                // GPS location can be null if GPS is switched off
                                                if (location != null) {
                                                    //lastLocation=location;
                                                    try {
                                                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                                        if (addresses != null && addresses.size() > 0) {
                                                            String address = addresses.get(0).getAddressLine(0);
                                                            String city = addresses.get(0).getLocality();


                                                            if (city != null && !city.isEmpty()) {
                                                                //edit_address.setText(city);
                                                                edit_landmark.setText(addresses.get(0).getAddressLine(0));
                                                            } else {
                                                                edit_landmark.setText(addresses.get(0).getAddressLine(0));
                                                                //edit_landmark.setText(addresses.get(0).getSubLocality());
                                                            }
                                                            pincode.setText(addresses.get(0).getPostalCode());
                                                            google_location = addresses.get(0).getAddressLine(0);
                                                            latitiude = String.valueOf(location.getLatitude());
                                                            longititude = String.valueOf(location.getLongitude());
                                                            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                                                            map.addMarker(new MarkerOptions().position(sydney).title("My Current location"));
                                                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
                                                            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                                                @Override
                                                                public void onMapClick(LatLng latLng) {
                                                                    map.clear();
                                                                    now = map.addMarker(new MarkerOptions().position(latLng));
                                                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                                                    Log.e("CLick", "click");
                                                                    LatLng pp = now.getPosition();
                                                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                                                    try {
                                                                        List<Address> addresses = geocoder.getFromLocation(pp.latitude, pp.longitude, 1);
                                                                        Address obj = addresses.get(0);

                                                                        String add = obj.getAddressLine(0);
                                                                        add = add + "\n" + obj.getCountryName();
                                                                        add = add + "\n" + obj.getCountryCode();
                                                                        add = add + "\n" + obj.getAdminArea();
                                                                        add = add + "\n" + obj.getPostalCode();
                                                                        add = add + "\n" + obj.getSubAdminArea();
                                                                        add = add + "\n" + obj.getLocality();
                                                                        add = add + "\n" + obj.getSubThoroughfare();
                                                                        Log.e("address", add);
                                                                        edit_landmark.setText(obj.getAddressLine(0) + "");
                                                                        pincode.setText(obj.getPostalCode() + "");

                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    return;
                                                }

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                if (commom_check) {
                                    startLocationUpdates();
                                }

                            } else {
                                if (commom_check) {
                                    startLocationUpdates();
                                }

                            }
                        }
                    });
        } else {
            requestPermissions();
        }

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void startLocationUpdates() {
        Log.e("start", "start");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                AddAddress.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
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

    private void location_set() {

        double la = Double.parseDouble(latitiude);
        double lo = Double.parseDouble(longititude);

        LatLng sydney = new LatLng(la, lo);
        map.addMarker(new MarkerOptions().position(sydney).title("Item Delivered here"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                now = map.addMarker(new MarkerOptions().position(latLng));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                Log.e("CLick", "click");
                LatLng pp = now.getPosition();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(pp.latitude, pp.longitude, 1);
                    Address obj = addresses.get(0);

                    String add = obj.getAddressLine(0);
                    add = add + "\n" + obj.getCountryName();
                    add = add + "\n" + obj.getCountryCode();
                    add = add + "\n" + obj.getAdminArea();
                    add = add + "\n" + obj.getPostalCode();
                    add = add + "\n" + obj.getSubAdminArea();
                    add = add + "\n" + obj.getLocality();
                    add = add + "\n" + obj.getSubThoroughfare();
                    Log.e("address", add);
                    edit_landmark.setText(obj.getAddressLine(0) + "");
                    pincode.setText(obj.getPostalCode() + "");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}