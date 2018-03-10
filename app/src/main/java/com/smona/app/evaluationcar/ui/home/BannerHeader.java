package com.smona.app.evaluationcar.ui.home;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.BannerItem;
import com.smona.app.evaluationcar.ui.home.banner.BannerView;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.List;

public class BannerHeader extends LinearLayout {
    private BannerView mBanner;

    public BannerHeader(Context context) {
        super(context);
    }

    public BannerHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    private void initViews() {
        mBanner = (BannerView) findViewById(R.id.banner);
    }

    public void update(List<BannerItem> bannerList) {
        if (bannerList == null || bannerList.size() <= 0) {
            ViewUtil.setViewVisible(mBanner, false);
        } else {
            ViewUtil.setViewVisible(mBanner, true);
            mBanner.update(bannerList);
        }
    }

    public void startPlay() {
        mBanner.startPlay();
    }

    public void stopPlay() {
        mBanner.stopPlay();
    }

}
