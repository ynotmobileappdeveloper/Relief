package com.ynot.relief;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.ynot.relief.Adapters.DocMedicineAdapter;
import com.ynot.relief.Models.DocmedicineModel;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DocMedicineFragment extends Fragment {

    DocMedicineAdapter adapter;
    ArrayList<DocmedicineModel> model = new ArrayList<>();
    RecyclerView rec;
    ProgressDialog progressDialog;
    ImageView nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doc_medicine, container, false);
        rec = root.findViewById(R.id.rec);
        nodata = root.findViewById(R.id.nodata);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait...");

        rec.setLayoutManager(new LinearLayoutManager(getContext()));


        return root;
    }

    private void get_data() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MY_MEDICINE_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    DocmedicineModel data = new DocmedicineModel();
                                    data.setAge(obb.getString("age"));
                                    data.setId(obb.getString("id"));
                                    data.setName(obb.getString("name"));
                                    data.setDate(obb.getString("date"));
                                    data.setGender(obb.getString("gender"));
                                    data.setStatus(obb.getString("image"));
                                    data.setMedicine(obb.getString("medicines"));
                                    model.add(data);

                                }
                                adapter = new DocMedicineAdapter(getContext(), model, new DocMedicineAdapter.Click() {
                                    @Override
                                    public void ItemClick(DocmedicineModel list) {
                                        Intent i = new Intent(getContext(), DocMedicineDetail.class);
                                        i.putExtra("id", list.getId());
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

    @Override
    public void onResume() {
        super.onResume();
        get_data();
    }
}