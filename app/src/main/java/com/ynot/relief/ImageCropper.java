package com.ynot.relief;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImageView;

public class ImageCropper extends AppCompatActivity {

    CropImageView crop;
    TextView btn_cancel, btn_save;
    public static Bitmap croppedBitmap;
    ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);
        crop = findViewById(R.id.cropImageView);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
        ivClose = findViewById(R.id.iv_close);

        try {
            crop.setImageBitmap(UploadActivity.profile_bitmap);

        } catch (Exception e) {
            Log.e("Error", e + "");
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                intent.putExtra("cancel", "cancel");
                startActivity(intent);
                finish();

            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                intent.putExtra("cancel", "cancel");
                startActivity(intent);
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                croppedBitmap = crop.getCroppedImage();
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                intent.putExtra("crop", "crop");
                startActivity(intent);
                finish();
            }
        });
    }
}