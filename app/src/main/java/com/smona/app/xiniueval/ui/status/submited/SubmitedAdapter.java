package com.smona.app.xiniueval.ui.status.submited;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.smona.app.xiniueval.data.item.UserItem;
import com.smona.app.xiniueval.framework.imageloader.ImageLoaderProxy;
import com.smona.app.xiniueval.ui.common.activity.CarbillReportActivity;
import com.smona.app.xiniueval.util.ActivityUtils;
import com.smona.app.xiniueval.util.ToastUtils;
import com.smona.app.xiniueval.util.UrlConstants;
import com.smona.app.xiniueval.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 4/7/17.
 */

public class SubmitedAdapter extends BaseAdapter implements AdapterView.OnItemClickListener
{
    private static final String TAG = SubmitedAdapter.class.getSimpleName();

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private Context mContext;
    private List<CarBillBean> mDataList = new ArrayList<CarBillBean>();
    private UserItem mUserItem;

    public SubmitedAdapter(Context context) {
        mContext = context;
        mUserItem = new UserItem();
        mUserItem.readUserProp(context);
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
                    R.layout.status_list_pass_item);
        }
        convertView.setTag(carbill);

        ImageView carImage = (ImageView) convertView.findViewById(R.id.carImage);
        ImageLoaderProxy.loadCornerImage(UrlConstants.getProjectInterface() + carbill.imageThumbPath, carImage);

        TextView textNum = (TextView) convertView.findViewById(R.id.carNum);
        String carTitle = TextUtils.isEmpty(carbill.carBillId) ? mContext.getString(R.string.no_carbillid) : carbill.carBillId;
        textNum.setText(mContext.getString(R.string.list_item_number) + " " + carTitle);

        TextView textPrice = (TextView) convertView.findViewById(R.id.carPrice);
        boolean isPass = carbill.status == 54 || carbill.status == 80;
        String price = carbill.evaluatePrice + "";
        if(!isPass) {
            price = "未定价";
        }
        textPrice.setText(mContext.getString(R.string.list_item_price) + " " + price);

        TextView textTime = (TextView) convertView.findViewById(R.id.carTime);
        textTime.setText(mContext.getString(R.string.list_item_time) + " " + carbill.createTime);

        int resId = R.drawable.auditing;
        int textId = R.string.auditing_status;
        if(isPass) {
            resId = R.drawable.finish;
            textId = R.string.finish_status;
        }
        TextView statusTv = (TextView)convertView.findViewById(R.id.status_list_item_arrow);
        Drawable drawable = mContext.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        statusTv.setCompoundDrawables(null, drawable, null,null);
        statusTv.setText(textId);

        return convertView;
    }

    protected void setScrollState(int state) {
        mScrollState = state;
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CarBillBean bean = mDataList.get(position);
        if(bean.status == 54) {
            ActivityUtils.jumpStatus(mContext, bean, CarbillReportActivity.class);
        } else {
            ToastUtils.show(mContext, R.string.auditing);
        }
    }
}
