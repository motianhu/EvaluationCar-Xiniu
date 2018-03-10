package com.smona.app.evaluationcar.framework.upload.quick;

import android.text.TextUtils;

import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.data.model.ResBaseArray;
import com.smona.app.evaluationcar.data.model.ResBaseModel;
import com.smona.app.evaluationcar.data.model.ResNormalArray;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.framework.upload.ActionTask;
import com.smona.app.evaluationcar.util.CarLog;

/**
 * Created by motianhu on 4/5/17.
 */

public class QuickPreStartupTask extends ActionTask {
    private static final String TAG = QuickPreStartupTask.class.getSimpleName();
    public QuickPreCarBillBean mCarBill;

    public void startTask() {
        if (TextUtils.isEmpty(mCarBillId)) {
            HttpDelegator.getInstance().submitQuickPreCallBill(userName, mCarBill, new ResponseCallback<String>() {
                @Override
                public void onSuccess(String content) {
                    CarLog.d(TAG, "onSuccess result: " + content);
                    ResBaseModel resModel = JsonParse.parseJson(content, ResBaseModel.class);
                    if (resModel.success) {
                        mCarBill.carBillId = (String)(resModel.object + "");
                        mCarBillId = mCarBill.carBillId;
                        DBDelegator.getInstance().updateQuickPreCarBill(mCarBill);
                        nextTask(mCarBillId, null);
                    } else {
                        QuickUploadTaskExecutor.getInstance().nextTask(mImageId, null);
                    }
                }

                @Override
                public void onFailed(String error) {
                    CarLog.d(TAG, "onError ex: " + error);
                    //没单号就跳过
                    QuickUploadTaskExecutor.getInstance().nextTask(mImageId, null);
                }
            });
        } else {
            nextTask(mCarBillId, null);
        }
    }
}
