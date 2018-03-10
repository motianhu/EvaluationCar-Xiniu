package com.smona.app.evaluationcar.ui.evaluation.preevaluation.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.business.param.CarbillParam;
import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.data.event.PreCarbillEvent;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.data.model.ResQuickPreCarBillPage;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.ui.common.refresh.NetworkTipUtil;
import com.smona.app.evaluationcar.ui.common.refresh.PullToRefreshLayout;
import com.smona.app.evaluationcar.ui.status.RequestFace;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by motianhu on 4/15/17.
 */

public class PreEvaluationPassListLayer extends PullToRefreshLayout implements RequestFace {
    private static final String TAG = PreEvaluationPassListLayer.class.getSimpleName();
    private static final int PAGE_SIZE = 10;

    private PreEvaluationListView mListView = null;
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
            PreCarbillEvent event = new PreCarbillEvent();
            event.setContent(null);
            EventProxy.post(event);
        }

        @Override
        public void onSuccess(String content) {
            CarLog.d(TAG, "onSuccess: " + content);
            ResQuickPreCarBillPage pages = JsonParse.parseJson(content, ResQuickPreCarBillPage.class);
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

    public PreEvaluationPassListLayer(Context context) {
        super(context);
        initRequestParams();
    }

    public PreEvaluationPassListLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRequestParams();
    }

    public PreEvaluationPassListLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initRequestParams();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mListView = (PreEvaluationListView) findViewById(R.id.local_listview);
        mListView.setOnRequestFace(this);
        mNoDataLayout = findViewById(R.id.no_content_layout);
        mLoadingView = findViewById(R.id.loading);
        mHeadView = findViewById(R.id.head_view);
        mFootView = findViewById(R.id.loadmore_view);
    }

    private void initRequestParams() {
        UserItem user = new UserItem();
        boolean success = user.readSelf(getContext());
        if (success) {
            mRequestParams.userName = user.mId;
            mRequestParams.curPage = 1;
            mRequestParams.pageSize = PAGE_SIZE;
            mRequestParams.status = "0,1,2";
            mRequestParams.type = "routine";
        }
    }

    @Override
    public void addObserver() {
        EventProxy.register(this);
        post();
    }

    private void post() {
        DataDelegator.getInstance().queryPreCarbillList(mRequestParams, mResonponseCallBack);
    }

    @Override
    public void deleteObserver() {
        EventProxy.unregister(this);
        mListView.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(PreCarbillEvent event) {
        List<QuickPreCarBillBean> deltaList = (List<QuickPreCarBillBean>) event.getContent();
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

    private void saveToDB(List<QuickPreCarBillBean> deltaList) {
        if (deltaList == null || deltaList.size() == 0) {
            return;
        }
        for (QuickPreCarBillBean bean : deltaList) {
            QuickPreCarBillBean temp = DBDelegator.getInstance().queryQuickPreCarBill(bean.carBillId);
            if (temp == null) {
                DBDelegator.getInstance().insertQuickPreCarBill(bean);
            } else {
                bean.imageId = temp.imageId;
                bean.uploadStatus = temp.uploadStatus;
                DBDelegator.getInstance().updatePreCarBill(bean);
            }
        }
    }

    private void notifyUpdateUI(List<QuickPreCarBillBean> deltaList) {
        PreCarbillEvent event = new PreCarbillEvent();
        event.setContent(deltaList);
        EventProxy.post(event);
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
