package com.project.level4.adgo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.level4.adgo.R;
import com.project.level4.adgo.fragments.BagFragment;
import com.project.level4.adgo.fragments.MapFragment;

/**
 * Created by Rob on 2/28/17.
 */

public class FixedTabsPagerAdapter extends FragmentPagerAdapter {
    Context context;

    public FixedTabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return BagFragment.newInstance();
            case 1:
                return MapFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position) {
            case (0):
                return context.getResources().getString(R.string.bag_fragment_title);
            case (1):
                return context.getResources().getString(R.string.map_fragment_title);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
