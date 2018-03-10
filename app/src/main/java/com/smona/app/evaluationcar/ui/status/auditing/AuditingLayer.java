package com.smona.app.evaluationcar.ui.status.auditing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.business.param.CarbillParam;
import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.data.event.AuditingStatusEvent;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.data.model.ResCarBillPage;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.ui.common.refresh.NetworkTipUtil;
import com.smona.app.evaluationcar.ui.common.refresh.PullToRefreshLayout;
import com.smona.app.evaluationcar.ui.status.Request1Page;
import com.smona.app.evaluationcar.ui.status.RequestFace;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AuditingLayer extends PullToRefreshLayout implements RequestFace, Request1Page {
    private static final String TAG = AuditingLayer.class.getSimpleName();
    private static final int PAGE_SIZE = 10;

    private AuditingListView mAuditingListView = null;
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
            AuditingStatusEvent event = new AuditingStatusEvent();
            event.setContent(null);
            EventProxy.post(event);
        }

        @Override
        public void onSuccess(String content) {
            ResCarBillPage pages = JsonParse.parseJson(content, ResCarBillPage.class);
            int total = mRequestParams.curPage * mRequestParams.pageSize;
            saveToDB(pages.data);
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

    public AuditingLayer(Context context) {
        super(context);
    }

    public AuditingLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AuditingLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initRequestParams() {
        UserItem user = new UserItem();
        boolean success = user.readSelf(getContext());
        if (success) {
            mRequestParams.userName = user.mId;
            mRequestParams.curPage = 1;
            mRequestParams.pageSize = PAGE_SIZE;
            mRequestParams.status = "21,22,24,31,32,34,41,42,44,51,52";
        }
    }

    @Override
    public void addObserver() {
        EventProxy.register(this);
    }

    private void post() {
        DataDelegator.getInstance().queryCarbillList(mRequestParams, mResonponseCallBack);
    }

    @Override
    public void deleteObserver() {
        EventProxy.unregister(this);
        mAuditingListView.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(AuditingStatusEvent event) {
        List<CarBillBean> deltaList = (List<CarBillBean>) event.getContent();
        CarLog.d(TAG, "update deltaList is null? " + (deltaList == null) + ", mPullRequest: " + mPullRequest + ", mTag: " + mTag);
        if (deltaList != null) {
            mAuditingListView.update(deltaList, mTag);
            if (mPullRequest) {
                if (mTag == StatusUtils.MESSAGE_REQUEST_ERROR) {
                    postLoadmoreFail();
                } else {
                    loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        } else if (mTag == StatusUtils.MESSAGE_REQUEST_PAGE_LAST) {
            mAuditingListView.update(null, mTag);
            loadmoreFinish(PullToRefreshLayout.SUCCEED);
        } else {
            if (mPullRequest) {
                loadmoreFinish(PullToRefreshLayout.FAIL);
            }
        }

        mLoadingView.setVisibility(GONE);
        CarLog.d(TAG, "update " + mAuditingListView.getItemCount());
        if (mAuditingListView.getItemCount() == 0) {
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
            if (temp == null) {
                DBDelegator.getInstance().insertCarBill(bean);
            } else {
                bean.imageId = temp.imageId;
                bean.uploadStatus = temp.uploadStatus;
                DBDelegator.getInstance().updateCarBill(bean);
            }
        }
    }

    private void notifyUpdateUI(List<CarBillBean> deltaList) {
        AuditingStatusEvent event = new AuditingStatusEvent();
        event.setContent(deltaList);
        EventProxy.post(event);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAuditingListView = (AuditingListView) findViewById(R.id.local_listview);
        mAuditingListView.setOnRequestFace(this);
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

    @Override
    public void request1Page() {
        initRequestParams();
        mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
        mAuditingListView.clear();
        changeState(INIT);
        mPullRequest = false;
        post();
    }
}
