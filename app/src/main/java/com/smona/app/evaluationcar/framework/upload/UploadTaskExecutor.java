package com.smona.app.evaluationcar.framework.upload;

import android.os.Handler;
import android.text.TextUtils;

import com.smona.app.evaluationcar.util.CarLog;

import java.util.Iterator;
import java.util.LinkedList;

public class UploadTaskExecutor {
    private static final String TAG = UploadTaskExecutor.class.getSimpleName();

    private static final int MULTI_THREAD_COUNT = 2;
    private static UploadTaskExecutor sInstance;
    private LinkedList<ActionTask> mWattingTasks = new LinkedList<ActionTask>();
    private LinkedList<ActionTask> mRunningTasks = new LinkedList<ActionTask>();
    private int sRunCount = 0;
    private Handler sHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ActionTask waitTask = mWattingTasks.poll();
            if (null != waitTask) {
                startTask(waitTask);
            } else {
                sRunCount--;
            }
            CarLog.d(TAG, "sRunCount:" + sRunCount + "  mWattingTasks.size():" + mWattingTasks.size());
        }
    };

    private UploadTaskExecutor() {
    }

    public static UploadTaskExecutor getInstance() {
        if (sInstance == null) {
            sInstance = new UploadTaskExecutor();
        }
        return sInstance;
    }

    public void init() {
    }

    private boolean existWaitingTasks(ActionTask task) {
        Iterator<ActionTask> it = mWattingTasks.iterator();
        while (it.hasNext()) {
            ActionTask action = it.next();
            if (action.isSelf(task)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWaittingTask(int imageId, String carBillId) {
        Iterator<ActionTask> it = mWattingTasks.iterator();
        while (it.hasNext()) {
            ActionTask action = it.next();
            if (!TextUtils.isEmpty(action.mCarBillId) && action.mCarBillId.equals(carBillId)) {
                return true;
            } else if ((imageId > 0) && (imageId == action.mImageId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRunningTask(int imageId, String carBillId) {
        for (ActionTask action : mRunningTasks) {
            //有CarbillId
            if (!TextUtils.isEmpty(carBillId) && carBillId.equals(action.mCarBillId)) {
                return true;
            } else if ((imageId > 0) && (imageId == action.mImageId)) {
                return true;
            }
        }
        return false;
    }

    public void pushTask(ActionTask task) {
        if (existWaitingTasks(task)) {
            CarLog.d(TAG, "existWaitingTasks pushTask [" + task.mImageId + "," + task.mCarBillId + "]");
            return;
        }

        if (existRunningTask(task)) {
            CarLog.d(TAG, "existRunningTask pushTask [" + task.mImageId + "," + task.mCarBillId + "]");
            return;
        }

        CarLog.d(TAG, "pushTask [" + task.mImageId + "," + task.mCarBillId + "]" + ", sRunCount: " + sRunCount);
        if (sRunCount >= MULTI_THREAD_COUNT) {
            mWattingTasks.offer(task);
        } else {
            startTask(task);
            sRunCount++;
        }
    }

    private void startTask(ActionTask task) {
        task.startTask();
        mRunningTasks.push(task);
    }

    public void nextTask(int curImageId, String curCarBillId) {
        removeRunningTask(curImageId, curCarBillId);
        sHandler.sendEmptyMessage(0);
    }

    private void removeRunningTask(int curImageId, String curCarBillId) {
        for (ActionTask task : mRunningTasks) {
            //有CarbillId
            if (!TextUtils.isEmpty(curCarBillId) && task.mCarBillId.equals(curCarBillId)) {
                mRunningTasks.remove(task);
                break;
            } else if ((curImageId > 0) && (task.mImageId == curImageId)) {
                mRunningTasks.remove(task);
                break;
            }
        }
    }

    private boolean existRunningTask(ActionTask task) {
        for (ActionTask action : mRunningTasks) {
            //有CarbillId
            if (!TextUtils.isEmpty(task.mCarBillId) && task.mCarBillId.equals(action.mCarBillId)) {
                return true;
            } else if ((task.mImageId > 0) && (task.mImageId == action.mImageId)) {
                return true;
            }
        }
        return false;
    }

}
