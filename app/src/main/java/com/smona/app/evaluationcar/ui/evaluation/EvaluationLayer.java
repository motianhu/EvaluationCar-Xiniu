package com.smona.app.evaluationcar.ui.evaluation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.data.event.BillTotalEvent;
import com.smona.app.evaluationcar.data.event.StatisticsStatusEvent;
import com.smona.app.evaluationcar.data.event.background.StatisticsStatusSubEvent;
import com.smona.app.evaluationcar.data.item.BillTotalItem;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.data.model.ResCountPage;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.ui.HomeActivity;
import com.smona.app.evaluationcar.ui.common.base.BaseRelativeLayout;
import com.smona.app.evaluationcar.ui.evaluation.preevaluation.PreEvaluationActivity;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;
import com.smona.app.evaluationcar.util.ToastUtils;
import com.smona.app.evaluationcar.util.ViewUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by motianhu on 2/28/17.
 */

public class EvaluationLayer extends BaseRelativeLayout implements View.OnClickListener {
    private static final String TAG = EvaluationLayer.class.getSimpleName();

    private TextView mNotice;

    private TextView mUnCommitTv;
    private TextView mAuditingTv;
    private TextView mNotPassTv;
    private TextView mPassTv;

    private int mLocalCount = 0;

    private UserItem mUser;
    private ResponseCallback<String> mCallbillCountCallback = new ResponseCallback<String>() {
        @Override
        public void onFailed(String error) {
            CarLog.d(TAG, "mCallbillCountCallback onFailed error= " + error);
        }

        @Override
        public void onSuccess(String content) {
            ResCountPage resCountPage = JsonParse.parseJson(content, ResCountPage.class);
            notifyUICount(resCountPage);
        }
    };

    public EvaluationLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init() {
        mUser = new UserItem();
        mUser.readSelf(getContext());
        mUser.readUserProp(getContext());

        View vin = findViewById(R.id.queryVin);
        vin.setOnClickListener(this);

        View preEva = findViewById(R.id.preEvalution);
        preEva.setOnClickListener(this);


        View eva = findViewById(R.id.evalution);
        eva.setOnClickListener(this);

        LinearLayout line = (LinearLayout) findViewById(R.id.evalution_container);

        if (mUser.userBean.isXianfeng()) {
            ViewUtil.setViewVisible(preEva, true);
            line.setWeightSum(3);

            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) vin.getLayoutParams();
            param.weight = 1;

            param = (LinearLayout.LayoutParams) preEva.getLayoutParams();
            param.weight = 1;

            param = (LinearLayout.LayoutParams) eva.getLayoutParams();
            param.weight = 1;
        } else if(mUser.userBean.isGuanghui()) {
            ViewUtil.setViewVisible(preEva, true);
            line.setWeightSum(3);

            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) vin.getLayoutParams();
            param.weight = 1;

            param = (LinearLayout.LayoutParams) preEva.getLayoutParams();
            ((TextView)preEva).setText(R.string.evalution_residual);
            param.weight = 1;

            param = (LinearLayout.LayoutParams) eva.getLayoutParams();
            param.weight = 1;
        }else {
            ViewUtil.setViewVisible(preEva, false);

            line.setWeightSum(2);
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) vin.getLayoutParams();
            param.weight = 1;

            param = (LinearLayout.LayoutParams) eva.getLayoutParams();
            param.weight = 1;
        }


        findViewById(R.id.uncommit).setOnClickListener(this);
        findViewById(R.id.auditing).setOnClickListener(this);
        findViewById(R.id.notpass).setOnClickListener(this);
        findViewById(R.id.pass).setOnClickListener(this);
        findViewById(R.id.rules).setOnClickListener(this);
        findViewById(R.id.photos).setOnClickListener(this);


        String content = getResources().getString(R.string.home_bill_total);

        mUnCommitTv = (TextView) findViewById(R.id.tv_uncommit);
        mUnCommitTv.setText(String.format(content, mLocalCount));

        mAuditingTv = (TextView) findViewById(R.id.tv_auditing);
        mAuditingTv.setText(String.format(content, 0));

        mNotPassTv = (TextView) findViewById(R.id.tv_notpass);
        mNotPassTv.setText(String.format(content, 0));

        mPassTv = (TextView) findViewById(R.id.tv_pass);
        mPassTv.setText(String.format(content, 0));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.uncommit:
                ((HomeActivity) getContext()).changeList(0);
                break;
            case R.id.auditing:
                ((HomeActivity) getContext()).changeList(1);
                break;
            case R.id.notpass:
                ((HomeActivity) getContext()).changeList(2);
                break;
            case R.id.pass:
                ((HomeActivity) getContext()).changeList(3);
                break;
            case R.id.preEvalution:
                if(mUser.userBean.isXianfeng()) {
                    ActivityUtils.jumpOnlyActivity(getContext(), PreEvaluationActivity.class);
                } else if(mUser.userBean.isGuanghui()) {
                    ActivityUtils.jumpEvaluation(getContext(), StatusUtils.BILL_STATUS_NONE, "", 0, true, EvaluationActivity.class);
                }
                break;
            case R.id.evalution:
                ActivityUtils.jumpEvaluation(getContext(), StatusUtils.BILL_STATUS_NONE, "", 0, false, EvaluationActivity.class);
                break;
            case R.id.queryVin:
                ToastUtils.show(getContext(), R.string.coming_soon);
                break;
            case R.id.photos:
                ActivityUtils.jumpReportWebActivity(getContext(), CacheContants.TYPE_TAKEPHOTO, "3");
                break;
            case R.id.rules:
                int pageId = -1;
                if(mUser.userBean.isGuanghui()) {
                    pageId = 5;
                } else if(mUser.userBean.isXianfeng()) {
                    pageId = 4;
                }
                ActivityUtils.jumpWebActivity(getContext(), CacheContants.TYPE_RULES, pageId);
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post();
    }

    private void post() {
        DataDelegator.getInstance().requestCarbillCount(mUser.mId, mCallbillCountCallback);
        EventProxy.post(new StatisticsStatusSubEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(BillTotalEvent event) {
        String content = getResources().getString(R.string.bill_count);
        ResCountPage bean = (ResCountPage) event.getContent();
        if (bean != null && bean.total > 0) {
            mUnCommitTv.setText(String.format(content, mLocalCount));
            for (BillTotalItem item : bean.data) {
                if (BillTotalItem.FINISHCOUNT.equals(item.infoType)) {
                    mPassTv.setText(String.format(content, item.countInfo));
                } else if (BillTotalItem.PROCESSCOUNT.equals(item.infoType)) {
                    mAuditingTv.setText(String.format(content, item.countInfo));
                } else if (BillTotalItem.REFUSECOUNT.equals(item.infoType)) {
                    mNotPassTv.setText(String.format(content, item.countInfo));
                }
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void reloadDBData(StatisticsStatusSubEvent event) {
        mLocalCount = DBDelegator.getInstance().queryLocalBillCount();
        notifyUILocalCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(StatisticsStatusEvent event) {
        String content = getResources().getString(R.string.bill_count);
        mUnCommitTv.setText(String.format(content, mLocalCount));
    }

    private void notifyUICount(ResCountPage page) {
        BillTotalEvent event = new BillTotalEvent();
        event.setContent(page);
        EventProxy.post(event);
    }

    private void notifyUILocalCount() {
        EventProxy.post(new StatisticsStatusEvent());
    }
}
