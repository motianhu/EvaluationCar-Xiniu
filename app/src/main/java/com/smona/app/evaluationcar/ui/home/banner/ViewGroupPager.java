package com.smona.app.evaluationcar.ui.home.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RemoteViews.RemoteView;
import android.widget.Scroller;

@RemoteView
public class ViewGroupPager extends ViewGroup {

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    public static final int SCROLL_DIRECTION_LEFT = -1;
    public static final int SCROLL_DIRECTION_MEDDILE = 0;
    public static final int SCROLL_DIRECTION_RIGHT = 1;
    private static final int INVALID_POINTER = -1;
    private static final int ANI_DURATION = 900;
    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips
    private static final int CLOSE_ENOUGH = 2; // dp
    private static final int INVALID_INDEX = -1;
    private static final Interpolator INTERPOLATOR = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private int mDirection = SCROLL_DIRECTION_MEDDILE;
    private int mScrollState = SCROLL_STATE_IDLE;
    private int mCurIndex;
    private int mWidth;
    private PageTranformationInfo mFirstPageTranformation = new PageTranformationInfo();
    private PageTranformationInfo mSecondPageTranformation = new PageTranformationInfo();
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mLastMotionX;
    private int mActivePointerId;
    private float mLastMotionY;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mFlingDistance;
    private int mCloseEnough;
    private PageListener mPageListener;
    private PageClickListener mPageClickListener;
    private boolean mConsistencyIntercepterVerifier = false;
    private float mDetalX = 0;
    private View mCurPage = null;
    private View mNextPage = null;
    private View mPrePage = null;
    private ViewGroupMatrix mEffectPrePage = new ViewGroupMatrix();
    private ViewGroupMatrix mEffectCurPage = new ViewGroupMatrix();
    private ViewGroupMatrix mEffectNextPage = new ViewGroupMatrix();
    private boolean mIsEnd = false;
    private boolean mIndexChanged = false;
    private int mCurrentPageIndex = INVALID_INDEX;
    private int mAlpha;

    public ViewGroupPager(Context context) {
        super(context);
        initViewPager();
    }

    public ViewGroupPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewPager();
    }

    void initViewPager() {
        setWillNotDraw(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        final Context context = getContext();
        mScroller = new Scroller(context, INTERPOLATOR);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop() / 2;
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        final float density = context.getResources().getDisplayMetrics().density;
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
        mCloseEnough = (int) (CLOSE_ENOUGH * density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));
        mWidth = getMeasuredWidth();

        int childWidthSize = getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight();
        int childHeightSize = getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                child.measure(MeasureSpec.makeMeasureSpec(childWidthSize,
                        MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                        childHeightSize, MeasureSpec.EXACTLY));
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int offset = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                left = offset + paddingLeft;
                child.layout(left, paddingTop, left + child.getMeasuredWidth(),
                        paddingTop + child.getMeasuredHeight());
            }
        }
        if (count > 0 && mCurIndex >= 0 && mCurIndex < count) {
            View child = getChildAt(mCurIndex);
            child.setTranslationX(-mWidth);
        }
    }

    public void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }
        mScrollState = newState;
        if (null != mPageListener) {
            mPageListener.onPageSrcollStateChange(mScrollState);
            if (mScrollState == SCROLL_STATE_IDLE) {
                mDetalX = 0;
                mDirection = SCROLL_DIRECTION_MEDDILE;
                mPageListener.onPageSelseted(mCurIndex);
                mPageListener.onPageScrollDirection(mDirection);
            }
        }
    }

    protected void interceptTouchDown(MotionEvent ev) {
        mScroller.abortAnimation();
        mLastMotionX = mInitialMotionX = ev.getX();
        mInitialMotionY = ev.getY();
        mActivePointerId = ev.getPointerId(0);
        mIsUnableToDrag = false;

        mScroller.computeScrollOffset();
        if (mScrollState == SCROLL_STATE_SETTLING
                && Math.abs(mScroller.getFinalX() - mScroller.getCurrX()) > mCloseEnough) {
            // Let the user 'catch' the pager as it animates.
            mScroller.abortAnimation();
            mIsBeingDragged = true;
            mDirection = SCROLL_DIRECTION_MEDDILE;
            if (null != mPageListener) {
                mPageListener.onPageScrollDirection(mDirection);
            }
            start();
            setScrollState(SCROLL_STATE_DRAGGING);
        } else {
            mIsBeingDragged = false;
            completeScroll();
        }
    }

    protected void interceptTouchMove(MotionEvent ev) {
        final int activePointerId = mActivePointerId;
        if (activePointerId == INVALID_POINTER) {
            return;
        }
        final int pointerIndex = ev.findPointerIndex(activePointerId);
        final float x = ev.getX(pointerIndex);
        final float dx = x - mLastMotionX;
        final float xDiff = Math.abs(dx);
        final float y = ev.getY(pointerIndex);
        final float yDiff = Math.abs(y - mLastMotionY);
        if (xDiff > mTouchSlop && xDiff > yDiff) {
            mIsBeingDragged = true;
            start();
            setScrollState(SCROLL_STATE_DRAGGING);
            mLastMotionX = dx > 0 ? mInitialMotionX + mTouchSlop
                    : mInitialMotionX - mTouchSlop;
        } else {
            if (yDiff > mTouchSlop) {
                mIsUnableToDrag = true;
            }
        }
        if (mIsBeingDragged) {
            if (performDrag(x)) {
                invalidate();
            }
        }
    }

    protected void interceptTouchReset(MotionEvent ev) {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;
        mActivePointerId = INVALID_POINTER;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getChildCount() <= 1) {
            return false;
        }

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            interceptTouchReset(ev);
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                return true;
            }
            if (mIsUnableToDrag) {
                return false;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                interceptTouchMove(ev);
                break;
            case MotionEvent.ACTION_DOWN:
                interceptTouchDown(ev);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            default:
                break;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        return mIsBeingDragged;
    }

    private void performClickItem(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mInitialMotionX = ev.getX();
            mInitialMotionY = ev.getY();
        } else if (action == MotionEvent.ACTION_UP) {
            performClickItemUp(ev);
        }
    }

    private void performClickItemUp(MotionEvent ev) {
        float deltaX = Math.abs(ev.getX() - mInitialMotionX);
        float deltaY = Math.abs(ev.getY() - mInitialMotionY);
        if (deltaX < mTouchSlop && deltaY < mTouchSlop) {
            if (mPageClickListener != null) {
                mPageClickListener.onPageClick();
            }
        }
    }

    private void dragMove(MotionEvent ev) {
        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
        final int pointCount = ev.getPointerCount();
        if (pointerIndex == -1 || pointerIndex > pointCount - 1) {
            return;
        }
        final float x = ev.getX(pointerIndex);
        final float xDiff = Math.abs(x - mLastMotionX);
        final float y = ev.getY(pointerIndex);
        final float yDiff = Math.abs(y - mLastMotionY);

        if (xDiff > mTouchSlop || xDiff > yDiff) {
            mIsBeingDragged = true;
            start();
            mLastMotionX = x - mInitialMotionX > 0 ? mInitialMotionX
                    + mTouchSlop : mInitialMotionX - mTouchSlop;
            setScrollState(SCROLL_STATE_DRAGGING);
        }
    }

    private void dragUp(MotionEvent ev) {
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        int initialVelocity;
        int totalDelta;
        final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
        final float x = ev.getX(activePointerIndex);

        totalDelta = (int) (x - mInitialMotionX);
        initialVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);

        float pageOffset = totalDelta * 1.0f / getWidth();

        int nextPage = determineTargetPage(mCurIndex, pageOffset,
                initialVelocity, totalDelta);
        int slidePara = mCurIndex - nextPage;

        if (totalDelta > 0 && slidePara < 0) {
            slidePara = 1;
        } else if (totalDelta < 0 && slidePara > 0) {
            slidePara = -1;
        }

        mScroller.startScroll(totalDelta, 0, slidePara * getWidth()
                - totalDelta, 0, ANI_DURATION);
        postInvalidate();
        setCurrentIndex(nextPage);
        setScrollState(SCROLL_STATE_SETTLING);
        mActivePointerId = INVALID_POINTER;
        endDrag();
    }

    private void perVelocityTracker(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
            mConsistencyIntercepterVerifier = false;
            mActivePointerId = INVALID_POINTER;
            mIsUnableToDrag = false;
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getChildCount() < 1) {
            return true;
        } else if (getChildCount() == 1) {
            performClickItem(ev);
            return true;
        }

        if (mConsistencyIntercepterVerifier) {
            perVelocityTracker(ev);
            return true;
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        touchEvent(ev);
        return true;
    }

    private void touchEvent(MotionEvent ev) {
        boolean needsInvalidate = false;
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mScroller.abortAnimation();
                // Remember where the motion event started
                mLastMotionX = mInitialMotionX = ev.getX();
                mInitialMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    dragMove(ev);
                }
                if (mIsBeingDragged && mActivePointerId != INVALID_POINTER) {
                    final int activePointerIndex = ev
                            .findPointerIndex(mActivePointerId);
                    if (activePointerIndex == -1) {
                        break;
                    }
                    final float x = mLastMotionX = ev.getX(activePointerIndex);
                    needsInvalidate = performDrag(x);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    dragUp(ev);
                }
                performClickItemUp(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                final float x = ev.getX(index);
                mLastMotionX = x;
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
                break;
            default:
                break;
        }
        if (needsInvalidate) {
            postInvalidate();
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    protected void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private float getScrollFraction(float detalX) {
        float fraction = detalX / getWidth();
        return fraction;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int newX = mScroller.getCurrX();
            float fraction = getScrollFraction(newX);
            setFactor(fraction);
            postInvalidate();
        } else {
            if (!mIsBeingDragged) {
                if (!mIsEnd) {
                    end();
                    onPageIndexChange();
                }
                completeScroll();
            }
        }
    }

    private void completeScroll() {
        boolean needPopulate = mScrollState == SCROLL_STATE_SETTLING;
        if (needPopulate) {
            mScroller.abortAnimation();
            setScrollState(SCROLL_STATE_IDLE);
        }
    }

    private boolean performDrag(float x) {
        float detalX = getDetalX(x);
        setFactor(getScrollFraction(detalX));
        mIndexChanged = false;
        return true;
    }

    private float getDetalX(float x) {
        float detalX = x - mInitialMotionX;
        mDetalX = detalX;
        return detalX;
    }

    public int getCurrentIndex() {
        return mCurIndex;
    }

    private void setCurrentIndex(int item) {
        if (mCurIndex == item) {
            mIndexChanged = false;
            return;
        }
        mCurIndex = item;
        mIndexChanged = true;
        if (mPageListener != null) {
            mPageListener.onPageSelseted(mCurIndex);
        }
    }

    private int determineTargetPage(int currentPage, float pageOffset,
                                    int velocity, int deltaX) {
        int targetPage;
        if (Math.abs(deltaX) > mFlingDistance
                && Math.abs(velocity) > mMinimumVelocity) {
            targetPage = velocity > 0 ? currentPage - 1 : currentPage + 1;
        } else if (Math.abs(pageOffset) > 0.5f && deltaX < 0) {
            targetPage = currentPage + 1;
        } else if (Math.abs(pageOffset) > 0.5f && deltaX > 0) {
            targetPage = currentPage - 1;
        } else {
            targetPage = currentPage;
        }

        if (targetPage < 0) {
            targetPage = getChildCount() - 1;
        } else if (targetPage > getChildCount() - 1) {
            targetPage = 0;
        }
        targetPage = Math.max(0, Math.min(targetPage, getChildCount() - 1));
        return targetPage;
    }

    private int getIndexofChild(View child) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (child == getChildAt(i)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = getChildCount();
        if (count <= 1) {
            super.dispatchDraw(canvas);
        } else {
            final long drawingTime = getDrawingTime();
            int firstIndex;
            int secondIndex;
            if (mIndexChanged) {
                secondIndex = mCurIndex;
                if (mCurIndex == 0) {
                    if (mDirection == SCROLL_DIRECTION_LEFT) {
                        firstIndex = count - 1;
                    } else {
                        firstIndex = mCurIndex + 1;
                    }
                } else if (mCurIndex == count - 1) {
                    if (mDirection == SCROLL_DIRECTION_LEFT) {
                        firstIndex = mCurIndex - 1;
                    } else {
                        firstIndex = 0;
                    }
                } else {
                    if (mDirection == SCROLL_DIRECTION_LEFT) {
                        firstIndex = mCurIndex - 1;
                    } else {
                        firstIndex = mCurIndex + 1;
                    }
                }
            } else {
                firstIndex = mCurIndex;
                if (mCurIndex == 0) {
                    if (mDirection == SCROLL_DIRECTION_LEFT) {
                        secondIndex = count - 1;
                    } else {
                        secondIndex = mCurIndex + 1;
                    }
                } else if (mCurIndex == count - 1) {
                    if (mDirection == SCROLL_DIRECTION_LEFT) {
                        secondIndex = mCurIndex - 1;
                    } else {
                        secondIndex = 0;
                    }
                } else {
                    if (mDirection == SCROLL_DIRECTION_LEFT) {
                        secondIndex = mCurIndex - 1;
                    } else {
                        secondIndex = mCurIndex + 1;
                    }
                }
            }
            View childFirst = getChildAt(firstIndex);
            View childSecond = getChildAt(secondIndex);
            drawChild(canvas, childFirst, drawingTime);
            canvas.drawARGB(mAlpha, 0, 0, 0);
            drawChild(canvas, childSecond, drawingTime);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean more = false;
        if (mScrollState != SCROLL_STATE_IDLE && !mIsEnd) {
            if (child == mFirstPageTranformation.mPagedView) {
                if (!mFirstPageTranformation.mPagedTransMatrix.isIdentity()
                        || mFirstPageTranformation.mIsMatrixDirty) {
                    canvas.save();
                    canvas.concat(mFirstPageTranformation.mPagedTransMatrix);
                    more = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                }
            } else if (child == mSecondPageTranformation.mPagedView) {
                if (!mSecondPageTranformation.mPagedTransMatrix.isIdentity()
                        || mSecondPageTranformation.mIsMatrixDirty) {
                    canvas.save();
                    canvas.concat(mSecondPageTranformation.mPagedTransMatrix);
                    more = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                }
            }
        } else {
            int index = getIndexofChild(child);
            if (index == mCurIndex) {
                more = super.drawChild(canvas, child, drawingTime);
            }
        }
        return more;
    }

    public void setPageListener(PageListener listen) {
        mPageListener = listen;
    }

    public void setPageClickListener(PageClickListener listen) {
        mPageClickListener = listen;
    }

    public void slideToPreviousPage() {
        int targetPage;
        targetPage = mCurIndex - 1;
        if (targetPage < 0) {
            targetPage = getChildCount() - 1;
        } else if (targetPage > getChildCount() - 1) {
            targetPage = 0;
        }
        targetPage = Math.max(0, Math.min(targetPage, getChildCount() - 1));
        int slidePara = mCurIndex - targetPage;

        if (slidePara < 0) {
            slidePara = 1;
        }
        start();
        int totalDelta = (int) mDetalX;
        mScroller.startScroll(totalDelta, 0, slidePara * getWidth()
                - totalDelta, 0, ANI_DURATION);
        mDetalX = 0;
        if (mPageListener != null) {
            mDirection = SCROLL_DIRECTION_LEFT;
            mPageListener.onPageScrollDirection(SCROLL_DIRECTION_LEFT);
        }
        setCurrentIndex(targetPage);
        setScrollState(SCROLL_STATE_SETTLING);
        mIsBeingDragged = false;
        invalidate();
    }

    public void slideToNextPage() {
        int targetPage;
        targetPage = mCurIndex + 1;
        if (targetPage < 0) {
            targetPage = getChildCount() - 1;
        } else if (targetPage > getChildCount() - 1) {
            targetPage = 0;
        }
        targetPage = Math.max(0, Math.min(targetPage, getChildCount() - 1));
        int slidePara = mCurIndex - targetPage;

        if (slidePara > 0) {
            slidePara = -1;
        }
        int totalDelta = (int) mDetalX;
        start();
        mScroller.startScroll(totalDelta, 0, slidePara * getWidth()
                - totalDelta, 0, ANI_DURATION);
        mDetalX = 0;
        if (mPageListener != null) {
            mDirection = SCROLL_DIRECTION_RIGHT;
            mPageListener.onPageScrollDirection(SCROLL_DIRECTION_RIGHT);
        }
        setCurrentIndex(targetPage);
        setScrollState(SCROLL_STATE_SETTLING);
        mIsBeingDragged = false;
        invalidate();
    }

    public void start() {

        int curPageIndex = mCurIndex;
        if (curPageIndex != mCurrentPageIndex) {
            mCurrentPageIndex = curPageIndex;
        } else {
            if (!mIsEnd) {
                return;
            }
        }
        mIsEnd = false;
        int pageCount = getChildCount();

        if (pageCount == 1) {
            mCurPage = null;
            return;
        }

        mCurPage = getChildAt(curPageIndex);

        if (curPageIndex == 0) {
            if (pageCount - 1 != curPageIndex + 1) {
                mPrePage = getChildAt(pageCount - 1);
                mNextPage = getChildAt(curPageIndex + 1);
            } else {
                mNextPage = getChildAt(curPageIndex + 1);
                mPrePage = mNextPage;
            }
        } else if (curPageIndex == pageCount - 1) {
            if (curPageIndex - 1 != 0) {
                mPrePage = getChildAt(curPageIndex - 1);
                mNextPage = getChildAt(0);
            } else {
                mPrePage = getChildAt(curPageIndex - 1);
                mNextPage = mPrePage;
            }
        } else {
            mPrePage = getChildAt(curPageIndex - 1);
            mNextPage = getChildAt(curPageIndex + 1);
        }

        if (mPrePage != null) {
            mPrePage.setTranslationX(-mWidth);
        }

        if (mNextPage != null) {
            mNextPage.setTranslationX(-mWidth);
        }

        mEffectPrePage.setPageView(mPrePage);
        mEffectCurPage.setPageView(mCurPage);
        mEffectNextPage.setPageView(mNextPage);
    }

    public void end() {
        mIsEnd = true;
        mAlpha = 0;
        mIndexChanged = false;
        mCurrentPageIndex = INVALID_INDEX;
        mEffectPrePage.clearTransform();
        mEffectCurPage.clearTransform();
        mEffectNextPage.clearTransform();
    }

    public void setFactor(float factor) {
        if (mCurPage == null) {
            return;
        }
        if (factor > 0) {
            setLeftFactor(factor - 1);
        } else {
            setRightFactor(factor);
        }
    }

    private void setRightFactor(float factor) {
        if (mDirection != SCROLL_DIRECTION_RIGHT) {
            mDirection = SCROLL_DIRECTION_RIGHT;
            if (mPageListener != null) {
                mPageListener.onPageScrollDirection(mDirection);
            }
        }
        mEffectCurPage.setPageTransformation(mFirstPageTranformation);
        mEffectNextPage.setPageTransformation(mSecondPageTranformation);
        float width = 0;
        width = mEffectCurPage.getWidth();
        if (width == 0) {
            width = mEffectNextPage.getWidth();
        }
        if (width > 0) {
            mEffectCurPage.setPosition(factor * width / 4, 0);
            mAlpha = (int) (-0.4f * factor * 256);
            mEffectNextPage.setPosition((factor + 1) * width, 0);
            mEffectCurPage.endEffect();
            mEffectNextPage.endEffect();
        }
    }

    private void setLeftFactor(float factor) {
        if (mDirection != SCROLL_DIRECTION_LEFT) {
            mDirection = SCROLL_DIRECTION_LEFT;
            if (mPageListener != null) {
                mPageListener.onPageScrollDirection(mDirection);
            }
        }
        mEffectPrePage.setPageTransformation(mFirstPageTranformation);
        mEffectCurPage.setPageTransformation(mSecondPageTranformation);
        float width = 0;
        width = mEffectPrePage.getWidth();
        if (width == 0) {
            width = mEffectCurPage.getWidth();
        }
        if (width > 0) {
            mEffectPrePage.setPosition(factor * width, 0);
            mAlpha = (int) (0.4f * (factor + 1) * 256);
            mEffectCurPage.setPosition((factor + 1) * width / 4, 0);
            mEffectPrePage.endEffect();
            mEffectCurPage.endEffect();
        }
    }

    private void clearPageView(View pageSView) {
        View curPageView = getChildAt(mCurIndex);
        if (pageSView != null && pageSView != curPageView) {
            pageSView.setTranslationX(0);
        }
    }

    private void onPageIndexChange() {
        clearPageView(mCurPage);
        clearPageView(mNextPage);
        clearPageView(mPrePage);
    }

    protected interface PageListener {

        void onPageSelseted(int index);

        void onPageSrcollStateChange(int state);

        void onPageScrollDirection(int direction);

    }

    public interface PageClickListener {
        void onPageClick();
    }

    static class PageTranformationInfo {
        protected final Matrix mPagedTransMatrix = new Matrix();
        protected boolean mIsMatrixDirty;
        protected View mPagedView;

        public void resetAndrecyle() {
            mPagedView = null;
            mPagedTransMatrix.reset();
        }
    }

}
