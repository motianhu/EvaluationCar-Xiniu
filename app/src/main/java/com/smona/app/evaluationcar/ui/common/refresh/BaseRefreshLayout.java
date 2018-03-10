package com.smona.app.evaluationcar.ui.common.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author Moth
 */
public abstract class BaseRefreshLayout extends RelativeLayout implements INotifyInterface {

    public BaseRefreshLayout(Context context) {
        super(context);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addObserver();
    }

    @Override
    protected void onDetachedFromWindow() {
        deleteObserver();
        super.onDetachedFromWindow();
    }

}
