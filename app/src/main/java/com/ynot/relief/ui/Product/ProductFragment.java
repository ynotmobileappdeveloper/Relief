package com.ynot.relief.ui.Product;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.HomeViewpagerAdapter;
import com.ynot.relief.Adapters.PharmacyAdpater;
import com.ynot.relief.Adapters.ShopAdapter;
import com.ynot.relief.LoginPage;
import com.ynot.relief.Models.HomeSlider;
import com.ynot.relief.Models.PharmacyModel;
import com.ynot.relief.PharmacyDetails;
import com.ynot.relief.R;
import com.ynot.relief.TypeAMedicine;
import com.ynot.relief.Webservices.SharedPrefManager;
import com.ynot.relief.Webservices.URLs;
import com.ynot.relief.Webservices.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ProductFragment extends Fragment {
    ViewPager viewpager;
    HomeViewpagerAdapter viewpagerAdapter;
    ArrayList<HomeSlider> homeSlider = new ArrayList<>();
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 6000;
    int currentPage = 0;
    int NUM_PAGES = 5;
    Timer timer;
    RecyclerView rec;
    ArrayList<PharmacyModel> model = new ArrayList<>();
    PharmacyAdpater adpater;
    ACProgressFlower dialog;
    ImageView nodata;
    Button upload, type_medicine;
    ArrayList<PharmacyModel> store_model = new ArrayList<>();
    RecyclerView shop_rec;
    ShopAdapter adapter;
    String latitude = "", longitude = "", city = "";
    LinearLayout near_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_product, container, false);
        viewpager = root.findViewById(R.id.viewpager);
        rec = root.findViewById(R.id.rec);
        rec.setNestedScrollingEnabled(false);
        nodata = root.findViewById(R.id.nodata);
        upload = root.findViewById(R.id.upload);
        type_medicine = root.findViewById(R.id.type_medicine);
        shop_rec = root.findViewById(R.id.shop_rec);
        near_layout = root.findViewById(R.id.near_layout);
        shop_rec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rec.setLayoutManager(new LinearLayoutManager(getContext()));


        dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait")
                .textSize(24)
                .themeColor(getResources().getColor(R.color.colorPrimary))
                .petalThickness(7)
                .fadeColor(Color.DKGRAY).build();

        if (SharedPrefManager.getInstatnce(getContext()).getshop().getName() != null) {
            latitude = SharedPrefManager.getInstatnce(getContext()).getshop().getLat();
            longitude = SharedPrefManager.getInstatnce(getContext()).getshop().getLon();
            city = SharedPrefManager.getInstatnce(getContext()).getshop().getLon();
        } else {
            SharedPreferences preferences = getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
            latitude = preferences.getString("latitude", "");
            longitude = preferences.getString("longitude", "");
            city = preferences.getString("city", "");
        }


        viewpagerAdapter = new HomeViewpagerAdapter(getContext(), homeSlider);
        viewpager.setAdapter(viewpagerAdapter);
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewpager.setCurrentItem(currentPage++, true);
            }
        };
        timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);

        viewpager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        viewpager.setPadding(70, 0, 70, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        viewpager.setPageMargin(20);
        get_stores();
        get_near_stores();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.upolad);
                } else {
                    startActivity(new Intent(getContext(), LoginPage.class));
                }


            }
        });
        type_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                    startActivity(new Intent(getContext(), TypeAMedicine.class));
                } else {
                    startActivity(new Intent(getContext(), LoginPage.class));
                }

            }
        });


        return root;
    }

    private void get_near_stores() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.NEAREST_STORE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("near_store", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                near_layout.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("store_id");
                                store_model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    store_model.add(new PharmacyModel(obj.getString("id"), obj.getString("name"), R.drawable.medi, obj.getString("address_line_1"), obj.getString("distance"), obj.getString("mobile"), obj.getString("mobile")));
                                }
                                adapter = new ShopAdapter(getContext(), store_model, new ShopAdapter.Click() {
                                    @Override
                                    public void ItemClick(PharmacyModel model) {
                                        Intent i = new Intent(getContext(), PharmacyDetails.class);
                                        i.putExtra("id", model.getId());
                                        i.putExtra("phone", "+91" + model.getPhone());
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                });
                                shop_rec.setAdapter(adapter);
                            } else {
                                near_layout.setVisibility(View.GONE);
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
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                Log.e("input", String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void get_stores() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_STORES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("store", response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);

                                JSONArray array = ob.getJSONArray("stores");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    model.add(new PharmacyModel(obj.getString("id"), obj.getString("name"), R.drawable.medi, obj.getString("address_line_1"), obj.getString("timezone"), obj.getString("mobile"), obj.getString("mobile")));
                                }
                                adpater = new PharmacyAdpater(getContext(), model, new PharmacyAdpater.Click() {
                                    @Override
                                    public void itemClick(PharmacyModel model) {
                                        Intent i = new Intent(getContext(), PharmacyDetails.class);
                                        i.putExtra("id", model.getId());
                                        i.putExtra("phone", "+91" + model.getPhone());
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }

                                    @Override
                                    public void call(PharmacyModel model) {

                                        if (checkpermission()) {
                                            String number = model.getPhone();
                                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                            callIntent.setData(Uri.parse("tel:" + number));
                                            startActivity(callIntent);
                                        }


                                    }

                                    @Override
                                    public void whatsapp(PharmacyModel model) {
                                        String contact = "+91" + model.getPhone(); // use country code with your phone number
                                        String url = "https://api.whatsapp.com/send?phone=" + contact;
                                        try {
                                            PackageManager pm = getActivity().getPackageManager();
                                            Log.e("package", String.valueOf(PackageManager.GET_ACTIVITIES));
                                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        } catch (PackageManager.NameNotFoundException e) {
                                            Toast.makeText(getContext(), "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }

                                    }
                                });
                                rec.setAdapter(adpater);
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
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);

    }

    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
            return false;
        } else {
            return true;
        }
    }
}