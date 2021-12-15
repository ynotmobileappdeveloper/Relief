package com.ynot.relief.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ynot.relief.MedicineFragment;
import com.ynot.relief.Models.SubcategoryModel;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    ArrayList<SubcategoryModel> model;
    String page;

    public TabAdapter(FragmentManager fm, int NumOfTabs, ArrayList<SubcategoryModel> model, String page) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.model = model;
        this.page = page;

    }

    @Override
    public Fragment getItem(int position) {

        return new MedicineFragment(model.get(position).getId(),page);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
