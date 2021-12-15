package com.ynot.relief;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class RegistrationPage extends AppCompatActivity implements View.OnClickListener {
    Button create;
    LinearLayout male, female, other;
    EditText name, pass, email, mob;
    String gender = "male";
    ACProgressFlower dialog;
    TextView signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        create = findViewById(R.id.create);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        other = findViewById(R.id.other);
        name = findViewById(R.id.name);
        pass = findViewById(R.id.pass);
        email = findViewById(R.id.email);
        mob = findViewById(R.id.mob);
        signin = findViewById(R.id.signin);


        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().isEmpty()) {
                    name.requestFocus();
                    name.setError("Please fill this field !!");
                    return;
                }
                if (pass.getText().toString().isEmpty()) {
                    pass.requestFocus();
                    pass.setError("Please fill this field !!");
                    return;
                }
                if (pass.getText().toString().isEmpty()) {
                    pass.requestFocus();
                    pass.setError("Please fill this field !!");
                    return;
                }
                if (mob.getText().toString().isEmpty() || mob.getText().toString().length() != 10) {
                    mob.requestFocus();
                    mob.setError("Please enter a valid mobile number !!");
                    return;
                }
                Otp_verification();


            }
        });

        male.setOnClickListener(this);
        female.setOnClickListener(this);
        other.setOnClickListener(this);
        signin.setOnClickListener(this);


    }

    private void Otp_verification() {
        dialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.OTP_VERIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("otp", response);

                            if (ob.getBoolean("status")) {

                                Intent i = new Intent(getApplicationContext(), Otpverify.class);
                                i.putExtra("name", name.getText().toString());
                                i.putExtra("mob", mob.getText().toString());
                                i.putExtra("pass", pass.getText().toString());
                                i.putExtra("email", "");
                                i.putExtra("gender", gender);
                                i.putExtra("otp", ob.getString("otp"));
                                startActivity(i);
                                Toast.makeText(RegistrationPage.this, ob.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(RegistrationPage.this, ob.getString("message"), Toast.LENGTH_SHORT).show();

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
                params.put("mob", mob.getText().toString());
                params.put("status", "1");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @Override
    public void onClick(View v) {
        if (v == male) {
            male.setBackgroundResource(R.drawable.txt_bg_enable);
            female.setBackgroundResource(R.drawable.txt_bg);
            other.setBackgroundResource(R.drawable.txt_bg);
            gender = "male";
        }
        if (v == female) {
            female.setBackgroundResource(R.drawable.txt_bg_enable);
            male.setBackgroundResource(R.drawable.txt_bg);
            other.setBackgroundResource(R.drawable.txt_bg);
            gender = "female";

        }
        if (v == other) {
            other.setBackgroundResource(R.drawable.txt_bg_enable);
            male.setBackgroundResource(R.drawable.txt_bg);
            female.setBackgroundResource(R.drawable.txt_bg);
            gender = "other";

        }
        if (v == signin) {
            startActivity(new Intent(getApplicationContext(), LoginPage.class));
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}