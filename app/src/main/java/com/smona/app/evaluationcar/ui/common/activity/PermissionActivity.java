package com.smona.app.evaluationcar.ui.common.activity;

import android.content.Intent;
import android.os.Bundle;

import com.smona.app.evaluationcar.util.CarLog;

public abstract class PermissionActivity extends BaseActivity implements PermissionManager.PermissionOk {
    private static final String TAG = PermissionActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
    }

    private void initPermission() {
        PermissionManager.getsInstance().processPermission(this,
                PermissionConstants.PERMISSION_REQUEST_CODE, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CarLog.d(TAG, "requestCode: " + requestCode
                + ";resultCode: " + resultCode);
        if (requestCode == PermissionConstants.PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                onPermissionOk();
            } else {
                finish();
            }
        }
    }

    public void onPermissionOk() {

    }

}
