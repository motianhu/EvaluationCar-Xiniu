package com.smona.app.evaluationcar.ui.evaluation.attach;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.item.Attachment;
import com.smona.app.evaluationcar.ui.evaluation.preview.PreviewPictureActivity;
import com.smona.app.evaluationcar.util.CacheContants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moth on 2017/9/4.
 */

public class AttachAdapter extends BaseAdapter {
    private List<Attachment> mDatas = new ArrayList<Attachment>();
    private Context mContext;
    public AttachAdapter(Context context) {
        mContext = context;
    }

    public void updateData(List<Attachment> data) {
        mDatas.clear();
        mDatas.addAll(data);
    }

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
        final Attachment attachment = mDatas.get(position);
        Button button;
        if (convertView == null) {
            button = new Button(mContext);
            button.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));//设置ImageView对象布局
            button.setText(mContext.getResources().getString(R.string.reason_attach));
            button.setPadding(8, 8, 8, 8);//设置间距
        }
        else {
            button = (Button) convertView;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, PreviewPictureActivity.class);
                intent.putExtra(CacheContants.ATTACH_FILE_URL, attachment.attachmentURL);
                mContext.startActivity(intent);
            }
        });
        return button;
    }
}
