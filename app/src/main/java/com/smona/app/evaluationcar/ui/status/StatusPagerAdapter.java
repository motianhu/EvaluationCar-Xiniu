package com.smona.app.evaluationcar.ui.status;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by motianhu on 2/28/17.
 */

public class StatusPagerAdapter extends PagerAdapter {
    private List<String> mTitleList;
    private List<View> mViewList;

    public StatusPagerAdapter(List<String> titleList, List<View> viewList) {
        mViewList = viewList;
        mTitleList = titleList;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
