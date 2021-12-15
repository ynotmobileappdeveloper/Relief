package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.User;
import com.ynot.relief.Webservices.VolleyMultipartRequest;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener {
    TextView male, female;
    ImageView image;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    Bitmap BOD, bod_c;
    String bod_n;
    Button save;
    ACProgressFlower dialog;
    TextInputEditText name, age, blood, height, weight, sugar, pressure, cho;
    String gender = "male";
    NestedScrollView layout;
    CheckBox sugar_check, pressure_check, cho_check;
    String sugar_status = "0", pressure_status = "0", cho_status = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        image = findViewById(R.id.image);
        save = findViewById(R.id.save);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        blood = findViewById(R.id.blood);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        sugar = findViewById(R.id.sugar);
        pressure = findViewById(R.id.pressure);
        cho = findViewById(R.id.cho);
        layout = findViewById(R.id.layout);
        sugar_check = findViewById(R.id.sugar_check);
        pressure_check = findViewById(R.id.pressure_check);
        cho_check = findViewById(R.id.cho_check);


        male.setOnClickListener(this);
        female.setOnClickListener(this);

        sugar_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sugar.setEnabled(true);
                    sugar_status = "1";
                } else {
                    sugar.setEnabled(false);
                    sugar_status = "0";
                    sugar.setText("");
                }
            }
        });
        pressure_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pressure.setEnabled(true);
                    pressure_status = "1";
                } else {
                    pressure.setEnabled(false);
                    pressure_status = "0";
                    pressure.setText("");
                }
            }
        });
        cho_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cho.setEnabled(true);
                    cho_status = "1";
                } else {
                    cho.setEnabled(false);
                    cho_status = "0";
                    cho.setText("");
                }
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
               /* Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //we will handle the returned data in onActivityResult
                startActivityForResult(captureIntent, CAMERA_CAPTURE);*/

                    if (ContextCompat.checkSelfPermission(ProfilePage.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ProfilePage.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    } else {
                        captures();
                    }

                } catch (ActivityNotFoundException anfe) {
                    Toast toast = Toast.makeText(ProfilePage.this, "This device doesn't support the crop action!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        get_profile();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().isEmpty()) {
                    name.setError("Please fill this field !!");
                    name.requestFocus();
                    return;
                }
                if (age.getText().toString().isEmpty()) {
                    age.setError("Please fill this field !!");
                    age.requestFocus();
                    return;
                }
                if (blood.getText().toString().isEmpty()) {
                    blood.setError("Please fill this field !!");
                    blood.requestFocus();
                    return;
                }
                if (height.getText().toString().isEmpty()) {
                    height.setError("Please fill this field !!");
                    height.requestFocus();
                    return;
                }
                if (weight.getText().toString().isEmpty()) {
                    weight.setError("Please fill this field !!");
                    weight.requestFocus();
                    return;
                }
                if (!sugar_status.equals("0")) {
                    if (sugar.getText().toString().isEmpty()) {
                        sugar.setError("Please fill this field !!");
                        sugar.requestFocus();
                        return;
                    }
                }
                if (!pressure_status.equals("0")) {
                    if (pressure.getText().toString().isEmpty()) {
                        pressure.setError("Please fill this field !!");
                        pressure.requestFocus();
                        return;
                    }
                }
                if (!cho_status.equals("0")) {
                    if (cho.getText().toString().isEmpty()) {
                        cho.setError("Please fill this field !!");
                        cho.requestFocus();
                        return;
                    }
                }
                upload_data();
            }
        });


    }

    private void upload_data() {
        dialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLs.UPLOAD_DATA,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        dialog.dismiss();
                        Log.e("res", String.valueOf(response));
                        try {
                            JSONObject ob = new JSONObject(new String(response.data));
                            Log.e("res", String.valueOf(ob));
                            if (ob.getBoolean("status")) {
                                Toast.makeText(ProfilePage.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ProfilePage.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("name", name.getText().toString());
                params.put("gender", gender);
                params.put("age", age.getText().toString());
                params.put("height", height.getText().toString());
                params.put("blood", blood.getText().toString());
                params.put("weight", weight.getText().toString());
                params.put("sugar", sugar.getText().toString());
                params.put("pressure", pressure.getText().toString());
                params.put("cholesterol", cho.getText().toString());
                params.put("pressure_status", pressure_status);
                params.put("sugar_status", sugar_status);
                params.put("cholesterol_status", cho_status);
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                Log.e("params", String.valueOf(params));

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                if (bod_c != null) {
                    params.put("image", new DataPart(bod_n, getFileDataFromDrawable(bod_c)));
                }
                Log.e("image", String.valueOf(params));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(40 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);


    }

    @Override
    public void onClick(View v) {
        if (v == male) {
            male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_enable, 0);
            female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_disable, 0);
            gender = "male";
        }
        if (v == female) {
            female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_enable, 0);
            male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_disable, 0);
            gender = "female";
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void captures() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captures();
                } else {
                    // Permission Denied
                    Toast.makeText(ProfilePage.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                image.setImageURI(resultUri);
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
                    BOD = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    if (BOD != null) {
                        bod_c = getResizedBitmap(BOD, 500);
                        bod_n = "IMG_" + timeStamp + ".jpg";

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {


        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }


        return Bitmap.createScaledBitmap(image, width, height, true);


    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();

    }

    private void get_profile() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("get_profile",response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                String id = SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId();
                                String mob = SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getMob();
                                String email = SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getMob();
                                User user = new User(id, ob.getString("name"), mob, email);
                                SharedPrefManager.getInstatnce(getApplicationContext()).userLogin(user);

                                name.setText(ob.getString("name"));
                                if (!ob.getString("image").isEmpty()) {
                                    Glide.with(getApplicationContext()).load(ob.getString("image")).into(image);
                                }

                                if (ob.getString("gender").equals("male")) {
                                    female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_disable, 0);
                                    male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_enable, 0);
                                    age.setText(ob.getString("age"));
                                } else {
                                    female.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_enable, 0);
                                    male.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gender_disable, 0);
                                    age.setText(ob.getString("age"));
                                }

                                if (ob.getString("pressure_status").equals("1")) {
                                    pressure.setEnabled(true);
                                    pressure_status = "1";
                                    pressure_check.setChecked(true);
                                } else {
                                    pressure.setEnabled(false);
                                    pressure_status = "0";
                                    pressure_check.setChecked(false);
                                }
                                if (ob.getString("sugar_status").equals("1")) {
                                    sugar.setEnabled(true);
                                    sugar_status = "1";
                                    sugar_check.setChecked(true);
                                } else {
                                    sugar.setEnabled(false);
                                    sugar_status = "0";
                                    sugar_check.setChecked(false);
                                }
                                if (ob.getString("cholesterol_status").equals("1")) {
                                    cho.setEnabled(true);
                                    cho_status = "1";
                                    cho_check.setChecked(true);
                                } else {
                                    cho.setEnabled(false);
                                    cho_status = "0";
                                    cho_check.setChecked(false);
                                }


                                height.setText(ob.getString("height"));
                                weight.setText(ob.getString("weight"));
                                blood.setText(ob.getString("blood"));
                                sugar.setText(ob.getString("sugar"));
                                pressure.setText(ob.getString("pressure"));
                                cho.setText(ob.getString("cholesterol"));

                            } else {
                                layout.setVisibility(View.GONE);
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
                params.put("user_id", SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getId());
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

}