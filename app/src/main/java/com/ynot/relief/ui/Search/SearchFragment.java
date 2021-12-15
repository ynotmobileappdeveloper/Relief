package com.ynot.relief.ui.Search;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.ProductAdapter;
import com.ynot.relief.LoginPage;
import com.ynot.relief.Models.MedicineModel;
import com.ynot.relief.ProductDetail;
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

public class SearchFragment extends Fragment {

    SearchView search;
    RecyclerView rec;
    ProductAdapter adapter;
    ArrayList<MedicineModel> model = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        search = root.findViewById(R.id.search);
        rec = root.findViewById(R.id.rec);
        rec.setLayoutManager(new GridLayoutManager(getContext(), 2));
        search.setIconifiedByDefault(false);


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                get_search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                get_search(s);
                return false;
            }
        });
        return root;
    }

    private void get_search(final String keyword) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SEARCH_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("search", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("products", response);

                            if (ob.getBoolean("status")) {
                                JSONArray array = ob.getJSONArray("products");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);

                                    String amount = String.format("%.2f", obj.getDouble("price"));
                                    model.add(new MedicineModel(obj.getString("p_id"), obj.getString("image"), obj.getString("brand"), obj.getString("name"), amount, obj.getString("fav_status")));
                                }
                                adapter = new ProductAdapter(getContext(), model, new ProductAdapter.Click() {
                                    @Override
                                    public void ItemClick(MedicineModel model) {
                                        if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                                            Intent i = new Intent(getContext(), ProductDetail.class);
                                            i.putExtra("id", model.getId());
                                            i.putExtra("name", model.getName());
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getContext(), LoginPage.class));
                                        }
                                    }
                                },"");
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

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("keyword", keyword);
                Log.e("in", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }
}