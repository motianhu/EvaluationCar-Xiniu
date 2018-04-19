package com.smona.app.xiniueval.ui.common.activity;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.business.HttpDelegator;
import com.smona.app.xiniueval.util.UrlConstants;

/**
 * Created by Moth on 2016/12/15.
 */

public class RefuseRulesActivity extends HeaderActivity {
    private static final String TAG = RefuseRulesActivity.class.getSimpleName();

    private WebView mHtmlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        mHtmlView = (WebView) findViewById(R.id.content_rules);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }


        String url = HttpDelegator.getInstance().getCacheKey(UrlConstants.GET_REFUSE_RULES) + "1.json";
        mHtmlView.loadUrl(url);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refuse_rules;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.evalution_rules;
    }
}

