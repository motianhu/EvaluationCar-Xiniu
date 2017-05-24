package com.smona.app.xiniu.framework.cache;

import android.content.Context;
import android.text.TextUtils;

import com.smona.app.xiniu.business.HttpDelegator;
import com.smona.app.xiniu.business.ResponseCallback;
import com.smona.app.xiniu.business.param.CarbillParam;
import com.smona.app.xiniu.business.param.UserParam;
import com.smona.app.xiniu.data.bean.CarBillBean;
import com.smona.app.xiniu.data.bean.CarImageBean;
import com.smona.app.xiniu.framework.provider.DBDelegator;
import com.smona.app.xiniu.util.UrlConstants;

import java.util.List;

/**
 * Created by Moth on 2017/4/6.
 */

public class DataDelegator {


    private volatile static DataDelegator sInstance;


    private DataDelegator() {
    }

    public static DataDelegator getInstance() {
        if (sInstance == null) {
            sInstance = new DataDelegator();
        }
        return sInstance;
    }

    public void init(Context appContext) {
        CacheDelegator.getInstance().init(appContext);
    }

    public void checkUser(UserParam params, ResponseCallback callback) {
        String url = UrlConstants.getInterface(UrlConstants.CHECK_USER) + "?userName=" + params.userName;
        boolean cache = CacheDelegator.getInstance().checkCacheExit(url);
        if (cache) {
            String cacheData = CacheDelegator.getInstance().loadCacheByUrl(url);
            if (!TextUtils.isEmpty(cacheData)) {
                CacheDelegator.getInstance().checkUser(cacheData, callback);
                return;
            }
        }
        HttpDelegator.getInstance().checkUser(params, callback);
    }


    public void uploadImage(String userName, CarImageBean bean, ResponseCallback callback) {
        HttpDelegator.getInstance().uploadImage(userName, bean, callback);
    }

    public void submitCarBill(String userName, CarBillBean carBill, ResponseCallback callback) {
        HttpDelegator.getInstance().submitCarBill(userName, carBill, callback);
    }

    public List<CarBillBean> queryLocalCarbill(int curPage, int pageSize) {
        List<CarBillBean> dataList = DBDelegator.getInstance().queryLocalCarbill(curPage, pageSize);
        return dataList;
    }

    public void queryCarbillList(CarbillParam param, ResponseCallback<String> callback) {
        HttpDelegator.getInstance().queryCarbillList(param, callback);
    }

    public void requestUpgradeInfo(ResponseCallback<String> callback) {
        HttpDelegator.getInstance().requestUpgradeInfo(callback);
    }
}
