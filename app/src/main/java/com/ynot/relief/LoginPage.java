package com.ynot.relief;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
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

public class LoginPage extends AppCompatActivity {
    TextView login;
    TextView signupp, forgot;
    EditText username, password, cpassword;
    ACProgressFlower dialog;
    ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        login = findViewById(R.id.login);
        signupp = findViewById(R.id.signupp);
        forgot = findViewById(R.id.forgot);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        logo = findViewById(R.id.logo);

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Glide.with(getApplicationContext()).load(R.drawable.logo_front).into(logo);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (username.getText().toString().length() != 10) {
                    username.setError("Please enter a valid Mobile number");
                    username.requestFocus();
                    username.setBackground(getResources().getDrawable(R.drawable.bgbuttongreenborder));
                    password.setBackground(getResources().getDrawable(R.drawable.bggray));
                    cpassword.setBackground(getResources().getDrawable(R.drawable.bggray));
                    username.setTextColor(getResources().getColor(R.color.black));
                    password.setCompoundDrawables(getResources().getDrawable(R.drawable.pass), null, null, null);
                    cpassword.setCompoundDrawables(getResources().getDrawable(R.drawable.pass), null, null, null);
                    username.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                    password.setTextColor(getResources().getColor(R.color.unselect));
                    cpassword.setTextColor(getResources().getColor(R.color.unselect));
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    password.setError("Please enter a password !!");
                    password.requestFocus();
                    password.setBackground(getResources().getDrawable(R.drawable.bgbuttongreenborder));
                    username.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                    cpassword.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                    password.setCompoundDrawables(getResources().getDrawable(R.drawable.pass), null, null, null);
                    username.setBackground(getResources().getDrawable(R.drawable.bggray));
                    cpassword.setBackground(getResources().getDrawable(R.drawable.bggray));
                    password.setTextColor(getResources().getColor(R.color.black));
                    username.setTextColor(getResources().getColor(R.color.unselect));
                    cpassword.setTextColor(getResources().getColor(R.color.unselect));
                    return;
                }
                if (!password.getText().toString().equals(cpassword.getText().toString())) {
                    cpassword.setError("Password Mismatch !");
                    cpassword.requestFocus();
                    cpassword.setBackground(getResources().getDrawable(R.drawable.bgbuttongreenborder));
                    username.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                    password.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                    cpassword.setCompoundDrawables(getResources().getDrawable(R.drawable.pass), null, null, null);
                    username.setBackground(getResources().getDrawable(R.drawable.bggray));
                    password.setBackground(getResources().getDrawable(R.drawable.bggray));
                    cpassword.setTextColor(getResources().getColor(R.color.black));
                    username.setTextColor(getResources().getColor(R.color.unselect));
                    password.setTextColor(getResources().getColor(R.color.unselect));
                    return;
                }
                check_login();
            }
        });
        signupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationPage.class));
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
        });
        username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                username.setBackground(getResources().getDrawable(R.drawable.bgbuttongreenborder));
                password.setBackground(getResources().getDrawable(R.drawable.bggray));
                cpassword.setBackground(getResources().getDrawable(R.drawable.bggray));
                username.setTextColor(getResources().getColor(R.color.black));
                password.setCompoundDrawables(getResources().getDrawable(R.drawable.pass), null, null, null);
                cpassword.setCompoundDrawables(getResources().getDrawable(R.drawable.pass), null, null, null);
                username.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                password.setTextColor(getResources().getColor(R.color.unselect));
                cpassword.setTextColor(getResources().getColor(R.color.unselect));
                return false;
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                password.setBackground(getResources().getDrawable(R.drawable.bgbuttongreenborder));
                username.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                cpassword.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                password.setCompoundDrawables(getResources().getDrawable(R.drawable.pass), null, null, null);
                username.setBackground(getResources().getDrawable(R.drawable.bggray));
                cpassword.setBackground(getResources().getDrawable(R.drawable.bggray));
                password.setTextColor(getResources().getColor(R.color.black));
                username.setTextColor(getResources().getColor(R.color.unselect));
                cpassword.setTextColor(getResources().getColor(R.color.unselect));
                return false;
            }
        });
        cpassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cpassword.setBackground(getResources().getDrawable(R.drawable.bgbuttongreenborder));
                username.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                password.setCompoundDrawables(getResources().getDrawable(R.drawable.usre), null, null, null);
                cpassword.setCompoundDrawables(getResources().getDrawable(R.drawable.pass), null, null, null);
                username.setBackground(getResources().getDrawable(R.drawable.bggray));
                password.setBackground(getResources().getDrawable(R.drawable.bggray));
                cpassword.setTextColor(getResources().getColor(R.color.black));
                username.setTextColor(getResources().getColor(R.color.unselect));
                password.setTextColor(getResources().getColor(R.color.unselect));
                return false;
            }
        });

    }

    private void check_login() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("user", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                User user = new User(ob.getString("user_id"), ob.getString("user_name"), ob.getString("mob"), "");
                                SharedPrefManager.getInstatnce(getApplicationContext()).userLogin(user);
                                Toast.makeText(LoginPage.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(LoginPage.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("error", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mob", username.getText().toString());
                params.put("pass", password.getText().toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
