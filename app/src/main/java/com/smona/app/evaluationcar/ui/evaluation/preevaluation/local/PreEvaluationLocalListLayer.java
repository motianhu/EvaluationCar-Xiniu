package com.smona.app.evaluationcar.ui.evaluation.preevaluation.local;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.data.event.PreLocalStatusEvent;
import com.smona.app.evaluationcar.data.event.background.PreLocalStatusSubEvent;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.ui.common.refresh.NetworkTipUtil;
import com.smona.app.evaluationcar.ui.common.refresh.PullToRefreshLayout;
import com.smona.app.evaluationcar.ui.evaluation.preevaluation.quick.QuickPreevaluationActivity;
import com.smona.app.evaluationcar.ui.status.RequestFace;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by motianhu on 4/15/17.
 */

public class PreEvaluationLocalListLayer extends PullToRefreshLayout implements RequestFace {
    private static final String TAG = PreEvaluationLocalListLayer.class.getSimpleName();
    private static final int PAGE_SIZE = 10;
    private PreEvaluationLocalListView mLocalListView = null;
    private View mNoDataLayout = null;
    private View mLoadingView = null;
    private View mHeadView;
    private View mFootView;
    private boolean mPullRequest = false;
    private int mCurPage = 1;
    private int mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
    private View.OnClickListener mReloadClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ActivityUtils.jumpEvaluation(getContext(), StatusUtils.BILL_STATUS_NONE, "", 0, false, QuickPreevaluationActivity.class);
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            loadmoreFinish(PullToRefreshLayout.LAST);
        }
    };

    public PreEvaluationLocalListLayer(Context context) {
        super(context);
    }

    public PreEvaluationLocalListLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreEvaluationLocalListLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addObserver() {
        EventProxy.register(this);
        post();
    }

    private void post() {
        EventProxy.post(new PreLocalStatusSubEvent());
    }

    @Override
    public void deleteObserver() {
        EventProxy.unregister(this);
        mLocalListView.clear();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void reloadDBData(PreLocalStatusSubEvent event) {
        CarLog.d(TAG, "PreLocalStatusSubEvent event.getTag()=" + event.getTag());
        if (PreLocalStatusSubEvent.TAG_ADD_CARBILL.equals(event.getTag())) {
            mLocalListView.clear();
            mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
            mCurPage = 1;
        }
        reloadNormal();
    }

    private void reloadNormal() {
        List<QuickPreCarBillBean> datas = DataDelegator.getInstance().queryLocalQuickPreCarbill(mCurPage, PAGE_SIZE);
        if (datas.size() < PAGE_SIZE) {
            mTag = StatusUtils.MESSAGE_REQUEST_PAGE_LAST;
        } else {
            mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
        }
        CarLog.d(TAG, "reloadNormal " + datas.size());
        PreLocalStatusEvent local = new PreLocalStatusEvent();
        local.setContent(datas);
        EventProxy.post(local);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(PreLocalStatusEvent event) {
        CarLog.d(TAG, "PreLocalStatusEvent event.getTag()=" + event.getTag() + "; mPullRequest=" + mPullRequest);
        List<QuickPreCarBillBean> deltaList = (List<QuickPreCarBillBean>) event.getContent();
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
            NetworkTipUtil.showNoDataTip(this, getContext().getString(R.string.no_data_tips), mReloadClickListener);
        } else {
            mNoDataLayout.setVisibility(GONE);
            mFootView.setVisibility(VISIBLE);
            mHeadView.setVisibility(VISIBLE);
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLocalListView = (PreEvaluationLocalListView) findViewById(R.id.local_listview);
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
}
