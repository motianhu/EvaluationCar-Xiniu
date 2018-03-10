package com.smona.app.evaluationcar.ui.evaluation.preevaluation;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;
import com.smona.app.evaluationcar.ui.evaluation.preevaluation.quick.QuickPreevaluationActivity;
import com.smona.app.evaluationcar.ui.status.StatusPagerAdapter;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.StatusUtils;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moth on 2017/3/6.
 */
public class PreEvaluationActivity extends HeaderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preevaluation;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.evalution_pre;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        View local = ViewUtil.inflater(this, R.layout.preevaluation_local_list_layer);
        View pass = ViewUtil.inflater(this, R.layout.preevaluation_pass_list_layer);
        View notpass = ViewUtil.inflater(this, R.layout.preevaluation_notpass_list_layer);

        List<View> viewList = new ArrayList<View>();
        viewList.add(local);
        viewList.add(pass);
        viewList.add(notpass);

        List<String> titleList = new ArrayList<String>();
        titleList.add(this.getResources().getString(R.string.evalution_pre_status_local));
        titleList.add(this.getResources().getString(R.string.evalution_pre_status_pass));
        titleList.add(this.getResources().getString(R.string.evalution_pre_status_notpass));

        StatusPagerAdapter pagerAdapter = new StatusPagerAdapter(titleList, viewList);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(1)));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pagerAdapter);

        findViewById(R.id.normal_preeva).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        findViewById(R.id.quick_preeva).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.jumpQuickPreEvaluation(PreEvaluationActivity.this, StatusUtils.BILL_STATUS_NONE, "", 0, QuickPreevaluationActivity.class);
            }
        });
    }
}
