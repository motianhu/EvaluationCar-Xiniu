package com.smona.app.evaluationcar.ui.evaluation.preevaluation.quick;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.data.bean.QuickPreCarImageBean;
import com.smona.app.evaluationcar.data.event.EvaActionEvent;
import com.smona.app.evaluationcar.data.event.background.TaskSubEvent;
import com.smona.app.evaluationcar.data.model.ResQuickPreCarBillPage;
import com.smona.app.evaluationcar.data.model.ResQuickPreCarImagePage;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;
import com.smona.app.evaluationcar.ui.common.base.LimitGridView;
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
 * Created by motianhu on 5/23/17.
 */

public class QuickPreevaluationActivity extends HeaderActivity implements View.OnClickListener {

    private static final String TAG = QuickPreevaluationActivity.class.getSimpleName();

    private View mRease;
    private WebView mReaseWebView;

    private LimitGridView mBaseGrid;
    private QuickImageModelAdapter mBaseAdapter;
    private List<QuickPreCarImageBean> mBaseData;

    private LimitGridView mSupplementGrid;
    private QuickImageModelAdapter mSupplementAdapter;
    private List<QuickPreCarImageBean> mSupplementData;

    private int mCurStatus = StatusUtils.BILL_STATUS_NONE;
    private String mQuickPreCarBillId = null;
    private QuickPreCarBillBean mPreQuickPreCarBillBean;
    private int mImageId = 0;

    private EditText mMark;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preevaluation_quick;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.evalution_pre_quick;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventProxy.register(this);
        //传递过来的状态和值
        initData();
        //控件初始化
        initViews();
        //数据链表初始化
        initImageList();
        //如果是驳回的，则获取服务器上的图片,保存到数据库
        requestImageForPreCarBillId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initStatus();
        initCarImage();
        notifyReloadCarImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventProxy.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CarLog.d(TAG, "requestCode: " + requestCode + "");
        if (requestCode == ActivityUtils.ACTION_CAMERA && resultCode == Activity.RESULT_OK && data != null) {

        }
    }

    private void initData() {
        initStatus();
        initCarImage();
        initCarBill();
    }

    private void initStatus() {
        if (!statusIsNone()) {
            return;
        }
        mCurStatus = (int) SPUtil.get(this, CacheContants.QUICK_BILL_STATUS, StatusUtils.BILL_STATUS_NONE);
        CarLog.d(TAG, "initStatus mCurStatus=" + mCurStatus);
    }

    private void initCarImage() {
        if (mImageId > 0) {
            return;
        }
        mImageId = (int) SPUtil.get(this, CacheContants.QUICK_IMAGEID, 0);
        CarLog.d(TAG, "initCarImage mImageId=" + mImageId);
    }

    private void initCarBill() {
        mQuickPreCarBillId = (String) SPUtil.get(this, CacheContants.QUICK_CARBILLID, "");
        CarLog.d(TAG, "initCarBill mQuickPreCarBillId=" + mQuickPreCarBillId);

        if (!TextUtils.isEmpty(mQuickPreCarBillId)) {
            mPreQuickPreCarBillBean = DBDelegator.getInstance().queryQuickPreCarBill(mQuickPreCarBillId);
        }
    }

    private void initViews() {
        //驳回原因
        mRease = findViewById(R.id.reason);
        mReaseWebView = (WebView) findViewById(R.id.reason_webview);
        refreshReasonView(mPreQuickPreCarBillBean);

        //基础照片
        mBaseGrid = (LimitGridView) findViewById(R.id.class_base);
        mBaseAdapter = new QuickImageModelAdapter(this, QuickImageModelDelegator.QUICK_BASE);
        mBaseGrid.setAdapter(mBaseAdapter);

        //补充照片
        mSupplementGrid = (LimitGridView) findViewById(R.id.class_supplement);
        mSupplementAdapter = new QuickImageModelAdapter(this, QuickImageModelDelegator.QUICK_SUPPLEMENT);
        mSupplementGrid.setAdapter(mSupplementAdapter);

        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);

        mMark = (EditText) findViewById(R.id.et_remark);
        if (mPreQuickPreCarBillBean != null) {
            mMark.setText(mPreQuickPreCarBillBean.mark);
        }
    }

    private void refreshReasonView(QuickPreCarBillBean bean) {
        if (statusIsReturn()) {
            ViewUtil.setViewVisible(mRease, true);
            ViewUtil.setViewVisible(mReaseWebView, true);
            String bodyHTML = bean.applyAllOpinion;
            mReaseWebView.setWebViewClient(new WebViewClient());
            mReaseWebView.getSettings().setDefaultTextEncodingName("utf-8");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mReaseWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            } else {
                mReaseWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }
            mReaseWebView.loadData(Utils.getHtmlData(bodyHTML), "text/html; charset=utf-8", "utf-8");
        } else {
            ViewUtil.setViewVisible(mRease, false);
            ViewUtil.setViewVisible(mReaseWebView, false);
        }
    }

    private void initImageList() {
        mBaseData = new ArrayList<>();
        mSupplementData = new ArrayList<>();
    }

    private void requestImageForPreCarBillId() {
        if (TextUtils.isEmpty(mQuickPreCarBillId)) {
            return;
        }
        HttpDelegator.getInstance().getQuickPreImage(mUserItem.mId, mQuickPreCarBillId, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CarLog.d(TAG, "getQuickPreImage onSuccess: " + result);
                ResQuickPreCarImagePage resp = JsonParse.parseJson(result, ResQuickPreCarImagePage.class);
                if (resp.total > 0) {
                    QuickPreCarImageBean tempBean = null;
                    for (QuickPreCarImageBean bean : resp.data) {
                        tempBean = DBDelegator.getInstance().queryImageForIdAndClass(bean.carBillId, bean.imageClass, bean.imageSeqNum);
                        if (tempBean == null) {
                            //依赖CarBillId更新
                            DBDelegator.getInstance().insertQuickPreCarImage(bean);
                        } else {
                            //依赖ImageID更新:可能是return的单,可能是save的单
                            tempBean.imageThumbPath = bean.imageThumbPath;
                            tempBean.imagePath = bean.imagePath;
                            DBDelegator.getInstance().updateQuickPreCarImage(tempBean);
                        }
                    }
                    notifyReloadCarImage();
                }
            }

            @Override
            public void onFailed(String error) {
                CarLog.d(TAG, "onError ex: " + error);
            }
        });
        DataDelegator.getInstance().getPreCarBillDetail(mUserItem.mId, mQuickPreCarBillId, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CarLog.d(TAG, "getQuickPreImage onSuccess: " + result);
                QuickPreCarBillBean resp = JsonParse.parseJson(result, QuickPreCarBillBean.class);
                if (resp != null) {
                    refreshReasonView(resp);
                }
            }

            @Override
            public void onFailed(String error) {
                CarLog.d(TAG, "onError ex: " + error);
            }
        });
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
            QuickPreCarBillBean bean = (QuickPreCarBillBean) event.obj;
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

    private void updateImageViews() {
        mBaseAdapter.update(mBaseData);
        mSupplementAdapter.update(mSupplementData);
    }

    private void reloadImageList() {
        initImageData(mBaseData, QuickImageModelDelegator.QUICK_BASE);
        initImageData(mSupplementData, QuickImageModelDelegator.QUICK_SUPPLEMENT);
    }

    private void clearImageList() {
        clearImageList(mBaseAdapter, mBaseData);
        clearImageList(mSupplementAdapter, mSupplementData);
    }

    private void clearImageList(QuickImageModelAdapter adapter, List<QuickPreCarImageBean> dataList) {
        if (adapter.isNeedReload()) {
            dataList.clear();
        }
    }

    private void initImageData(List<QuickPreCarImageBean> data, int type) {
        if (statusIsReturn()) {
            List<QuickPreCarImageBean> tempData = QuickImageModelDelegator.getInstance().getHttpModel(type, mQuickPreCarBillId);
            data.addAll(tempData);
        } else {
            List<QuickPreCarImageBean> tempData = QuickImageModelDelegator.getInstance().getSaveModel(type, mImageId);
            data.addAll(tempData);
        }
    }

    public void startTarkForReturn(QuickPreCarBillBean bean) {
        QuickPreCarBillBean carBillBean = DBDelegator.getInstance().queryQuickPreCarBill(mQuickPreCarBillId);
        carBillBean.mark = bean.mark;
        carBillBean.uploadStatus = StatusUtils.BILL_UPLOAD_STATUS_UPLOADING;
        DBDelegator.getInstance().updatePreCarBill(carBillBean);

        ActivityUtils.startQuickUpService(this);
    }

    public void startTarkForSave(QuickPreCarBillBean bean) {
        QuickPreCarBillBean localBean = DBDelegator.getInstance().queryLocalQuickPreCarbill(bean.imageId);
        localBean.createTime = TextUtils.isEmpty(localBean.createTime) ? DateUtils.getCurrDate() : localBean.createTime;
        localBean.modifyTime = DateUtils.getCurrDate();
        localBean.uploadStatus = StatusUtils.BILL_UPLOAD_STATUS_UPLOADING;
        localBean.mark = bean.mark;
        DBDelegator.getInstance().updatePreCarBill(localBean);

        CarLog.d(TAG, "startTarkForSave localBean=" + localBean);
        ActivityUtils.startQuickUpService(this);
    }

    private boolean statusIsNone() {
        return mCurStatus == StatusUtils.BILL_STATUS_NONE;
    }

    private boolean statusIsReturn() {
        return mCurStatus == StatusUtils.BILL_STATUS_RETURN;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionMainEvent(EvaActionEvent event) {
        int action = event.action;
        CarLog.d(TAG, "actionMainEvent action " + action);
        if (action == EvaActionEvent.REFRESH) {
            updateImageViews();
        } else {
            runFinish();
        }
    }

    private void runFinish() {
        ToastUtils.show(this, R.string.preevaluation_submit_tips);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        CarLog.d(TAG, "onClick v " + v);
        switch (id) {
            case R.id.btn_submit:
                onSubmit();
                break;
            default:
                break;
        }
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
        postEvent();
    }

    private boolean isTakePhoto() {
        return checkPhoto(mBaseAdapter) &&
                checkPhoto(mSupplementAdapter);
    }

    private boolean checkPhoto(QuickImageModelAdapter adapter) {
        QuickPreCarImageBean bean = adapter.checkPhoto();
        if (bean != null) {
            String tip = String.format(getString(R.string.evalution_submit_tips), bean.imageClass, bean.displayName);
            ToastUtils.show(this, tip);
            return false;
        }
        return true;
    }

    private void submitNone() {
        if (!isTakePhoto()) {
            return;
        }
        postEvent();
    }

    private void postEvent() {
        String mark = mMark.getText().toString();
        QuickPreCarBillBean bean = new QuickPreCarBillBean();
        bean.carBillId = mQuickPreCarBillId;
        bean.mark = mark;
        bean.imageId = mImageId;

        //send background post
        TaskSubEvent event = new TaskSubEvent();
        event.obj = bean;
        event.action = TaskSubEvent.ACTION_TASK;
        EventProxy.post(event);
    }
}
