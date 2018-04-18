package com.smona.app.xiniueval.ui.common.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.business.HttpDelegator;
import com.smona.app.xiniueval.framework.imageloader.ImageLoaderManager;
import com.smona.app.xiniueval.util.UrlConstants;
import com.smona.app.xiniueval.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moth on 2016/12/15.
 */

public class PhotoRulesActivity extends HeaderActivity {
    private static final int PHOTO_COUNT = 27;
    private List<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initViews();
    }

    private void initData() {
        String prefix = HttpDelegator.getInstance().getCacheKey(UrlConstants.GET_TAKE_PHOTOS);
        for (int i = 0; i < PHOTO_COUNT; i++) {
            mDatas.add(prefix + (i + 1) + ".jpg");
        }
    }

    private void initViews() {
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new PhotoRulesAdapter());
    }

    private class PhotoRulesAdapter extends BaseAdapter {

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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ViewUtil.inflater(PhotoRulesActivity.this,
                        R.layout.photo_rules_item);
            }
            ImageLoaderManager.getInstance().loadImage(mDatas.get(position), (ImageView) convertView);
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_rules;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.setting_mine_paizhao;
    }
}

