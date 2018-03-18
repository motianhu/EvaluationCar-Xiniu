package com.smona.app.xiniueval.ui.status.nosubmit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.event.LocalStatusEvent;
import com.smona.app.xiniueval.data.event.background.LocalStatusSubEvent;
import com.smona.app.xiniueval.framework.cache.DataDelegator;
import com.smona.app.xiniueval.framework.event.EventProxy;
import com.smona.app.xiniueval.ui.common.refresh.NetworkTipUtil;
import com.smona.app.xiniueval.ui.common.refresh.PullToRefreshLayout;
import com.smona.app.xiniueval.ui.evaluation.EvaluationActivity;
import com.smona.app.xiniueval.ui.status.Request1Page;
import com.smona.app.xiniueval.ui.status.RequestFace;
import com.smona.app.xiniueval.util.ActivityUtils;
import com.smona.app.xiniueval.util.CarLog;
import com.smona.app.xiniueval.util.StatusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class NoSubmitLayer extends PullToRefreshLayout implements RequestFace, Request1Page {
    //23,33,43,53+本地
    private static final String TAG = NoSubmitLayer.class.getSimpleName();
    private static final int PAGE_SIZE = 10;
    private NoSubmitListView mLocalListView = null;

    private StatusFilter mCurFilter= StatusFilter.All;

    private View mNoDataLayout = null;
    private View mLoadingView = null;
    private View mHeadView;
    private View mFootView;

    private boolean mPullRequest = false;
    private int mCurPage = 1;
    private int mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;

    private boolean mComplementInit = false;

    private View.OnClickListener mReloadClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ActivityUtils.jumpEvaluation(getContext(), StatusUtils.BILL_STATUS_NONE, "", 0, false, EvaluationActivity.class);
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            loadmoreFinish(PullToRefreshLayout.LAST);
        }
    };

    public NoSubmitLayer(Context context) {
        super(context);
    }

    public NoSubmitLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoSubmitLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFilter(StatusFilter filter) {
        if(mCurFilter == filter) {
            return;
        }
        mCurFilter = filter;
        notifyFilter();
    }

    public StatusFilter getFilter() {
        return mCurFilter;
    }

    private void notifyFilter() {

    }

    @Override
    public void addObserver() {
        EventProxy.register(this);
        if(!mComplementInit) {
            mComplementInit = true;
            request1Page();
        }
    }

    private void post() {
        EventProxy.post(new LocalStatusSubEvent());
    }

    @Override
    public void deleteObserver() {
        EventProxy.unregister(this);
        mLocalListView.clear();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void reloadDBData(LocalStatusSubEvent event) {
        CarLog.d(TAG, "LocalStatusSubEvent event.getTag()=" + event.getTag());
        if (LocalStatusSubEvent.TAG_ADD_CARBILL.equals(event.getTag())) {
            mLocalListView.clear();
            mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
            mCurPage = 1;
        }
        reloadNormal();
    }

    private void reloadNormal() {
        List<CarBillBean> datas = DataDelegator.getInstance().queryLocalCarbill(mCurPage, PAGE_SIZE);
        if (datas.size() < PAGE_SIZE) {
            mTag = StatusUtils.MESSAGE_REQUEST_PAGE_LAST;
        } else {
            mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
        }
        CarLog.d(TAG, "reloadNormal " + datas.size());
        LocalStatusEvent local = new LocalStatusEvent();
        local.setContent(datas);
        EventProxy.post(local);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(LocalStatusEvent event) {
        CarLog.d(TAG, "LocalStatusEvent event.getTag()=" + event.getTag() + "; mPullRequest=" + mPullRequest);
        List<CarBillBean> deltaList = (List<CarBillBean>) event.getContent();
        if (deltaList != null) {
            mLocalListView.update(deltaList, mTag);
            if (mPullRequest) {
                if (mTag == StatusUtils.MESSAGE_REQUEST_ERROR) {
                    postLoadmoreFail();
                } else {
                    loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        } else if (mTag == StatusUtils.MESSAGE_REQUEST_PAGE_LAST) {
            mLocalListView.update(deltaList, mTag);
            loadmoreFinish(PullToRefreshLayout.SUCCEED);
        } else {
            if (mPullRequest) {
                loadmoreFinish(PullToRefreshLayout.FAIL);
            }
        }

        mLoadingView.setVisibility(GONE);
        CarLog.d(TAG, "update " + mLocalListView.getItemCount());
        if (mLocalListView.getItemCount() == 0) {
            mNoDataLayout.setVisibility(VISIBLE);
            mFootView.setVisibility(INVISIBLE);
            mHeadView.setVisibility(INVISIBLE);
            NetworkTipUtil.showNoDataTip(NoSubmitLayer.this, getContext().getString(R.string.no_data_tips), mReloadClickListener);
        } else {
            mNoDataLayout.setVisibility(GONE);
            mFootView.setVisibility(VISIBLE);
            mHeadView.setVisibility(VISIBLE);
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLocalListView = (NoSubmitListView) findViewById(R.id.local_listview);
        mLocalListView.setOnRequestFace(this);
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
        mPullRequest = true;
        requestNext();
    }

    @Override
    public void requestNext() {
        if (mTag == StatusUtils.MESSAGE_REQUEST_PAGE_LAST) {
            postDelayed(mRunnable, 1000);
        } else {
            mPullRequest = true;
            mCurPage += 1;
            post();
        }
    }

    @Override
    public void request1Page() {
        mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
        mCurPage = 1;
        mLocalListView.clear();
        changeState(INIT);
        mPullRequest = false;
        post();
    }
}
