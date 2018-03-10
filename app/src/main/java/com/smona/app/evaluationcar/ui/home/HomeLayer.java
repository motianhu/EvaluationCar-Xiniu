package com.smona.app.evaluationcar.ui.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.param.BannerParam;
import com.smona.app.evaluationcar.data.event.BannerEvent;
import com.smona.app.evaluationcar.data.event.NewsEvent;
import com.smona.app.evaluationcar.data.item.BannerItem;
import com.smona.app.evaluationcar.data.item.NewsItem;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.ui.common.base.BaseLinearLayout;
import com.smona.app.evaluationcar.ui.common.refresh.NetworkTipUtil;
import com.smona.app.evaluationcar.util.ViewUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by motianhu on 4/11/17.
 */

public class HomeLayer extends BaseLinearLayout {
    private static final String TAG = HomeLayer.class.getSimpleName();

    private HomeListView mHomeList;
    private View mNoContent;
    private View mLoading;
    private OnClickListener mReloadClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setLoading();
            requestBanner();
            requestNews();
        }
    };

    public HomeLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init() {
        mHomeList = (HomeListView) findViewById(R.id.content_list);
        mNoContent = findViewById(R.id.no_content);
        mLoading = findViewById(R.id.loading);

        setLoading();
    }

    private void setLoading() {
        ViewUtil.setViewVisible(mHomeList, false);
        ViewUtil.setViewVisible(mLoading, true);
        ViewUtil.setViewVisible(mNoContent, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(BannerEvent event) {
        List<BannerItem> list = (List<BannerItem>) event.getContent();
        if (list != null) {
            mHomeList.updateHeader(list);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(NewsEvent event) {
        List<NewsItem> list = (List<NewsItem>) event.getContent();
        if (list != null) {
            setHasData();
            mHomeList.updateAdapter(list);
        } else {
            setNoData();
        }
    }

    private void setNoData() {
        ViewUtil.setViewVisible(mLoading, false);
        ViewUtil.setViewVisible(mHomeList, false);
        ViewUtil.setViewVisible(mNoContent, true);
        NetworkTipUtil.showNetworkTip(this, mReloadClick);
    }

    private void setHasData() {
        ViewUtil.setViewVisible(mNoContent, false);
        ViewUtil.setViewVisible(mLoading, false);
        ViewUtil.setViewVisible(mHomeList, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        request();
    }


    private void request() {
        requestBanner();
        requestNews();
    }

    private void requestNews() {
        BannerParam param = new BannerParam();
        param.classType = "最新资讯";
        DataDelegator.getInstance().requestLatestNews(param);
    }

    private void requestBanner() {
        DataDelegator.getInstance().queryPageElementLatest();
    }

}
