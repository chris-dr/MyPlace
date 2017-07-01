package com.drevnitskaya.myplace.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by air on 30.06.17.
 */

public class PagesAdapter extends FragmentPagerAdapter {

    private List<Fragment> items = new ArrayList<>();
    private List<String> titles = new ArrayList<>();


    public PagesAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addItem(String title, Fragment item) {
        titles.add(title);
        items.add(item);
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
