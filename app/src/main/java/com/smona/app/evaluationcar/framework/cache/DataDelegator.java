package com.smona.app.evaluationcar.framework.cache;

import android.content.Context;
import android.text.TextUtils;

import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.business.param.CarbillParam;
import com.smona.app.evaluationcar.business.param.UserParam;
import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.data.bean.CarImageBean;
import com.smona.app.evaluationcar.data.bean.ImageMetaBean;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.util.UrlConstants;

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

    public void requestImageMeta(ResponseCallback<String> callback) {
        String url = UrlConstants.getInterface(UrlConstants.QUERY_NEWS_LATEST);
        boolean cache = CacheDelegator.getInstance().checkCacheExit(url);
        if (cache) {
            CacheDelegator.getInstance().queryOperationDesc(callback);
        } else {
            HttpDelegator.getInstance().queryOperationDesc(callback);
        }
    }

    public ImageMetaBean requestImageMeta(String imageClass, int imageSeqNum) {
        return DBDelegator.getInstance().queryImageMeta(imageClass, imageSeqNum);
    }

    public void requestUpgradeInfo(ResponseCallback<String> callback) {
        HttpDelegator.getInstance().requestUpgradeInfo(callback);
    }

    public void queryPageElementDetail(int pageId, ResponseCallback<String> callback) {
        HttpDelegator.getInstance().queryPageElementDetail(pageId, callback);
    }
}
