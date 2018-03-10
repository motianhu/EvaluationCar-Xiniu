package com.smona.app.evaluationcar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.common.activity.UserActivity;

/**
 * Created by Moth on 2017/3/29.
 */

public class StartupActivity extends UserActivity {

    private Handler mMainHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            intent.setClass(StartupActivity.this, HomeActivity.class);
            StartupActivity.this.startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    public void onPermissionOk() {
        mMainHandler.sendEmptyMessageDelayed(0, 3000);
    }
}
