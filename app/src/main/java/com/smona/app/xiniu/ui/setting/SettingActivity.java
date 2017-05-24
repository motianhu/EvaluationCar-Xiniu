package com.smona.app.xiniu.ui.setting;

import com.smona.app.xiniu.R;
import com.smona.app.xiniu.ui.common.activity.HeaderActivity;

/**
 * Created by motianhu on 4/14/17.
 */

public class SettingActivity extends HeaderActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.mine_about;
    }
}
