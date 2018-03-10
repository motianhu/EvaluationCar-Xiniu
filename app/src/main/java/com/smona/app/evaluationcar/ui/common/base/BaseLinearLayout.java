package com.smona.app.evaluationcar.ui.common.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.smona.app.evaluationcar.framework.event.EventProxy;

/**
 * Created by motianhu on 2/27/17.
 */

public abstract class BaseLinearLayout extends LinearLayout {
    public BaseLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public abstract void init();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventProxy.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventProxy.unregister(this);
    }
}
