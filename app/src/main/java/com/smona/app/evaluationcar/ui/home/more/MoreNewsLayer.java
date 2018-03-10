package com.smona.app.evaluationcar.ui.home.more;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.business.param.PageParam;
import com.smona.app.evaluationcar.data.event.MoreNewsEvent;
import com.smona.app.evaluationcar.data.item.NewsItem;
import com.smona.app.evaluationcar.data.model.ResNewsPage;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.ui.common.refresh.NetworkTipUtil;
import com.smona.app.evaluationcar.ui.common.refresh.PullToRefreshLayout;
import com.smona.app.evaluationcar.ui.status.RequestFace;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by motianhu on 5/3/17.
 */

public class MoreNewsLayer extends PullToRefreshLayout implements RequestFace {
    private static final String TAG = MoreNewsLayer.class.getSimpleName();
    private static final int PAGE_SIZE = 10;

    private static final String CLASS_TYPE = "最新资讯";

    private MoreNewsListView mListView = null;
    private View mNoDataLayout = null;
    private View mLoadingView = null;
    private View mHeadView;
    private View mFootView;
    private boolean mPullRequest = false;

    private int mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;

    private PageParam mRequestParams = new PageParam();
    private ResponseCallback<String> mResonponseCallBack = new ResponseCallback<String>() {
        @Override
        public void onFailed(String error) {
            mTag = StatusUtils.MESSAGE_REQUEST_ERROR;
            CarLog.d(TAG, "error: " + error);
            MoreNewsEvent event = new MoreNewsEvent();
            event.setContent(null);
            EventProxy.post(event);
        }

        @Override
        public void onSuccess(String content) {
            CarLog.d(TAG, "onSuccess: " + content);
            ResNewsPage pages = JsonParse.parseJson(content, ResNewsPage.class);
            int total = mRequestParams.curPage * mRequestParams.pageSize;
            if (pages.total <= total) {
                mTag = StatusUtils.MESSAGE_REQUEST_PAGE_LAST;
                notifyUpdateUI(pages.data);
            } else {
                mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
                notifyUpdateUI(pages.data);
            }
        }
    };
    private OnClickListener mReloadClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            CarLog.d(TAG, "mReloadClickListener reload " + TAG);
            post();
            mLoadingView.setVisibility(VISIBLE);
            mNoDataLayout.setVisibility(GONE);
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            loadmoreFinish(PullToRefreshLayout.LAST);
        }
    };

    public MoreNewsLayer(Context context) {
        super(context);
        initRequestParams();
    }

    public MoreNewsLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRequestParams();
    }

    public MoreNewsLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initRequestParams();
    }

    private void initRequestParams() {
        mRequestParams.curPage = 1;
        mRequestParams.pageSize = PAGE_SIZE;
    }

    @Override
    public void addObserver() {
        EventProxy.register(this);
        post();
    }

    private void post() {
        DataDelegator.getInstance().queryMoreNews(CLASS_TYPE, mRequestParams, mResonponseCallBack);
    }

    @Override
    public void deleteObserver() {
        EventProxy.unregister(this);
        mListView.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MoreNewsEvent event) {
        List<NewsItem> deltaList = (List<NewsItem>) event.getContent();
        CarLog.d(TAG, "update " + deltaList + ", mPullRequest: " + mPullRequest + ", mTag: " + mTag);
        if (deltaList != null) {
            mListView.update(deltaList, mTag);
            if (mPullRequest) {
                if (mTag == StatusUtils.MESSAGE_REQUEST_ERROR) {
                    postLoadmoreFail();
                } else {
                    loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        } else if (deltaList == null && mTag == StatusUtils.MESSAGE_REQUEST_PAGE_LAST) {
            mListView.update(deltaList, mTag);
            loadmoreFinish(PullToRefreshLayout.SUCCEED);
        } else {
            if (mPullRequest) {
                loadmoreFinish(PullToRefreshLayout.FAIL);
            }
        }

        mLoadingView.setVisibility(GONE);
        CarLog.d(TAG, "update " + mListView.getItemCount());
        if (mListView.getItemCount() == 0) {
            mNoDataLayout.setVisibility(VISIBLE);
            mFootView.setVisibility(INVISIBLE);
            mHeadView.setVisibility(INVISIBLE);
            NetworkTipUtil.showNetworkTip(this, mReloadClickListener);
        } else {
            mNoDataLayout.setVisibility(GONE);
            mFootView.setVisibility(VISIBLE);
            mHeadView.setVisibility(VISIBLE);
        }
    }

    private void notifyUpdateUI(List<NewsItem> deltaList) {
        MoreNewsEvent event = new MoreNewsEvent();
        event.setContent(deltaList);
        EventProxy.post(event);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mListView = (MoreNewsListView) findViewById(R.id.listView);
        mListView.setOnRequestFace(this);
        mNoDataLayout = findViewById(R.id.no_content_layout);
        mLoadingView = findViewById(R.id.loading);
        mHeadView = findViewById(R.id.head_view);
        mFootView = findViewById(R.id.loadmore_view);
    }

    @Override
    protected void onRefresh() {
        refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    protected void onLoadMore() {
        CarLog.d(TAG, "onLoadMore mPullRequest=" + mPullRequest);
        requestNext();
    }

    @Override
    public void requestNext() {
        if (mTag == StatusUtils.MESSAGE_REQUEST_PAGE_LAST) {
            postDelayed(mRunnable, 1000);
        } else {
            mPullRequest = true;
            mRequestParams.curPage += 1;
            post();
        }
    }
}