package com.smona.app.xiniueval.ui.home.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.ui.status.nosubmit.StatusFilter;
import com.smona.app.xiniueval.ui.status.submited.SubmitedLayer;


/**
 * Created by Moth on 2015/8/28 0028.
 */
public class SubmitedFragment extends ContentFragment implements View.OnClickListener {

    private SubmitedLayer mLayer;
    private TextView mTvFilter;

    private final String[] items={"所有","已提交","已完成"};

    protected int getLayoutId() {
        return R.layout.status_pass_layer;
    }


    protected void init(View root) {
        mLayer = (SubmitedLayer)root.findViewById(R.id.submitted_layer);
        mLayer.request1Page();
        mTvFilter = (TextView) root.findViewById(R.id.filter);
        mTvFilter.setOnClickListener(this);
        hideSoftInput(getContext(), root.findViewById(R.id.search));
    }

    public void hideSoftInput(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter:
                showFilter();
                break;
        }
    }

    private void showFilter() {
        View contentView= LayoutInflater.from(getContext()).inflate(R.layout.popwin_filter, null);
        int w = getContext().getResources().getDimensionPixelSize(R.dimen.popwind_w);
        int h = getContext().getResources().getDimensionPixelSize(R.dimen.popwind_h);
        final PopupWindow window=new PopupWindow(contentView, w, h, true);
        TextView head = (TextView)contentView.findViewById(R.id.head_position);
        head.setText(items[0]);
        head.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mLayer.setFilter(StatusFilter.All);
                mTvFilter.setText(items[0]);
                window.dismiss();
            }
        });
        TextView middle = (TextView)contentView.findViewById(R.id.middle_position);
        middle.setText(items[1]);
        middle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mLayer.setFilter(StatusFilter.Submited);
                mTvFilter.setText(items[1]);
                window.dismiss();
            }
        });
        TextView tail = (TextView)contentView.findViewById(R.id.tail_position);
        tail.setText(items[2]);
        tail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mLayer.setFilter(StatusFilter.Pass);
                mTvFilter.setText(items[2]);
                window.dismiss();
            }
        });

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(mTvFilter, 0, 0);
    }
}
