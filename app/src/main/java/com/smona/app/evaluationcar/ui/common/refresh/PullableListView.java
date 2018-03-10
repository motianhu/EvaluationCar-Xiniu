package com.smona.app.evaluationcar.ui.common.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public abstract class PullableListView extends ListView implements Pullable {

    public PullableListView(Context context) {
        super(context);
    }

    public PullableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (getCount() == 0) {
            return false;
        } else if (isPageTop() && getFirstVisiblePosition() == 0 && getChildAt(0) != null && getChildAt(0).getTop() >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canPullUp() {
        if (getCount() == 0) {
            return false;
        } else if (isPageLast() && (getLastVisiblePosition() == (getCount() - 1))) {
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(getLastVisiblePosition()
                    - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }

    protected boolean isPageTop() {
        return true;
    }

    protected abstract boolean isPageLast();
}
