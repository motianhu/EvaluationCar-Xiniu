package com.smona.app.evaluationcar.framework.upload.quick;

import android.text.TextUtils;

import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.data.event.ToastEvent;
import com.smona.app.evaluationcar.data.event.background.LocalStatusSubEvent;
import com.smona.app.evaluationcar.data.event.background.StatisticsStatusSubEvent;
import com.smona.app.evaluationcar.data.model.ResBaseModel;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.framework.upload.ActionTask;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.StatusUtils;

/**
 * Created by motianhu on 4/5/17.
 */

public class QuickPreCompleteTask extends ActionTask {
    private static final String TAG = QuickPreCompleteTask.class.getSimpleName();
    public QuickPreCarBillBean carBill;

    public void startTask() {
        //前期如果有失败的,则不提交,同时进入下一个任务
        if (carBill == null || !TextUtils.isEmpty(mMessage)) {
            String error = "上传失败,具体原因是: " + mMessage + " 没上传成功!";
            CarLog.d(TAG, "onSuccess  上传失败,具体原因是: " + error + " 没上传成功, carBill=" + carBill);
            postMessage(error);

            QuickUploadTaskExecutor.getInstance().nextTask(0, mCarBillId);
        } else {
            carBill.carBillId = mCarBillId;
            carBill.uploadStatus = StatusUtils.BILL_UPLOAD_STATUS_NONE;
            if (carBill.status == 0) {
                DBDelegator.getInstance().deleteQuickPreCarbill(carBill);
            } else {
                DBDelegator.getInstance().updateQuickPreCarBill(carBill);
            }
            QuickUploadTaskExecutor.getInstance().nextTask(0, mCarBillId);
            postMessage("单号" + mCarBillId + "上传成功!");
        }
    }

    private void postMessage(String error) {
        ToastEvent event = new ToastEvent();
        event.message = error;
        EventProxy.post(event);
    }
}
