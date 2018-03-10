package com.smona.app.evaluationcar.ui.common.activity;

import android.os.Bundle;

import com.smona.app.evaluationcar.data.item.UserItem;

/**
 * Created by motianhu on 4/11/17.
 */

public class UserActivity extends PermissionActivity {
    private static final String TAG = UserActivity.class.getSimpleName();
    protected UserItem mUserItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUserInfo();
    }

    private void initUserInfo() {
        mUserItem = new UserItem();
        mUserItem.readSelf(this);
        mUserItem.readUserProp(this);
    }
}
