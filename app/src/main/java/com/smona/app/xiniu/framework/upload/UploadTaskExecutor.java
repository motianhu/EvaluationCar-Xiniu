package com.smona.app.xiniu.framework.upload;

import android.os.Handler;
import android.text.TextUtils;

import com.smona.app.xiniu.util.CarLog;

import java.util.Iterator;
import java.util.LinkedList;

public class UploadTaskExecutor {
    private static final String TAG = UploadTaskExecutor.class.getSimpleName();

    private static final int MULTI_THREAD_COUNT = 2;

    private LinkedList<ActionTask> sTasks = new LinkedList<ActionTask>();
    private int sRunCount = 0;
    private static UploadTaskExecutor sInstance;

    private UploadTaskExecutor() {
    }

    public static UploadTaskExecutor getInstance() {
        if (sInstance == null) {
            sInstance = new UploadTaskExecutor();
        }
        return sInstance;
    }

    public void init(){}

    private Handler sHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ActionTask waitTask = sTasks.poll();
            if (null != waitTask) {
                waitTask.startTask();
            } else {
                sRunCount--;
            }
            CarLog.d(TAG, "sRunCount:" + sRunCount + "  sTasks.size():" + sTasks.size());
        }
    };


    private boolean existTask(ActionTask task) {
        Iterator<ActionTask> it = sTasks.iterator();
        while (it.hasNext()) {
            ActionTask action = it.next();
            if (action.isSelf(task)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUploading(String carBillId) {
        Iterator<ActionTask> it = sTasks.iterator();
        while (it.hasNext()) {
            ActionTask action = it.next();
            if (!TextUtils.isEmpty(action.mCarBillId) && action.mCarBillId.equals(carBillId)) {
                return true;
            }
        }
        return false;
    }

    public void pushTask(ActionTask task) {
        if (existTask(task)) {
            CarLog.d(TAG, "existTask pushTask " + task);
            return;
        }
        CarLog.d(TAG, "pushTask " + task + ", sRunCount: " + sRunCount);
        if (sRunCount >= MULTI_THREAD_COUNT) {
            sTasks.offer(task);
        } else {
            task.startTask();
            sRunCount++;
        }
    }

    public void nextTask() {
        sHandler.sendEmptyMessage(0);
    }

}
