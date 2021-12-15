package com.ynot.relief;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ynot.relief.Webservices.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ForgotPassword extends AppCompatActivity {
    EditText mob, pass, cpass;
    Button verify;
    ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mob = findViewById(R.id.mob);
        pass = findViewById(R.id.pass);
        cpass = findViewById(R.id.cpass);
        verify = findViewById(R.id.verify);

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mob.getText().toString().length() != 10 || mob.getText().toString().isEmpty()) {
                    mob.setError("Enter valid mobile Number !!");
                    mob.requestFocus();
                    return;
                }
                if (pass.getText().toString().isEmpty()) {
                    pass.setError("Please enter a password !!");
                    pass.requestFocus();
                    return;
                }
                if (cpass.getText().toString().isEmpty() || !cpass.getText().toString().equals(pass.getText().toString())) {
                    cpass.setError("Password Miss match !!");
                    cpass.requestFocus();
                    return;
                }

                Otp_verification();
            }
        });


    }

    private void Otp_verification() {
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

                                Intent i = new Intent(getApplicationContext(), Otpverify.class);
                                i.putExtra("otp", ob.getString("otp"));
                                i.putExtra("forgot", "1");
                                i.putExtra("mob", mob.getText().toString());
                                i.putExtra("f_pass", cpass.getText().toString());
                                startActivity(i);
                                Toast.makeText(ForgotPassword.this, ob.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ForgotPassword.this, ob.getString("message"), Toast.LENGTH_SHORT).show();

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
                params.put("status","2");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest);

    }
}