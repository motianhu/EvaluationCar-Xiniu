package com.smona.app.evaluationcar.ui.home.more;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.NewsItem;
import com.smona.app.evaluationcar.framework.imageloader.ImageLoaderProxy;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by motianhu on 4/7/17.
 */

public class MoreNewsAdapter extends BaseAdapter implements View.OnClickListener {
    private static final String TAG = MoreNewsAdapter.class.getSimpleName();

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private Context mContext;
    private List<NewsItem> mDataList = new ArrayList<NewsItem>();

    public MoreNewsAdapter(Context context) {
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
        final NewsItem info = mDataList.get(position);
        if (convertView == null) {
            convertView = ViewUtil.inflater(mContext, R.layout.home_list_item);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.jumpWebActivity(mContext, CacheContants.TYPE_NEWS, info.id);
            }
        });
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        ImageLoaderProxy.loadCornerImage(info.imageThumb, image);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(info.title);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        time.setText(info.createTime);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        summary.setText(info.shortContent);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        NewsItem item = (NewsItem) v.getTag();
        ActivityUtils.jumpWebActivity(mContext, CacheContants.TYPE_NEWS, item.id);
    }

    protected void setScrollState(int state) {
        mScrollState = state;
    }

    public void clear() {
        mDataList.clear();
    }
}
