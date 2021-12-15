package com.ynot.relief.ui.BookedCheckups;

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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.BookedAdapter;
import com.ynot.relief.Models.BookedModel;
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


public class BookedCheckups extends Fragment {

    BookedAdapter adapter;
    ArrayList<BookedModel> model = new ArrayList<>();
    RecyclerView rec;
    ACProgressFlower progressDialog;
    ImageView nodata;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_booked_checkups, container, false);
        progressDialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        rec = root.findViewById(R.id.rec);
        nodata = root.findViewById(R.id.nodata);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    private void get_data() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_BOOKED_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    BookedModel data = new BookedModel();
                                    data.setAge(obb.getString("age"));
                                    data.setPrice(obb.getString("price"));
                                    data.setName(obb.getString("name"));
                                    data.setService_name(obb.getString("booked_name"));
                                    data.setGender(obb.getString("gender"));
                                    data.setPhone(obb.getString("mobile"));
                                    data.setStatus(obb.getString("status"));
                                    data.setDate(obb.getString("date"));
                                    data.setLab_name(obb.getString("lab_name"));
                                    data.setType(obb.getString("type"));
                                    model.add(data);

                                }
                                adapter = new BookedAdapter(getContext(), model);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);


    }

    @Override
    public void onResume() {
        super.onResume();
        get_data();
    }
}