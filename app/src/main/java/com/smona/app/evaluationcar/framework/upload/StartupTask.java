package com.smona.app.evaluationcar.framework.upload;

import android.text.TextUtils;

import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.util.CarLog;

/**
 * Created by motianhu on 4/5/17.
 */

public class StartupTask extends ActionTask {
    private static final String TAG = StartupTask.class.getSimpleName();
    public CarBillBean mCarBill;

    public void startTask() {
        if (TextUtils.isEmpty(mCarBillId)) {
            HttpDelegator.getInstance().createCarBillId(new ResponseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    CarLog.d(TAG, "onSuccess result: " + result);
                    mCarBill.carBillId = result.substring(1, result.length() - 1);
                    mCarBillId = mCarBill.carBillId;

                    DBDelegator.getInstance().updateCarBill(mCarBill);
                    nextTask(mCarBillId, null);
                }

                @Override
                public void onFailed(String error) {
                    CarLog.d(TAG, "onError ex: " + error);
                    //没单号就跳过
                    UploadTaskExecutor.getInstance().nextTask(mImageId, null);
                }
            });
        } else {
            nextTask(mCarBillId, null);
        }
    }
}
