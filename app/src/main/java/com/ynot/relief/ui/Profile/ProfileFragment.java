package com.ynot.relief.ui.Profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.ynot.relief.Adapters.RecentAdapter;
import com.ynot.relief.MedicineTabbed;
import com.ynot.relief.Models.RecentModel;
import com.ynot.relief.MyCheckups;
import com.ynot.relief.MyConsulting;
import com.ynot.relief.MyLabTests;
import com.ynot.relief.MyOrders;
import com.ynot.relief.ProfilePage;
import com.ynot.relief.R;
import com.ynot.relief.RecentDoc;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.User;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


public class ProfileFragment extends Fragment {
    RecyclerView rec;
    RecentAdapter adapter;
    ArrayList<RecentModel> model = new ArrayList<>();
    ImageView edit, image;
    TextView name, user_id, age, blood, height, weight, sugar, pressur, Cholesterol;
    Activity activity;
    ACProgressFlower dialog;
    LinearLayout layout, blood_lay, height_lay, weight_lay, sugar_lay, pressure_lay, cho_lay;
    CardView my_orders, consulting, my_medicine, labs, my_checkups;
    LinearLayout doc_layout;
    Button view_doc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();
        blood_lay = root.findViewById(R.id.blood_lay);
        height_lay = root.findViewById(R.id.height_lay);
        weight_lay = root.findViewById(R.id.weight_lay);
        sugar_lay = root.findViewById(R.id.sugar_lay);
        pressure_lay = root.findViewById(R.id.pressure_lay);
        cho_lay = root.findViewById(R.id.cho_lay);
        consulting = root.findViewById(R.id.consulting);
        my_medicine = root.findViewById(R.id.my_medicine);
        doc_layout = root.findViewById(R.id.doc_layout);
        view_doc = root.findViewById(R.id.view_doc);
        labs = root.findViewById(R.id.labs);
        my_checkups = root.findViewById(R.id.my_checkups);


        rec = root.findViewById(R.id.rec);
        edit = root.findViewById(R.id.edit);
        name = root.findViewById(R.id.name);
        user_id = root.findViewById(R.id.user_id);
        image = root.findViewById(R.id.image);
        age = root.findViewById(R.id.age);
        blood = root.findViewById(R.id.blood);
        height = root.findViewById(R.id.height);
        weight = root.findViewById(R.id.weight);
        sugar = root.findViewById(R.id.sugar);
        pressur = root.findViewById(R.id.pressur);
        Cholesterol = root.findViewById(R.id.Cholesterol);
        layout = root.findViewById(R.id.layout);
        my_orders = root.findViewById(R.id.my_orders);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProfilePage.class));
            }
        });

        if (SharedPrefManager.getInstatnce(getContext()).getUser().getId() != null) {
            user_id.setText("ID : " + SharedPrefManager.getInstatnce(getContext()).getUser().getId());
            name.setText(SharedPrefManager.getInstatnce(getContext()).getUser().getName());
        }

        my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyOrders.class));
            }
        });
        consulting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyConsulting.class));
            }
        });
        my_checkups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyCheckups.class));
            }
        });
        my_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), MedicineTabbed.class));
            }
        });
        labs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), MyLabTests.class));
            }
        });
        view_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RecentDoc.class));
            }
        });


        return root;
    }

    private void get_profile() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("profile", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                layout.setVisibility(View.VISIBLE);
                                String id = SharedPrefManager.getInstatnce(getContext()).getUser().getId();
                                String mob = SharedPrefManager.getInstatnce(getContext()).getUser().getMob();
                                String email = SharedPrefManager.getInstatnce(getContext()).getUser().getMob();
                                User user = new User(id, ob.getString("name"), mob, email);
                                SharedPrefManager.getInstatnce(getContext()).userLogin(user);

                                name.setText(ob.getString("name"));
                                if (!ob.getString("image").isEmpty()) {
                                    Glide.with(getContext()).load(ob.getString("image")).into(image);
                                }

                                if (!ob.getString("age").isEmpty()) {
                                    if (ob.getString("gender").equals("male")) {
                                        age.setText("");
                                        age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_male, 0, 0, 0);
                                        age.setText("Age : " + ob.getString("age") + ", " + ob.getString("gender"));
                                    } else {
                                        age.setText("");
                                        age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_female, 0, 0, 0);
                                        age.setText("Age : " + ob.getString("age") + ", " + ob.getString("gender"));
                                    }
                                } else {
                                    if (ob.getString("gender").equals("male")) {
                                        age.setText("");
                                        age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_male, 0, 0, 0);
                                        age.setText(ob.getString("gender"));
                                    } else {
                                        age.setText("");
                                        age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_female, 0, 0, 0);
                                        age.setText(ob.getString("gender"));
                                    }
                                }


                                if (!ob.getString("height").isEmpty()) {
                                    height_lay.setVisibility(View.VISIBLE);
                                    height.setText(ob.getString("height") + "CM");
                                } else {
                                    height_lay.setVisibility(View.GONE);
                                }
                                if (!ob.getString("weight").isEmpty()) {
                                    weight_lay.setVisibility(View.VISIBLE);
                                    weight.setText(ob.getString("weight") + "Kg");
                                } else {
                                    weight_lay.setVisibility(View.GONE);
                                }
                                if (!ob.getString("blood").isEmpty()) {
                                    blood_lay.setVisibility(View.VISIBLE);
                                    blood.setText(ob.getString("blood"));
                                } else {
                                    blood_lay.setVisibility(View.GONE);
                                }
                                if (!ob.getString("sugar").isEmpty()) {
                                    sugar_lay.setVisibility(View.VISIBLE);
                                    sugar.setText(ob.getString("sugar"));
                                } else {
                                    sugar_lay.setVisibility(View.GONE);
                                }
                                if (!ob.getString("pressure").isEmpty()) {
                                    pressure_lay.setVisibility(View.VISIBLE);
                                    pressur.setText(ob.getString("pressure"));
                                } else {
                                    pressure_lay.setVisibility(View.GONE);
                                }

                                if (!ob.getString("cholesterol").isEmpty()) {
                                    cho_lay.setVisibility(View.VISIBLE);
                                    Cholesterol.setText(ob.getString("cholesterol"));
                                } else {
                                    cho_lay.setVisibility(View.GONE);
                                }


                            } else {
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
                params.put("user_id", SharedPrefManager.getInstatnce(getContext()).getUser().getId());
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);

    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        get_profile();
        get_recent_doc();
    }

    private void get_recent_doc() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.RECENT_DOC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("recent_doc", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                doc_layout.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("data");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new RecentModel(obb.getString("name"), obb.getString("department"), obb.getString("consult_date")));
                                }
                                adapter = new RecentAdapter(getContext(), model, "profile");
                                rec.setAdapter(adapter);
                            } else {
                                doc_layout.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", new String(error.networkResponse.data));

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
}