package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ynot.relief.Webservices.RealPathUtil;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.ui.Upload.StoreModel;
import com.ynot.relief.ui.Upload.UploadFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class UploadActivity extends AppCompatActivity {

    Bitmap BCF, BCB, IMG1, IMG2, BOD, bcf_c, bcb_c, img1_c, img2_c, bod_c;
    String bcf_n, bcb_n, img1_n, img2_n, bod_n;
    int maxWidth = 200, maxHeight = 200;
    ImageView image;
    int flag = 0;
    public static Bitmap profile_bitmap;
    String currentPhotoPath;
    Uri profile_uri;
    Activity activity;
    Button upload;
    ArrayList<StoreModel> model = new ArrayList<>();
    List<String> store_name = new ArrayList<>();
    ACProgressFlower dialog;
    Spinner stores;
    String store_id = "";
    ImageView cam;
    String latitude = "", longitude = "", city = "";
    TextView choose;
    String filePath = "";
    public static final int PERMISSION_READ = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    public static final int PROFILE_CAMERA = 1;
    public static final int PROFILE_GALLERY = 2;
    Dialog dialog_camera;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;

    public static final int CAMERA = 1;
    public static final int GALLERY = 2;
    EditText note;
    ImageView gallery, camera, close;
    AppCompatActivity activity2;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Prescriptions");
        image = findViewById(R.id.image);
        upload = findViewById(R.id.upload);
        cam = findViewById(R.id.cam);
        note = findViewById(R.id.note);
        dialog = new ACProgressFlower.Builder(UploadActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCanceledOnTouchOutside(false);


        View modalbottomsheet = getLayoutInflater().inflate(R.layout.bottom_sheet_sample, null);
        dialog_camera = new Dialog(this);
        dialog_camera.setContentView(modalbottomsheet);
        dialog_camera.setTitle("Choose item");
        dialog_camera.setCanceledOnTouchOutside(false);
        dialog_camera.setCancelable(false);
        camera = dialog_camera.findViewById(R.id.camera);
        choose = dialog_camera.findViewById(R.id.choose);

        close = dialog_camera.findViewById(R.id.close);
        gallery = dialog_camera.findViewById(R.id.gallery);
        Window window = dialog_camera.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_camera.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (getIntent().hasExtra("crop")) {
            Glide.with(getApplicationContext()).load(ImageCropper.croppedBitmap).into(image);
        }
        if (getIntent().hasExtra("cancel")) {
            Glide.with(getApplicationContext()).load(profile_bitmap).into(image);
        }

        if (SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getName() != null) {
            latitude = SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getLat();
            longitude = SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getLon();
            city = SharedPrefManager.getInstatnce(getApplicationContext()).getshop().getLon();

        } else {
            SharedPreferences preferences = getSharedPreferences("location", Context.MODE_PRIVATE);
            latitude = preferences.getString("latitude", "");
            longitude = preferences.getString("longitude", "");
            city = preferences.getString("city", "");
        }


        // get_stores();

        get_store_location_based();


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile_bitmap == null) {
                    Toast.makeText(getApplicationContext(), "Please upload your document", Toast.LENGTH_SHORT).show();
                    return;
                }

                image.buildDrawingCache();
                Bitmap bm = image.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedBitmap = Base64.encodeToString(b, Base64.DEFAULT);

                SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = shre.edit();
                edit.putString("image_data", encodedBitmap);
                edit.putString("store_id", store_id);
                edit.commit();

                Intent i = new Intent(getApplicationContext(), CheckoutPage.class);
                i.putExtra("upload", "upload");
                i.putExtra("note", note.getText().toString());
                startActivity(i);
                finish();

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_camera.dismiss();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(PROFILE_CAMERA, PROFILE_GALLERY);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        filePath = image.getAbsolutePath();
        return image;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_GALLERY) {

            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                profile_uri = imageUri;

                try {
                    profile_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profile_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File file = savebitmap(profile_bitmap);
                Log.e("file", String.valueOf(file));

                if (profile_bitmap == null) {
                    Toast.makeText(this, "Please retake Picture again !!", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getApplicationContext(), ImageCropper.class));
                    finish();
                }

            }
        }
        if (requestCode == PROFILE_CAMERA) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            profile_bitmap = BitmapFactory.decodeFile(filePath, options);
            Log.e("file_path", filePath);
            startActivity(new Intent(getApplicationContext(), ImageCropper.class));
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
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

    private void get_store_location_based() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                store_id = ob.getString("store_id");
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
                params.put("latitude", latitude);
                params.put("longitude", longitude);

                // params.put("loc_name",city);
                Log.e("params_location", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);


    }

    private void checkPermission(final int profileCamera, final int profileGallery) {

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(UploadActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            dialog_camera.show();
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, profileCamera);*/
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {

                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(UploadActivity.this,
                                    "com.ynot.relief.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, profileCamera);


                        }
                    }
                    dialog_camera.dismiss();
                }
            });
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent logoIntent = new Intent();
                    logoIntent.setType("image/*");
                    logoIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(logoIntent, "Select Picture"), profileGallery);
                    dialog_camera.dismiss();
                }
            });


        }
    }


    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        Log.e("ext", extStorageDirectory);
        // String temp = null;
        File file = new File(extStorageDirectory, "temp.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp.png");

            Log.e("file", file.getPath());

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }
}