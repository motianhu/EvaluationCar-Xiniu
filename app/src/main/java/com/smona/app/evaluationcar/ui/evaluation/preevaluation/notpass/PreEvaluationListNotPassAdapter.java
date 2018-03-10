package com.smona.app.evaluationcar.ui.evaluation.preevaluation.notpass;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.framework.imageloader.ImageLoaderProxy;
import com.smona.app.evaluationcar.ui.evaluation.preevaluation.quick.QuickPreevaluationActivity;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.StatusUtils;
import com.smona.app.evaluationcar.util.UrlConstants;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 4/7/17.
 */

public class PreEvaluationListNotPassAdapter extends BaseAdapter implements View.OnClickListener {
    private static final String TAG = PreEvaluationListNotPassAdapter.class.getSimpleName();

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private Context mContext;
    private List<QuickPreCarBillBean> mDataList = new ArrayList<>();

    public PreEvaluationListNotPassAdapter(Context context) {
        mContext = context;
    }

    public void update(List deltaList) {
        if (deltaList != null) {
            mDataList.addAll(mDataList.size(), deltaList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuickPreCarBillBean carbill = mDataList.get(position);
        if (convertView == null) {
            convertView = ViewUtil.inflater(mContext,
                    R.layout.status_list_preevalution_notpass_item);
        }

        convertView.setOnClickListener(this);
        convertView.setTag(carbill);

        ImageView carImage = (ImageView) convertView.findViewById(R.id.carImage);
        ImageLoaderProxy.loadCornerImage(UrlConstants.getProjectInterface() + carbill.imageThumbPath, carImage);

        TextView textNum = (TextView) convertView.findViewById(R.id.carNum);
        String carTitle = TextUtils.isEmpty(carbill.carBillId) ? mContext.getString(R.string.no_carbillid) : carbill.carBillId;
        textNum.setText(mContext.getString(R.string.list_item_number) + " " + carTitle);

        TextView textTime = (TextView) convertView.findViewById(R.id.carTime);
        textTime.setText(mContext.getString(R.string.list_item_time) + " " + carbill.createTime);

        TextView notPassTime = (TextView) convertView.findViewById(R.id.carNotPassTime);
        notPassTime.setText(mContext.getString(R.string.list_item_notpass_time) + " " + carbill.modifyTime);
        return convertView;
    }

    private void setNameValue(View parent, String name, String value) {
        TextView tvName = (TextView) parent.findViewById(R.id.name);
        TextView tvValue = (TextView) parent.findViewById(R.id.value);
        tvName.setText(name);
        tvValue.setText(value);
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof QuickPreCarBillBean) {
            QuickPreCarBillBean info = (QuickPreCarBillBean) tag;
            ActivityUtils.jumpQuickPreEvaluation(mContext, StatusUtils.BILL_STATUS_RETURN, info.carBillId, 0, QuickPreevaluationActivity.class);
        }
    }

    protected void setScrollState(int state) {
        mScrollState = state;
    }

    public void clear() {
        mDataList.clear();
    }
}
