package com.smona.app.xiniueval.ui.home.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Moth on 2015/8/31 0031.
 */
public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 4;
    private ContentFragment[] mFragmentHome = new ContentFragment[PAGER_COUNT];

    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentHome[0] = new NoSubmitFragment();
        mFragmentHome[1] = new SubmitedFragment();
        mFragmentHome[2] = new KefuFragment();
        mFragmentHome[3] = new SettingFragment();
    }

    @Override
    public int getCount() {
        return mFragmentHome.length;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentHome[position];
    }


}

