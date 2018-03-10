package com.smona.app.evaluationcar.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.data.bean.QuickPreCarImageBean;
import com.smona.app.evaluationcar.data.event.background.QuickUploadEvent;
import com.smona.app.evaluationcar.data.item.UserItem;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.framework.upload.ActionTask;
import com.smona.app.evaluationcar.framework.upload.quick.QuickPreCompleteTask;
import com.smona.app.evaluationcar.framework.upload.quick.QuickPreImageTask;
import com.smona.app.evaluationcar.framework.upload.quick.QuickPreStartupTask;
import com.smona.app.evaluationcar.framework.upload.quick.QuickUploadTaskExecutor;
import com.smona.app.evaluationcar.ui.common.refresh.NetworkTipUtil;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Moth on 2017/6/17.
 */

public class QuickUploadService extends Service {
    private static final String TAG = QuickUploadService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CarLog.d(TAG, "onCreate");
        QuickUploadTaskExecutor.getInstance().init();
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
        QuickUploadEvent event = new QuickUploadEvent();
        EventProxy.post(event);
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void startTask(QuickUploadEvent event) {
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

        List<QuickPreCarBillBean> uploadDatas = DBDelegator.getInstance().queryQuickPreCarBillInUpload();
        for (QuickPreCarBillBean carbill : uploadDatas) {
            CarLog.d(TAG, "startTask carbill=" + carbill);
            if (TextUtils.isEmpty(carbill.carBillId)) {
                putTaskInSave(user.mId, carbill);
            } else if (StatusUtils.isPreNotPass(carbill.status)) {
                putTaskInReturn(user.mId, carbill);
            } else {
                putTaskInSaveFailed(user.mId, carbill);
            }
        }
    }

    private void putTaskInSave(String userName, QuickPreCarBillBean carbill) {
        List<QuickPreCarImageBean> images = DBDelegator.getInstance().queryPreQuickImages(carbill.imageId);
        generateTask(userName, carbill, images);
    }

    private void putTaskInReturn(String userName, QuickPreCarBillBean carbill) {
        List<QuickPreCarImageBean> images = DBDelegator.getInstance().queryQuickPreUpdateImages(carbill.carBillId);
        generateTask(userName, carbill, images);
    }

    private void putTaskInSaveFailed(String userName, QuickPreCarBillBean carbill) {
        List<QuickPreCarImageBean> images = DBDelegator.getInstance().queryPreQuickImages(carbill.imageId);
        generateTaskSaveFailed(userName, carbill, images);
    }

    private void generateTask(String userName, QuickPreCarBillBean bean, List<QuickPreCarImageBean> images) {
        QuickPreStartupTask startTask = new QuickPreStartupTask();
        startTask.mCarBill = bean;
        startTask.userName = userName;
        startTask.mCarBillId = bean.carBillId;

        ActionTask preTask = startTask;

        for (QuickPreCarImageBean image : images) {
            QuickPreImageTask task = new QuickPreImageTask();
            task.carImageBean = image;
            task.userName = userName;
            preTask.mNextTask = task;

            preTask = task;
        }

        QuickPreCompleteTask comleteTask = new QuickPreCompleteTask();
        comleteTask.carBill = bean;
        comleteTask.userName = userName;

        preTask.mNextTask = comleteTask;

        QuickUploadTaskExecutor.getInstance().pushTask(startTask);
    }

    private void generateTaskSaveFailed(String userName, QuickPreCarBillBean bean, List<QuickPreCarImageBean> images) {
        QuickPreStartupTask startTask = new QuickPreStartupTask();
        startTask.mCarBill = bean;
        startTask.userName = userName;
        startTask.mCarBillId = bean.carBillId;

        ActionTask preTask = startTask;

        for (QuickPreCarImageBean image : images) {
            CarLog.d(TAG, "image: " + image);
            if (!TextUtils.isEmpty(image.imagePath)) {
                continue;
            }
            QuickPreImageTask task = new QuickPreImageTask();
            task.carImageBean = image;
            task.userName = userName;
            preTask.mNextTask = task;

            preTask = task;
        }

        QuickPreCompleteTask comleteTask = new QuickPreCompleteTask();
        comleteTask.carBill = bean;
        comleteTask.userName = userName;

        preTask.mNextTask = comleteTask;

        QuickUploadTaskExecutor.getInstance().pushTask(startTask);
    }
}