package com.smona.app.evaluationcar.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.data.bean.BaseBean;
import com.smona.app.evaluationcar.data.event.PageElementEvent;
import com.smona.app.evaluationcar.data.item.BannerItem;
import com.smona.app.evaluationcar.data.item.NewsItem;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.ViewUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Moth on 2016/12/15.
 */

public class WebActivity extends HeaderActivity {
    private static final String TAG = WebActivity.class.getSimpleName();

    private View mContainer;
    private WebView mHtmlView;
    private TextView mTitle;
    private TextView mTime;

    private View mNoContent;
    private View mLoading;

    private int mType;
    private int mId;
    private ResponseCallback<String> mCallback = new ResponseCallback<String>() {
        @Override
        public void onFailed(String error) {
            CarLog.d(TAG, "mCallback onFailed error=" + error);
            poseBanner(null);
        }

        @Override
        public void onSuccess(String content) {
            if (isBanner()) {
                parseBanner(content);
            } else if (isNews()) {
                parseNewsItem(content);
            } else if (isRules()) {
                parseRules(content);
            } else {
                poseBanner(null);
            }
        }
    };
    private View.OnClickListener mReloadClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setLoading();
            requestData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initViews();
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventProxy.unregister(this);
    }

    private void initData() {
        EventProxy.register(this);

        mType = getIntent().getIntExtra(CacheContants.WEB_ACTIVITY_TYPE, -1);
        mId = getIntent().getIntExtra(CacheContants.PAGE_ELEMENT_ID, -1);
        CarLog.d(TAG, "type=" + mType + ", id=" + mId);

    }

    private void initViews() {
        mContainer = findViewById(R.id.content_container);
        mTitle = (TextView) findViewById(R.id.title);
        mTime = (TextView) findViewById(R.id.time);
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

        mNoContent = findViewById(R.id.no_content);
        mNoContent.setOnClickListener(mReloadClick);
        mLoading = findViewById(R.id.loading);

        updateTitle();
        setLoading();
    }

    private void setHasData() {
        ViewUtil.setViewVisible(mNoContent, false);
        ViewUtil.setViewVisible(mLoading, false);
        ViewUtil.setViewVisible(mContainer, true);
    }

    private void setNoData() {
        ViewUtil.setViewVisible(mNoContent, true);
        ViewUtil.setViewVisible(mLoading, false);
        ViewUtil.setViewVisible(mContainer, false);
    }

    private void setLoading() {
        ViewUtil.setViewVisible(mNoContent, false);
        ViewUtil.setViewVisible(mLoading, true);
        ViewUtil.setViewVisible(mContainer, false);
    }

    private void updateTitle() {
        if (isBanner()) {
            updateTitle(R.string.html_title_banner);
        } else if (isNews()) {
            updateTitle(R.string.html_title_news);
        } else if (isRules()) {
            updateTitle(R.string.evalution_rules);
        }
    }

    @Override
    protected void onDelete() {
        super.onDelete();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.html_title;
    }

    private void requestData() {
        if (isBanner()) {
            DataDelegator.getInstance().queryPageElementDetail(mId, mCallback);
        } else if (isNews()) {
            DataDelegator.getInstance().queryNewsDetail(mId, mCallback);
        } else if(isRules()) {
            DataDelegator.getInstance().queryPageElementDetail(mId, mCallback);
        }
    }

    private boolean isBanner() {
        return CacheContants.TYPE_BANNER == mType;
    }

    private boolean isNews() {
        return CacheContants.TYPE_NEWS == mType;
    }

    private boolean isRules() {
        return CacheContants.TYPE_RULES == mType;
    }

    private void parseBanner(String content) {
        BannerItem item = JsonParse.parseJson(content, BannerItem.class);
        poseBanner(item);
    }

    private void parseNewsItem(String content) {
        NewsItem item = JsonParse.parseJson(content, NewsItem.class);
        poseBanner(item);
    }

    private void poseBanner(BaseBean item) {
        PageElementEvent event = new PageElementEvent();
        event.setContent(item);
        EventProxy.post(event);
    }

    private void parseRules(String content) {
        BannerItem item = JsonParse.parseJson(content, BannerItem.class);
        poseBanner(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(PageElementEvent event) {
        BaseBean item = (BaseBean) event.getContent();
        String content = null;
        String title = null;
        String time = null;
        if (item instanceof BannerItem) {
            title = ((BannerItem) item).previewWord;
            content = ((BannerItem) item).detailContent;
            time = String.format(getString(R.string.news_time), ((BannerItem) item).createTime);
        } else if (item instanceof NewsItem) {
            content = ((NewsItem) item).content;
            title = ((NewsItem) item).title;
            time = String.format(getString(R.string.news_time), ((NewsItem) item).createTime);
        }

        if (item != null) {
            setHasData();
            mTitle.setText(title);
            mTime.setText(time);
            String str = "<html><head><title>欢迎你</title></head><body>"
                    + content
                    + "</body></html>";
            mHtmlView.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);
        } else {
            setNoData();
        }
    }
}
