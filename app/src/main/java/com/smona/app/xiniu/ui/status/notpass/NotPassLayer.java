package com.smona.app.xiniu.ui.status.notpass;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smona.app.xiniu.R;
import com.smona.app.xiniu.business.ResponseCallback;
import com.smona.app.xiniu.business.param.CarbillParam;
import com.smona.app.xiniu.data.bean.CarBillBean;
import com.smona.app.xiniu.data.event.NotPassStatusEvent;
import com.smona.app.xiniu.data.item.UserItem;
import com.smona.app.xiniu.data.model.ResCarBillPage;
import com.smona.app.xiniu.framework.cache.DataDelegator;
import com.smona.app.xiniu.framework.event.EventProxy;
import com.smona.app.xiniu.framework.json.JsonParse;
import com.smona.app.xiniu.framework.provider.DBDelegator;
import com.smona.app.xiniu.ui.common.refresh.NetworkTipUtil;
import com.smona.app.xiniu.ui.common.refresh.PullToRefreshLayout;
import com.smona.app.xiniu.ui.status.RequestFace;
import com.smona.app.xiniu.util.CarLog;
import com.smona.app.xiniu.util.StatusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class NotPassLayer extends PullToRefreshLayout implements RequestFace {
    private static final String TAG = NotPassLayer.class.getSimpleName();
    private static final int PAGE_SIZE = 10;

    private NotPassListView mNotPassListView = null;
    private View mNoDataLayout = null;
    private View mLoadingView = null;
    private View mHeadView;
    private View mFootView;
    private boolean mPullRequest = false;

    private int mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;

    private CarbillParam mRequestParams = new CarbillParam();
    private ResponseCallback<String> mResonponseCallBack = new ResponseCallback<String>() {
        @Override
        public void onFailed(String error) {
            mTag = StatusUtils.MESSAGE_REQUEST_ERROR;
            CarLog.d(TAG, "error: " + error);
            NotPassStatusEvent event = new NotPassStatusEvent();
            event.setContent(null);
            EventProxy.post(event);
        }

        @Override
        public void onSuccess(String content) {
            ResCarBillPage pages = JsonParse.parseJson(content, ResCarBillPage.class);
            int total = mRequestParams.curPage * mRequestParams.pageSize;
            if (pages.total <= total) {
                mTag = StatusUtils.MESSAGE_REQUEST_PAGE_LAST;
                saveToDB(pages.data);
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
            DataDelegator.getInstance().queryCarbillList(mRequestParams, mResonponseCallBack);
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

    public NotPassLayer(Context context) {
        super(context);
    }

    public NotPassLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotPassLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initRequestParams() {
        UserItem user = new UserItem();
        boolean success = user.readSelf(getContext());
        if (success) {
            mRequestParams.userName = user.mId;
            mRequestParams.curPage = 1;
            mRequestParams.pageSize = PAGE_SIZE;
            mRequestParams.status = "23,33,43,53";
        }
    }

    @Override
    public void addObserver() {
        EventProxy.register(this);
        initRequestParams();
        post();
    }

    private void post() {
        DataDelegator.getInstance().queryCarbillList(mRequestParams, mResonponseCallBack);
    }

    @Override
    public void deleteObserver() {
        EventProxy.unregister(this);
        mNotPassListView.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(NotPassStatusEvent event) {
        List<CarBillBean> deltaList = (List<CarBillBean>) event.getContent();
        CarLog.d(TAG, "update deltaList is null? " + (deltaList==null) + ", mPullRequest: " + mPullRequest + ", mTag: " + mTag);
        if (deltaList != null) {
            mNotPassListView.update(deltaList, mTag);
            if (mPullRequest) {
                if (mTag == StatusUtils.MESSAGE_REQUEST_ERROR) {
                    postLoadmoreFail();
                } else {
                    loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        } else if (deltaList == null && mTag == StatusUtils.MESSAGE_REQUEST_PAGE_LAST) {
            mNotPassListView.update(deltaList, mTag);
            loadmoreFinish(PullToRefreshLayout.SUCCEED);
        } else {
            if (mPullRequest) {
                loadmoreFinish(PullToRefreshLayout.FAIL);
            }
        }

        mLoadingView.setVisibility(GONE);
        CarLog.d(TAG, "update " + mNotPassListView.getItemCount());
        if (mNotPassListView.getItemCount() == 0) {
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

    private void saveToDB(List<CarBillBean> deltaList) {
        if (deltaList == null || deltaList.size() == 0) {
            return;
        }
        for (CarBillBean bean : deltaList) {
            CarBillBean temp = DBDelegator.getInstance().queryCarBill(bean.carBillId);
            if(temp == null) {
                DBDelegator.getInstance().insertCarBill(bean);
            }
        }
    }

    private void notifyUpdateUI(List<CarBillBean> deltaList) {
        NotPassStatusEvent event = new NotPassStatusEvent();
        event.setContent(deltaList);
        EventProxy.post(event);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNotPassListView = (NotPassListView) findViewById(R.id.local_listview);
        mNotPassListView.setOnRequestFace(this);
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
            DataDelegator.getInstance().queryCarbillList(mRequestParams, mResonponseCallBack);
        }
    }
}
