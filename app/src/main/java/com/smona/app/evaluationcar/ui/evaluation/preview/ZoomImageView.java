package com.smona.app.evaluationcar.ui.evaluation.preview;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 自定义的ImageView控制，可对图片进行多点触控缩放和拖动
 *
 * @author Moth
 */
public class ZoomImageView extends View implements OnGestureListener {
    /**
     * 初始化状态常量
     */
    public static final int STATUS_INIT = 1;
    /**
     * 图片放大状态常量
     */
    public static final int STATUS_ZOOM_OUT = 2;
    /**
     * 图片缩小状态常量
     */
    public static final int STATUS_ZOOM_IN = 3;
    /**
     * 图片拖动状态常量
     */
    public static final int STATUS_MOVE = 4;
    // 暂停
    public static final int STATUS_PUASE = 5;
    // 重新显示
    public static final int STATUS_RESUME = 6;
    private static final String TAG = "ZoomImageView";
    private static final int TIME_DURATIOIN = 6000;
    /**
     * 用于对图片进行移动和缩放变换的矩阵
     */
    private Matrix mMatrix = new Matrix();
    /**
     * 待展示的Bitmap对象
     */
    private Bitmap mSourceBitmap;
    /**
     * 记录当前操作的状态，可选值为STATUS_INIT、STATUS_ZOOM_OUT、STATUS_ZOOM_IN和STATUS_MOVE
     */
    private int mCurrentStatus;
    /**
     * ZoomImageView控件的宽度
     */
    private int mWidth;
    /**
     * ZoomImageView控件的高度
     */
    private int mHeight;
    /**
     * 记录两指同时放在屏幕上时，中心点的横坐标值
     */
    private float mCenterPointX;
    /**
     * 记录两指同时放在屏幕上时，中心点的纵坐标值
     */
    private float mCenterPointY;
    /**
     * 记录当前图片的宽度，图片被缩放时，这个值会一起变动
     */
    private float mCurrentBitmapWidth;
    /**
     * 记录当前图片的高度，图片被缩放时，这个值会一起变动
     */
    private float mCurrentBitmapHeight;
    /**
     * 记录上次手指移动时的横坐标
     */
    private float mLastXMove = -1;
    /**
     * 记录上次手指移动时的纵坐标
     */
    private float mLastYMove = -1;
    /**
     * 记录手指在横坐标方向上的移动距离
     */
    private float mMovedDistanceX;
    /**
     * 记录手指在纵坐标方向上的移动距离
     */
    private float mMovedDistanceY;
    /**
     * 记录图片在矩阵上的横向偏移值
     */
    private float mTotalTranslateX;
    /**
     * 记录图片在矩阵上的纵向偏移值
     */
    private float mTotalTranslateY;
    /**
     * 记录图片在矩阵上的总缩放比例
     */
    private float mTotalRatio;
    /**
     * 记录手指移动的距离所造成的缩放比例
     */
    private float mScaledRatio;
    /**
     * 记录图片初始化时的缩放比例
     */
    private float mInitRatio;
    private float mMaxRatio;
    private float mMinRatio;
    /**
     * 记录上次两指之间的距离
     */
    private double mLastFingerDis;
    private ValueAnimator mValueAnimator;
    private int mInitBitmapWidth, mInitBitmapHeight;
    private GestureDetector mGestureDetector;
    private boolean mCanMove = false;
    private boolean mHaveInitBitmap = false;
    private float mWidthDiff = 0;
    TypeEvaluator<PointF> typeEvaluator = new TypeEvaluator<PointF>() {
        @Override
        public PointF evaluate(float fraction, PointF startValue,
                               PointF endValue) {
            float time = 1 - fraction;
            PointF point = new PointF();
            point.x = getTimeMove(time);
            point.y = mCurrentBitmapHeight / 2;
            return point;
        }
    };

    /**
     * ZoomImageView构造函数，将当前操作状态设为STATUS_INIT。
     *
     * @param context
     * @param attrs
     */
    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCurrentStatus = STATUS_INIT;
        initGesture();
    }

    private void initGesture() {
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    /**
     * 将待展示的图片设置进来。
     *
     * @param bitmap 待展示的Bitmap对象
     */
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        mHaveInitBitmap = false;
        mSourceBitmap = bitmap;
        mCurrentStatus = STATUS_INIT;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            // 分别获取到ZoomImageView的宽度和高度
            mWidth = getWidth();
            mHeight = getHeight();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mCanMove) {
            return true;
        }
        if (!mHaveInitBitmap) {
            return true;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    // 当有两个手指按在屏幕上时，计算两指之间的距离
                    mLastFingerDis = distanceBetweenFingers(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    // 只有单指按在屏幕上移动时，为拖动状态
                    float xMove = event.getX();
                    float yMove = event.getY();
                    if (mLastXMove == -1 && mLastYMove == -1) {
                        mLastXMove = xMove;
                        mLastYMove = yMove;
                    }
                    mCurrentStatus = STATUS_MOVE;
                    mMovedDistanceX = xMove - mLastXMove;
                    mMovedDistanceY = yMove - mLastYMove;
                    // 进行边界检查，不允许将图片拖出边界
                    if (mTotalTranslateX + mMovedDistanceX > 0) {
                        mMovedDistanceX = -mTotalTranslateX;
                    } else if (mWidth - (mTotalTranslateX + mMovedDistanceX) > mCurrentBitmapWidth) {
                        mMovedDistanceX = mWidth - mCurrentBitmapWidth
                                - mTotalTranslateX;
                    }
                    if (mTotalTranslateY + mMovedDistanceY > 0) {
                        mMovedDistanceY = 0;
                    } else if (mHeight - (mTotalTranslateY + mMovedDistanceY) > mCurrentBitmapHeight) {
                        mMovedDistanceY = 0;
                    }
                    // 调用onDraw()方法绘制图片
                    invalidate();
                    mLastXMove = xMove;
                    mLastYMove = yMove;
                } else if (event.getPointerCount() == 2) {
                    // 有两个手指按在屏幕上移动时，为缩放状态
                    centerPointBetweenFingers(event);
                    double fingerDis = distanceBetweenFingers(event);
                    if (fingerDis > mLastFingerDis) {
                        mCurrentStatus = STATUS_ZOOM_OUT;
                    } else {
                        mCurrentStatus = STATUS_ZOOM_IN;
                    }
                    // 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
                    if ((mCurrentStatus == STATUS_ZOOM_OUT && mTotalRatio < mMaxRatio)
                            || (mCurrentStatus == STATUS_ZOOM_IN && mTotalRatio > mMinRatio)) {
                        mScaledRatio = (float) (fingerDis / mLastFingerDis);
                        mTotalRatio = mTotalRatio * mScaledRatio;
                        if (mTotalRatio > mMaxRatio) {
                            mTotalRatio = mMaxRatio;
                        } else if (mTotalRatio < mMinRatio) {
                            mTotalRatio = mMinRatio;
                        }
                        // 调用onDraw()方法绘制图片
                        invalidate();
                        mLastFingerDis = fingerDis;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    // 手指离开屏幕时将临时值还原
                    mLastXMove = -1;
                    mLastYMove = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 手指离开屏幕时将临时值还原
                mLastXMove = -1;
                mLastYMove = -1;
                break;
            default:
                break;
        }
        return true;
    }

    public void setCanMove(boolean canMove) {
        mCanMove = canMove;
    }

    public boolean isFarLeft() {
        return ((int) Math.abs(mTotalTranslateX)) == 0;
    }

    public boolean isFarRight() {
        return ((int) Math.abs(mWidth - mCurrentBitmapWidth - mTotalTranslateX)) == 0;
    }

    /**
     * 根据currentStatus的值来决定对图片进行什么样的绘制操作。
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (!mHaveInitBitmap) {
            mCurrentStatus = STATUS_INIT;
        }
        super.onDraw(canvas);
        switch (mCurrentStatus) {
            case STATUS_ZOOM_OUT:
            case STATUS_ZOOM_IN:
                zoom(canvas);
                break;
            case STATUS_MOVE:
                move(canvas);
                break;
            case STATUS_INIT:
                initBitmap(canvas);
                break;
            default:
                if (mSourceBitmap != null) {
                    canvas.drawBitmap(mSourceBitmap, mMatrix, null);
                }
                break;
        }
    }

    /**
     * 对图片进行缩放处理。
     *
     * @param canvas
     */
    private void zoom(Canvas canvas) {
        mMatrix.reset();
        // 将图片按总缩放比例进行缩放
        mMatrix.postScale(mTotalRatio, mTotalRatio);
        float scaledWidth = mSourceBitmap.getWidth() * mTotalRatio;
        float scaledHeight = mSourceBitmap.getHeight() * mTotalRatio;
        float translateX = 0f;
        float translateY = 0f;
        // 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。否则按两指的中心点的横坐标进行水平缩放
        if (mCurrentBitmapWidth < mWidth) {
            translateX = (mWidth - scaledWidth) / 2f;
        } else {
            translateX = mTotalTranslateX * mScaledRatio + mCenterPointX
                    * (1 - mScaledRatio);
            // 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
            if (translateX > 0) {
                translateX = 0;
            } else if (mWidth - translateX > scaledWidth) {
                translateX = mWidth - scaledWidth;
            }
        }
        // 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。否则按两指的中心点的纵坐标进行垂直缩放
        if (mCurrentBitmapHeight < mHeight) {
            translateY = (mHeight - scaledHeight) / 2f;
        } else {
            translateY = mTotalTranslateY * mScaledRatio + mCenterPointY
                    * (1 - mScaledRatio);
            // 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
            if (translateY > 0) {
                translateY = 0;
            } else if (mHeight - translateY > scaledHeight) {
                translateY = mHeight - scaledHeight;
            }
        }
        // 缩放后对图片进行偏移，以保证缩放后中心点位置不变
        mMatrix.postTranslate(translateX, translateY);
        mTotalTranslateX = translateX;
        mTotalTranslateY = translateY;
        mCurrentBitmapWidth = scaledWidth;
        mCurrentBitmapHeight = scaledHeight;
        canvas.drawBitmap(mSourceBitmap, mMatrix, null);
    }

    /**
     * 对图片进行平移处理
     *
     * @param canvas
     */
    private void move(Canvas canvas) {
        mMatrix.reset();
        // 根据手指移动的距离计算出总偏移值
        float translateX = mTotalTranslateX + mMovedDistanceX;
        float translateY = mTotalTranslateY + mMovedDistanceY;
        // 先按照已有的缩放比例对图片进行缩放
        mMatrix.postScale(mTotalRatio, mTotalRatio);
        // 再根据移动距离进行偏移
        mMatrix.postTranslate(translateX, translateY);
        mTotalTranslateX = translateX;
        mTotalTranslateY = translateY;
        canvas.drawBitmap(mSourceBitmap, mMatrix, null);
    }

    /**
     * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽高时对图片进行压缩。
     *
     * @param canvas
     */
    private void initBitmap(Canvas canvas) {
        if (mSourceBitmap != null) {
            mMatrix.reset();
            mInitBitmapWidth = mSourceBitmap.getWidth();
            mInitBitmapHeight = mSourceBitmap.getHeight();

            mMinRatio = mWidth / (mInitBitmapWidth * 1.0f);
            mMaxRatio = mHeight / (mInitBitmapHeight * 1.0f);
            if (mMinRatio > mMaxRatio) {
                float temp = mMinRatio;
                mMinRatio = mMaxRatio;
                mMaxRatio = temp;
            }
            mTotalRatio = mInitRatio = mMaxRatio;
            mCurrentBitmapWidth = mInitBitmapWidth * mInitRatio;
            mCurrentBitmapHeight = mInitBitmapHeight * mInitRatio;
            mMatrix.postScale(mTotalRatio, mTotalRatio);
            if (mTotalTranslateY == 0) {
                mMatrix.postTranslate(mTotalTranslateX, mTotalTranslateY);
            } else {
                mTotalTranslateX = 0;
                mTotalTranslateY = 0;
            }
            canvas.drawBitmap(mSourceBitmap, mMatrix, null);
        }
        mHaveInitBitmap = true;
    }

    public float getCurrentBitmapWidth() {
        return mCurrentBitmapWidth;
    }

    public int getBitmapRawWidth() {
        return mInitBitmapWidth;
    }

    public int getBitmapRawHeight() {
        return mInitBitmapHeight;
    }

    public float getWidthDiff() {
        return mCurrentBitmapWidth - mWidth;
    }

    private float getTimeMove(float time) {
        return (mWidthDiff) * time;
    }

    public void onDestroy() {

    }

    public void onPause() {
        mCurrentStatus = STATUS_PUASE;
    }

    public void onResume() {
        mCurrentStatus = STATUS_RESUME;
    }

    public void startAnimal() {
        // 类型估值 - 抛物线示例
        mWidthDiff = getWidthDiff();
        mValueAnimator = ValueAnimator
                .ofObject(typeEvaluator, new PointF(0, 0));
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.setRepeatCount(Integer.MAX_VALUE - 1);
        mValueAnimator.setDuration(TIME_DURATIOIN);
        mValueAnimator.start();

        mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                float xMove = point.x;
                float yMove = point.y;
                if (mLastXMove == -1 && mLastYMove == -1) {
                    mLastXMove = xMove;
                    mLastYMove = yMove;
                }
                mCurrentStatus = STATUS_MOVE;
                mMovedDistanceX = xMove - mLastXMove;
                mMovedDistanceY = yMove - mLastYMove;
                if (mTotalTranslateX + mMovedDistanceX > 0) {
                    mMovedDistanceX = 0;
                } else if (mWidth - (mTotalTranslateX + mMovedDistanceX) > mCurrentBitmapWidth) {
                    mMovedDistanceX = 0;
                }
                if (mTotalTranslateY + mMovedDistanceY > 0) {
                    mMovedDistanceY = 0;
                } else if (mHeight - (mTotalTranslateY + mMovedDistanceY) > mCurrentBitmapHeight) {
                    mMovedDistanceY = 0;
                }

                // 调用onDraw()方法绘制图片
                invalidate();
                mLastXMove = xMove;
                mLastYMove = yMove;
            }
        });
    }

    public void stopAnimal() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        mLastXMove = -1;
        mLastYMove = -1;
    }

    public boolean isAnimalRuning() {
        if (mValueAnimator != null) {
            return mValueAnimator.isRunning();
        }
        return false;
    }

    /**
     * 计算两个手指之间的距离。
     *
     * @param event
     * @return 两个手指之间的距离
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 计算两个手指之间中心点的坐标。
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        mCenterPointX = (xPoint0 + xPoint1) / 2;
        mCenterPointY = (yPoint0 + yPoint1) / 2;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (e1.getPointerCount() == 1 && e2.getPointerCount() == 1) {
            float minMove = 120; // 最小滑动距离
            float minVelocity = 0; // 最小滑动速度
            float beginX = e1.getX();
            float endX = e2.getX();
            float beginY = e1.getY();
            float endY = e2.getY();
            float distanceX = endX - beginX;
            float distanceY = endY - beginY;
            double angle = Math.atan2(-distanceY, distanceX);

            if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity
                    && (angle > Math.PI / 6 && angle < 5 * Math.PI / 6)) { // 上滑

            }
        }
        return false;
    }

}
