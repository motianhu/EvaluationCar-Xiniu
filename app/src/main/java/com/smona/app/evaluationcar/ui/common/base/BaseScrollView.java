package com.smona.app.evaluationcar.ui.common.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by motianhu on 3/7/17.
 */

public class BaseScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;

    public BaseScrollView(Context paramContext) {
        super(paramContext);
    }

    public BaseScrollView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public BaseScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        if (this.scrollViewListener == null)
            return;
        this.scrollViewListener.onScrollChanged(this, paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void setScrollViewListener(ScrollViewListener paramScrollViewListener) {
        this.scrollViewListener = paramScrollViewListener;
    }
}