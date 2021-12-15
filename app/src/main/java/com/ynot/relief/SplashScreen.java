package com.ynot.relief;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {
    ImageView image;
    int appversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        /*FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }*/
        image = findViewById(R.id.image);
        image.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
        Glide.with(getApplicationContext()).load(R.drawable.logo_front).into(image);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;   //version name
            appversion = pInfo.versionCode;      //version code
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //noinspection deprecation


    }

    private void check_updation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("check_update", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                if (ob.has("version")) {
                                    if (appversion < ob.getInt("version")) {
                                        AlertDialog.Builder alert = new AlertDialog.Builder(SplashScreen.this);
                                        alert.setMessage("Please update My Relief to get new features! ");
                                        alert.setTitle("New update available!");
                                        alert.setCancelable(false);
                                        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ynot.relief"));
                                                startActivity(intent);
                                            }
                                        });
                                        alert.show();
                                    } else {
                                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finishAffinity();
                                        } else {
                                            SharedPreferences prefs = getSharedPreferences("slider", MODE_PRIVATE);
                                            String name = prefs.getString("slider", "");//"No name defined" is the default value.
                                            if (!name.isEmpty()) {
                                                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                                finishAffinity();
                                            } else {
                                                startActivity(new Intent(getApplicationContext(), SliderPage.class));
                                                finish();
                                            }
                                        }
                                    }
                                } else {
                                    if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finishAffinity();
                                    } else {
                                        SharedPreferences prefs = getSharedPreferences("slider", MODE_PRIVATE);
                                        String name = prefs.getString("slider", "");//"No name defined" is the default value.
                                        if (!name.isEmpty()) {
                                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                            finishAffinity();
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), SliderPage.class));
                                            finish();
                                        }
                                    }
                                }

                            } else {
                                if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finishAffinity();
                                } else {
                                    SharedPreferences prefs = getSharedPreferences("slider", MODE_PRIVATE);
                                    String name = prefs.getString("slider", "");//"No name defined" is the default value.
                                    if (!name.isEmpty()) {
                                        startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                        finishAffinity();
                                    } else {
                                        startActivity(new Intent(getApplicationContext(), SliderPage.class));
                                        finish();
                                    }
                                }
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
                params.put("version_code", String.valueOf(appversion));
                Log.e("params", String.valueOf(params));
                return super.getParams();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                check_updation();

              /*  if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                } else {
                    SharedPreferences prefs = getSharedPreferences("slider", MODE_PRIVATE);
                    String name = prefs.getString("slider", "");//"No name defined" is the default value.
                    if (!name.isEmpty()) {
                        startActivity(new Intent(getApplicationContext(), LoginPage.class));
                        finishAffinity();
                    } else {
                        startActivity(new Intent(getApplicationContext(), SliderPage.class));
                        finish();
                    }
                }*/


            }
        }, 3000);
    }
}
