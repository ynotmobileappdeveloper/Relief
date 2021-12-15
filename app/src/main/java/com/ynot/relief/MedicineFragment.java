package com.ynot.relief;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.ProductAdapter;
import com.ynot.relief.Models.MedicineModel;
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

import static com.facebook.FacebookSdk.getApplicationContext;


public class MedicineFragment extends Fragment implements SearchView.OnQueryTextListener {

    RecyclerView rec;
    String id, page;
    ProductAdapter adapter;
    ArrayList<MedicineModel> model = new ArrayList<>();
    ACProgressFlower dialog;
    androidx.appcompat.widget.SearchView search;
    ImageView nodata;
    Button srch_btn;
    Button order, cancel;
    EditText list;

    Dialog search_dialog;

    public MedicineFragment(String id, String page) {
        this.id = id;
        this.page = page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        View root = inflater.inflate(R.layout.fragment_medicine, container, false);
        rec = root.findViewById(R.id.rec);
        search = root.findViewById(R.id.search);
        nodata = root.findViewById(R.id.nodata);
        srch_btn = root.findViewById(R.id.srch_btn);
        rec.setLayoutManager(new GridLayoutManager(getContext(), 2));

        get_products(id);
        if (page.equals("3")) {
            srch_btn.setVisibility(View.VISIBLE);
        } else {
            srch_btn.setVisibility(View.GONE);
            search.setOnQueryTextListener(this);
        }
        srch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("search_item", search.getQuery().toString());
                if (model.size() > 0) {
                    if (TextUtils.isEmpty(search.getQuery().toString())) {
                        adapter.getfilter().filter("");
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.getfilter().filter(search.getQuery().toString());
                        adapter.notifyDataSetChanged();
                        if (adapter.data) {
                            Display();
                        }
                        Log.e("resul", String.valueOf(adapter.resultCheck()));
                    }
                }
            }
        });
        return root;
    }

    private void get_products(final String id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("products", response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                search.setVisibility(View.VISIBLE);
                                srch_btn.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("products");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    String amount = String.format("%.2f", obj.getDouble("selling_price"));
                                    model.add(new MedicineModel(obj.getString("id"), obj.getString("pic"), "", obj.getString("name"), amount, ""));
                                }
                                adapter = new ProductAdapter(getActivity(), model, new ProductAdapter.Click() {
                                    @Override
                                    public void ItemClick(MedicineModel model) {
                                        if (SharedPrefManager.getInstatnce(getApplicationContext()).isLoggedIn()) {
                                            Intent i = new Intent(getApplicationContext(), ProductDetail.class);
                                            i.putExtra("id", model.getId());
                                            i.putExtra("name", model.getName());
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                        }
                                    }
                                }, page);
                                rec.setAdapter(adapter);
                            } else {
                                search.setVisibility(View.GONE);
                                srch_btn.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);

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
                if (id.isEmpty()) {
                    params.put("subcategory_id", "0");
                } else {
                    params.put("subcategory_id", id);
                }
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (model.size() > 0) {
            if (TextUtils.isEmpty(query)) {
                adapter.getfilter().filter("");
                adapter.notifyDataSetChanged();
            } else {
                adapter.getfilter().filter(query.trim());
                adapter.notifyDataSetChanged();
            }
        }


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (model.size() > 0) {
            if (TextUtils.isEmpty(newText)) {
                adapter.getfilter().filter("");
                adapter.getfilter().filter("");
                adapter.notifyDataSetChanged();
            } else {
                adapter.getfilter().filter(newText.trim());
                adapter.notifyDataSetChanged();
            }
        }

        return false;
    }

    public void Display() {
        search_dialog = new Dialog(getActivity());
        search_dialog.setContentView(R.layout.search_type_layout);
        search_dialog.setCanceledOnTouchOutside(false);
        Window window = search_dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        search_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        order = search_dialog.findViewById(R.id.order);
        cancel = search_dialog.findViewById(R.id.cancel);
        list = search_dialog.findViewById(R.id.list);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_dialog.dismiss();
               // adapter.data = true;
                //adapter.notifyDataSetChanged();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchTypeAMedicine.class));
                search_dialog.dismiss();
            }
        });
        list.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    order.setBackgroundResource(R.drawable.search_enable);
                    order.setEnabled(true);
                } else {
                    order.setBackgroundResource(R.drawable.search_disable);
                    order.setEnabled(false);
                }

            }
        });
        search_dialog.show();

    }
}