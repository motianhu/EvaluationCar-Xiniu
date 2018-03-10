package com.smona.app.evaluationcar.ui.evaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.data.bean.CarImageBean;
import com.smona.app.evaluationcar.data.event.EvaActionEvent;
import com.smona.app.evaluationcar.data.event.background.TaskSubEvent;
import com.smona.app.evaluationcar.data.model.ResCarImagePage;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;
import com.smona.app.evaluationcar.ui.common.base.BaseScrollView;
import com.smona.app.evaluationcar.ui.common.base.LimitGridView;
import com.smona.app.evaluationcar.ui.evaluation.attach.AttachmentActivity;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.DateUtils;
import com.smona.app.evaluationcar.util.SPUtil;
import com.smona.app.evaluationcar.util.StatusUtils;
import com.smona.app.evaluationcar.util.ToastUtils;
import com.smona.app.evaluationcar.util.Utils;
import com.smona.app.evaluationcar.util.ViewUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moth on 2016/12/18.
 */

public class EvaluationActivity extends HeaderActivity implements View.OnClickListener {
    private static final String TAG = EvaluationActivity.class.getSimpleName();
    private BaseScrollView mScrollView;
    private View mReasonContainer;
    private WebView mReasonWebView;

    //登记证
    private View mClassRegistrationTitle;
    private LimitGridView mClassRegistrationGrid;
    private ImageModelAdapter mClassRegistrationAdapter;
    private List<CarImageBean> mClassRegistrationList;

    //行驶证
    private View mClassDrivingLicenseTitle;
    private LimitGridView mClassDrivingLicenseGrid;
    private ImageModelAdapter mClassDrivingLicenseAdapter;
    private List<CarImageBean> mClassDrivingLicenseList;

    //铭牌
    private View mClassVehicleNameplateTitle;
    private LimitGridView mClassVehicleNameplateGrid;
    private ImageModelAdapter mClassVehicleNameplateAdapter;
    private List<CarImageBean> mClassVehicleNameplateList;

    //车身外观
    private View mClassCarBodyTitle;
    private LimitGridView mClassCarBodyGrid;
    private ImageModelAdapter mClassCarBodyAdapter;
    private List<CarImageBean> mClassCarBodyList;

    //车体骨架
    private View mClassCarFrameTitle;
    private LimitGridView mClassCarFrameGrid;
    private ImageModelAdapter mClassCarFrameAdapter;
    private List<CarImageBean> mClassCarFrameList;

    //车辆内饰
    private View mClassVehicleInteriorTitle;
    private LimitGridView mClassVehicleInteriorGrid;
    private ImageModelAdapter mClassVehicleInteriorAdapter;
    private List<CarImageBean> mClassVehicleInteriorList;

    //差异补充
    private View mClassDifferenceSupplementTitle;
    private LimitGridView mClassDifferenceSupplementGrid;
    private ImageModelAdapter mClassDifferenceSupplementAdapter;
    private List<CarImageBean> mClassDifferenceSupplementList;

    //原车保险
    private View mClassOriginalCarInsurancetTitle;
    private LimitGridView mClassOriginalCarInsurancetGrid;
    private ImageModelAdapter mClassOriginalCarInsurancetAdapter;
    private List<CarImageBean> mClassOriginalCarInsurancetList;

    private View mInputGroup;

    private EditText mPrice;
    private EditText mNote;

    private RadioGroup mLeaseTerm;

    private String mAddPicStr;
    private int mCurStatus = StatusUtils.BILL_STATUS_NONE;

    //save data
    private int mImageId = 0;
    //carbill data
    private String mCarBillId = null;
    private CarBillBean mCarBill = null;

    //is residual
    private boolean mIsResidual = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatas();
        initViews();
        initImageList();
        //updateImageViews();
        requestImageForCarBillId();
        EventProxy.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initStatus();
        initCarImage();
        notifyReloadCarImage();
    }

    private void initDatas() {
        initStatus();
        initCarImage();
        initCarBill();
        initOther();
    }

    private void initStatus() {
        if (!statusIsNone()) {
            return;
        }
        mCurStatus = (int) SPUtil.get(this, CacheContants.BILL_STATUS, StatusUtils.BILL_STATUS_NONE);
        CarLog.d(TAG, "initStatus mCurStatus=" + mCurStatus);
    }

    private void initCarImage() {
        if (mImageId > 0) {
            return;
        }
        mImageId = (int) SPUtil.get(this, CacheContants.IMAGEID, 0);
        CarLog.d(TAG, "initCarImage imageId=" + mImageId);
    }

    private void initCarBill() {
        mCarBillId = (String) SPUtil.get(this, CacheContants.CARBILLID, "");

        if (!TextUtils.isEmpty(mCarBillId)) {
            mCarBill = DBDelegator.getInstance().queryCarBill(mCarBillId);
        } else if (statusIsSave()) {
            mCarBill = DBDelegator.getInstance().queryLocalCarbill(mImageId);
        }
        CarLog.d(TAG, "initCarBill carBillId=" + mCarBillId + "; mCarBill=" + mCarBill);
    }

    private void initOther() {
        mAddPicStr = getString(R.string.add_picture);
        mIsResidual = (boolean) SPUtil.get(this, CacheContants.ISResidualEVALUATION, false);
    }

    private void requestImageForCarBillId() {
        if (TextUtils.isEmpty(mCarBillId)) {
            return;
        }

        HttpDelegator.getInstance().getCarbillImages(mUserItem.mId, mCarBillId, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CarLog.d(TAG, "getCarbillImages onSuccess: " + result);
                ResCarImagePage resp = JsonParse.parseJson(result, ResCarImagePage.class);
                if (resp.total > 0) {
                    CarImageBean tempBean = null;
                    for (CarImageBean bean : resp.data) {
                        tempBean = DBDelegator.getInstance().queryImageClassForCarBillId(bean.carBillId, bean.imageClass, bean.imageSeqNum);
                        if (tempBean == null) {
                            //依赖CarBillId更新
                            DBDelegator.getInstance().insertCarImage(bean);
                        } else {
                            //依赖ImageID更新:可能是return的单,可能是save的单
                            tempBean.imageThumbPath = bean.imageThumbPath;
                            tempBean.imagePath = bean.imagePath;
                            DBDelegator.getInstance().updateCarImage(tempBean);
                        }
                    }
                    setNeedReload();
                    notifyReloadCarImage();
                }
            }

            @Override
            public void onFailed(String error) {
                CarLog.d(TAG, "onError ex: " + error);
            }
        });
    }

    private void setNeedReload() {
        mClassRegistrationAdapter.setNeedReload(true);
        mClassDrivingLicenseAdapter.setNeedReload(true);
        mClassVehicleNameplateAdapter.setNeedReload(true);
        mClassCarBodyAdapter.setNeedReload(true);
        mClassCarFrameAdapter.setNeedReload(true);
        mClassVehicleInteriorAdapter.setNeedReload(true);
        mClassDifferenceSupplementAdapter.setNeedReload(true);
        mClassOriginalCarInsurancetAdapter.setNeedReload(true);
    }

    private void initViews() {
        mScrollView = (BaseScrollView) findViewById(R.id.baseScrollView);
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        mScrollView.setFocusable(true);
        mScrollView.setFocusableInTouchMode(true);

        //退回原因
        mReasonContainer = findViewById(R.id.reason);
        mReasonWebView = (WebView) findViewById(R.id.reason_webview);
        if (statusIsReturn()) {
            ViewUtil.setViewVisible(findViewById(R.id.reason_attach), true);
            ViewUtil.setViewVisible(mReasonContainer, true);
            ViewUtil.setViewVisible(mReasonWebView, true);
            findViewById(R.id.reason_attach).setOnClickListener(this);

            String bodyHTML = mCarBill.applyAllOpinion;
            mReasonWebView.setWebViewClient(new WebViewClient());
            mReasonWebView.getSettings().setDefaultTextEncodingName("utf-8");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mReasonWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            } else {
                mReasonWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }
            mReasonWebView.loadData(Utils.getHtmlData(bodyHTML), "text/html; charset=utf-8", "utf-8");
        } else {
            ViewUtil.setViewVisible(mReasonContainer, false);
            ViewUtil.setViewVisible(mReasonWebView, false);
        }

        //登记证
        mClassRegistrationTitle = findViewById(R.id.class_registration_layer);
        mClassRegistrationGrid = (LimitGridView) findViewById(R.id.class_registration);
        mClassRegistrationAdapter = new ImageModelAdapter(this, ImageModelDelegator.IMAGE_Registration);
        mClassRegistrationGrid.setAdapter(mClassRegistrationAdapter);

        //行驶证
        mClassDrivingLicenseTitle = findViewById(R.id.class_driving_license_layer);
        mClassDrivingLicenseGrid = (LimitGridView) findViewById(R.id.class_driving_license);
        mClassDrivingLicenseAdapter = new ImageModelAdapter(this, ImageModelDelegator.IMAGE_DrivingLicense);
        mClassDrivingLicenseGrid.setAdapter(mClassDrivingLicenseAdapter);

        //车辆铭牌
        mClassVehicleNameplateTitle = findViewById(R.id.class_vehicle_nameplate_layer);
        mClassVehicleNameplateGrid = (LimitGridView) findViewById(R.id.class_vehicle_nameplate);
        mClassVehicleNameplateAdapter = new ImageModelAdapter(this, ImageModelDelegator.IMAGE_VehicleNameplate);
        mClassVehicleNameplateGrid.setAdapter(mClassVehicleNameplateAdapter);

        //车身外观
        mClassCarBodyTitle = findViewById(R.id.class_car_body_layer);
        mClassCarBodyGrid = (LimitGridView) findViewById(R.id.class_car_body);
        mClassCarBodyAdapter = new ImageModelAdapter(this, ImageModelDelegator.IMAGE_CarBody);
        mClassCarBodyGrid.setAdapter(mClassCarBodyAdapter);

        //车骨架
        mClassCarFrameTitle = findViewById(R.id.class_car_frame_layer);
        mClassCarFrameGrid = (LimitGridView) findViewById(R.id.class_car_frame);
        mClassCarFrameAdapter = new ImageModelAdapter(this, ImageModelDelegator.IMAGE_CarFrame);
        mClassCarFrameGrid.setAdapter(mClassCarFrameAdapter);

        //车辆内饰
        mClassVehicleInteriorTitle = findViewById(R.id.class_vehicle_interior_layer);
        mClassVehicleInteriorGrid = (LimitGridView) findViewById(R.id.class_vehicle_interior);
        mClassVehicleInteriorAdapter = new ImageModelAdapter(this, ImageModelDelegator.IMAGE_VehicleInterior);
        mClassVehicleInteriorGrid.setAdapter(mClassVehicleInteriorAdapter);

        //差异补充
        mClassDifferenceSupplementTitle = findViewById(R.id.class_difference_supplement_layer);
        mClassDifferenceSupplementGrid = (LimitGridView) findViewById(R.id.class_difference_supplement);
        mClassDifferenceSupplementAdapter = new ImageModelAdapter(this, ImageModelDelegator.IMAGE_DifferenceSupplement);
        mClassDifferenceSupplementGrid.setAdapter(mClassDifferenceSupplementAdapter);

        //原车保险
        mClassOriginalCarInsurancetTitle = findViewById(R.id.class_original_car_insurancet_layer);
        mClassOriginalCarInsurancetGrid = (LimitGridView) findViewById(R.id.class_original_car_insurancet);
        mClassOriginalCarInsurancetAdapter = new ImageModelAdapter(this, ImageModelDelegator.IMAGE_OriginalCarInsurancet);
        mClassOriginalCarInsurancetGrid.setAdapter(mClassOriginalCarInsurancetAdapter);


        mInputGroup = findViewById(R.id.include_input);

        //price and remark
        mPrice = (EditText) findViewById(R.id.et_price);
        mNote = (EditText) findViewById(R.id.et_remark);

        //lease term
        View leaseTermGroup = findViewById(R.id.group_lease_term);
        boolean isResidual = isResidual();
        ViewUtil.setViewVisible(leaseTermGroup, isResidual);
        mLeaseTerm = (RadioGroup) findViewById(R.id.rg_lease_term);

        //设置定位按钮事件及初始化定位
        findViewById(R.id.rb_car_license).setOnClickListener(this);
        findViewById(R.id.rb_car_body).setOnClickListener(this);
        findViewById(R.id.rb_car_frame).setOnClickListener(this);
        findViewById(R.id.rb_vehicle_interior).setOnClickListener(this);
        findViewById(R.id.rb_supplement).setOnClickListener(this);

        findViewById(R.id.rb_car_license).performClick();

        //保存及提交
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);

        if (mCarBill == null) {
            return;
        }
        mPrice.setText(mCarBill.preSalePrice + "");
        mNote.setText(mCarBill.mark);

        if(isResidual && (mCarBill.leaseTerm > 0)) {
           mLeaseTerm.check(getResIdForLeaseTerm(mCarBill.leaseTerm));
        }
    }

    private void initImageList() {
        mClassRegistrationList = new ArrayList<CarImageBean>();
        mClassDrivingLicenseList = new ArrayList<CarImageBean>();
        mClassVehicleNameplateList = new ArrayList<CarImageBean>();
        mClassCarBodyList = new ArrayList<CarImageBean>();
        mClassCarFrameList = new ArrayList<CarImageBean>();
        mClassVehicleInteriorList = new ArrayList<CarImageBean>();
        mClassDifferenceSupplementList = new ArrayList<CarImageBean>();
        mClassOriginalCarInsurancetList = new ArrayList<CarImageBean>();
    }

    private void updateImageViews() {
        mClassRegistrationAdapter.update(mClassRegistrationList);
        mClassDrivingLicenseAdapter.update(mClassDrivingLicenseList);
        mClassVehicleNameplateAdapter.update(mClassVehicleNameplateList);
        mClassCarBodyAdapter.update(mClassCarBodyList);
        mClassCarFrameAdapter.update(mClassCarFrameList);
        mClassVehicleInteriorAdapter.update(mClassVehicleInteriorList);
        mClassDifferenceSupplementAdapter.update(mClassDifferenceSupplementList);
        mClassOriginalCarInsurancetAdapter.update(mClassOriginalCarInsurancetList);
    }

    private boolean isResidual() {
        return mUserItem.userBean.isGuanghui() && mIsResidual;
    }

    private void notifyReloadCarImage() {
        TaskSubEvent event = new TaskSubEvent();
        event.action = TaskSubEvent.ACTION_RELOAD;
        EventProxy.post(event);
        CarLog.d(TAG, "notifyReloadCarImage");
    }

    private void notifyActionMain(int action) {
        EvaActionEvent event = new EvaActionEvent();
        event.action = action;
        EventProxy.post(event);
        CarLog.d(TAG, "notifyActionMain");
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void actionSubEvent(TaskSubEvent event) {
        int action = event.action;
        CarLog.d(TAG, "actionSubEvent action= " + action);
        if (action == TaskSubEvent.ACTION_TASK) {
            CarBillBean bean = (CarBillBean) event.obj;
            if (statusIsReturn()) {
                startTarkForReturn(bean);
            } else {
                startTarkForSave(bean);
            }
            notifyActionMain(EvaActionEvent.FINISH);
        } else {
            clearImageList();
            reloadImageList();
            notifyActionMain(EvaActionEvent.REFRESH);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionMainEvent(EvaActionEvent actionEvent) {
        int action = actionEvent.action;
        CarLog.d(TAG, "actionMainEvent action " + action);
        if (action == EvaActionEvent.REFRESH) {
            updateImageViews();
        } else {
            runFinish();
        }
    }

    private void reloadImageList() {
        initImageData(mClassRegistrationAdapter, mClassRegistrationList, ImageModelDelegator.IMAGE_Registration);
        initImageData(mClassDrivingLicenseAdapter, mClassDrivingLicenseList, ImageModelDelegator.IMAGE_DrivingLicense);
        initImageData(mClassVehicleNameplateAdapter, mClassVehicleNameplateList, ImageModelDelegator.IMAGE_VehicleNameplate);
        initImageData(mClassCarBodyAdapter, mClassCarBodyList, ImageModelDelegator.IMAGE_CarBody);
        initImageData(mClassCarFrameAdapter, mClassCarFrameList, ImageModelDelegator.IMAGE_CarFrame);
        initImageData(mClassVehicleInteriorAdapter, mClassVehicleInteriorList, ImageModelDelegator.IMAGE_VehicleInterior);
        initImageData(mClassDifferenceSupplementAdapter, mClassDifferenceSupplementList, ImageModelDelegator.IMAGE_DifferenceSupplement);
        initImageData(mClassOriginalCarInsurancetAdapter, mClassOriginalCarInsurancetList, ImageModelDelegator.IMAGE_OriginalCarInsurancet);
    }

    private void clearImageList() {
        clearImageList(mClassRegistrationAdapter, mClassRegistrationList);
        clearImageList(mClassDrivingLicenseAdapter, mClassDrivingLicenseList);
        clearImageList(mClassVehicleNameplateAdapter, mClassVehicleNameplateList);
        clearImageList(mClassCarBodyAdapter, mClassCarBodyList);
        clearImageList(mClassCarFrameAdapter, mClassCarFrameList);
        clearImageList(mClassVehicleInteriorAdapter, mClassVehicleInteriorList);
        clearImageList(mClassDifferenceSupplementAdapter, mClassDifferenceSupplementList);
        clearImageList(mClassOriginalCarInsurancetAdapter, mClassOriginalCarInsurancetList);
    }

    private void clearImageList(ImageModelAdapter adapter, List<CarImageBean> dataList) {
        if (adapter.isNeedReload()) {
            dataList.clear();
        }
    }

    private void initImageData(ImageModelAdapter adapter, List<CarImageBean> data, int type) {
        if (!adapter.isNeedReload()) {
            return;
        }
        String imageClass = ImageModelDelegator.getInstance().getImageClassForType(type);
        if (statusIsReturn()) {
            List<CarImageBean> tempData = ImageModelDelegator.getInstance().getHttpModel(mCarBillId, imageClass);

            CarImageBean bean = new CarImageBean();
            bean.displayName = mAddPicStr;
            bean.imageClass = imageClass;
            bean.imageSeqNum = tempData.size();
            tempData.add(bean);

            data.addAll(tempData);
        } else {
            List<CarImageBean> tempData = ImageModelDelegator.getInstance().getSaveModel(type, mImageId);

            CarImageBean bean = new CarImageBean();
            bean.displayName = mAddPicStr;
            bean.imageClass = imageClass;
            bean.imageSeqNum = tempData.size();
            bean.imageId = mImageId;
            tempData.add(bean);

            data.addAll(tempData);
        }
    }

    private boolean statusIsNone() {
        return mCurStatus == StatusUtils.BILL_STATUS_NONE;
    }

    private boolean statusIsSave() {
        return mCurStatus == StatusUtils.BILL_STATUS_SAVE;
    }

    private boolean statusIsReturn() {
        return mCurStatus == StatusUtils.BILL_STATUS_RETURN;
    }

    private void onSave() {
        if (!TextUtils.isEmpty(mCarBillId)) {
            mCarBill = DBDelegator.getInstance().queryCarBill(mCarBillId);
        } else if (statusIsSave()) {
            mCarBill = DBDelegator.getInstance().queryLocalCarbill(mImageId);
        }
        if (mCarBill != null) {
            if(TextUtils.isEmpty(mPrice.getText().toString())) {
                mCarBill.preSalePrice = 0;
            } else {
                mCarBill.preSalePrice = Double.valueOf(mPrice.getText().toString());
            }
            mCarBill.mark = mNote.getText().toString();
            mCarBill.leaseTerm = getLeaseTerm();
            DBDelegator.getInstance().updateCarBill(mCarBill);
        }
        finish();
    }

    private void onSubmit() {
        if (statusIsReturn()) {
            submitReturn();
        } else {
            submitNone();
        }
    }

    private void submitReturn() {
        if (!isTakePhoto()) {
            return;
        }

        String preScalePrice = mPrice.getText().toString();
        if (TextUtils.isEmpty(preScalePrice)) {
            ToastUtils.show(this, R.string.evaluation_input_pre_price);
            return;
        }

        if(isResidual() && getLeaseTerm() < 1) {
            ToastUtils.show(this, R.string.please_select_evaluation_select_date);
            return;
        }

        postEvent(preScalePrice);
    }

    private void submitNone() {
        if (!isTakePhoto()) {
            return;
        }

        String preScalePrice = mPrice.getText().toString();
        if (TextUtils.isEmpty(preScalePrice)) {
            ToastUtils.show(this, R.string.evaluation_input_pre_price);
            return;
        }

        if (Double.valueOf(preScalePrice) <= 0) {
            ToastUtils.show(this, R.string.evaluation_input_pre_price);
            return;
        }

        if(isResidual() && getLeaseTerm() < 1) {
            ToastUtils.show(this, R.string.please_select_evaluation_select_date);
            return;
        }

        postEvent(preScalePrice);
    }

    private void postEvent(String preScalePrice) {
        String mark = mNote.getText().toString();
        CarBillBean bean = new CarBillBean();
        bean.carBillId = mCarBillId;
        bean.preSalePrice = Double.valueOf(preScalePrice);
        bean.mark = mark;
        bean.imageId = mImageId;
        bean.leaseTerm = getLeaseTerm();

        //send background post
        TaskSubEvent event = new TaskSubEvent();
        event.obj = bean;
        event.action = TaskSubEvent.ACTION_TASK;
        EventProxy.post(event);
    }

    private int getLeaseTerm() {
        int resId = mLeaseTerm.getCheckedRadioButtonId();
        switch(resId) {
            case R.id.leaseTerm12:
                return 12;
            case R.id.leaseTerm24:
                return 24;
            case R.id.leaseTerm36:
                return 36;
            default:
                return 0;
        }
    }


    private int getResIdForLeaseTerm(int leaseTerm) {
        switch (leaseTerm) {
            case 24:
                return R.id.leaseTerm24;
            case 36:
                return R.id.leaseTerm36;
            default:
                return R.id.leaseTerm12;
        }
    }

    private boolean isTakePhoto() {
        return checkPhoto(mClassRegistrationAdapter) &&
                checkPhoto(mClassDrivingLicenseAdapter) &&
                checkPhoto(mClassVehicleNameplateAdapter) &&
                checkPhoto(mClassCarBodyAdapter) &&
                checkPhoto(mClassCarFrameAdapter) &&
                checkPhoto(mClassVehicleInteriorAdapter);
    }

    private boolean checkPhoto(ImageModelAdapter adapter) {
        CarImageBean bean = adapter.checkPhoto();
        if (bean != null) {
            String tip = String.format(getString(R.string.evalution_submit_tips), bean.imageClass, bean.displayName);
            ToastUtils.show(this, tip);
            return false;
        }
        return true;
    }

    private void runFinish() {
        ToastUtils.show(EvaluationActivity.this, R.string.evaluation_submit_tips);
        finish();
    }

    public void startTarkForReturn(CarBillBean bean) {
        CarBillBean carBillBean = DBDelegator.getInstance().queryCarBill(mCarBillId);
        carBillBean.preSalePrice = bean.preSalePrice;
        carBillBean.mark = bean.mark;
        carBillBean.uploadStatus = StatusUtils.BILL_UPLOAD_STATUS_UPLOADING;
        carBillBean.leaseTerm = bean.leaseTerm;
        DBDelegator.getInstance().updateCarBill(carBillBean);

        ActivityUtils.startUpService(this);
    }

    public void startTarkForSave(CarBillBean bean) {
        CarBillBean localBean = DBDelegator.getInstance().queryLocalCarbill(bean.imageId);
        localBean.createTime = TextUtils.isEmpty(localBean.createTime) ? DateUtils.getCurrDate() : localBean.createTime;
        localBean.modifyTime = DateUtils.getCurrDate();
        localBean.uploadStatus = StatusUtils.BILL_UPLOAD_STATUS_UPLOADING;
        localBean.preSalePrice = bean.preSalePrice;
        localBean.mark = bean.mark;
        localBean.leaseTerm = bean.leaseTerm;
        DBDelegator.getInstance().updateCarBill(localBean);

        CarLog.d(TAG, "startTarkForSave localBean=" + localBean);

        ActivityUtils.startUpService(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CarLog.d(TAG, "requestCode: " + requestCode + "");
        if (requestCode == ActivityUtils.ACTION_CAMERA && resultCode == Activity.RESULT_OK && data != null) {

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_evaluation;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.create_carbill;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventProxy.unregister(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rb_car_license:
                mScrollView.smoothScrollTo(0, mClassRegistrationTitle.getTop());
                return;
            case R.id.rb_car_body:
                mScrollView.smoothScrollTo(0, mClassCarBodyTitle.getTop());
                return;
            case R.id.rb_car_frame:
                mScrollView.smoothScrollTo(0, mClassCarFrameTitle.getTop());
                return;
            case R.id.rb_vehicle_interior:
                mScrollView.smoothScrollTo(0, mClassVehicleInteriorTitle.getTop());
                return;
            case R.id.rb_supplement:
                mScrollView.smoothScrollTo(0, mClassDifferenceSupplementTitle.getTop());
                return;
            case R.id.btn_save:
                onSave();
                break;
            case R.id.btn_submit:
                onSubmit();
                break;
            case R.id.reason_attach:
                gotoAttach();
                break;
        }
    }

    private void gotoAttach() {
        Intent intent = new Intent();
        intent.setClass(this, AttachmentActivity.class);
        intent.putExtra(CacheContants.ATTACH_CARBILLID, mCarBillId);
        startActivity(intent);
    }
}
