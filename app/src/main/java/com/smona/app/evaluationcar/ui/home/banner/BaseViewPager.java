package com.smona.app.evaluationcar.ui.home.banner;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

public abstract class BaseViewPager extends ViewGroupPager {
    private static final String TAG = "BaseViewPager";
    private static final long DELAY_TIME = 5000;
    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isScrollEnable()) {
                slideToNextPage();
            }
            mHandler.postDelayed(mRunnable, DELAY_TIME);
        }
    };

    public BaseViewPager(Context context) {
        super(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void stopAutoScroll() {
        mHandler.removeCallbacks(mRunnable);
    }

    public void startAutoScroll() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, DELAY_TIME);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                stopAutoScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startAutoScroll();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAutoScroll();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        startAutoScroll();
        super.onAttachedToWindow();
    }

    public abstract boolean isScrollEnable();

}
