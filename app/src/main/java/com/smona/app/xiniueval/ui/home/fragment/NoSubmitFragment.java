package com.smona.app.xiniueval.ui.home.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.smona.app.xiniueval.R;
import com.smona.app.xiniueval.framework.event.MessageManager;
import com.smona.app.xiniueval.ui.status.nosubmit.NoSubmitLayer;
import com.smona.app.xiniueval.ui.status.nosubmit.StatusFilter;

import org.w3c.dom.Text;

/**
 * Created by Moth on 2015/8/28 0028.
 */
public class NoSubmitFragment extends ContentFragment implements View.OnClickListener {

    private NoSubmitLayer mLayer;
    private TextView mTvFilter;

    private final String[] items={"所有","未提交","驳回"};

    protected int getLayoutId() {
        return R.layout.status_local_layer;
    }

    protected void init(View root) {
        mLayer = (NoSubmitLayer)root.findViewById(R.id.local_root);
        mTvFilter = (TextView) root.findViewById(R.id.filter);
        mTvFilter.setOnClickListener(this);
        root.findViewById(R.id.local_refresh).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter:
                showFilter();
                break;
            case R.id.local_refresh:
                refreshLocal();
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
                mLayer.setFilter(StatusFilter.UnSub);
                mTvFilter.setText(items[1]);
                window.dismiss();
            }
        });
        TextView tail = (TextView)contentView.findViewById(R.id.tail_position);
        tail.setText(items[2]);
        tail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mLayer.setFilter(StatusFilter.Reject);
                mTvFilter.setText(items[2]);
                window.dismiss();
            }
        });

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(mTvFilter, 0, 0);
    }

    private void refreshLocal() {
        //刷新未提交
        MessageManager.refreshNoSubmitStatus();
    }
}
