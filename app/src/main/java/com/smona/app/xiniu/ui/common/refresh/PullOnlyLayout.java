package com.smona.app.xiniu.ui.common.refresh;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Moth
 */
public abstract class PullOnlyLayout extends PullToRefreshLayout {
    public PullOnlyLayout(Context context) {
        super(context);
    }

    public PullOnlyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PullOnlyLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void changeState(int to) {
        justChangeState(to);
    }

    @Override
    protected void loadmoreFinish(int refreshResult) {
        justLoadMoreFinish(refreshResult);
    }

    @Override
    protected void initView() {
        // do nothing
    }
}
