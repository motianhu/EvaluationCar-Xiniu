package com.smona.app.evaluationcar.ui.evaluation.preevaluation;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.UrlConstants;

/**
 * Created by Moth on 2016/12/15.
 */

public class ReportWebActivity extends HeaderActivity {
    private static final String TAG = ReportWebActivity.class.getSimpleName();

    private WebView mHtmlView;
    private int mType = -1;
    private String mCarBillId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initViews();
        updateTitle();
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        mType = getIntent().getIntExtra(CacheContants.DIRECT_WEB_TYPE, -1);
        if(isReport()) {
            mCarBillId = getIntent().getStringExtra(CacheContants.WEB_ACTIVITY_TYPE);
        } else if(isTakePhotos()) {
            mCarBillId = getIntent().getStringExtra(CacheContants.WEB_ACTIVITY_TYPE);
        }
        CarLog.d(TAG, "mCarBillId=" + mCarBillId);
    }

    private boolean isReport() {
        return CacheContants.TYPE_REPORT == mType;
    }

    private boolean isTakePhotos() {
        return CacheContants.TYPE_TAKEPHOTO == mType;
    }

    private void initViews() {
        mHtmlView = (WebView) findViewById(R.id.content_web);
        WebSettings webSettings = mHtmlView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report_web;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.html_title_report;
    }

    private void updateTitle() {
        if (isReport()) {
            updateTitle(R.string.html_title_report);
        } else if (isTakePhotos()) {
            updateTitle(R.string.evalution_takephone);
        }
    }

    private void requestData() {
        String url = null;
        if(isReport()) {
            url = HttpDelegator.getInstance().getCacheKey(UrlConstants.QUERY_QUICKPREEVALUATION_REPORT, "?carBillId=" + mCarBillId + "&userName=" + mUserItem.mId + "&clientName=android");
        }else if(isTakePhotos()) {
            url = HttpDelegator.getInstance().getCacheKey(UrlConstants.GET_TAKE_PHOTOS, "?id=" + mCarBillId + "&userName=" + mUserItem.mId + "&clientName=android");
        }
        if(TextUtils.isEmpty(url)) {
            return;
        }
        mHtmlView.loadUrl(url);
    }
}

