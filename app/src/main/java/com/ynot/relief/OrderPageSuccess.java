package com.ynot.relief;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ynot.relief.Webservices.SharedPrefManager;


public class OrderPageSuccess extends AppCompatActivity {

    TextView order, amount, gmail, emailtxt;
    CardView cardView;
    Button home, view;
    String upload = "";
    LinearLayout layoutone, layouttwo;
    String page = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page_success);
        order = findViewById(R.id.orderId);
        amount = findViewById(R.id.amt);
        gmail = findViewById(R.id.email);
        cardView = findViewById(R.id.card);
        home = findViewById(R.id.back);
        emailtxt = findViewById(R.id.emailtxt);
        layoutone = findViewById(R.id.layoutone);
        layouttwo = findViewById(R.id.layouttwo);
        view = findViewById(R.id.view);

        if (getIntent().hasExtra("upload")) {

            page = getIntent().getStringExtra("upload");
            order.setText(getIntent().getStringExtra("order_id"));
            layoutone.setVisibility(View.VISIBLE);
            layouttwo.setVisibility(View.GONE);
            if (SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getEmail() != null) {
                //gmail.setText(SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getEmail());
                //emailtxt.setVisibility(View.VISIBLE);
            } else {
                emailtxt.setVisibility(View.GONE);
            }
        }

        if (getIntent().hasExtra("type")) {
            page = getIntent().getStringExtra("type");
            layoutone.setVisibility(View.GONE);
            layouttwo.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            emailtxt.setVisibility(View.GONE);
        }


        if (getIntent().hasExtra("order")) {
            page = getIntent().getStringExtra("order");
            order.setText(getIntent().getStringExtra("order_id"));
            amount.setText("Rs. " + getIntent().getStringExtra("total_price"));
            if (SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getEmail() != null) {
                gmail.setText(SharedPrefManager.getInstatnce(getApplicationContext()).getUser().getEmail());
                emailtxt.setVisibility(View.GONE);
            } else {
                emailtxt.setVisibility(View.GONE);
            }
            layoutone.setVisibility(View.VISIBLE);
            layouttwo.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            emailtxt.setVisibility(View.GONE);
        }
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pop);
        cardView.startAnimation(animation);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finishAffinity();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (page.equals("order")) {
                    Intent i = new Intent(getApplicationContext(), MyOrders.class);
                    i.putExtra("success", "success");
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(getApplicationContext(), MyPrescription.class);
                    i.putExtra("success", "success");
                    startActivity(i);
                    finish();
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
