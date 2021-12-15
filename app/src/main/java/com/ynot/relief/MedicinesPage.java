package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.ynot.relief.Adapters.MedicalAdapter;
import com.ynot.relief.Models.MedicalModel;

import java.util.ArrayList;

public class MedicinesPage extends AppCompatActivity {
    RecyclerView rec;
    ArrayList<MedicalModel>model;
    MedicalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rec=findViewById(R.id.rec);
        //rec.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL));
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        adapter=new MedicalAdapter(getApplicationContext(), model, new MedicalAdapter.Click() {
            @Override
            public void Itemclick() {

            }
        });
        rec.setAdapter(adapter);

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}