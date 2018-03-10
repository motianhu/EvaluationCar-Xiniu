package com.smona.app.evaluationcar.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.data.bean.CarImageBean;
import com.smona.app.evaluationcar.data.event.background.UploadEvent;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.framework.upload.ActionTask;
import com.smona.app.evaluationcar.framework.upload.CompleteTask;
import com.smona.app.evaluationcar.framework.upload.ImageTask;
import com.smona.app.evaluationcar.framework.upload.StartupTask;
import com.smona.app.evaluationcar.framework.upload.UploadTaskExecutor;
import com.smona.app.evaluationcar.ui.common.refresh.NetworkTipUtil;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by motianhu on 4/13/17.
 */

public class UploadService extends Service {
    private static final String TAG = UploadService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CarLog.d(TAG, "onCreate");
        UploadTaskExecutor.getInstance().init();
        EventProxy.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CarLog.d(TAG, "onDestroy");
        EventProxy.unregister(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CarLog.d(TAG, "onStartCommand");
        UploadEvent event = new UploadEvent();
        EventProxy.post(event);
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void startTask(UploadEvent event) {
        UserItem user = new UserItem();
        user.readSelf(this);
        if (TextUtils.isEmpty(user.mId)) {
            CarLog.d(TAG, "user is null");
            return;
        }

        if (!NetworkTipUtil.hasNetworkInfo(this)) {
            CarLog.d(TAG, "no network!");
            return;
        }

        List<CarBillBean> uploadDatas = DBDelegator.getInstance().queryCarBillInUpload();
        for (CarBillBean carbill : uploadDatas) {
            CarLog.d(TAG, "startTask carbill=" + carbill);
            if (TextUtils.isEmpty(carbill.carBillId)) {
                putTaskInSave(user.mId, carbill);
            } else if (StatusUtils.isNotPass(carbill.status)) {
                putTaskInReturn(user.mId, carbill);
            } else {
                putTaskInSaveFailed(user.mId, carbill);
            }
        }
    }

    private void putTaskInSave(String userName, CarBillBean carbill) {
        List<CarImageBean> images = DBDelegator.getInstance().queryImages(carbill.imageId);
        generateTask(userName, carbill, images);
    }

    private void putTaskInReturn(String userName, CarBillBean carbill) {
        List<CarImageBean> images = DBDelegator.getInstance().queryUpdateImages(carbill.carBillId);
        generateTask(userName, carbill, images);
    }

    private void putTaskInSaveFailed(String userName, CarBillBean carbill) {
        List<CarImageBean> images = DBDelegator.getInstance().queryImages(carbill.imageId);
        generateTaskSaveFailed(userName, carbill, images);
    }

    private void generateTask(String userName, CarBillBean bean, List<CarImageBean> images) {
        StartupTask startTask = new StartupTask();
        startTask.mCarBill = bean;
        startTask.userName = userName;
        startTask.mCarBillId = bean.carBillId;

        ActionTask preTask = startTask;

        for (CarImageBean image : images) {
            ImageTask task = new ImageTask();
            task.carImageBean = image;
            task.userName = userName;
            preTask.mNextTask = task;

            preTask = task;
        }

        CompleteTask comleteTask = new CompleteTask();
        comleteTask.carBill = bean;
        comleteTask.userName = userName;

        preTask.mNextTask = comleteTask;

        UploadTaskExecutor.getInstance().pushTask(startTask);
    }

    private void generateTaskSaveFailed(String userName, CarBillBean bean, List<CarImageBean> images) {
        StartupTask startTask = new StartupTask();
        startTask.mCarBill = bean;
        startTask.userName = userName;
        startTask.mCarBillId = bean.carBillId;

        ActionTask preTask = startTask;

        for (CarImageBean image : images) {
            CarLog.d(TAG, "image: " + image);
            if (!TextUtils.isEmpty(image.imagePath)) {
                continue;
            }
            ImageTask task = new ImageTask();
            task.carImageBean = image;
            task.userName = userName;
            preTask.mNextTask = task;

            preTask = task;
        }

        CompleteTask comleteTask = new CompleteTask();
        comleteTask.carBill = bean;
        comleteTask.userName = userName;

        preTask.mNextTask = comleteTask;

        UploadTaskExecutor.getInstance().pushTask(startTask);
    }
}
