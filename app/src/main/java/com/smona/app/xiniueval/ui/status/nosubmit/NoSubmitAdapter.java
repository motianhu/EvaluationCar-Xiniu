package com.smona.app.xiniueval.ui.status.nosubmit;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.framework.cache.DataDelegator;
import com.smona.app.xiniueval.framework.imageloader.ImageLoaderProxy;
import com.smona.app.xiniueval.framework.upload.UploadTaskExecutor;
import com.smona.app.xiniueval.ui.evaluation.EvaluationActivity;
import com.smona.app.xiniueval.util.ActivityUtils;
import com.smona.app.xiniueval.util.CarLog;
import com.smona.app.xiniueval.util.StatusUtils;
import com.smona.app.xiniueval.util.ToastUtils;
import com.smona.app.xiniueval.util.UrlConstants;
import com.smona.app.xiniueval.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 4/7/17.
 */

public class NoSubmitAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = NoSubmitAdapter.class.getSimpleName();

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private Context mContext;
    private List<CarBillBean> mDataList = new ArrayList<CarBillBean>();

    public NoSubmitAdapter(Context context) {
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
        convertView.setTag(carbill);

        TextView textNum = (TextView) convertView.findViewById(R.id.carNum);
        String carTitle = TextUtils.isEmpty(carbill.carBillId) ? mContext.getString(R.string.no_carbillid) : carbill.carBillId;
        textNum.setText(mContext.getString(R.string.list_item_number) + " " + carTitle);

        TextView uploadStatus = (TextView) convertView.findViewById(R.id.uploadstatus);
        String upload = mContext.getString(R.string.saving_status);
        boolean isRunning = UploadTaskExecutor.getInstance().isRunningTask(carbill.imageId, carbill.carBillId);
        boolean isWaiting = UploadTaskExecutor.getInstance().isWaittingTask(carbill.imageId, carbill.carBillId);
        boolean isInject = isInject(carbill);

        ImageView carImage = (ImageView) convertView.findViewById(R.id.carImage);
        String imagepath = (isInject ? UrlConstants.getProjectInterface():"") + carbill.imageThumbPath;
        ImageLoaderProxy.loadCornerImage(imagepath, carImage);

        int resId = R.drawable.unfinish;
        if (isInject) {
            resId = R.drawable.inject;
            upload = mContext.getString(R.string.inject_status);
        } else if (isRunning) {
            upload = mContext.getString(R.string.uploading_status);
            resId = R.drawable.uploading;
        } else if (isWaiting) {
            upload = mContext.getString(R.string.waiting_status);
        }
        String content = mContext.getString(R.string.status_process)+"<font color=\"#FE6026\">" + upload + "</font>";
        uploadStatus.setText(Html.fromHtml(content));

        TextView textTime = (TextView) convertView.findViewById(R.id.carTime);
        textTime.setText(mContext.getString(R.string.list_item_time) + " " + carbill.createTime);

        ImageView image = (ImageView) convertView.findViewById(R.id.status_list_item_arrow);
        image.setImageResource(resId);

        return convertView;
    }

    protected void setScrollState(int state) {
        mScrollState = state;
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    private boolean isInject(CarBillBean carbill) {
        return (carbill.status == 23 || carbill.status == 33 || carbill.status == 43 || carbill.status == 53);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CarBillBean info = mDataList.get(position);
        if (isInject(info)) {
            //驳回
            ActivityUtils.jumpEvaluation(mContext, StatusUtils.BILL_STATUS_RETURN, info.carBillId, info.imageId, info.leaseTerm != 0, EvaluationActivity.class);
        } else if (info.uploadStatus == StatusUtils.BILL_UPLOAD_STATUS_UPLOADING &&
                !TextUtils.isEmpty(info.carBillId) &&
                UploadTaskExecutor.getInstance().isWaittingTask(info.imageId, info.carBillId)) {
            ToastUtils.show(mContext, R.string.uploading_no_action);
        } else {
            //保存重新编辑
            ActivityUtils.jumpEvaluation(mContext, StatusUtils.BILL_STATUS_SAVE, info.carBillId, info.imageId, info.leaseTerm != 0, EvaluationActivity.class);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        CarBillBean info = mDataList.get(position);
        CarLog.d(TAG, "onItemLongClick " + info);
        processLongClick(info);
        return true;
    }

    private void processLongClick(final CarBillBean info) {
        if (info.uploadStatus == StatusUtils.BILL_UPLOAD_STATUS_UPLOADING &&
                !TextUtils.isEmpty(info.carBillId) &&
                UploadTaskExecutor.getInstance().isWaittingTask(info.imageId, info.carBillId)) {
        } else if (info.status == 23) {
        } else {
            new AlertDialog.Builder(mContext).setTitle("提示").setMessage("确定删除此单据？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DataDelegator.getInstance().deleteLocalCarBill(info);
                            DataDelegator.getInstance().deleteLocalCarImage(info);
                            mDataList.remove(info);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
    }
}
