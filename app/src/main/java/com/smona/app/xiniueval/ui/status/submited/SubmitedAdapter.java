package com.smona.app.xiniueval.ui.status.submited;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.item.UserItem;
import com.smona.app.xiniueval.framework.imageloader.ImageLoaderProxy;
import com.smona.app.xiniueval.ui.status.StatusActivity;
import com.smona.app.xiniueval.util.ActivityUtils;
import com.smona.app.xiniueval.util.UrlConstants;
import com.smona.app.xiniueval.util.ViewUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 4/7/17.
 */

public class SubmitedAdapter extends BaseAdapter implements View.OnClickListener {
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

        convertView.setOnClickListener(this);
        convertView.setTag(carbill);

        ImageView carImage = (ImageView) convertView.findViewById(R.id.carImage);
        ImageLoaderProxy.loadCornerImage(UrlConstants.getProjectInterface() + carbill.imageThumbPath, carImage);

        TextView textNum = (TextView) convertView.findViewById(R.id.carNum);
        String carTitle = TextUtils.isEmpty(carbill.carBillId) ? mContext.getString(R.string.no_carbillid) : carbill.carBillId;
        textNum.setText(mContext.getString(R.string.list_item_number) + " " + carTitle);

        TextView textPrice = (TextView) convertView.findViewById(R.id.carPrice);
        boolean notVisible = mUserItem.userBean.isRichanJinrong();
        textPrice.setText(mContext.getString(R.string.list_item_price) + " " + carbill.evaluatePrice);
        ViewUtil.setViewVisible(textPrice, !notVisible);

        TextView textTime = (TextView) convertView.findViewById(R.id.carTime);
        textTime.setText(mContext.getString(R.string.list_item_time) + " " + carbill.createTime);

        int resId = R.drawable.auditing;
        if(carbill.status == 54) {
            resId = R.drawable.finish;
        }
        ImageView image = (ImageView)convertView.findViewById(R.id.status_list_item_arrow);
        image.setImageResource(resId);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof CarBillBean) {
            CarBillBean info = (CarBillBean) tag;
            ActivityUtils.jumpStatus(mContext, info, StatusActivity.class);
        }
    }

    protected void setScrollState(int state) {
        mScrollState = state;
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }
}
