package com.smona.app.evaluationcar.ui.common.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

/**
 * Created by motianhu on 3/7/17.
 */

public class LimitGridView extends GridView {
    public LimitGridView(Context paramContext) {
        super(paramContext);
    }

    public LimitGridView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public LimitGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
        if (paramMotionEvent.getAction() == MotionEvent.ACTION_MOVE)
            return true;
        return super.dispatchTouchEvent(paramMotionEvent);
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    }
}