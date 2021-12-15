package com.ynot.relief.ui.Upload;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import com.ynot.relief.CheckoutPage;
import com.ynot.relief.MainActivity;
import com.ynot.relief.R;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static android.app.Activity.RESULT_OK;


public class UploadFragment extends Fragment {
    Bitmap BCF, BCB, IMG1, IMG2, BOD, bcf_c, bcb_c, img1_c, img2_c, bod_c;
    String bcf_n, bcb_n, img1_n, img2_n, bod_n;
    int maxWidth = 200, maxHeight = 200;
    ImageView image;
    int flag = 0;
    Bitmap bitmap;
    String currentPhotoPath;
    Uri image_uri;
    Activity activity;
    Button upload;
    ArrayList<StoreModel> model = new ArrayList<>();
    List<String> store_name = new ArrayList<>();
    ACProgressFlower dialog;
    Spinner stores;
    String store_id = "";
    ImageView cam;
    String latitude = "", longitude = "", city = "";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;

    public static final int CAMERA = 1;
    public static final int GALLERY = 2;
    EditText note;
    BottomSheetDialog dia;
    ImageView gallery, camera, close;
    AppCompatActivity activity2;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        activity2 = (AppCompatActivity) view.getContext();

        image = view.findViewById(R.id.image);
        upload = view.findViewById(R.id.upload);
        stores = view.findViewById(R.id.stores);
        cam = view.findViewById(R.id.cam);
        note = view.findViewById(R.id.note);
        dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCanceledOnTouchOutside(false);
        if (SharedPrefManager.getInstatnce(getContext()).getshop().getName() != null) {
            latitude = SharedPrefManager.getInstatnce(getContext()).getshop().getLat();
            longitude = SharedPrefManager.getInstatnce(getContext()).getshop().getLon();
            city = SharedPrefManager.getInstatnce(getContext()).getshop().getLon();

        } else {
            SharedPreferences preferences = getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
            latitude = preferences.getString("latitude", "");
            longitude = preferences.getString("longitude", "");
            city = preferences.getString("city", "");
        }


        View modalbottomsheet = getLayoutInflater().inflate(R.layout.bottom_sheet_sample, null);

        dia = new BottomSheetDialog(activity);
        dia.setContentView(modalbottomsheet);
        dia.setTitle("Choose item");
        dia.setCanceledOnTouchOutside(false);
        dia.setCancelable(false);
        camera = dia.findViewById(R.id.camera);
        close = dia.findViewById(R.id.close);
        gallery = dia.findViewById(R.id.gallery);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });


        // get_stores();

        get_store_location_based();

        stores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(android.R.id.text1);
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (store_id.isEmpty()) {
                   /* TextView error = (TextView) stores.getSelectedView();
                    error.setError("");
                    error.setTextColor(Color.RED);
                    error.setText("Please choose Store !");*/
                    Toast.makeText(getContext(), "No Store Found !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (img1_c == null) {
                    Toast.makeText(getContext(), "Please upload your document", Toast.LENGTH_SHORT).show();
                    return;
                }

                image.buildDrawingCache();
                Bitmap bm = image.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedBitmap = Base64.encodeToString(b, Base64.DEFAULT);

                SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = shre.edit();
                edit.putString("image_data", encodedBitmap);
                edit.putString("store_id", store_id);
                edit.commit();

                Intent i = new Intent(getContext(), CheckoutPage.class);
                i.putExtra("upload", "upload");
                i.putExtra("note", note.getText().toString());
                startActivity(i);
                getActivity().finish();

            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkPermission(CAMERA, GALLERY);
                flag = 3;

                try {
               /* Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //we will handle the returned data in onActivityResult
                startActivityForResult(captureIntent, CAMERA_CAPTURE);*/

                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    } else {
                        captures();
                    }

                } catch (ActivityNotFoundException anfe) {
                    Toast toast = Toast.makeText(getContext(), "This device doesn't support the crop action!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        return view;
    }

    private void captures() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), UploadFragment.this);


//        Intent intent = CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .getIntent(getContext());
//
//        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);


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
        Volley.newRequestQueue(getContext()).add(stringRequest);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();


            } else {

                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }

    // @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("inside", "true");
//        if (resultCode == RESULT_CANCELED) {
//            return;
//        }
//        if(requestCode == CAMERA && resultCode == RESULT_OK) {
//            Uri uri = Uri.parse(currentPhotoPath);
////            File file = getImageFile(); // 2
////            Uri destinationUri = Uri.fromFile(file);
//            openCropActivity(uri, uri);
//        }
//        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
//            Uri uri = UCrop.getOutput(data);
//
//            showImage(uri);
//        }
//        else if (requestCode == GALLERY && resultCode == RESULT_OK && data != null) {
//            Uri sourceUri = data.getData(); // 1
//            File file = getImageFile(); // 2
//            Uri destinationUri = Uri.fromFile(file);  // 3
//            openCropActivity(sourceUri, destinationUri);  // 4
//        }
////        if (requestCode == GALLERY) {
////            if (resultCode == RESULT_OK) {
////                Uri imageUri = data.getData();
////                openCropActivity(imageUri, imageUri);}
////            if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
////                Uri uri = UCrop.getOutput(data);
////                showImage(uri);
////
//////                cam.setVisibility(View.GONE);
//////                image_uri = imageUri;
//////                try {
//////                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image_uri);
//////                } catch (IOException e) {
//////                    e.printStackTrace();
//////                }
//////                image.setImageBitmap(bitmap);
////            }
////        }
////        if (requestCode == CAMERA) {
////            cam.setVisibility(View.GONE);
////            BitmapFactory.Options options = new BitmapFactory.Options();
////            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
////            bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);
////            image.setImageBitmap(bitmap);
////        }
////
////
//
//    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                String path = resultUri.getPath();
                Log.e("file_path", path);
                Log.e("Path", String.valueOf(resultUri));

                Bitmap bitmap = result.getBitmap();


                if (flag == 3) {
                    image.setImageURI(resultUri);
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());

                        IMG1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        if (IMG1 != null) {
                            image.setImageURI(resultUri);
                            img1_c = getResizedBitmap(IMG1, 500);
                            img1_n = "IMG_" + timeStamp + ".jpg";

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


// or
                //Bitmap cropped = cropImageView.getCroppedImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("error", error.toString());
            }
        }

        //super.onActivityResult(requestCode,resultCode,data);
/*
            Log.e("data_camera", String.valueOf(data.getExtras().get("data")));
            if (requestCode == CAMERA_CAPTURE) {
                if (resultCode == RESULT_OK) {

                    Uri p= (Uri) data.getExtras().get("data");


                    if (data != null) {

                      *//* Bitmap photo = (Bitmap) data.getExtras().get("data");
                bcf.setImageBitmap(photo);*//*
         *//*  UCrop.of(data, data.getData())
                                .withAspectRatio(16, 9)
                                .withMaxResultSize(128, 128)
                                .start(this);*//*

                        performCrop(p);


                    } else {

                        Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();

                    }

                }

              //Uri uri=picUri;
             // picUri =data.getData();

               *//* CropImage.activity(android.net.Uri.parse(String.valueOf(picUri)))
                        .setAspectRatio(1,1)
                        .setFixAspectRatio(true)
                        .start(getContext(),this);*//*

            }*/

        // user is returning from cropping the image
        /*else if (requestCode == PIC_CROP) {

         *//* CropImage.ActivityResult result = CropImage.getActivityResult(data);

                Log.e("ttt", String.valueOf(result.getUri()));
                Bitmap bitmap = result.getBitmap();
                bcf.setImageBitmap(bitmap);*//*
                // get the returned data
                if (data != null) {
                    Bundle extras = data.getExtras();
                    // get the cropped bitmap
                    if(extras!=null) {
                        Bitmap thePic = extras.getParcelable("data");
                        if(flag==1) {
                            bcf.setImageBitmap(thePic);
                        }
                        if (flag==2)
                        {
                            bcb.setImageBitmap(thePic);
                        }
                        if(flag==3)
                        {
                            img1.setImageBitmap(thePic);
                        }
                        if(flag==4)
                        {
                            img2.setImageBitmap(thePic);
                        }
                        if(flag==5)
                        {
                            bod.setImageBitmap(thePic);
                        }
                    }
                    else
                    {

                        Bitmap myBitmap = BitmapFactory.decodeFile(picUri.getPath());
                        if(flag==1)
                        {
                            bcf.setImageBitmap(myBitmap);

                        }
                        if (flag==2)
                        {
                            bcb.setImageBitmap(myBitmap);
                        }
                        if(flag==3)
                        {
                            img1.setImageBitmap(myBitmap);
                        }
                        if(flag==4)
                        {
                            img2.setImageBitmap(myBitmap);
                        }
                        if(flag==5)
                        {
                            bod.setImageBitmap(myBitmap);
                        }
                    }
                    //  ImageView picView = (ImageView) findViewById(R.id.picture);


                }

            }*/
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

    private void showImage(Uri imageUri) {

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.setImageBitmap(bitmap);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void checkPermission(final int profileCamera, final int profileGallery) {

        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA))) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            dia.show();
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, profileCamera);*/
//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                        File photoFile = null;
//                        try
//                        {
//                            photoFile = createImageFile();
//                        }
//                        catch (IOException ex)
//                        {
//                        Log.e("exception",ex.toString());
//                        }
//                        if (photoFile != null) {
//                            Uri photoURI = FileProvider.getUriForFile(getActivity(),
//                                    "com.ynot.aaspassdeliveringlife.fileprovider",
//                                    photoFile);
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                            startActivityForResult(takePictureIntent, profileCamera);
//
//
//                        }
//                    }
                    openCamera();

                    dia.dismiss();
                }
            });
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent logoIntent = new Intent();
                    logoIntent.setType("image/*");
                    logoIntent.setAction(Intent.ACTION_GET_CONTENT);
                    //logoIntent.setAction(Intent.ACTION_CHOOSER);
                    startActivityForResult(Intent.createChooser(logoIntent, "Select Picture"), profileGallery);

//                  openImagesDocument(profileGallery);
                    dia.dismiss();
                }
            });


        }
    }

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContext().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    private void openCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile(); // 1
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(getActivity(), "com.ynot.aaspassdeliveringlife.fileprovider",
                    file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, CAMERA);

    }


    private File getImageFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = null;
        try {
            file = File.createTempFile(
                    imageFileName, ".jpg", storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPhotoPath = "file:" + file.getAbsolutePath();
        Log.e("file_path", currentPhotoPath);
        return file;

    }

    private void openImagesDocument(int profileGallery) {
        Intent logoIntent = new Intent();
        logoIntent.setType("image/*");
        logoIntent.setAction(Intent.ACTION_GET_CONTENT);
        //logoIntent.setAction(Intent.ACTION_CHOOSER);
        startActivityForResult(Intent.createChooser(logoIntent, "Select Picture"), profileGallery);

    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);


        return byteArrayOutputStream.toByteArray();

    }

}