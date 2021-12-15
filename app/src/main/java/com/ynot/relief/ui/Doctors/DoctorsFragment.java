package com.ynot.relief.ui.Doctors;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.DocCategoryAdapter;
import com.ynot.relief.DoctorsList;
import com.ynot.relief.LoginPage;
import com.ynot.relief.Models.DepartmentModel;
import com.ynot.relief.R;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


public class DoctorsFragment extends Fragment {


    RecyclerView rec;
    DocCategoryAdapter adapter;
    ArrayList<DepartmentModel> model = new ArrayList<>();
    ACProgressFlower progress;
    ImageView search_view;
    SearchView search;
    LinearLayout search_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_doctors, container, false);
        progress = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        rec = root.findViewById(R.id.rec);
        search_view = root.findViewById(R.id.search_view);
        search = root.findViewById(R.id.search);
        search_layout = root.findViewById(R.id.search_layout);
        rec.setLayoutManager(new GridLayoutManager(getContext(), 3));

        get_departments();

        search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setVisibility(View.VISIBLE);
                search_layout.setVisibility(View.GONE);
                search.setIconified(false);
                search.setIconifiedByDefault(true);
            }
        });


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    adapter.getfilter().filter("");
                    adapter.notifyDataSetChanged();


                } else {
                    adapter.getfilter().filter(query.toString());
                    adapter.notifyDataSetChanged();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.getfilter().filter("");
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.getfilter().filter(newText.toString());
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        return root;
    }

    private void get_departments() {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_DEPARTMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Log.e("resp", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {

                                JSONArray array = ob.getJSONArray("departments");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new DepartmentModel(obb.getString("id"), obb.getString("department"), obb.getString("pic")));

                                }
                                adapter = new DocCategoryAdapter(getContext(), model, new DocCategoryAdapter.Click() {
                                    @Override
                                    public void ItemClick(DepartmentModel model, int position) {

                                        if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                                            Intent i = new Intent(getContext(), DoctorsList.class);
                                            i.putExtra("name", model.getName());
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getContext(), LoginPage.class));
                                            getActivity().finish();
                                        }
                                    }
                                });
                                rec.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
            }
        }) {
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }
}