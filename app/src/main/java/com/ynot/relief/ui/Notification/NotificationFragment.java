package com.ynot.relief.ui.Notification;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.NotificationAdapter;
import com.ynot.relief.Models.NotificationModel;
import com.ynot.relief.PrescriptionListPage;
import com.ynot.relief.R;
import com.ynot.relief.Webservices.SharedPrefManager;
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

public class NotificationFragment extends Fragment {

    ArrayList<NotificationModel> model = new ArrayList<>();
    RecyclerView rec;
    NotificationAdapter adapter;
    ACProgressFlower dialog;
    ImageView nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        rec = view.findViewById(R.id.rec);
        nodata = view.findViewById(R.id.nodata);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void get_notification() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("notification", response);
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    NotificationModel mo = new NotificationModel();
                                    mo.setId(obb.getString("id"));
                                    mo.setDate(obb.getString("date"));
                                    mo.setOp_time(obb.getString("op_time"));
                                    mo.setOp_date(obb.getString("date"));
                                    mo.setMedicine(obb.getString("medicine"));
                                    mo.setLab_test(obb.getString("lab_test"));
                                    mo.setStatus(obb.getString("status"));
                                    mo.setDoc_name(obb.getString("doc_name"));
                                    model.add(mo);

                                }
                                adapter = new NotificationAdapter(getContext(), model, new NotificationAdapter.Click() {
                                    @Override
                                    public void ItemClick(String id) {
                                        Intent i = new Intent(getContext(), PrescriptionListPage.class);
                                        i.putExtra("id", id);
                                        i.putExtra("name", "");
                                        i.putExtra("age", "");
                                        i.putExtra("gender", "");
                                        i.putExtra("date", "");
                                        i.putExtra("disable", "disable");
                                        startActivity(i);
                                    }
                                });
                                rec.setAdapter(adapter);

                            } else {
                                nodata.setVisibility(View.VISIBLE);
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
                params.put("user_id", SharedPrefManager.getInstatnce(getContext()).getUser().getId());
                Log.e("input", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        get_notification();
    }
}