package com.smona.app.evaluationcar.ui.common.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.util.ViewUtil;

/**
 * Created by motianhu on 3/11/17.
 */

public abstract class HeaderActivity extends UserActivity {

    private TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initHeader();
    }

    protected abstract int getLayoutId();

    protected abstract boolean showDelete();

    protected abstract int getHeaderTitle();

    private void initHeader() {
        HeaderListener headerListener = new HeaderListener();
        findViewById(R.id.left).setOnClickListener(headerListener);
        mTitle = (TextView) findViewById(R.id.center);
        updateTitle(getHeaderTitle());
        ViewUtil.setViewVisible(findViewById(R.id.right), showDelete());
        findViewById(R.id.right).setOnClickListener(headerListener);
    }

    protected void updateTitle(int resId) {
        mTitle.setText(resId);
    }

    protected void onDelete() {
    }

    private class HeaderListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.left:
                    finish();
                    break;
                case R.id.right:
                    onDelete();
                    break;
            }
        }
    }
}
