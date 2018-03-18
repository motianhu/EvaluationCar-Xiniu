package com.smona.app.xiniueval.ui.home.fragment;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.business.HttpDelegator;
import com.smona.app.xiniueval.data.item.UserItem;
import com.smona.app.xiniueval.util.CarLog;
import com.smona.app.xiniueval.util.UrlConstants;


/**
 * Created by Moth on 2015/8/28 0028.
 */

public class KefuFragment extends ContentFragment {
    protected int getLayoutId() {
        return R.layout.fragment_kefu;
    }

    protected void init(View root) {
        WebView webView = (WebView)root.findViewById(R.id.kefu_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);

        UserItem userItem = new UserItem();
        userItem.readSelf(getContext());
        String url = HttpDelegator.getInstance().getCacheKey(UrlConstants.KEFU, "?clientUser=" + userItem.mId);
        CarLog.d("motianhu", "url=" +url);
        webView.loadUrl(url);
    }
}
