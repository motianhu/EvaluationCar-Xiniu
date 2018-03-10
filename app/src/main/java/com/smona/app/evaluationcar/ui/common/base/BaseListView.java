package com.smona.app.evaluationcar.ui.common.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.smona.app.evaluationcar.ui.common.AbstractAdapter;

/**
 * Created by Moth on 2017/2/24.
 */

public abstract class BaseListView extends ListView {

    protected AbstractAdapter mAdapter;

    public BaseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public abstract void init();
}
