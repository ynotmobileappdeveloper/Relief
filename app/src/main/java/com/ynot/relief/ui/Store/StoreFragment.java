package com.ynot.relief.ui.Store;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.StoreCategory;
import com.ynot.relief.AllProducts;
import com.ynot.relief.PharmCategoryModel;
import com.ynot.relief.R;
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


public class StoreFragment extends Fragment {

    RecyclerView rec;
    StoreCategory adapter;
    ArrayList<PharmCategoryModel> model = new ArrayList<>();
    ACProgressFlower dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        rec = view.findViewById(R.id.rec);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        get_store_category();


        return view;
    }

    private void get_store_category() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.AASPAAS_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("cat",response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("category");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    model.add(new PharmCategoryModel(obj.getString("cat_id"), obj.getString("cat_name")));
                                }
                               adapter=new StoreCategory(getContext(), model, new StoreCategory.Click() {
                                   @Override
                                   public void ItemClick(PharmCategoryModel model) {
                                       Intent i=new Intent(getContext(), AllProducts.class);
                                       i.putExtra("id",model.getId());
                                       startActivity(i);
                                   }
                               });
                                rec.setAdapter(adapter);

                            } else {

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
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);

    }

    private void get_category() {


    }
}