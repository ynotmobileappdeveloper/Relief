package com.ynot.relief.ui.TestResults;

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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.TestResultAdapter;
import com.ynot.relief.Models.TestModel;
import com.ynot.relief.R;
import com.ynot.relief.TestResultDetails;
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

public class TestResults extends Fragment {

    TestResultAdapter adapter;
    ArrayList<TestModel> model = new ArrayList<>();
    RecyclerView rec;
    ACProgressFlower progressDialog;
    ImageView nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_test_results, container, false);
        rec = root.findViewById(R.id.rec);
        nodata = root.findViewById(R.id.nodata);

        progressDialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        get_data();
    }

    private void get_data() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_TEST_RESULTS,
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
                                    TestModel data = new TestModel();
                                    data.setPrice(obb.getString("price"));
                                    data.setDate(obb.getString("date"));
                                    data.setBooked_name(obb.getString("booked_name"));
                                    data.setLab_name(obb.getString("lab_name"));
                                    data.setAge(obb.getString("age"));
                                    data.setContent(obb.getString("content"));
                                    data.setImage(obb.getString("image"));
                                    data.setName(obb.getString("name"));
                                    data.setType(obb.getString("type"));
                                    data.setAddress(obb.getString("address"));
                                    data.setTime(obb.getString("time"));
                                    data.setPhone(obb.getString("mobile"));
                                    data.setGender(obb.getString("gender"));
                                    model.add(data);

                                }
                                adapter = new TestResultAdapter(getContext(), model, new TestResultAdapter.ItemClick() {
                                    @Override
                                    public void Click(TestModel list) {
                                        Intent i = new Intent(getContext(), TestResultDetails.class);
                                        i.putExtra("data", list);
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
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);


    }
}