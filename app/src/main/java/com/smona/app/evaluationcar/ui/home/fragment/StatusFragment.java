package com.smona.app.evaluationcar.ui.home.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.status.StatusPagerAdapter;
import com.smona.app.evaluationcar.ui.status.auditing.AuditingLayer;
import com.smona.app.evaluationcar.ui.status.local.LocalLayer;
import com.smona.app.evaluationcar.ui.status.notpass.NotPassLayer;
import com.smona.app.evaluationcar.ui.status.pass.PassLayer;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.ViewUtil;

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

        final LocalLayer view1 = (LocalLayer) ViewUtil.inflater(getContext(), R.layout.status_local_layer);

        final AuditingLayer view2 = (AuditingLayer) ViewUtil.inflater(getContext(), R.layout.status_auditing_layer);

        final NotPassLayer view3 = (NotPassLayer) ViewUtil.inflater(getContext(), R.layout.status_notpass_layer);

        final PassLayer view4 = (PassLayer) ViewUtil.inflater(getContext(), R.layout.status_pass_layer);


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
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                // This space for rent
                CarLog.d("StatusFragment", "position: " + position);
                switch (position) {
                    case 0:
                        view1.request1Page();
                        break;
                    case 1:
                        view2.request1Page();
                        break;
                    case 2:
                        view3.request1Page();
                        break;
                    case 3:
                        view4.request1Page();
                        break;
                }
            }
        });
    }

    public void changeFragment(int position) {
        mViewPager.setCurrentItem(position);
    }
}
