package com.smona.app.xiniueval.ui.home.fragment;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.ui.status.nosubmit.NoSubmitLayer;
import com.smona.app.xiniueval.ui.status.nosubmit.StatusFilter;

/**
 * Created by Moth on 2015/8/28 0028.
 */
public class NoSubmitFragment extends ContentFragment implements View.OnClickListener {

    private NoSubmitLayer mLayer;
    private TextView mTvFilter;

    protected int getLayoutId() {
        return R.layout.status_local_layer;
    }

    protected void init(View root) {
        mLayer = (NoSubmitLayer)root.findViewById(R.id.local_root);
        mTvFilter = (TextView) root.findViewById(R.id.filter);
        mTvFilter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter:
                showFilter(v);
                break;
        }
    }

    private void showFilter(View v) {
        StatusFilter filter = mLayer.getFilter();
        int pos = filter == StatusFilter.All ? 0 : filter == StatusFilter.UnSub ? 1 : 2;
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        /**
         * 设置内容区域为单选列表项
         */
        final String[] items={"所有","未提交","驳回"};
        builder.setSingleChoiceItems(items, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        mLayer.setFilter(StatusFilter.All);
                        break;
                    case 1:
                        mLayer.setFilter(StatusFilter.UnSub);
                        break;
                    case 2:
                        mLayer.setFilter(StatusFilter.Reject);
                        break;
                }
                mTvFilter.setText(items[i]);
                dialogInterface.dismiss();
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
