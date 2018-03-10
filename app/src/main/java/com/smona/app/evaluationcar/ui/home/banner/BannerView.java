package com.smona.app.evaluationcar.ui.home.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.BannerItem;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class BannerView extends FrameLayout {
    private static final String TAG = BannerView.class.getSimpleName();

    private int mPageCount = 0;
    private int mIndicatorCount = 0;

    private BannerViewPager mBannerViewPager;
    private LinearLayout mIndicatorGroup;

    private int mAdIndicatorSize = 0;
    private int mAdIndicatorMargin = 0;

    private List<BannerItem> mDataList = new ArrayList<BannerItem>();
    private Object mLock = new Object();
    private BannerViewPager.PageSelecteListener mSelectListener = new BannerViewPager.PageSelecteListener() {
        @Override
        public void onPageSelecteListener(int pos) {
            setIndicatorEnabled(pos % mIndicatorCount);
        }
    };

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    @Override
    public void setVisibility(int visibility) {
        boolean isVisible = VISIBLE == visibility;
        if (isVisible) {
            startPlay();
        } else {
            stopPlay();
        }
        super.setVisibility(visibility);
    }

    private void initViews() {
        mAdIndicatorSize = getContext().getResources().getDimensionPixelSize(R.dimen.banner_indicator_size);
        mAdIndicatorMargin = getContext().getResources().getDimensionPixelSize(R.dimen.banner_indicator_margin);
        mIndicatorGroup = (LinearLayout) findViewById(R.id.indicator);
        mBannerViewPager = (BannerViewPager) findViewById(R.id.view_pager);
        mBannerViewPager.setPageSelecteListener(mSelectListener);
    }

    private void updateBanner() {
        Context context = getContext();
        updateBannerView(context, mPageCount);
        updateIndicator(context, mIndicatorGroup, mIndicatorCount);
    }

    private void updateBannerView(final Context context, int count) {
        mBannerViewPager.update(mDataList);
    }

    private void updateIndicator(final Context context, ViewGroup layout,
                                 int count) {
        layout.removeAllViews();
        LinearLayout.LayoutParams params;
        View dot;
        for (int i = 0; i < count; i++) {
            dot = ViewUtil.inflater(getContext(), R.layout.banner_scroll_indicator);
            params = new LinearLayout.LayoutParams(mAdIndicatorSize, mAdIndicatorSize);
            params.leftMargin = mAdIndicatorMargin;
            params.rightMargin = mAdIndicatorMargin;
            dot.setLayoutParams(params);
            dot.setEnabled(false);
            layout.addView(dot);
        }

        if (layout.getChildCount() > 0) {
            layout.getChildAt(0).findViewById(R.id.view_normal).setAlpha(0);
            layout.getChildAt(0).findViewById(R.id.view_activite).setAlpha(1);
        }
    }

    private void setIndicatorEnabled(int pos) {
        for (int i = 0; i < mIndicatorGroup.getChildCount(); i++) {
            mIndicatorGroup.getChildAt(i).findViewById(R.id.view_normal).setAlpha(1);
            mIndicatorGroup.getChildAt(i).findViewById(R.id.view_activite).setAlpha(0);
        }

        if (mIndicatorGroup.getChildCount() > 0) {
            mIndicatorGroup.getChildAt(pos).findViewById(R.id.view_normal).setAlpha(0);
            mIndicatorGroup.getChildAt(pos).findViewById(R.id.view_activite).setAlpha(1);
        }
    }

    public void startPlay() {
        mBannerViewPager.startAutoScroll();
    }

    public void stopPlay() {
        mBannerViewPager.stopAutoScroll();
    }

    public void update(List<BannerItem> bannerList) {
        updateDataList(bannerList);
        updateBanner();
    }

    private void updateDataList(List<BannerItem> bannerList) {
        synchronized (mLock) {
            mDataList.clear();
            for (BannerItem info : bannerList) {
                mDataList.add(info);
            }
            int size = mDataList.size();
            mIndicatorCount = size;
            mPageCount = size;
        }
    }
}
