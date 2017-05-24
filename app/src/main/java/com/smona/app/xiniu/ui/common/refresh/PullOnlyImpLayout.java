package com.smona.app.xiniu.ui.common.refresh;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Moth
 */
public class PullOnlyImpLayout extends PullOnlyLayout {
    public PullOnlyImpLayout(Context context) {
        super(context);
    }

    public PullOnlyImpLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullOnlyImpLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addObserver() {
        // do nothing
    }

    @Override
    public void deleteObserver() {
        // do nothing
    }

    @Override
    protected void onRefresh() {
        refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    protected void onLoadMore() {
        loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }
}
