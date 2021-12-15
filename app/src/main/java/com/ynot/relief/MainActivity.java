package com.ynot.relief;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.ynot.relief.CartLayout.NotificationCountSetClass;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    AHBottomNavigation bottomNavigation;
    DrawerLayout drawer;
    NavigationView navigationView;

    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final int PERMISSION_ID = 102;
    private FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    private LocationCallback locationCallback;
    TextView location_txt;
    int cart_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        location_txt = findViewById(R.id.location);

        location_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LocationPage.class));
            }
        });
        createLocationRequest();
        if (SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getName() != null) {
            location_txt.setText(SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getName());
        } else {
            askLocationSettings();
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    //checkLocationAPI(location.getLatitude()+"",location.getLongitude()+"");
                    Log.e("lat_long_for", String.valueOf(location.getLatitude()));
                    Log.e("lat_long_for", String.valueOf(location.getLongitude()));


                    FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
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
                                                String district = addresses.get(0).getSubAdminArea();
                                                String state = addresses.get(0).getAdminArea();
                                                Log.e("city", city);
                                                SharedPreferences.Editor editor = getSharedPreferences("location", MODE_PRIVATE).edit();
                                                editor.putString("latitude", String.valueOf(location.getLatitude()));
                                                editor.putString("longitude", String.valueOf(location.getLongitude()));
                                                editor.putString("city", city);
                                                editor.apply();
                                                location_txt.setText(city + "," + district + "," + state);

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return;
                                    } else {
                                        startLocationUpdates();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
                                    e.printStackTrace();
                                }
                            });


                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        };


        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.ic_home);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Profile", R.drawable.ic_profile);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Cart", R.drawable.ic_cart);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Notifications", R.drawable.ic_notification);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Search", R.drawable.ic_search);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FFFFFF"));
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setAccentColor(Color.parseColor("#EDAD10"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        bottomNavigation.setForceTint(true);
        bottomNavigation.setColored(false);
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.doctor_reg, R.id.upolad, R.id.pharm_reg, R.id.disc_reg, R.id.lab_reg, R.id.nav_reg, R.id.nav_pharmacy, R.id.nav_doctors, R.id.cat_frag, R.id.cart_frag, R.id.profile, R.id.search_frag, R.id.notification)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.logout:
                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                            SharedPrefManager.getInstatnce(getApplicationContext()).logout();
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                    case R.id.nav_reg:
                        navController.navigate(R.id.nav_reg);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_home:
                        navController.navigate(R.id.nav_home);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.address:
                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), AddressPage.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                    case R.id.appointments:
                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), MyAppoinments.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                    case R.id.orders:
                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), MyOrders.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                    case R.id.documents:
                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), MyPrescription.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                    case R.id.call:
                        if (checkpermission()) {
                            String number = "8592808886";
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + number));
                            startActivity(callIntent);
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                    case R.id.whatsapp:
                        openWhatsApp();
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position == 0) {
                    navController.navigate(R.id.nav_home);
                }
                if (position == 1) {
                    if (SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId() != null) {
                        navController.navigate(R.id.profile);
                    } else {
                        startActivity(new Intent(getApplicationContext(), LoginPage.class));
                    }

                }
                if (position == 2) {
                    if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                        navController.navigate(R.id.cart_frag);
                    } else {
                        startActivity(new Intent(getApplicationContext(), LoginPage.class));
                    }

                }
                if (position == 3) {
                    navController.navigate(R.id.notification);
                }
                if (position == 4) {
                    navController.navigate(R.id.search_frag);
                }


                return true;
            }
        });

        if (getIntent().hasExtra("cart")) {
            navController.navigate(R.id.cart_frag);
            bottomNavigation.setCurrentItem(2);
        }

    }

    private void set_user_data() {
        View username = navigationView.getHeaderView(0);
        TextView user = username.findViewById(R.id.username);
        if (SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId() != null) {
            user.setText(SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getName());
        }

        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
            Menu login = navigationView.getMenu();
            login.findItem(R.id.logout).setTitle("Logout").setIcon(R.drawable.ic_logout);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
       /* if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();


            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }


        }*/
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
        SettingsClient client = LocationServices.getSettingsClient(MainActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e("Location", "ON");
                getLastLocation();
            }
        });

        task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Location", "OFF");
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void getLastLocation() {

        if (checkPermissions()) {
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
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                //checkLocationAPI(location.getLatitude()+"",location.getLongitude()+"");
                               /* Log.e("lat_long_for", String.valueOf(location.getLatitude()));
                                Log.e("lat_long_for", String.valueOf(location.getLongitude()));
                                Log.e("Last Location", "Not Available");*/


                                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
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
                                                            String district = addresses.get(0).getSubAdminArea();
                                                            String state = addresses.get(0).getAdminArea();
                                                            Log.e("city", city);
                                                            SharedPreferences.Editor editor = getSharedPreferences("location", MODE_PRIVATE).edit();
                                                            editor.putString("latitude", String.valueOf(location.getLatitude()));
                                                            editor.putString("longitude", String.valueOf(location.getLongitude()));
                                                            editor.putString("city", city);
                                                            editor.apply();

                                                            location_txt.setText(city + "," + district + "," + state);

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
                                                Log.d("MapDemoActivity", "Error trying to get last GPS location");
                                                e.printStackTrace();
                                            }
                                        });


                                startLocationUpdates();
                            } else {
                                startLocationUpdates();
                            }
                        }
                    });
        } else {
            requestPermissions();
        }

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void startLocationUpdates() {
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
                MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE},
                PERMISSION_ID
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                Log.e("Location", "ON");
                getLastLocation();
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        set_user_data();
        get_cart_count();
        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
            get_cart_count();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_cart);
        NotificationCountSetClass.setAddToCart(MainActivity.this, item, cart_count);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cart:
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void get_cart_count() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CART_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {

                                cart_count = Integer.parseInt(ob.getString("cart_count"));
                                invalidateOptionsMenu();

                            } else {
                                cart_count = 0;
                                invalidateOptionsMenu();
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
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return false;
        } else {
            return true;
        }
    }

    private void openWhatsApp() {
        String smsNumber = "918078223344"; //without '+'
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Whatsapp not installed!", Toast.LENGTH_SHORT).show();
        }
    }
}
