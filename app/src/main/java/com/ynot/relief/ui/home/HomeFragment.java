package com.ynot.relief.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.ynot.relief.Adapters.CategoryAdapter;
import com.ynot.relief.Adapters.HomeDoctorsAdapter;
import com.ynot.relief.Adapters.HomeViewpagerAdapter;
import com.ynot.relief.Adapters.MedicalAdapter;
import com.ynot.relief.Adapters.MedicineAdpater;
import com.ynot.relief.AllProducts;
import com.ynot.relief.BuildConfig;
import com.ynot.relief.DocDetails;
import com.ynot.relief.EnquiryPage;
import com.ynot.relief.LoginPage;
import com.ynot.relief.Models.HomeCategoryModel;
import com.ynot.relief.Models.HomeDoctorsModel;
import com.ynot.relief.Models.HomeSlider;
import com.ynot.relief.Models.MedicalModel;
import com.ynot.relief.Models.MedicineModel;
import com.ynot.relief.MyPrescription;
import com.ynot.relief.ProductDetail;
import com.ynot.relief.R;
import com.ynot.relief.TypeAMedicine;
import com.ynot.relief.UploadActivity;
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

public class HomeFragment extends Fragment implements View.OnClickListener {

    LinearLayout doctors, lab, pharamcy, registration;
    NavController navController;
    ViewPager viewpager;
    HomeViewpagerAdapter viewpagerAdapter;
    CategoryAdapter adapter;
    MedicalAdapter medic_adpater;
    MedicineAdpater medi_adpater;
    HomeDoctorsAdapter doc_adapter;
    ArrayList<HomeSlider> homeSlider = new ArrayList<>();
    ArrayList<MedicineModel> medi_model = new ArrayList<>();
    ArrayList<MedicalModel> medic_model = new ArrayList<>();
    ArrayList<HomeCategoryModel> categoryModel = new ArrayList<>();
    ArrayList<HomeDoctorsModel> docmodel = new ArrayList<>();
    final long DELAY_MS = 3000;
    final long PERIOD_MS = 6000;
    int currentPage = 0;
    int NUM_PAGES = 4;
    Timer timer;
    RecyclerView cat_rec, doc_rec, medical_rec, medic_rec;
    TextView all_medi;
    Button enquiry, store;
    LinearLayout medicine_layout, doc_layout;
    RelativeLayout update_layout;
    int appversion;
    TextView update_now, track;
    CardView type, products, upload, all_products;
    DotsIndicator dots_indicator;
    ImageView home_image, non_products;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        viewpager = root.findViewById(R.id.viewpager);
        dots_indicator = root.findViewById(R.id.dots_indicator);
        store = root.findViewById(R.id.store);
        cat_rec = root.findViewById(R.id.cat_rec);
        doc_rec = root.findViewById(R.id.doc_rec);
        medical_rec = root.findViewById(R.id.medical_rec);
        medic_rec = root.findViewById(R.id.medic_rec);
        all_medi = root.findViewById(R.id.all_medi);
        update_layout = root.findViewById(R.id.update_layout);
        upload = root.findViewById(R.id.upload);
        update_now = root.findViewById(R.id.update_now);
        enquiry = root.findViewById(R.id.enquiry);
        track = root.findViewById(R.id.track);
        doc_layout = root.findViewById(R.id.doc_layout);
        type = root.findViewById(R.id.type);
        products = root.findViewById(R.id.products);
        home_image = root.findViewById(R.id.home_image);
        non_products = root.findViewById(R.id.non_products);
        appversion = BuildConfig.VERSION_CODE;
        medicine_layout = root.findViewById(R.id.medicine_layout);
        all_products = root.findViewById(R.id.all_products);
        cat_rec.setLayoutManager(new GridLayoutManager(getContext(), 2));
        doc_rec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        medical_rec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        medic_rec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryModel = new ArrayList<>();
        categoryModel.add(new HomeCategoryModel(R.drawable.ic_doctor, "Doctor\nBooking"));
        categoryModel.add(new HomeCategoryModel(R.drawable.ic_nurse, "Nursing\nCare"));
        categoryModel.add(new HomeCategoryModel(R.drawable.ic_medicine_book, "Purchase\nMedicine"));
        categoryModel.add(new HomeCategoryModel(R.drawable.ic_checkup, "Medical\nCheckup"));

        adapter = new CategoryAdapter(getContext(), categoryModel, new CategoryAdapter.Click() {
            @Override
            public void ItemClick(HomeCategoryModel model, int position) {

                Log.e("pos", String.valueOf(position));

                if (position == 0) {
                    navController.navigate(R.id.nav_doctors);
                }
                if (position == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "nurse");
                    navController.navigate(R.id.nurse_booking, bundle);
                }
                if (position == 2) {
                    navController.navigate(R.id.product);
                }
                if (position == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "medical");
                    navController.navigate(R.id.nurse_booking, bundle);
                }
            }
        });
        cat_rec.setAdapter(adapter);

        medi_model = new ArrayList<>();

        medic_adpater = new MedicalAdapter(getContext(), medic_model, new MedicalAdapter.Click() {
            @Override
            public void Itemclick() {
                Bundle bundle = new Bundle();
                bundle.putString("type", "medical");
                navController.navigate(R.id.nurse_booking, bundle);
            }
        });
        medical_rec.setAdapter(medic_adpater);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AllProducts.class);
                i.putExtra("id", "3");
                startActivity(i);
            }
        });
        all_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AllProducts.class);
                i.putExtra("id", "1");
                startActivity(i);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UploadActivity.class));
            }
        });
        // get_products();
        //get_doctors();
        Glide.with(getContext()).load(R.drawable.junior).into(home_image);
        Glide.with(getContext()).load(R.drawable.non).into(non_products);

        homeSlider.add(new HomeSlider(R.drawable.slider_one));
        homeSlider.add(new HomeSlider(R.drawable.slider_two));
        homeSlider.add(new HomeSlider(R.drawable.slider_three));
        homeSlider.add(new HomeSlider(R.drawable.slider_four));
        viewpagerAdapter = new HomeViewpagerAdapter(getContext(), homeSlider);
        viewpager.setAdapter(viewpagerAdapter);
        dots_indicator.setViewPager(viewpager);
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

        // viewpager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        //  viewpager.setPadding(70, 0, 70, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        // viewpager.setPageMargin(20);

        // get_slider();


        all_medi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AllProducts.class));
            }
        });


        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.store_frag);
            }
        });

        update_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
                startActivity(intent);*/
            }
        });
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MyPrescription.class));
            }
        });

        enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                    startActivity(new Intent(getContext(), EnquiryPage.class));
                } else {
                    startActivity(new Intent(getContext(), LoginPage.class));
                }
            }
        });

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TypeAMedicine.class));
            }
        });

        return root;
    }

    private void get_doctors() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.AVAILABLE_DOC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("doc", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                doc_layout.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("doc");
                                docmodel = new ArrayList<>();
                                if (array.length() == 0) {
                                    doc_layout.setVisibility(View.GONE);
                                } else {
                                    doc_layout.setVisibility(View.VISIBLE);
                                }
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obb = array.getJSONObject(i);
                                    docmodel.add(new HomeDoctorsModel(obb.getString("doc_id"), obb.getString("name"), obb.getString("image"), obb.getString("qualification"), obb.getString("patients_count"), obb.getString("experience"), obb.getString("rating")));
                                }
                                doc_adapter = new HomeDoctorsAdapter(getContext(), docmodel, new HomeDoctorsAdapter.Click() {
                                    @Override
                                    public void ItemClick(HomeDoctorsModel model) {
                                        if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                                            Intent i = new Intent(getContext(), DocDetails.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            i.putExtra("id", model.getId());
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getContext(), LoginPage.class));
                                        }

                                    }
                                });
                                doc_rec.setAdapter(doc_adapter);


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

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void get_products() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("products", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                medicine_layout.setVisibility(View.VISIBLE);
                                JSONArray array = ob.getJSONArray("products");
                                medi_model = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);

                                    String amount = String.format("%.2f", obj.getDouble("price"));
                                    medi_model.add(new MedicineModel(obj.getString("p_id"), obj.getString("image"), obj.getString("brand"), obj.getString("name"), amount, obj.getString("fav_status")));
                                }
                                medi_adpater = new MedicineAdpater(getContext(), medi_model, new MedicineAdpater.Click() {
                                    @Override
                                    public void itemClick(MedicineModel model) {

                                        if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
                                            Intent i = new Intent(getContext(), ProductDetail.class);
                                            i.putExtra("id", model.getId());
                                            i.putExtra("name", model.getName());
                                            startActivity(i);
                                        } else {
                                            startActivity(new Intent(getContext(), LoginPage.class));
                                        }
                                    }
                                });
                                medic_rec.setAdapter(medi_adpater);
                            } else {
                                medicine_layout.setVisibility(View.GONE);

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
                params.put("sub_id", "0");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

   /* private void get_slider() {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLs.distributor_registration,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob=new JSONObject(response);

                            if (ob.getBoolean("status"))
                            {

                            }
                            else
                            {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();

                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }*/

    @Override
    public void onClick(View view) {

        if (view == doctors) {
            navController.navigate(R.id.nav_doctors);
        }
        if (view == lab) {
        }
        if (view == pharamcy) {
            navController.navigate(R.id.nav_pharmacy);
        }
        if (view == registration) {

            navController.navigate(R.id.nav_reg);
            /*RegistrationFragment reg = new RegistrationFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.nav_host_fragment, reg).addToBackStack(null).commit();*/
        }

    }

    private void check_updation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("update", response);
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                if (appversion < ob.getInt("version")) {
                                    update_layout.setVisibility(View.VISIBLE);
                                    update_layout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.down_to_up));
                                } else {
                                    // update_layout.setVisibility(View.GONE);
                                }

                            } else {
                                //update_layout.setVisibility(View.GONE);
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
                params.put("version_code", String.valueOf(appversion));
                Log.e("update_params", String.valueOf(params));
                return params;
            }
        };
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);

    }

    private void track_order() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CHECK_ORDERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ob = new JSONObject(response);
                            if (ob.getBoolean("status")) {
                                track.setVisibility(View.VISIBLE);
                            } else {
                                track.setVisibility(View.GONE);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getmInstance(getContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        // check_updation();
        if (SharedPrefManager.getInstatnce(getContext()).isLoggedIn()) {
            //track_order();
        }
    }
}
