package com.smona.app.evaluationcar.ui.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.BannerItem;
import com.smona.app.evaluationcar.data.item.NewsItem;
import com.smona.app.evaluationcar.ui.common.base.BaseListView;
import com.smona.app.evaluationcar.ui.home.more.MoreNewsActivity;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.List;

/**
 * Created by Moth on 2017/2/24.
 */

public class HomeListView extends BaseListView {
    private static final String TAG = HomeListView.class.getSimpleName();
    private BannerHeader mHeader;

    public HomeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init() {
        mHeader = (BannerHeader) ViewUtil.inflater(getContext(), R.layout.banner_header);
        this.addHeaderView(mHeader);

        findViewById(R.id.moreNews).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.jumpOnlyActivity(getContext(), MoreNewsActivity.class);
            }
        });

        mAdapter = new HomeAdapter(getContext());
        this.setAdapter(mAdapter);
    }

    public void updateHeader(List<BannerItem> list) {
        mHeader.update(list);
    }

    public void updateAdapter(List<NewsItem> list) {
        mAdapter.update(list);
    }
}
