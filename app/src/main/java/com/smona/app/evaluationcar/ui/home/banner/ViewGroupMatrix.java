package com.smona.app.evaluationcar.ui.home.banner;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.View;

import com.smona.app.evaluationcar.ui.home.banner.ViewGroupPager.PageTranformationInfo;

public class ViewGroupMatrix {

    private PageTranformationInfo mTranformationInfo;
    private View mView;
    private boolean mValid = false;

    private PointF mTranslation = new PointF();
    private int mOriginX = 0;
    private int mOriginY = 0;

    private Camera mCamera = null;
    private Matrix mMatrix3D = null;

    public void setPageView(View view) {
        mView = view;
        if (view != null) {
            mValid = true;
            mOriginX = view.getWidth() / 2;
            mOriginY = view.getHeight() / 2;
        } else {
            mValid = false;
        }
        mTranslation.set(0, 0);
    }

    void setPageTransformation(PageTranformationInfo info) {
        mTranformationInfo = info;
        mTranformationInfo.mPagedView = mView;
    }

    public void setPosition(float x, float y) {
        if (mValid) {
            mTranslation.set(x, y);
        }
    }

    public float getWidth() {
        if (mValid) {
            return mView.getWidth();
        }
        return 0;
    }

    public void endEffect() {
        if (mValid) {
            Matrix tmpMatrix = null;

            final PageTranformationInfo info = mTranformationInfo;
            if (info == null) {
                return;
            }
            tmpMatrix = info.mPagedTransMatrix;
            info.mIsMatrixDirty = true;

            if (null == tmpMatrix) {
                return;
            }
            tmpMatrix.reset();

            if (mCamera == null) {
                mCamera = new Camera();
                mMatrix3D = new Matrix();
            }

            mCamera.save();

            mCamera.getMatrix(mMatrix3D);
            mMatrix3D.preTranslate(-mOriginX, -mOriginY);
            mMatrix3D.postTranslate(mOriginX + mTranslation.x, mOriginY
                    + mTranslation.y);
            tmpMatrix.postConcat(mMatrix3D);
            mCamera.restore();
        }
    }

    public void clearTransform() {
        if (mValid) {
            if (null != mTranformationInfo) {
                mTranformationInfo.resetAndrecyle();
            }
        }
    }

}
