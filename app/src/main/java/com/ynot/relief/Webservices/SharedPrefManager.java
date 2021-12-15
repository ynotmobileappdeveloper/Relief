package com.ynot.relief.Webservices;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ynot.relief.LoginPage;
import com.ynot.relief.Models.LocationModel;

public class SharedPrefManager {


    private static final String SHARED_PREF_NAME = "volleylogin";
    private static final String SHARED_PREF_NAME_SHOP = "shopname";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_MOB = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String STORE_NAME = "shopname";
    private static final String STORE_ID = "shopid";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    private static SharedPrefManager mInstance;
    private static Context ctx;

    private SharedPrefManager(Context context) {

        ctx = context;

    }

    public static synchronized SharedPrefManager getInstatnce(Context context) {

        if (mInstance == null) {

            mInstance = new SharedPrefManager(context);

        }
        return mInstance;

    }
    public LocationModel getshop()
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME_SHOP, Context.MODE_PRIVATE);

        return new LocationModel(
                sharedPreferences.getString(STORE_ID,null),
                sharedPreferences.getString(STORE_NAME,null),
                sharedPreferences.getString(LATITUDE,null),
                sharedPreferences.getString(LONGITUDE,null)

        );
    }


    public void userLogin(User user) {


        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getName());
        editor.putString(KEY_MOB, user.getMob());
        editor.putString(KEY_EMAIL, user.getEmail());

        editor.apply();


    }





    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    public User getUser() {

        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return new User(

                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_MOB, null),
                sharedPreferences.getString(KEY_EMAIL, null)

        );

    }

    public void ShopDetails(LocationModel shopsModel) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME_SHOP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORE_ID, shopsModel.getId());
        editor.putString(STORE_NAME, shopsModel.getName());
        editor.putString(LATITUDE, shopsModel.getLat());
        editor.putString(LONGITUDE, shopsModel.getLon());
        editor.apply();
    }


    public void logout() {


        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent i = new Intent(ctx, LoginPage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(i);
        //   ((Activity)ctx).finish();


    }

    public void clear_shop()
    {
        SharedPreferences sharedPreferences1 = ctx.getSharedPreferences(SHARED_PREF_NAME_SHOP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.clear();
        editor1.apply();
    }


}
