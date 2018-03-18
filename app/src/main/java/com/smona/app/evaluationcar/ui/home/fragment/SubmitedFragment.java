package com.smona.app.evaluationcar.ui.home.fragment;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.status.nosubmit.StatusFilter;
import com.smona.app.evaluationcar.ui.status.submited.SubmitedLayer;


/**
 * Created by Moth on 2015/8/28 0028.
 */
public class SubmitedFragment extends ContentFragment implements View.OnClickListener {

    private SubmitedLayer mLayer;
    private TextView mTvFilter;

    private final String[] items={"所有","已提交","完成"};

    protected int getLayoutId() {
        return R.layout.status_pass_layer;
    }


    protected void init(View root) {
        mLayer = (SubmitedLayer)root.findViewById(R.id.submitted_layer);
        mLayer.request1Page();
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
        int pos = filter == StatusFilter.All ? 0 : filter == StatusFilter.Submited ? 1 : 2;
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        /**
         * 设置内容区域为单选列表项
         */

        builder.setCancelable(true);

        builder.setSingleChoiceItems(items, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        mLayer.setFilter(StatusFilter.All);
                        break;
                    case 1:
                        mLayer.setFilter(StatusFilter.Submited);
                        break;
                    case 2:
                        mLayer.setFilter(StatusFilter.Pass);
                        break;
                }
                mTvFilter.setText(items[i]);
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
