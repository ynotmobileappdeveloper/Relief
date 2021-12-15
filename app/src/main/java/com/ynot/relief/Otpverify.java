package com.ynot.relief;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chaos.view.PinView;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.User;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class Otpverify extends AppCompatActivity {
    TextView change_number, resend, email_txt;
    Button verify;
    String mob;
    PinView pinview;
    String name, pass, email, gender, otp_sms;
    ACProgressFlower dialog;
    String forgot = "", f_pass = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        verify = findViewById(R.id.verify);
        pinview = findViewById(R.id.pinview);
        email_txt = findViewById(R.id.email_txt);
        resend = findViewById(R.id.resend);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        if (getIntent().hasExtra("name")) {
            name = getIntent().getStringExtra("name");
        }
        if (getIntent().hasExtra("pass")) {
            pass = getIntent().getStringExtra("pass");
        }
        if (getIntent().hasExtra("email")) {
            email = getIntent().getStringExtra("email");
        }
        if (getIntent().hasExtra("gender")) {
            gender = getIntent().getStringExtra("gender");
        }
        if (getIntent().hasExtra("mob")) {
            mob = getIntent().getStringExtra("mob");
        }
        if (getIntent().hasExtra("forgot")) {
            forgot = getIntent().getStringExtra("forgot");
        }
        if (getIntent().hasExtra("f_pass")) {
            f_pass = getIntent().getStringExtra("f_pass");
        }
        otp_sms = getIntent().getStringExtra("otp");

        email_txt.setText("Enter the OTP you received to\n" + "+91 " + mob);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinview.getText().toString().isEmpty()) {
                    Toast.makeText(Otpverify.this, "Please enter the otp code !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (forgot.equals("1")) {
                    forgot_pass();
                } else {
                    registration();
                }

            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resend_otp();
            }
        });

    }

    private void forgot_pass() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.FORGOT_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("for", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                Toast.makeText(Otpverify.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                finishAffinity();

                            } else {
                                Toast.makeText(Otpverify.this, ob.getString("message"), Toast.LENGTH_SHORT).show();

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
                params.put("pass", f_pass);
                params.put("user_otp", pinview.getText().toString());
                params.put("otp", otp_sms);
                params.put("mob", mob);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void resend_otp() {
        dialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.OTP_VERIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("otp", response);

                            if (ob.getBoolean("status")) {
                                otp_sms = ob.getString("otp");
                                Toast.makeText(Otpverify.this, ob.getString("message"), Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(Otpverify.this, ob.getString("message"), Toast.LENGTH_SHORT).show();

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
                params.put("mob", mob);
                if (forgot.equals("1")) {
                    params.put("status", "2");
                } else {
                    params.put("status", "1");
                }
                Log.e("resend_otp", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void registration() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REGISTRATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                User user = new User(ob.getString("user_id"), name, mob, "");
                                SharedPrefManager.getInstatnce(getApplicationContext()).userLogin(user);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                Toast.makeText(Otpverify.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                finishAffinity();
                            } else {
                                Toast.makeText(Otpverify.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("name", name);
                params.put("password", pass);
                params.put("email", email);
                params.put("mob", mob);
                params.put("otp", otp_sms);
                params.put("user_otp", pinview.getText().toString());
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
