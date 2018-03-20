package com.smona.app.xiniueval.ui.status.nosubmit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.business.ResponseCallback;
import com.smona.app.xiniueval.business.param.CarbillParam;
import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.event.LocalStatusEvent;
import com.smona.app.xiniueval.data.event.PassStatusEvent;
import com.smona.app.xiniueval.data.event.background.LocalStatusSubEvent;
import com.smona.app.xiniueval.data.item.UserItem;
import com.smona.app.xiniueval.data.model.ResCarBillPage;
import com.smona.app.xiniueval.framework.cache.DataDelegator;
import com.smona.app.xiniueval.framework.event.EventProxy;
import com.smona.app.xiniueval.framework.json.JsonParse;
import com.smona.app.xiniueval.framework.provider.DBDelegator;
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
    // 思路：
    // 先把服务器上所有的驳回单据拉取下来并保存，然后查询驳回单据以及正在上传和未提交的单据，并按分批显示
    //23,33,43,53+本地
    private static final String TAG = NoSubmitLayer.class.getSimpleName();
    private static final int PAGE_SIZE = 200;
    private NoSubmitListView mLocalListView = null;

    private StatusFilter mCurFilter= StatusFilter.All;

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

    private CarbillParam mRequestParams = new CarbillParam();
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

    private void postNoPass() {
        DataDelegator.getInstance().queryCarbillList(mRequestParams, mResonponseCallBack);
    }
    private ResponseCallback<String> mResonponseCallBack = new ResponseCallback<String>() {
        @Override
        public void onFailed(String error) {
            CarLog.d(TAG, "error: " + error);
            //失败则加载本地未提交或者正在提交的数据
            reloadNormal(false);
        }

        @Override
        public void onSuccess(String content) {
            CarLog.d(TAG, "content: " + content);
            ResCarBillPage pages = JsonParse.parseJson(content, ResCarBillPage.class);
            //我们认为所有驳回数据拉取成功，并保存起来
            saveToDB(pages.data);
            reloadNormal(true);
        }
    };

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
        request1Page();
    }

    private void post() {
        EventProxy.post(new LocalStatusSubEvent());
    }

    @Override
    public void deleteObserver() {
        EventProxy.unregister(this);
        mLocalListView.clear();
    }

    private void reloadNormal(boolean success) {
        List<CarBillBean> datas = DataDelegator.getInstance().queryNoSubmitCarBill(mCurPage, PAGE_SIZE);
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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void reloadDBData(LocalStatusSubEvent event) {
        CarLog.d(TAG, "LocalStatusSubEvent event.getTag()=" + event.getTag());
        if (LocalStatusSubEvent.TAG_ADD_CARBILL.equals(event.getTag())) {
            mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
            mCurPage = 1;
        }
        reloadNormal(true);
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
        //一次性拉取完此用户所有的驳回单据
        initRequestParams();
        mTag = StatusUtils.MESSAGE_REQUEST_PAGE_MORE;
        mCurPage = 1;
        mLocalListView.clear();
        changeState(INIT);
        mPullRequest = false;
        postNoPass();
    }
}
