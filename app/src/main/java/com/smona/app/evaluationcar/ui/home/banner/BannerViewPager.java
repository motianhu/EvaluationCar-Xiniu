package com.smona.app.evaluationcar.ui.home.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.BannerItem;
import com.smona.app.evaluationcar.framework.imageloader.ImageLoaderProxy;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class BannerViewPager extends BaseViewPager {
    final static float START_DAMPING_TOUCH_SLOP_ANGLE = (float) Math.PI / 6;
    final static float MAX_SWIPE_ANGLE = (float) Math.PI / 3;
    final static float TOUCH_SLOP_DAMPING_FACTOR = 4;
    private static final String TAG = "BannerViewPager";
    private static final int CHILD_COUNT = 2;
    // touch
    private final static int MIN_DIS = 10;
    // click
    OnSingleTouchListener onSingleTouchListener;
    private boolean mScrollEnable = false;
    private List<BannerItem> mDataList = new ArrayList<BannerItem>();
    private int mCurrentIndex = 0;
    private int mCurrentPos = 0;
    private int mDirection = SCROLL_DIRECTION_MEDDILE;
    private PageSelecteListener mPageSelecteListener = null;
    private PointF mDownP = new PointF();
    private PointF mCurrP = new PointF();
    private boolean mIsCalc = false;
    private PageListener mPageListener = new PageListener() {

        @Override
        public void onPageSrcollStateChange(int state) {

        }

        @Override
        public void onPageSelseted(int index) {
            if (index != mCurrentIndex) {
                mCurrentIndex = index;
                setNewPostion(mDirection);
                if (mPageSelecteListener != null) {
                    mPageSelecteListener.onPageSelecteListener(mCurrentPos);
                }
            }
        }

        @Override
        public void onPageScrollDirection(int direction) {
            if (direction == SCROLL_DIRECTION_LEFT
                    && mDirection != SCROLL_DIRECTION_LEFT) {
                mDirection = direction;
                int index = getLeftIndex();
                int pos = getLeftPos();
                requestImage(pos, index);
            } else if (direction == SCROLL_DIRECTION_RIGHT
                    && mDirection != SCROLL_DIRECTION_RIGHT) {
                mDirection = direction;
                int index = getRightIndex();
                int pos = getRightPos();
                requestImage(pos, index);
            } else if (direction == SCROLL_DIRECTION_MEDDILE) {
                mDirection = direction;
            }
        }
    };
    private OnSingleTouchListener mPageClickListener = new OnSingleTouchListener() {
        @Override
        public void onSingleTouch() {
            if (mDataList.size() <= mCurrentPos) {
                return;
            }
            BannerItem info = mDataList.get(mCurrentPos);
            ActivityUtils.jumpWebActivity(getContext(), CacheContants.TYPE_BANNER, info.id);
        }

    };

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        for (int i = 0; i < CHILD_COUNT; i++) {
            View view = ViewUtil.inflater(context, R.layout.banner_item);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setScaleType(ScaleType.FIT_XY);
            addView(view);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 每次进行onTouch事件都记录当前的按下的坐标
        mCurrP.x = event.getX();
        mCurrP.y = event.getY();
        stopPlay();
        final int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            // 记录按下时候的坐标
            // 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
            mDownP.x = event.getX();
            mDownP.y = event.getY();
            mIsCalc = false;
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!mIsCalc) {
                processMove(event);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            boolean isClick = ((Math.abs(mCurrP.x - mDownP.x) <= MIN_DIS) && (Math
                    .abs(mCurrP.y - mDownP.y) <= MIN_DIS));
            if (isClick) {
                onSingleTouch();
                return true;
            } else {
                processMove(event);
            }
            startPlay();
        }
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                interceptTouchMove(event);
                break;
            case MotionEvent.ACTION_DOWN:
                interceptTouchDown(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void processMove(MotionEvent ev) {
        float deltaX = Math.abs(ev.getX() - mDownP.x);
        float deltaY = Math.abs(ev.getY() - mDownP.y);

        if (Float.compare(deltaX, 0f) == 0) {
            return;
        }
        mIsCalc = true;
        float slope = deltaY / deltaX;
        float theta = (float) Math.atan(slope);

        if (theta > MAX_SWIPE_ANGLE) {
            // Above MAX_SWIPE_ANGLE, we don't want to ever start scrolling the
            // workspace
            getParent().requestDisallowInterceptTouchEvent(false);
            return;
        }
    }

    public void onSingleTouch() {
        if (onSingleTouchListener != null) {
            onSingleTouchListener.onSingleTouch();
        }
    }

    public void startPlay() {
        super.startAutoScroll();
    }

    public void stopPlay() {
        super.stopAutoScroll();
    }

    public void setOnSingleTouchListener(
            OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }

    public void setPageSelecteListener(PageSelecteListener listener) {
        mPageSelecteListener = listener;
    }

    public void update(List<BannerItem> data) {
        if (!data.isEmpty()) {
            mDataList = new ArrayList<BannerItem>(data);
            mCurrentPos = 0;
            setPageListener(mPageListener);
            setOnSingleTouchListener(mPageClickListener);
            if (data.size() > 1) {
                setScrollEnable(true);
            }
            requestImage(mCurrentPos, mCurrentIndex);
        }
    }

    public int getPagerCount() {
        return mDataList.size();
    }

    @Override
    public boolean isScrollEnable() {
        return mScrollEnable;
    }

    public void setScrollEnable(boolean enable) {
        mScrollEnable = enable;
    }

    private void requestImage(int pos, int index) {
        if (index >= getChildCount()) {
            return;
        }
        if (mDataList.size() > 0) {
            BannerItem info = mDataList.get(pos);
            String url = info.previewMedia;
            ImageView view = (ImageView) getChildAt(index).findViewById(
                    R.id.image);
            ImageLoaderProxy.loadImage(url, view);
        }
    }

    private void setNewPostion(int direction) {
        if (direction == SCROLL_DIRECTION_LEFT) {
            if (mCurrentPos - 1 < 0) {
                mCurrentPos = mDataList.size() - 1;
            } else {
                mCurrentPos = mCurrentPos - 1;
            }
        } else if (direction == SCROLL_DIRECTION_RIGHT) {
            if (mCurrentPos + 1 >= mDataList.size()) {
                mCurrentPos = 0;
            } else {
                mCurrentPos = mCurrentPos + 1;
            }
        }
        if (mCurrentPos < 0) {
            mCurrentPos = 0;
        }
    }

    private int getLeftIndex() {
        if (mCurrentIndex - 1 < 0) {
            return getChildCount() - 1;
        }
        return mCurrentIndex - 1;
    }

    private int getRightIndex() {
        if (mCurrentIndex + 1 >= getChildCount()) {
            return 0;
        }
        return mCurrentIndex + 1;
    }

    private int getLeftPos() {
        if (mCurrentPos - 1 < 0) {
            return mDataList.size() - 1;
        }
        return mCurrentPos - 1;
    }

    private int getRightPos() {
        if (mCurrentPos + 1 >= mDataList.size()) {
            return 0;
        }
        return mCurrentPos + 1;
    }

    public interface OnSingleTouchListener {
        void onSingleTouch();
    }

    public interface PageSelecteListener {
        void onPageSelecteListener(int pos);
    }
}
