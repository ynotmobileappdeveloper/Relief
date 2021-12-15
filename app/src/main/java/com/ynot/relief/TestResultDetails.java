package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynot.relief.Models.TestModel;

public class TestResultDetails extends AppCompatActivity {

    TestModel model;
    TextView title, description, original, name, gender, age, address, mobile, service_name, date, time, price;
    ImageView image, nodata;
    RelativeLayout card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Test Results");

        title = findViewById(R.id.test);
        image = findViewById(R.id.image);
        nodata = findViewById(R.id.nodata);
        description = findViewById(R.id.description);
        title = findViewById(R.id.test);
        original = findViewById(R.id.original);
        service_name = findViewById(R.id.tests);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        address = findViewById(R.id.address);
        mobile = findViewById(R.id.mobile);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        card = findViewById(R.id.card);
        price = findViewById(R.id.price);

        model = (TestModel) getIntent().getSerializableExtra("data");

        name.setText(model.getName());
        age.setText(model.getAge());
        gender.setText(model.getGender());
        date.setText(model.getDate());
        time.setText(model.getTime());
        service_name.setText(model.getBooked_name());
        price.setText("Rs. " + model.getPrice());
        mobile.setText("+91 " + model.getPhone());


        if (model.getImage().isEmpty()) {
            description.setText(model.getContent());
            description.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
        } else {
            card.setVisibility(View.VISIBLE);
            description.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(model.getImage()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    nodata.setVisibility(View.VISIBLE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    nodata.setVisibility(View.GONE);
                    return false;
                }
            }).into(image);
        }


        Log.e("name", model.getName());


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}