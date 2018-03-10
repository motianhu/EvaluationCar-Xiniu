package com.smona.app.evaluationcar.ui.common.activity;

import android.app.Activity;

import com.smona.app.evaluationcar.util.CarLog;

/**
 * Created by motianhu on 3/29/17.
 */

public class PermissionTask {

    private PermissionTask mNextTask;
    private boolean isExcute = false;

    private String mPerMission;
    private int mResultCode;

    public void setNextTask(PermissionTask nextTask) {
        this.mNextTask = nextTask;
    }

    public void setPerMission(String perMission, int resultCode) {
        this.mPerMission = perMission;
        this.mResultCode = resultCode;
    }

    public boolean checkSelfPermission(Activity activity) {
        return PermissionManager.getsInstance()
                .checkSelfPermission(activity,
                        mPerMission);
    }

    public void requestPermission(Activity activity) {
        boolean needPermession = checkSelfPermission(activity);
        CarLog.d("PermissionTask", "requestPermission mResultCode=" + mResultCode
                + ", needPermession: " + needPermession + ", isExcute=" + isExcute);

        if (!isExcute && needPermession) {
            isExcute = true;
            PermissionManager
                    .getsInstance()
                    .requestPermissions(
                            activity,
                            new String[]{mPerMission},
                            mResultCode);
        } else {
            if (mNextTask != null) {
                mNextTask.requestPermission(activity);
            }
        }
    }
}
