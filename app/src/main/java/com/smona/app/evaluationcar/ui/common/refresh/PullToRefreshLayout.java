package com.smona.app.evaluationcar.ui.common.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;

/**
 * @author Moth
 */
public abstract class PullToRefreshLayout extends BaseRefreshLayout {
    public static final String TAG = PullToRefreshLayout.class.getSimpleName();
    public static final int INIT = 0;
    public static final int RELEASE_TO_REFRESH = 1;
    public static final int REFRESHING = 2;
    public static final int RELEASE_TO_LOAD = 3;
    public static final int LOADING = 4;
    public static final int DONE = 5;
    public static final int SUCCEED = 0;
    public static final int FAIL = 1;
    public static final int LAST = 2;
    private static final int DIST = 100;
    protected int mState = INIT;
    protected View mRefreshView;
    protected View mLoadmoreView;
    protected View mLoadingView;
    protected TextView mLoadStateTextView;
    private float mDownY, mLastY;
    private float mPullDownY = 0;
    private float mPullUpY = 0;
    private float mRefreshDist = 200;
    private float mLoadmoreDist = 200;
    private float mSpeed = 8;
    private boolean mIsLayout = false;
    private boolean mIsTouch = false;
    protected Runnable mUpdateRunnable = new Runnable() {

        @Override
        public void run() {

            mSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2
                    / getMeasuredHeight() * (mPullDownY + Math.abs(mPullUpY))));
            boolean removed = false;

            if (!mIsTouch) {
                if (mState == REFRESHING && mPullDownY <= mRefreshDist) {
                    mPullDownY = mRefreshDist;
                    removeCallbacks(mUpdateRunnable);
                } else if (mState == LOADING && -mPullUpY <= mLoadmoreDist) {
                    mPullUpY = -mLoadmoreDist;
                    removeCallbacks(mUpdateRunnable);
                }
            }
            if (mPullDownY > 0) {
                mPullDownY -= mSpeed;
            } else if (mPullUpY < 0) {
                mPullUpY += mSpeed;
            }
            if (mPullDownY < 0) {
                mPullDownY = 0;
                if (mState != REFRESHING && mState != LOADING)
                    changeState(INIT);
                removeCallbacks(mUpdateRunnable);
                removed = true;
            }
            if (mPullUpY > 0) {
                mPullUpY = 0;
                if (mState != REFRESHING && mState != LOADING)
                    changeState(INIT);
                removeCallbacks(mUpdateRunnable);
                removed = true;
            }
            if (mPullDownY == 0 && mPullUpY == 0) {
                removeCallbacks(mUpdateRunnable);
                removed = true;
            }
            requestLayout();
            if (!removed) {
                postDelayed(mUpdateRunnable, 5);
            }
        }
    };
    protected Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            changeState(DONE);
            post(mUpdateRunnable);
        }
    };
    private float mRadio = 2;
    private View mPullableView;
    private int mEvents;
    private boolean mCanPullDown = true;
    private boolean mCanPullUp = true;
    private Runnable mFailRunnable = new Runnable() {

        @Override
        public void run() {
            loadmoreFinish(PullToRefreshLayout.FAIL);
        }
    };

    public PullToRefreshLayout(Context context) {
        super(context);
        init();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        mRefreshDist = DIST * density;
        mLoadmoreDist = DIST * density;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    public void refreshFinish(int refreshResult) {
        changeState(DONE);
        post(mUpdateRunnable);
    }

    protected void postLoadmoreFail() {
        postDelayed(mFailRunnable, 1000);
    }

    protected void loadmoreFinish(int refreshResult) {
        mLoadingView.setVisibility(View.GONE);
        switch (refreshResult) {
            case SUCCEED:
                mLoadStateTextView.setText("");
                break;
            case LAST:
                mLoadStateTextView.setText(R.string.already_bottom);
                break;
            case FAIL:
            default:
                mLoadStateTextView.setText(R.string.load_fail);
                break;
        }
        if (refreshResult == SUCCEED) {
            changeState(DONE);
            post(mUpdateRunnable);
        } else {
            postDelayed(mRunnable, 600);
        }
    }

    protected void justLoadMoreFinish(int refreshResult) {
        if (refreshResult == SUCCEED) {
            changeState(DONE);
            post(mUpdateRunnable);
        } else {
            postDelayed(mRunnable, 600);
        }
    }

    protected void justChangeState(int to) {
        mState = to;

    }

    protected void changeState(int to) {
        mState = to;
        switch (mState) {
            case INIT:
                mLoadStateTextView.setText(R.string.pullup_to_load);
                break;
            case RELEASE_TO_REFRESH:
                break;
            case REFRESHING:

                break;
            case RELEASE_TO_LOAD:
                mLoadStateTextView.setText(R.string.release_to_load);
                break;
            case LOADING:
                mLoadingView.setVisibility(View.VISIBLE);
                mLoadStateTextView.setText(R.string.loading);
                break;
            case DONE:
                break;
        }
    }

    private void releasePull() {
        mCanPullDown = true;
        mCanPullUp = true;
    }

    protected boolean disptchSuperTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isChildInForceTouch() && ev.getAction() != MotionEvent.ACTION_UP) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                mLastY = mDownY;
                removeCallbacks(mUpdateRunnable);
                mEvents = 0;
                releasePull();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mEvents == 0) {
                    if (((Pullable) mPullableView).canPullDown() && mCanPullDown
                            && mState != LOADING) {
                        mPullDownY = mPullDownY + (ev.getY() - mLastY) / mRadio;
                        if (mPullDownY < 0) {
                            mPullDownY = 0;
                            mCanPullDown = false;
                            mCanPullUp = true;
                        }
                        if (mPullDownY > getMeasuredHeight() / 4.0f)
                            mPullDownY = getMeasuredHeight() / 4.0f;
                        if (mState == REFRESHING) {
                            mIsTouch = true;
                        }
                    } else if (((Pullable) mPullableView).canPullUp() && mCanPullUp
                            && mState != REFRESHING) {
                        mPullUpY = mPullUpY + (ev.getY() - mLastY) / mRadio;
                        if (mPullUpY > 0) {
                            mPullUpY = 0;
                            mCanPullDown = true;
                            mCanPullUp = false;
                        }
                        if (mPullUpY < -getMeasuredHeight() / 4.0f)
                            mPullUpY = -getMeasuredHeight() / 4.0f;
                        if (mState == LOADING) {
                            mIsTouch = true;
                        }
                    } else {
                        releasePull();
                    }
                } else {
                    mEvents = 0;
                }
                mLastY = ev.getY();
                // 根据下拉距离改变比例
                mRadio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                        * (mPullDownY + Math.abs(mPullUpY))));
                requestLayout();
                if (mPullDownY <= mRefreshDist && mState == RELEASE_TO_REFRESH) {
                    changeState(INIT);
                }
                if (mPullDownY >= mRefreshDist && mState == INIT) {
                    changeState(RELEASE_TO_REFRESH);
                }
                if (-mPullUpY <= mLoadmoreDist && mState == RELEASE_TO_LOAD) {
                    changeState(INIT);
                }
                if (-mPullUpY >= mLoadmoreDist && mState == INIT) {
                    changeState(RELEASE_TO_LOAD);
                }
                if ((mPullDownY + Math.abs(mPullUpY)) > 8) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mPullDownY > mRefreshDist || -mPullUpY > mLoadmoreDist) {
                    mIsTouch = false;
                }
                if (mState == RELEASE_TO_REFRESH) {
                    changeState(REFRESHING);
                    onRefresh();
                } else if (mState == RELEASE_TO_LOAD) {
                    changeState(LOADING);
                    onLoadMore();
                    postDelayed(mUpdateRunnable, 100);
                    break;
                }
                changeState(DONE);
                post(mUpdateRunnable);
                break;
            default:
                changeState(DONE);
                post(mUpdateRunnable);
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    protected void initView() {
        mRefreshView = getChildAt(0);
        mPullableView = getChildAt(1);
        mLoadmoreView = getChildAt(2);
        mLoadStateTextView = (TextView) mLoadmoreView
                .findViewById(R.id.loadstate_tv);
        mLoadingView = mLoadmoreView.findViewById(R.id.loading_icon);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mIsLayout) {
            mIsLayout = true;
            mRefreshDist = getmRefreshDist();
            mLoadmoreDist = getmLoadmoreDist();
        }
        mRefreshView.layout(0, (int) (mPullDownY + mPullUpY) - mRefreshView.getMeasuredHeight(),
                mRefreshView.getMeasuredWidth(), (int) (mPullDownY + mPullUpY));
        mPullableView.layout(0, (int) (mPullDownY + mPullUpY),
                mPullableView.getMeasuredWidth(), (int) (mPullDownY + mPullUpY)
                        + mPullableView.getMeasuredHeight());
        mLoadmoreView.layout(0, (int) (mPullDownY + mPullUpY) + mPullableView.getMeasuredHeight(),
                mLoadmoreView.getMeasuredWidth(),
                (int) (mPullDownY + mPullUpY) + mPullableView.getMeasuredHeight()
                        + mLoadmoreView.getMeasuredHeight());
    }

    protected abstract void onRefresh();

    protected abstract void onLoadMore();

    public boolean isChildInForceTouch() {
        return false;
    }

    protected float getmRefreshDist() {
        float dist = ((ViewGroup) mRefreshView).getChildAt(0)
                .getMeasuredHeight();
        return dist;
    }

    protected float getmLoadmoreDist() {
        float dist = ((ViewGroup) mLoadmoreView).getChildAt(0)
                .getMeasuredHeight();
        return dist;
    }
}
