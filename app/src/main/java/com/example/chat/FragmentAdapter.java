package com.example.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragments;
    public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragment) {
        super(fm);
        this.mFragments = fragment;
    }
    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return mFragments.get(arg0);
    }

}
