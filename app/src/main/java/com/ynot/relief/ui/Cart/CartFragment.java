package com.ynot.relief.ui.Cart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.CartAdapter;
import com.ynot.relief.CheckoutPage;
import com.ynot.relief.MainActivity;
import com.ynot.relief.Models.CartModel;
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

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


public class CartFragment extends Fragment {
    RecyclerView rec;
    CartAdapter adapter;
    CardView total_cart;
    ArrayList<CartModel> model = new ArrayList<>();
    Button proceed;
    LinearLayout nodata;
    ACProgressFlower dialog;
    TextView count, total, shipping, total_withtax, grand_tot;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        rec = root.findViewById(R.id.rec);
        proceed = root.findViewById(R.id.proceed);
        nodata = root.findViewById(R.id.nodata);
        count = root.findViewById(R.id.count);
        total = root.findViewById(R.id.total);
        shipping = root.findViewById(R.id.shipping);
        grand_tot = root.findViewById(R.id.grand_tot);
        total_cart = root.findViewById(R.id.total_cart);
        total_withtax = root.findViewById(R.id.total_withtax);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CheckoutPage.class));
            }
        });

        get_cart_items();

        return root;
    }

    private void get_cart_items() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CART_ITEMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("cart", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                total_cart.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("items");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    //model.add(new CartModel(obb.getString("cart_id"), obb.getString("img"), obb.getString("name"), obb.getString("price"), obb.getString("qty"), obb.getString("product_id")));
                                }

                                if (model.size() > 1) {
                                    count.setText(model.size() + " Items In cart");
                                } else {
                                    count.setText(model.size() + " Item In cart");
                                }
                                total.setText("Rs. " + ob.getString("total_price"));
                                double delivery = Double.parseDouble(ob.getString("shipping"));
                                if (delivery < 1) {
                                    shipping.setText("FREE");
                                    shipping.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                } else {
                                    shipping.setText("Rs. " + ob.getString("shipping"));
                                }

                                total_withtax.setText("Rs. " + ob.getString("total_with_tax"));
                                double tot = Double.parseDouble(ob.getString("total_with_tax")) + Double.parseDouble(ob.getString("shipping"));
                                grand_tot.setText("Rs. " + String.format("%.2f", tot));

                                adapter = new CartAdapter(getContext(), model, new CartAdapter.Click() {
                                    @Override
                                    public void ItemClik(CartModel list) {
                                        Intent i = new Intent(getContext(), ProductDetail.class);
                                        i.putExtra("id", list.getProduct_id());
                                        i.putExtra("count", list.getBrand());
                                        startActivity(i);
                                    }

                                    @Override
                                    public void plus(CartModel list) {
                                        String id = list.getId();
                                        int count = Integer.parseInt(list.getBrand());
                                        int new_count = count + 1;
                                        list.setBrand(new_count + "");
                                        update_count(id, new_count);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void minus(CartModel list) {
                                        String id = list.getId();
                                        int count = Integer.parseInt(list.getBrand());
                                        int new_count = count - 1;
                                        list.setBrand(new_count + "");
                                        update_count(id, new_count);
                                        adapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void remove(CartModel list, int position) {
                                        remove_item(list.getId(), position);
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
                dialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", SharedPrefManager.getInstatnce(getContext()).getUser().getId());
                Log.e("params", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void remove_item(final String id, final int position) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REMOVE_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                model.remove(position);
                                adapter.notifyDataSetChanged();
                                ((MainActivity) getActivity()).get_cart_count();
                                get_cart_items();
                                Toast.makeText(getContext(), "Item Removed !!", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getContext(), "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
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
                params.put("cart_id", id);
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void update_count(final String id, final int new_count) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.UPDATE_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                get_cart_items();
                            } else {
                                Toast.makeText(getContext(), "Sorry Insufficient Stock", Toast.LENGTH_SHORT).show();
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
                params.put("cart_id", id);
                params.put("user_id", SharedPrefManager.getInstatnce(getContext()).getUser().getId());
                params.put("qty", String.valueOf(new_count));
                Log.e("update_params", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }
}