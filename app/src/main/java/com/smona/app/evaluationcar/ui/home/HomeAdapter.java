package com.smona.app.evaluationcar.ui.home;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.NewsItem;
import com.smona.app.evaluationcar.framework.imageloader.ImageLoaderProxy;
import com.smona.app.evaluationcar.ui.common.AbstractAdapter;
import com.smona.app.evaluationcar.util.ActivityUtils;
import com.smona.app.evaluationcar.util.CacheContants;
import com.smona.app.evaluationcar.util.ViewUtil;

import java.util.List;

/**
 * Created by Moth on 2017/2/24.
 */

public class HomeAdapter extends AbstractAdapter {

    public HomeAdapter(Context context) {
        super(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final NewsItem info = (NewsItem) (mDatas.get(position));
        if (convertView == null) {
            convertView = ViewUtil.inflater(mContext, R.layout.home_list_item);
        }
        convertView.setOnClickListener(this);
        convertView.setTag(info);

        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        ImageLoaderProxy.loadCornerImage(info.imageThumb, image);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(info.title);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        time.setText(info.createTime);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        summary.setText(Html.fromHtml(info.shortContent));
        return convertView;
    }

    @Override
    public void update(List datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        NewsItem info = (NewsItem) v.getTag();
        ActivityUtils.jumpWebActivity(mContext, CacheContants.TYPE_NEWS, info.id);
    }
}
