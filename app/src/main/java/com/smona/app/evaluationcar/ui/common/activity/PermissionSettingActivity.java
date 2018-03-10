package com.smona.app.evaluationcar.ui.common.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.util.CarLog;

public class PermissionSettingActivity extends BaseActivity implements
        PermissionManager.IPermissionsResultCallback, OnClickListener {

    private static final String TAG = PermissionSettingActivity.class.getSimpleName();
    private Step mStep = Step.CREATE;

    private PermissionTask mPhoneTask;
    private PermissionTask mMediaTask;
    private PermissionTask mCameraTask;
    private PermissionTask mLocationTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_permission);
        initViews();

        initTask();
    }

    private void initTask() {
        mPhoneTask = new PermissionTask();
        mPhoneTask.setPerMission(Manifest.permission.READ_PHONE_STATE, PermissionConstants.CHECK_PHONE_PERMISSION_CODE);

        mMediaTask = new PermissionTask();
        mMediaTask.setPerMission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionConstants.CHECK_MEDIA_PERMISSION_CODE);

        mCameraTask = new PermissionTask();
        mCameraTask.setPerMission(Manifest.permission.CAMERA, PermissionConstants.CHECK_CAMERA_PERMISSION_CODE);

        mLocationTask = new PermissionTask();
        mLocationTask.setPerMission(Manifest.permission.ACCESS_COARSE_LOCATION, PermissionConstants.CHECK_LOCATION_PERMISSION_CODE);

        mPhoneTask.setNextTask(mMediaTask);
        mMediaTask.setNextTask(mCameraTask);
        mCameraTask.setNextTask(mLocationTask);
    }

    private void initViews() {
        findViewById(R.id.permission_reset).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }

    private void requestPermission() {
        if (mStep == Step.CREATE) {
            mPhoneTask.requestPermission(this);
        } else if (mStep == Step.SETTINGS) {
            executeFinish(true);
        }
    }

    private void executeFinish(boolean isExit) {
        boolean result = mPhoneTask.checkSelfPermission(this);
        result = result || mMediaTask.checkSelfPermission(this);
        result = result || mCameraTask.checkSelfPermission(this);
        result = result || mLocationTask.checkSelfPermission(this);
        if (!result) {
            finish(RESULT_OK);
        } else if (isExit) {
            finish(RESULT_CANCELED);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] var2, int[] var3) {
        CarLog.d(TAG, "onRequestPermissionsResult requestCode: " + requestCode);

        if (PermissionConstants.CHECK_LOCATION_PERMISSION_CODE == requestCode) {
            gotoStep(Step.CUSTOM);
            executeFinish(false);
        }
    }

    private void gotoStep(Step step) {
        mStep = step;
    }

    private void finish(int result) {
        Intent intent = new Intent();
        setResult(result, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.permission_reset:
                gotoSettings();
                break;
        }
    }

    private void gotoSettings() {
        gotoStep(Step.CUSTOM);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        gotoStep(Step.SETTINGS);
    }

    private enum Step {
        CREATE, CUSTOM, SETTINGS
    }
}
