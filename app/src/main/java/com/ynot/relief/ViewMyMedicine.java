package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.MedicineViewAdapter;
import com.ynot.relief.Models.MedicineViewModel;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ViewMyMedicine extends AppCompatActivity {

    ArrayList<MedicineViewModel> model = new ArrayList<>();
    MedicineViewAdapter adapter;
    RecyclerView rec;
    TextView name, address, mob;
    String id;
    TextView morning, noon, night, after, before, nos, ml, grm, medi_time, medi_count, medicine_time, add_time;
    Dialog medi_dialog;
    LinearLayout time_layout;
    ImageView close;
    ACProgressFlower dialog;
    CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_medicine);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Medicine");
        rec = findViewById(R.id.rec);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        card = findViewById(R.id.card);
        mob = findViewById(R.id.mob);
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        id = getIntent().getStringExtra("id");
        name.setText(getIntent().getStringExtra("name"));
        address.setText(getIntent().getStringExtra("address"));
        mob.setText("Contact : +91 " + getIntent().getStringExtra("mob"));
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();


        medi_dialog = new Dialog(ViewMyMedicine.this);
        medi_dialog.setContentView(R.layout.medi_time);
        medi_dialog.setCanceledOnTouchOutside(true);
        morning = medi_dialog.findViewById(R.id.morning);
        noon = medi_dialog.findViewById(R.id.noon);
        night = medi_dialog.findViewById(R.id.night);
        after = medi_dialog.findViewById(R.id.after);
        before = medi_dialog.findViewById(R.id.before);
        nos = medi_dialog.findViewById(R.id.nos);
        ml = medi_dialog.findViewById(R.id.ml);
        grm = medi_dialog.findViewById(R.id.grm);
        medi_count = medi_dialog.findViewById(R.id.count);
        medi_time = medi_dialog.findViewById(R.id.time);
        time_layout = medi_dialog.findViewById(R.id.time_layout);
        close = medi_dialog.findViewById(R.id.close);
        medicine_time = medi_dialog.findViewById(R.id.medicine_time);
        add_time = medi_dialog.findViewById(R.id.add_time);
        Window windows = medi_dialog.getWindow();
        windows.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        medi_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        get_data();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medi_dialog.dismiss();
            }
        });
    }

    private void get_data() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.VIEW_MY_MEDICINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                card.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new MedicineViewModel(obb.getString("name"), obb.getString("qty"),
                                            obb.getString("morning"), obb.getString("noon"), obb.getString("night"), obb.getString("after"),
                                            obb.getString("before"), obb.getString("nos"), obb.getString("ml"), obb.getString("grm"), obb.getString("medi_count"), obb.getString("medi_time")));
                                }
                                adapter = new MedicineViewAdapter(getApplicationContext(), model, new MedicineViewAdapter.Click() {
                                    @Override
                                    public void ItemClick(MedicineViewModel list) {
                                        medi_dialog.show();
                                        if (list.getAfter().equals("1")) {
                                            after.setBackgroundResource(R.drawable.tab_selected_back);
                                            after.setVisibility(View.VISIBLE);

                                        }
                                        if (list.getBefore().equals("1")) {
                                            before.setBackgroundResource(R.drawable.tab_selected_back);
                                            before.setVisibility(View.VISIBLE);
                                        }
                                        if (list.getMorning().equals("1")) {
                                            morning.setBackgroundResource(R.drawable.tab_selected_back);
                                            morning.setVisibility(View.VISIBLE);
                                            medicine_time.setVisibility(View.VISIBLE);
                                        }
                                        if (list.getNoon().equals("1")) {
                                            noon.setBackgroundResource(R.drawable.tab_selected_back);
                                            noon.setVisibility(View.VISIBLE);
                                            medicine_time.setVisibility(View.VISIBLE);
                                        }
                                        if (list.getNight().equals("1")) {
                                            night.setBackgroundResource(R.drawable.tab_selected_back);
                                            night.setVisibility(View.VISIBLE);
                                            medicine_time.setVisibility(View.VISIBLE);
                                        }
                                        if (!list.getMedi_time().isEmpty()) {
                                            time_layout.setVisibility(View.VISIBLE);
                                            medi_time.setText(list.getMedi_time());
                                            add_time.setVisibility(View.VISIBLE);
                                        }
                                        if (list.getNos().equals("1")) {
                                            nos.setBackgroundResource(R.drawable.tab_selected_back);
                                            nos.setVisibility(View.VISIBLE);
                                        }
                                        if (list.getMl().equals("1")) {
                                            ml.setBackgroundResource(R.drawable.tab_selected_back);
                                            ml.setVisibility(View.VISIBLE);
                                        }
                                        if (list.getGrm().equals("1")) {
                                            grm.setBackgroundResource(R.drawable.tab_selected_back);
                                            grm.setVisibility(View.VISIBLE);
                                        }
                                        medi_count.setText(list.getMedi_count());
                                    }
                                });
                                rec.setAdapter(adapter);
                            } else {
                                card.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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