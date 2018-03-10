package com.smona.app.evaluationcar.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.common.activity.BaseActivity;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.UrlConstants;

/**
 * Created by Moth on 2016/12/15.
 */

public class RegisterActivity extends BaseActivity {

    private WebView wvShow;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String url = UrlConstants.getInterface(UrlConstants.REGISTRE);
        wvShow = (WebView) findViewById(R.id.web_view);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        wvShow.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                //页面加载完毕
                CarLog.d("", "url: " + url);
                super.onPageFinished(view, url);
                if (pbLoading != null && pbLoading.getVisibility() == View.VISIBLE) {
                    pbLoading.setVisibility(View.GONE);//进度条不可见
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //页面开始加载
                super.onPageStarted(view, url, favicon);
                if (pbLoading != null && pbLoading.getVisibility() == View.INVISIBLE) {
                    pbLoading.setVisibility(View.VISIBLE);//进度条可见
                }
            }

        });

        WebSettings webSettings = wvShow.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);

        wvShow.loadUrl(url);
    }
}
