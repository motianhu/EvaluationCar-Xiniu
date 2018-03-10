package com.smona.app.evaluationcar.ui.common;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moth on 2017/2/24.
 */

public abstract class AbstractAdapter<T> extends BaseAdapter implements View.OnClickListener {

    protected Context mContext;
    protected List<T> mDatas;

    public AbstractAdapter(Context context) {
        mDatas = new ArrayList<T>();
        mContext = context;
    }

    public abstract void update(List<T> datas);

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
