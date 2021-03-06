package com.smona.app.xiniueval.ui.evaluation.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.bean.CarImageBean;
import com.smona.app.xiniueval.data.bean.ImageMetaBean;
import com.smona.app.xiniueval.data.event.background.LocalStatusSubEvent;
import com.smona.app.xiniueval.framework.cache.DataDelegator;
import com.smona.app.xiniueval.framework.event.EventProxy;
import com.smona.app.xiniueval.framework.provider.DBDelegator;
import com.smona.app.xiniueval.framework.storage.DeviceStorageManager;
import com.smona.app.xiniueval.ui.evaluation.ImageModelDelegator;
import com.smona.app.xiniueval.util.ActivityUtils;
import com.smona.app.xiniueval.util.CacheContants;
import com.smona.app.xiniueval.util.CarLog;
import com.smona.app.xiniueval.util.DateUtils;
import com.smona.app.xiniueval.util.SPUtil;
import com.smona.app.xiniueval.util.StatusUtils;
import com.smona.app.xiniueval.util.ToastUtils;
import com.smona.app.xiniueval.util.Utils;
import com.smona.app.xiniueval.util.ViewUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = CameraActivity.class.getSimpleName();

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private int mCameraId = 0;

    //屏幕宽高
    private int mScreenWidth;
    private int mScreenHeight;
    private int mPicHeight;

    //闪光灯模式 0:关闭 1: 开启 2: 自动
    private int mLightModel = 0;
    private ImageView mFlashLight;

    private boolean mPreViewRunning = false;


    private ImageView mClose;
    private ImageView mTakePhoto;

    private ImageView mThumbnail;

    private View mDesLayer;
    private TextView mDescription;
    private TextView mNote;
    private TextView mNumPhoto;

    private ImageView mImageDesc;
    private ImageView mWaterImage;

    private TextView mGallery;
    private TextView mCancel;

    //Animation
    private View mBtnView;
    private View mExplainView;
    private Animation mCollapseAnimation;
    private Animation mExpandAnimation;

    private String mBitmapPath;
    private Bitmap mBitmap;

    private int mStatus;
    private int mImageId;
    private String mImageClass;
    private int mImageSeqNum;
    private CarImageBean mCurCarImage;
    private List<CarImageBean> mCarImageList;

    private String mCarBillId;
    private CarBillBean mCarBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initDatas();
        initView();
        initAnimate();
        initCamera();
        loadWaterAndDes();
    }

    private void initDatas() {
        initStatus();
        initOther();
    }

    private void initStatus() {
        mStatus = (int) SPUtil.get(this, CacheContants.BILL_STATUS, StatusUtils.BILL_STATUS_NONE);
        mImageId = (int) SPUtil.get(this, CacheContants.IMAGEID, 0);
        mImageClass = (String) SPUtil.get(this, CacheContants.IMAGECLASS, "");
        mImageSeqNum = (int) SPUtil.get(this, CacheContants.IMAGESEQNUM, 0);
        mCarBillId = (String) SPUtil.get(this, CacheContants.CARBILLID, "");

        CarLog.d(TAG, "initStatus billStatus=" + mStatus + ", imageId=" + mImageId + ", imageClass=" + mImageClass + ", imageSeqNum=" + mImageSeqNum + ", mCarBillId=" + mCarBillId);
        if (statusIsNone()) {
            initCarImageForNone();
        } else if (statusIsSave()) {
            initCarImageForSave();
        } else {
            initCarImageForReturn();
        }

        CarLog.d(TAG, "initStatus mCarImageList=" + mCarImageList.size());
        CarLog.d(TAG, "initStatus mCarBill=" + mCarBill);
        CarLog.d(TAG, "initStatus mCurCarImage=" + mCurCarImage);

        //save(maybe) or carbill
        if (mCurCarImage != null) {
            initDisplayName();
            return;
        }

        for (CarImageBean bean : mCarImageList) {
            if (bean.imageSeqNum == mImageSeqNum) {
                mCurCarImage = bean;
                initDisplayName();
                break;
            }
        }

        CarLog.d(TAG, "imageList carImage=" + mCurCarImage);
        //还是null，肯定是添加照片
        if (mCurCarImage == null) {
            mCurCarImage = new CarImageBean();
            mCurCarImage.imageSeqNum = mImageSeqNum;
            mCurCarImage.imageClass = mImageClass;
            mCurCarImage.imageId = mImageId;
            mCurCarImage.carBillId = mCarBillId;
            mCurCarImage.displayName = getString(R.string.add_picture);
        }
    }

    private boolean statusIsNone() {
        return mStatus == StatusUtils.BILL_STATUS_NONE;
    }

    private boolean statusIsSave() {
        return mStatus == StatusUtils.BILL_STATUS_SAVE;
    }

    private void initCarImageForNone() {
        int type = ImageModelDelegator.getInstance().getTypeForImageClass(mImageClass);
        mCarImageList = ImageModelDelegator.getInstance().getSaveModel(type, mImageId);
    }

    private void initCarImageForSave() {
        mCurCarImage = DBDelegator.getInstance().queryImageClassForImageId(mImageId, mImageClass, mImageSeqNum);
        mCarBill = DBDelegator.getInstance().queryLocalCarbill(mImageId);

        int type = ImageModelDelegator.getInstance().getTypeForImageClass(mImageClass);
        mCarImageList = ImageModelDelegator.getInstance().getSaveModel(type, mImageId);
    }

    private void initCarImageForReturn() {
        mCarBill = DBDelegator.getInstance().queryCarBill(mCarBillId);
        mCurCarImage = DBDelegator.getInstance().queryImageClassForCarBillId(mCarBillId, mImageClass, mImageSeqNum);
        mCarImageList = ImageModelDelegator.getInstance().getHttpModel(mCarBillId, mImageClass);
    }


    private void initDisplayName() {
        int type = ImageModelDelegator.getInstance().getTypeForImageClass(mImageClass);
        String disPlayName = ImageModelDelegator.getInstance().getDisplayName(type, mCurCarImage.imageSeqNum);
        if (disPlayName == null) {
            disPlayName = getString(R.string.add_picture);
        }
        mCurCarImage.displayName = disPlayName;
    }

    private void initOther() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);

        mThumbnail = (ImageView) findViewById(R.id.thumbnail);
        ViewUtil.setViewVisible(mThumbnail, false);

        mTakePhoto = (ImageView) findViewById(R.id.img_camera);
        mTakePhoto.setOnClickListener(this);

        //关闭相机界面按钮
        mClose = (ImageView) findViewById(R.id.camera_close);
        mClose.setOnClickListener(this);

        //闪光灯
        mFlashLight = (ImageView) findViewById(R.id.flash_light);
        mFlashLight.setOnClickListener(this);

        mGallery = (TextView) findViewById(R.id.gallery);
        mGallery.setOnClickListener(this);
        mCancel = (TextView) findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);

        //水印和说明
        mImageDesc = (ImageView) findViewById(R.id.iv_take_photo_model);
        mWaterImage = (ImageView) findViewById(R.id.structure);

        mDesLayer = findViewById(R.id.desLayer);
        mDescription = (TextView) findViewById(R.id.description);
        mDescription.setText(mCurCarImage.displayName);
        if (mCurCarImage.displayName.contains("选拍")) {
            mDescription.setTextColor(getResources().getColor(R.color.green));
        }
        mNote = (TextView) findViewById(R.id.note);
        mNumPhoto = (TextView) findViewById(R.id.numPhoto);
        refreshNext();

        mBtnView = findViewById(R.id.lin_explain_btn);
        mBtnView.setOnClickListener(this);
        mExplainView = findViewById(R.id.rel_explain);
        mExplainView.setOnClickListener(this);
        ViewUtil.setViewVisible(mExplainView, false);

        showTakePhotoPicture(false);
    }

    private void refreshNext() {
        boolean isAddPic = mCurCarImage.imageSeqNum >= mCarImageList.size();
        mNumPhoto.setText((mCurCarImage.imageSeqNum + 1) + "/" + (isAddPic ? mCarImageList.size() + 1 : mCarImageList.size()));
        CarLog.d(TAG, "refreshNext mCurCarImage: " + mCurCarImage);
        loadWaterAndDes();
    }

    private void loadWaterAndDes() {
        ImageMetaBean imageMeta = DataDelegator.getInstance().requestImageMeta(mImageClass, mCurCarImage.imageSeqNum);
        if (imageMeta != null) {
            CarLog.d(TAG, "refreshNext imageMeta: " + imageMeta);
            //ImageLoaderProxy.loadImageDesc(UrlConstants.getProjectInterface() + imageMeta.imageDesc, mImageDesc);
            //ImageLoaderProxy.loadImageWaterMark(UrlConstants.getProjectInterface() + imageMeta.waterMark, mWaterImage);
        }
    }

    private void initAnimate() {
        mExpandAnimation = AnimationUtils.loadAnimation(this, R.anim.expend_up);
        mCollapseAnimation = AnimationUtils.loadAnimation(this, R.anim.collapse_down);
        mCollapseAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation paramAnimation) {
            }

            public void onAnimationRepeat(Animation paramAnimation) {
            }

            public void onAnimationStart(Animation paramAnimation) {
            }
        });
    }

    private void showTakePhotoPicture(boolean isShow) {
        setPreviewRunning(!isShow);

        mThumbnail.setImageBitmap(isShow ? mBitmap : null);
        if (!isShow) {
            recycle(mBitmap);
        }

        mGallery.setText(isShow ? R.string.take_again : R.string.gallery);
        int resId = isShow ? R.string.take_next : R.string.cancel;
        if (isShow && ((mCurCarImage.imageSeqNum + 1) >= mCarImageList.size())) {
            resId = R.string.complete;
        }
        mCancel.setText(resId);

        ViewUtil.setViewVisible(mThumbnail, isShow);
        ViewUtil.setViewVisible(mFlashLight, !isShow);
        ViewUtil.setViewVisible(mTakePhoto, !isShow);
        ViewUtil.setViewVisible(mBtnView, !isShow);
        ViewUtil.setViewVisible(mNumPhoto, !isShow);
        ViewUtil.setViewVisible(mDesLayer, !isShow);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lin_explain_btn) {
            onAnimationExplain();
        } else {
            if (closeAnimal()) {
                return;
            }
            switch (id) {
                case R.id.img_camera:
                    onCamera();
                    break;
                //退出相机界面 释放资源
                case R.id.camera_close:
                    finish();
                    break;
                //闪光灯
                case R.id.flash_light:
                    onFlashLight();
                    break;
                case R.id.gallery:
                    onGallery();
                    break;
                case R.id.cancel:
                    onCancel();
                    break;
                case R.id.lin_explain_btn:
                    onAnimationExplain();
                    break;
                case R.id.rel_explain:
                    closeAnimal();
                    break;
            }
        }

    }

    private void onAnimationExplain() {
        if (mExplainView.getVisibility() == View.VISIBLE) {
            closeAnimal();
            return;
        }
        mExplainView.setVisibility(View.VISIBLE);
        mExplainView.startAnimation(this.mExpandAnimation);
    }

    private boolean closeAnimal() {
        if (mExplainView.getVisibility() != View.VISIBLE)
            return false;
        this.mExplainView.setVisibility(View.GONE);
        this.mExplainView.startAnimation(this.mCollapseAnimation);
        return true;
    }


    private void takeNextPicture() {
        setPreviewRunning(false);
        changePreview();
        showTakePhotoPicture(false);
        processImageData();
        refreshNext();
    }

    private void changePreview() {
        if (mPreViewRunning) {
            mCamera.stopPreview();
        } else {
            mCamera.startPreview();
        }
    }

    private void processImageData() {
        mCurCarImage.imageLocalUrl = mBitmapPath;
        if (statusIsNone()) {
            processImageDataInNone();
        } else if (statusIsSave()) {
            processImageDataInSave();
        } else {
            processImageDataInReturn();
        }
        changeNextCarImage();
    }

    private void changeNextCarImage() {
        if ((mCurCarImage.imageSeqNum + 1) < mCarImageList.size()) {
            mCurCarImage = mCarImageList.get(mCurCarImage.imageSeqNum + 1);
            initDisplayName();
            mDescription.setText(mCurCarImage.displayName);
            if (mCurCarImage.displayName.contains("选拍")) {
                mDescription.setTextColor(getResources().getColor(R.color.green));
            }
        }
    }

    private void processImageDataInNone() {
        if (mImageId <= 0) {
            mImageId = DBDelegator.getInstance().getDBMaxId() + 1;
            SPUtil.put(this, CacheContants.IMAGEID, mImageId);
            SPUtil.put(this, CacheContants.BILL_STATUS, StatusUtils.BILL_STATUS_SAVE);
        }

        mCurCarImage.imageId = mImageId;
        mCurCarImage.createTime = DateUtils.getCurrDate();
        mCurCarImage.updateTime = DateUtils.getCurrDate();

        DBDelegator.getInstance().insertCarImage(mCurCarImage);

        if (mCarBill == null) {
            mCarBill = new CarBillBean();
            mCarBill.carBillId = null;
            mCarBill.imageId = mImageId;
            mCarBill.createTime = DateUtils.getCurrDate();
            mCarBill.modifyTime = DateUtils.getCurrDate();
            boolean success = DBDelegator.getInstance().insertCarBill(mCarBill);
            CarLog.d(TAG, "processImageDataInNone success " + success + ", mCarBill=" + mCarBill);
            postLocalCarbill();
        }
    }

    private void postLocalCarbill() {
        LocalStatusSubEvent local = new LocalStatusSubEvent();
        local.setTag(LocalStatusSubEvent.TAG_ADD_CARBILL);
        EventProxy.post(local);
    }

    private void processImageDataInSave() {
        mCurCarImage.imageId = mImageId;
        mCurCarImage.createTime = DateUtils.getCurrDate();
        mCurCarImage.updateTime = DateUtils.getCurrDate();
        boolean success = DBDelegator.getInstance().insertCarImage(mCurCarImage);
        CarLog.d(TAG, "processImageDataInSave success " + success + ", mCurCarImage=" + mCurCarImage);
    }

    private void processImageDataInReturn() {
        //依赖CarBillId更新
        mCurCarImage.imageId = 0;
        mCurCarImage.carBillId = mCarBillId;
        mCurCarImage.imageUpdate = StatusUtils.IMAGE_UPDATE;
        mCurCarImage.createTime = DateUtils.getCurrDate();
        mCurCarImage.updateTime = DateUtils.getCurrDate();
        boolean update = DBDelegator.getInstance().updateCarImage(mCurCarImage);
        if (!update) {
            DBDelegator.getInstance().insertCarImage(mCurCarImage);
        }
        CarLog.d(TAG, "processImageDataInReturn mCurCarImage=" + mCurCarImage + ", update=" + update);
    }


    private void onCamera() {
        if (mPreViewRunning) {
            switch (mLightModel) {
                case 0:
                    //关闭
                    CameraUtil.getInstance().turnLightOff(mCamera);
                    break;
                case 1:
                    CameraUtil.getInstance().turnLightOn(mCamera);
                    break;
                case 2:
                    //自动
                    CameraUtil.getInstance().turnLightAuto(mCamera);
                    break;
            }
            captrue();
            setPreviewRunning(false);
        }
    }

    private void onFlashLight() {
        Camera.Parameters parameters = mCamera.getParameters();
        switch (mLightModel) {
            case 0:
                //打开
                mLightModel = 1;
                mFlashLight.setImageResource(R.drawable.btn_camera_flash_on);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启
                mCamera.setParameters(parameters);
                break;
            case 1:
                //自动
                mLightModel = 2;
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
                mFlashLight.setImageResource(R.drawable.btn_camera_flash_auto);
                break;
            case 2:
                //关闭
                mLightModel = 0;
                //关闭
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mFlashLight.setImageResource(R.drawable.btn_camera_flash_off);
                break;
        }
    }


    private void onCancel() {
        String text = mCancel.getText().toString();
        if (getResources().getString(R.string.cancel).equals(text)) {
            finish();
        } else if (getResources().getString(R.string.complete).equals(text)) {
            processImageData();
            finish();
        } else {
            takeNextPicture();
        }
    }

    private void onGallery() {
        String text = mGallery.getText().toString();
        if (getResources().getString(R.string.gallery).equals(text)) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, ActivityUtils.ACTION_GALLERY);
        } else {
            setPreviewRunning(false);
            changePreview();
            mBitmapPath = null;
            showTakePhotoPicture(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityUtils.ACTION_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String imagePath = Utils.getRealPathFromURI(this, selectedImage);
            CarLog.d(TAG, "onActivityResult imagePath: " + imagePath);

            recycle(mBitmap);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            int srcH = options.outHeight;
            int srcW = options.outWidth;
            CarLog.d(TAG, "onActivityResult srcH = " + srcH + ", srcW = " + srcW);
            if (srcH <= 0 || srcW <= 0) {
                return;
            }
            //standard w*h=1024*1024
            int scaleW = srcW / 1024;
            int scaleH = srcH / 1024;
            //取小的那个值，最小为1。
            int scale = scaleH > scaleW ? scaleW : scaleH;
            scale = scale > 1 ? scale : 1;
            CarLog.d(TAG, "onActivityResult scaleH = " + scaleH + ", scaleW = " + scaleW);
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            mBitmap = BitmapFactory.decodeFile(imagePath, options);

            //save as
            mBitmapPath = DeviceStorageManager.getInstance().getThumbnailPath() +
                    File.separator + System.currentTimeMillis() + ".jpg";
            BitmapUtils.saveJPGE_After(CameraActivity.this, mBitmap, mBitmapPath, 100);
            CarLog.d(TAG, "onActivityResult mBitmapPath " + mBitmapPath);

            setShowPicture();
        }
    }

    private void initCamera() {
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
            if (mHolder != null) {
                startPreview(mCamera, mHolder);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        recycle(mBitmap);
    }

    /**
     * 获取Camera实例
     *
     * @return
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {
            CarLog.d(TAG, "getCamera e: " + e);
        }
        return camera;
    }

    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            CameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
            camera.startPreview();
            setPreviewRunning(true);
        } catch (IOException e) {
            e.printStackTrace();
            CarLog.d(TAG, "IOException startPreview e: " + e);
        } catch (RuntimeException e) {
            e.printStackTrace();
            CarLog.d(TAG, "RuntimeException startPreview e: " + e);
            ToastUtils.show(this, R.string.camera_busy_now);
            finish();
        }
    }

    private void setPreviewRunning(boolean preview) {
        mPreViewRunning = preview;
    }


    private void captrue() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                recycle(mBitmap);

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                mBitmapPath = DeviceStorageManager.getInstance().getThumbnailPath() +
                        File.separator + System.currentTimeMillis() + ".jpeg";


                if (bitmap == null) {
                    return;
                }
                int srcH = bitmap.getHeight();
                int srcW = bitmap.getWidth();
                CarLog.d(TAG, "takePicture srcW = (" + srcW + ", " + srcH + ")");
                if (srcH <= 0 || srcW <= 0) {
                    return;
                }
                //standard w*h=1024*1024
                int scaleW = srcW / 1024;
                int scaleH = srcH / 1024;
                //取小的那个值，最小为1。
                int scale = scaleH > scaleW ? scaleW : scaleH;
                scale = scale > 1 ? scale : 1;
                int targetW = srcW / scale;
                int targetH = srcH / scale;
                CarLog.d(TAG, "takePicture scale = (" + scaleH + ", " + scaleW + "), target=(" + targetW + "," + targetH + ")");
                mBitmap = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);
                if(mBitmap != bitmap) {
                    recycle(bitmap);
                }

                BitmapUtils.saveJPGE_After(CameraActivity.this, mBitmap, mBitmapPath, 100);
                CarLog.d(TAG, "captrue mBitmapPath " + mBitmapPath);

                setShowPicture();
            }
        });
    }

    private void recycle(Bitmap b) {
        if (b != null && !b.isRecycled()) {
            b.recycle();
        }
    }

    private void setShowPicture() {
        showTakePhotoPicture(true);
    }

    /**
     * 设置
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPreviewSizes(), 800);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictrueSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPictureSizes(), 800);
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);

        camera.setParameters(parameters);

        /**
         * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
         * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width
         * previewSize.width才是surfaceView的高度
         * 一般相机都是屏幕的宽度 这里设置为屏幕宽度 高度自适应 你也可以设置自己想要的大小
         *
         */
        mPicHeight = (mScreenWidth * pictrueSize.width) / pictrueSize.height;
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CarLog.d(TAG, "surfaceCreated");
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CarLog.d(TAG, "surfaceChanged");
        mCamera.stopPreview();
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CarLog.d(TAG, "surfaceDestroyed");
    }

}
