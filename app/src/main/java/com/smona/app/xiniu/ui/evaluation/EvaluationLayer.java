package com.smona.app.xiniu.ui.evaluation;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smona.app.xiniu.R;
import com.smona.app.xiniu.data.item.UserItem;
import com.smona.app.xiniu.ui.HomeActivity;
import com.smona.app.xiniu.util.ActivityUtils;
import com.smona.app.xiniu.util.StatusUtils;
import com.smona.app.xiniu.util.ToastUtils;
import com.smona.app.xiniu.util.ViewUtil;

/**
 * Created by motianhu on 2/28/17.
 */

public class EvaluationLayer extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = EvaluationLayer.class.getSimpleName();

    private TextView mNotice;

    private TextView mUnCommitTv;
    private TextView mAuditingTv;
    private TextView mNotPassTv;
    private TextView mPassTv;

    private int mLocalCount = 0;

    private UserItem mUser;

    public EvaluationLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void init() {
        UserItem userItem = new UserItem();
        userItem.readSelf(getContext());
        userItem.readUserProp(getContext());

        View vin = findViewById(R.id.queryVin);
        vin.setOnClickListener(this);

        View preEva = findViewById(R.id.preEvalution);
        preEva.setOnClickListener(this);


        View eva = findViewById(R.id.evalution);
        eva.setOnClickListener(this);

        LinearLayout line = (LinearLayout) findViewById(R.id.evalution_container);

        if(userItem.userBean.isXianfeng()) {
            ViewUtil.setViewVisible(preEva, true);
            line.setWeightSum(3);

            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)vin.getLayoutParams();
            param.weight = 1;

            param = (LinearLayout.LayoutParams)preEva.getLayoutParams();
            param.weight = 1;

            param = (LinearLayout.LayoutParams)eva.getLayoutParams();
            param.weight = 1;
        } else {
            ViewUtil.setViewVisible(preEva, false);

            line.setWeightSum(2);
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)vin.getLayoutParams();
            param.weight = 1;

            param = (LinearLayout.LayoutParams)eva.getLayoutParams();
            param.weight = 1;
        }




        findViewById(R.id.uncommit).setOnClickListener(this);
        findViewById(R.id.auditing).setOnClickListener(this);
        findViewById(R.id.notpass).setOnClickListener(this);
        findViewById(R.id.pass).setOnClickListener(this);

        findViewById(R.id.notice).setOnClickListener(this);


        mNotice = (TextView) findViewById(R.id.notice_content);
        mNotice.setText(Html.fromHtml(getContext().getString(R.string.notice_content)));

        String content = getResources().getString(R.string.home_bill_total);

        mUnCommitTv = (TextView) findViewById(R.id.tv_uncommit);
        mUnCommitTv.setText(String.format(content, mLocalCount));

        mAuditingTv = (TextView) findViewById(R.id.tv_auditing);
        mAuditingTv.setText(String.format(content, 0));

        mNotPassTv = (TextView) findViewById(R.id.tv_notpass);
        mNotPassTv.setText(String.format(content, 0));

        mPassTv = (TextView) findViewById(R.id.tv_pass);
        mPassTv.setText(String.format(content, 0));

        mUser = new UserItem();
        mUser.readSelf(getContext());
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
            case R.id.evalution:
                ActivityUtils.jumpEvaluation(getContext(), StatusUtils.BILL_STATUS_NONE, "", 0, EvaluationActivity.class);
                break;
            case R.id.queryVin:
                ToastUtils.show(getContext(), R.string.coming_soon);
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
