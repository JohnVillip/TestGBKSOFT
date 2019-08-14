package com.villip.testgbksoft.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.villip.testgbksoft.R;
import com.villip.testgbksoft.ui.main.MapFragment;
import com.villip.testgbksoft.ui.main.PointListFragment;
import com.villip.testgbksoft.ui.main.ProfileFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_point_list, R.string.tab_map, R.string.tab_profile};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return PointListFragment.newInstance();
            case 1:
                return MapFragment.newInstance();
            case 2:
                return ProfileFragment.newInstance();
        }

        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}