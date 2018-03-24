package com.smona.app.xiniueval.framework.upload;

import android.text.TextUtils;

import com.smona.app.xiniueval.business.ResponseCallback;
import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.event.LocalStatusRefreshEvent;
import com.smona.app.xiniueval.data.event.SubmitStatusEvent;
import com.smona.app.xiniueval.data.event.ToastEvent;
import com.smona.app.xiniueval.data.event.background.LocalStatusSubEvent;
import com.smona.app.xiniueval.data.model.ResBaseModel;
import com.smona.app.xiniueval.framework.cache.DataDelegator;
import com.smona.app.xiniueval.framework.event.EventProxy;
import com.smona.app.xiniueval.framework.json.JsonParse;
import com.smona.app.xiniueval.framework.provider.DBDelegator;
import com.smona.app.xiniueval.util.CarLog;
import com.smona.app.xiniueval.util.StatusUtils;

/**
 * Created by motianhu on 4/5/17.
 */

public class CompleteTask extends ActionTask {
    private static final String TAG = CompleteTask.class.getSimpleName();
    public CarBillBean carBill;

    public void startTask() {
        //前期如果有失败的,则不提交,同时进入下一个任务
        if (carBill == null || carBill.preSalePrice <= 0.0 || !TextUtils.isEmpty(mMessage)) {
            String error = "上传失败,具体原因是: " + mMessage + " 没上传成功!";
            CarLog.d(TAG, "onSuccess  上传失败,具体原因是: " + error + " 没上传成功, carBill=" + carBill);
            postMessage(error);
            UploadTaskExecutor.getInstance().nextTask(0, mCarBillId);
        } else {
            carBill.carBillId = mCarBillId;
            DataDelegator.getInstance().submitCarBill(userName, carBill, new ResponseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    CarLog.d(TAG, "onSuccess result: " + result + ", carBill: " + carBill);
                    ResBaseModel<String> resBaseModel = JsonParse.parseJson(result, ResBaseModel.class);
                    if (resBaseModel.success) {
                        //不管是本地(无单号还是保存过的或者上传失败的)
                        //删除单据相关信息
                        DBDelegator.getInstance().deleteCarbill(carBill);
                        DBDelegator.getInstance().deleteBatchCarImages(carBill.imageId);
                        //刷新未提交
                        LocalStatusRefreshEvent event = new LocalStatusRefreshEvent();
                        EventProxy.post(event);
                        //刷新已提交
                        EventProxy.post(new SubmitStatusEvent());
                        //给出成功提示
                        postMessage("单号" + mCarBillId + "上传成功!");
                    } else {
                        postMessage("单号" + mCarBillId + "上传失败!具体原因:" + resBaseModel.message);
                    }
                    UploadTaskExecutor.getInstance().nextTask(0, mCarBillId);
                }

                @Override
                public void onFailed(String error) {
                    CarLog.d(TAG, "onError ex: " + error);
                    postMessage(error);

                    UploadTaskExecutor.getInstance().nextTask(0, mCarBillId);
                }
            });
        }

    }

    private void postMessage(String error) {
        ToastEvent event = new ToastEvent();
        event.message = error;
        EventProxy.post(event);
    }
}
