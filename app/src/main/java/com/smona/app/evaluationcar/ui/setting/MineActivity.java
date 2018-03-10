package com.smona.app.evaluationcar.ui.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.data.bean.UserInfoBean;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;

/**
 * Created by motianhu on 4/14/17.
 */

public class MineActivity extends HeaderActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshViews(mUserItem.userBean);
    }

    private void refreshViews(UserInfoBean bean) {
        ViewGroup parent = (ViewGroup) findViewById(R.id.name);
        TextView key = (TextView) parent.findViewById(R.id.key);
        key.setText(R.string.mine_display_name);
        TextView value = (TextView) parent.findViewById(R.id.value);
        value.setText(bean.userChineseName);

        parent = (ViewGroup) findViewById(R.id.company);
        key = (TextView) parent.findViewById(R.id.key);
        key.setText(R.string.mine_belong_company);
        value = (TextView) parent.findViewById(R.id.value);
        value.setText(bean.companyName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.mine_info;
    }
}
