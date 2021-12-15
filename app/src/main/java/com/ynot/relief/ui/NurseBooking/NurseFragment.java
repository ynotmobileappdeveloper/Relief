package com.ynot.relief.ui.NurseBooking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ynot.relief.Adapters.HomeViewpagerAdapter;
import com.ynot.relief.Adapters.NurseAdapter;
import com.ynot.relief.Adapters.PharmacyAdpater;
import com.ynot.relief.LoginPage;
import com.ynot.relief.Models.HomeSlider;
import com.ynot.relief.Models.NurseModel;
import com.ynot.relief.Models.PharmacyModel;
import com.ynot.relief.NurseBooking;
import com.ynot.relief.NurseDetailPage;
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
import java.util.Timer;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class NurseFragment extends Fragment implements View.OnClickListener {

    ViewPager viewpager;
    HomeViewpagerAdapter viewpagerAdapter;
    ArrayList<HomeSlider> homeSlider = new ArrayList<>();
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 6000;
    int currentPage = 0;
    int NUM_PAGES = 5;
    Timer timer;
    ArrayList<NurseModel> model = new ArrayList<>();
    ArrayList<PharmacyModel> store_model = new ArrayList<>();
    NurseAdapter adapter;
    PharmacyAdpater shop_adapter;
    RecyclerView rec;
    ACProgressFlower dialog;
    String type;
    TextView type_name;
    ImageView nodata;
    String latitude = "", longitude = "", city = "";
    ImageView search_view;
    SearchView search;
    LinearLayout search_layout, package_layout;
    TextView lab, result;
    String data = "1";
    String lab_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nurse, container, false);
        viewpager = root.findViewById(R.id.viewpager);
        rec = root.findViewById(R.id.rec);
        type_name = root.findViewById(R.id.type);
        nodata = root.findViewById(R.id.nodata);
        search_view = root.findViewById(R.id.search_view);
        search = root.findViewById(R.id.search);
        search_layout = root.findViewById(R.id.search_layout);
        package_layout = root.findViewById(R.id.package_layout);
        lab = root.findViewById(R.id.lab);
        result = root.findViewById(R.id.result);
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

        if (getArguments() != null) {
            type = getArguments().getString("type");
        }
        if (type.equals("nurse")) {
            type_name.setText("Nursing Services");
            get_nursing_services();
        } else if (type.equals("medical")) {
            type_name.setText("Medical Checkup");
            get_labs();
        } else {
            package_layout.setVisibility(View.VISIBLE);
            type_name.setText("Medical Checkup");
            lab_id = getArguments().getString("id");
            get_packages(lab_id);
        }

        dialog.setCancelable(false);
        lab.setOnClickListener(this);
        result.setOnClickListener(this);


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

        if (type.equals("nurse")) {
            search.setQueryHint("Search Nursing Services...");
        } else if (type.equals("service")) {
            search.setQueryHint("Search Packages...");
        }
        search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.size() > 0) {
                    if (type.equals("service")) {
                        search.setVisibility(View.VISIBLE);
                        search_layout.setVisibility(View.GONE);
                        search.setIconified(false);
                        search.setIconifiedByDefault(true);
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
                    }
                }
                if (store_model.size() > 0) {
                    if (type.equals("medical")) {
                        search.setQueryHint("Search Labs...");
                        search.setVisibility(View.VISIBLE);
                        search_layout.setVisibility(View.GONE);
                        search.setIconified(false);
                        search.setIconifiedByDefault(true);
                        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                if (TextUtils.isEmpty(query)) {
                                    shop_adapter.getfilter().filter("");
                                    shop_adapter.notifyDataSetChanged();


                                } else {
                                    shop_adapter.getfilter().filter(query.toString());
                                    shop_adapter.notifyDataSetChanged();
                                }

                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                if (TextUtils.isEmpty(newText)) {
                                    shop_adapter.getfilter().filter("");
                                    shop_adapter.notifyDataSetChanged();
                                } else {
                                    shop_adapter.getfilter().filter(newText.toString());
                                    shop_adapter.notifyDataSetChanged();
                                }
                                return false;
                            }
                        });
                    }
                }
            }
        });


        return root;
    }

    private void get_labs() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_LABS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject ob = new JSONObject(response);
                            Log.e("labs", response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);

                                JSONArray array = ob.getJSONArray("data");
                                store_model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    store_model.add(new PharmacyModel(obj.getString("id"), obj.getString("name"), R.drawable.medi, obj.getString("address_line_1"), "null", obj.getString("mobile"), obj.getString("mobile")));
                                }
                                shop_adapter = new PharmacyAdpater(getContext(), store_model, new PharmacyAdpater.Click() {
                                    @Override
                                    public void itemClick(PharmacyModel model) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "service");
                                        bundle.putString("id", model.getId());
                                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                        navController.navigate(R.id.nurse_booking, bundle);
                                       /* Intent i = new Intent(getContext(), PharmacyDetails.class);
                                        i.putExtra("id", model.getId());
                                        i.putExtra("phone", "+91" + model.getPhone());
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);*/
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
                                rec.setAdapter(shop_adapter);
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
                params.put("user_id", SharedPrefManager.getInstatnce(getContext()).getUser().getId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void get_packages(final String lab_id) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_PACKAGE_SERVICES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("service", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("services");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new NurseModel(obb.getString("id"), obb.getString("image"), obb.getString("name"), obb.getString("type"), obb.getString("fav_status"), String.format("%.2f", obb.getDouble("offer_price")), String.format("%.2f", obb.getDouble("actual_price"))));
                                }
                                adapter = new NurseAdapter(getContext(), model, new NurseAdapter.Click() {
                                    @Override
                                    public void ItemClik(NurseModel list) {
                                        Intent i = new Intent(getContext(), NurseDetailPage.class);
                                        i.putExtra("id", list.getId());
                                        i.putExtra("type", data);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);

                                    }

                                    @Override
                                    public void booknow(NurseModel list) {
                                        if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                                            Intent i = new Intent(getContext(), NurseBooking.class);
                                            i.putExtra("id", list.getId());
                                            i.putExtra("data", list.getService());
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getContext(), LoginPage.class));
                                        }
                                    }
                                });
                                rec.setAdapter(adapter);

                            } else {
                                nodata.setVisibility(View.VISIBLE);
                                rec.setAdapter(null);

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
                params.put("lab_id", lab_id);
                params.put("type", data);
                Log.e("service_input", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void get_nursing_services() {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_NURSING_SERVICES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("service", response);
                        try {
                            JSONObject ob = new JSONObject(response);

                            if (ob.getBoolean("status")) {
                                nodata.setVisibility(View.GONE);
                                JSONArray array = ob.getJSONArray("services");
                                model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    model.add(new NurseModel(obb.getString("id"), obb.getString("image"), obb.getString("name"), obb.getString("type"), obb.getString("fav_status"), String.format("%.2f", obb.getDouble("offer_price")), String.format("%.2f", obb.getDouble("actual_price"))));
                                }
                                adapter = new NurseAdapter(getContext(), model, new NurseAdapter.Click() {
                                    @Override
                                    public void ItemClik(NurseModel list) {
                                        Intent i = new Intent(getContext(), NurseDetailPage.class);
                                        i.putExtra("id", list.getId());
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);

                                    }

                                    @Override
                                    public void booknow(NurseModel list) {
                                        if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                                            Intent i = new Intent(getContext(), NurseBooking.class);
                                            i.putExtra("id", list.getId());
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getContext(), LoginPage.class));
                                        }
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

    @Override
    public void onClick(View v) {
        if (v == lab) {
            lab.setTextColor(getResources().getColor(android.R.color.white));
            lab.setBackgroundResource(R.drawable.tab_selected_bg);
            result.setBackgroundResource(R.drawable.tab_bg);
            result.setTextColor(getResources().getColor(android.R.color.black));
            data = "1";
            get_packages(lab_id);
        }
        if (v == result) {
            result.setTextColor(getResources().getColor(android.R.color.white));
            result.setBackgroundResource(R.drawable.tab_selected_bg);
            lab.setBackgroundResource(R.drawable.tab_bg);
            lab.setTextColor(getResources().getColor(android.R.color.black));
            data = "2";
            get_packages(lab_id);
        }
    }
}