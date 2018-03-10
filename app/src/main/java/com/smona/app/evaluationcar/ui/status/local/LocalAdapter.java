package com.smona.app.evaluationcar.ui.status.local;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.framework.imageloader.ImageLoaderProxy;
import com.smona.app.evaluationcar.framework.upload.UploadTaskExecutor;
import com.smona.app.evaluationcar.ui.evaluation.EvaluationActivity;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;
import com.smona.app.evaluationcar.util.ToastUtils;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 4/7/17.
 */

public class LocalAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = LocalAdapter.class.getSimpleName();

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private Context mContext;
    private List<CarBillBean> mDataList = new ArrayList<CarBillBean>();

    public LocalAdapter(Context context) {
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
        CarBillBean carbill = mDataList.get(position);
        if (convertView == null) {
            convertView = ViewUtil.inflater(mContext,
                    R.layout.status_list_local_item);
        }

        convertView.setOnClickListener(this);
        convertView.setOnLongClickListener(this);
        convertView.setTag(carbill);

        ImageView carImage = (ImageView) convertView.findViewById(R.id.carImage);
        ImageLoaderProxy.loadCornerImage(carbill.imageThumbPath, carImage);

        TextView textNum = (TextView) convertView.findViewById(R.id.carNum);
        String carTitle = TextUtils.isEmpty(carbill.carBillId) ? mContext.getString(R.string.no_carbillid) : carbill.carBillId;
        textNum.setText(mContext.getString(R.string.list_item_number) + " " + carTitle);

        TextView uploadStatus = (TextView) convertView.findViewById(R.id.uploadstatus);
        String upload = mContext.getString(R.string.saving_status);
        boolean isRunning = UploadTaskExecutor.getInstance().isRunningTask(carbill.imageId, carbill.carBillId);
        boolean isWaiting = UploadTaskExecutor.getInstance().isWaittingTask(carbill.imageId, carbill.carBillId);
        if(isRunning) {
            upload = mContext.getString(R.string.uploading_status);
        } else if(isWaiting) {
            upload = mContext.getString(R.string.waiting_status);
        }
        uploadStatus.setText(mContext.getString(R.string.status_process) + " " + upload);

        TextView textTime = (TextView) convertView.findViewById(R.id.carTime);
        textTime.setText(mContext.getString(R.string.list_item_time) + " " + carbill.createTime);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof CarBillBean) {
            CarBillBean info = (CarBillBean) tag;
            if (info.uploadStatus == StatusUtils.BILL_UPLOAD_STATUS_UPLOADING &&
                    !TextUtils.isEmpty(info.carBillId) &&
                    UploadTaskExecutor.getInstance().isWaittingTask(info.imageId, info.carBillId)) {
                ToastUtils.show(mContext, R.string.uploading_no_action);
            } else {
                ActivityUtils.jumpEvaluation(mContext, StatusUtils.BILL_STATUS_SAVE, info.carBillId, info.imageId, info.leaseTerm != 0, EvaluationActivity.class);
            }
        }
    }

    protected void setScrollState(int state) {
        mScrollState = state;
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View v) {
        CarLog.d(TAG, "TAG " + v);
        return false;
    }
}
