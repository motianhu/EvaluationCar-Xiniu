package com.smona.app.xiniu.ui.common.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.smona.app.xiniu.util.CarLog;

public class PermissionManager {
    private static final String TAG = PermissionManager.class.getSimpleName();

    private static PermissionManager sInstance = null;

    private PermissionManager() {

    }

    public static synchronized PermissionManager getsInstance() {
        if (sInstance == null) {
            sInstance = new PermissionManager();
        }
        return sInstance;
    }

    public void requestPermissions(final Activity activity,
                                   final String[] permissions, final int requestCode) {
        if (AndroidSdkUtil.sdkLessThan23()) {
            return;
        }
        activity.requestPermissions(permissions, requestCode);
    }

    public boolean checkSelfPermission(final Activity activity,
                                       String permission) {
        if (AndroidSdkUtil.sdkLessThan23()) {
            return false;
        } else {
            return activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED;
        }
    }

    public void processPermission(final Activity activity, int requestCode, PermissionOk callback) {
        if (AndroidSdkUtil.sdkLessThan23()) {
            callback.onPermissionOk();
            return;
        }

        boolean checkMediaPermission = PermissionManager.getsInstance()
                .checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean checkCameraPermission = PermissionManager.getsInstance()
                .checkSelfPermission(activity,
                        Manifest.permission.CAMERA);

        CarLog.d(TAG, "checkMediaPermission: " + checkMediaPermission + ", checkCameraPermission: " + checkCameraPermission);

        if (checkMediaPermission || checkCameraPermission) {
            Intent intent = new Intent();
            intent.setClass(activity, PermissionSettingActivity.class);
            activity.startActivityForResult(intent, requestCode);
        } else {
            callback.onPermissionOk();
        }
    }

    public interface IPermissionsResultCallback {
        void onRequestPermissionsResult(int var1, String[] var2, int[] var3);
    }

    public interface PermissionOk {
        void onPermissionOk();
    }
}
