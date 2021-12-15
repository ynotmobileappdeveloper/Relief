package com.ynot.relief;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.EveningSlotAdpater;
import com.ynot.relief.Adapters.MorningSlotAdpater;
import com.ynot.relief.Adapters.NoonSlotAdpater;
import com.ynot.relief.Models.SlotModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AppointmentBooking extends AppCompatActivity {
    RecyclerView morning_rec, noon_rec, evening_rec;
    ArrayList<SlotModel> mornig_model = new ArrayList<>();
    ArrayList<SlotModel> noon_model = new ArrayList<>();
    ArrayList<SlotModel> evenig_model = new ArrayList<>();
    MorningSlotAdpater morningSlotAdpater;
    NoonSlotAdpater noonSlotAdpater;
    EveningSlotAdpater eveningSlotAdpater;
    CalendarView calender;
    String select_date = "", doc_id = "", select_slot = "";
    Button book;
    ACProgressFlower dialog;
    TextView morning_txt, noon_txt, evening_txt, no_slots;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Appointment Booking");
        doc_id = getIntent().getStringExtra("id");
        Date d = new Date();
        CharSequence s = DateFormat.format("d-MM-yyyy ", d.getTime());
        select_date = String.valueOf(s);
        Log.e("select_date", select_date);

        morning_rec = findViewById(R.id.morning);
        calender = findViewById(R.id.calender);
        noon_rec = findViewById(R.id.noon);
        book = findViewById(R.id.book);
        evening_rec = findViewById(R.id.evening);
        morning_txt = findViewById(R.id.morning_txt);
        noon_txt = findViewById(R.id.noon_txt);
        evening_txt = findViewById(R.id.evening_txt);
        layout = findViewById(R.id.layout);
        no_slots = findViewById(R.id.no_slots);


        morning_rec.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        noon_rec.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        evening_rec.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        get_time_slots(select_date);

        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String y = String.valueOf(year);
                String m = String.valueOf(month + 1);
                String d = String.valueOf(dayOfMonth);
                select_date = d + "-" + m + "-" + y;
                get_time_slots(select_date);
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_slot.isEmpty()) {
                    Toast.makeText(AppointmentBooking.this, "Please Choose booking slot !", Toast.LENGTH_SHORT).show();
                    return;
                }
                booking();
            }
        });


    }

    private void booking() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.DOC_BOOKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                Toast.makeText(AppointmentBooking.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(AppointmentBooking.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("slot_id", select_slot);
                params.put("booking_date", select_date);
                Log.e("booking_params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private void get_time_slots(final String select_date) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_TIME_SLOTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                no_slots.setVisibility(View.GONE);
                                book.setVisibility(View.VISIBLE);
                                layout.setVisibility(View.VISIBLE);
                                JSONArray mrng = ob.getJSONArray("mrng_slot");
                                mornig_model = new ArrayList<>();
                                for (int i = 0; i < mrng.length(); i++) {
                                    JSONObject ob_mrng = mrng.getJSONObject(i);
                                    mornig_model.add(new SlotModel(ob_mrng.getString("slot_id"), ob_mrng.getString("time")));
                                }
                                if (mornig_model.size() > 0) {
                                    morning_txt.setVisibility(View.VISIBLE);
                                } else {
                                    morning_txt.setVisibility(View.GONE);
                                }
                                morningSlotAdpater = new MorningSlotAdpater(getApplicationContext(), mornig_model, new MorningSlotAdpater.Select() {
                                    @Override
                                    public void Click(String status, SlotModel model) {
                                        if (status.equals("yes")) {
                                            noonSlotAdpater.disable();
                                            eveningSlotAdpater.disable();
                                            eveningSlotAdpater.notifyDataSetChanged();
                                            noonSlotAdpater.notifyDataSetChanged();
                                            select_slot = model.getId();
                                            Log.e("select", select_slot);
                                        }

                                    }
                                });
                                morning_rec.setAdapter(morningSlotAdpater);


                                JSONArray noon = ob.getJSONArray("noon_slot");
                                noon_model = new ArrayList<>();
                                for (int i = 0; i < noon.length(); i++) {
                                    JSONObject ob_noon = noon.getJSONObject(i);
                                    noon_model.add(new SlotModel(ob_noon.getString("slot_id"), ob_noon.getString("time")));
                                }
                                if (noon_model.size() > 0) {
                                    noon_txt.setVisibility(View.VISIBLE);
                                } else {
                                    noon_txt.setVisibility(View.GONE);
                                }
                                noonSlotAdpater = new NoonSlotAdpater(getApplicationContext(), noon_model, new NoonSlotAdpater.Select() {
                                    @Override
                                    public void Click(String status, SlotModel model) {
                                        if (status.equals("yes")) {
                                            morningSlotAdpater.disable();
                                            eveningSlotAdpater.disable();
                                            eveningSlotAdpater.notifyDataSetChanged();
                                            morningSlotAdpater.notifyDataSetChanged();
                                            select_slot = model.getId();
                                            Log.e("select", select_slot);
                                        }
                                    }
                                });
                                noon_rec.setAdapter(noonSlotAdpater);

                                JSONArray eve = ob.getJSONArray("evening_slot");
                                evenig_model = new ArrayList<>();
                                for (int i = 0; i < eve.length(); i++) {
                                    JSONObject ob_eve = eve.getJSONObject(i);
                                    evenig_model.add(new SlotModel(ob_eve.getString("slot_id"), ob_eve.getString("time")));
                                }
                                if (evenig_model.size() > 0) {
                                    evening_txt.setVisibility(View.VISIBLE);
                                } else {
                                    evening_txt.setVisibility(View.GONE);
                                }
                                eveningSlotAdpater = new EveningSlotAdpater(getApplicationContext(), evenig_model, new EveningSlotAdpater.Select() {
                                    @Override
                                    public void Click(String status, SlotModel model) {
                                        if (status.equals("yes")) {
                                            morningSlotAdpater.disable();
                                            noonSlotAdpater.disable();
                                            noonSlotAdpater.notifyDataSetChanged();
                                            morningSlotAdpater.notifyDataSetChanged();
                                            select_slot = model.getId();
                                            Log.e("select", select_slot);

                                        }
                                    }
                                });
                                evening_rec.setAdapter(eveningSlotAdpater);
                            } else {
                                no_slots.setVisibility(View.VISIBLE);
                                book.setVisibility(View.GONE);
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
                params.put("doc_id", doc_id);
                params.put("booking_date", select_date);
                Log.e("slot_params", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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