package com.smona.app.xiniu.ui.home.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.smona.app.xiniu.R;
import com.smona.app.xiniu.ui.status.StatusPagerAdapter;
import com.smona.app.xiniu.ui.status.auditing.AuditingLayer;
import com.smona.app.xiniu.ui.status.local.LocalLayer;
import com.smona.app.xiniu.ui.status.notpass.NotPassLayer;
import com.smona.app.xiniu.ui.status.pass.PassLayer;
import com.smona.app.xiniu.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moth on 2015/8/28 0028.
 */

public class StatusFragment extends ContentFragment {

    private ViewPager mViewPager;

    protected int getLayoutId() {
        return R.layout.fragment_list;
    }


    protected void init(View root) {
        mViewPager = (ViewPager) root.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tabs);

        LocalLayer view1 = (LocalLayer) ViewUtil.inflater(getContext(), R.layout.status_local_layer);

        AuditingLayer view2 = (AuditingLayer) ViewUtil.inflater(getContext(), R.layout.status_auditing_layer);

        NotPassLayer view3 = (NotPassLayer) ViewUtil.inflater(getContext(), R.layout.status_notpass_layer);

        PassLayer view4 = (PassLayer) ViewUtil.inflater(getContext(), R.layout.status_pass_layer);


        List<View> viewList = new ArrayList<View>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);

        List<String> titleList = new ArrayList<String>();
        titleList.add(getContext().getResources().getString(R.string.uncommit));
        titleList.add(getContext().getResources().getString(R.string.auditing));
        titleList.add(getContext().getResources().getString(R.string.notpass));
        titleList.add(getContext().getResources().getString(R.string.pass));

        StatusPagerAdapter pagerAdapter = new StatusPagerAdapter(titleList, viewList);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(0)));//添加tab选项卡
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(3)));


        mViewPager.setAdapter(pagerAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。

        mViewPager.setAdapter(pagerAdapter);
    }

    public void changeFragment(int position) {
        mViewPager.setCurrentItem(position);
    }
}
