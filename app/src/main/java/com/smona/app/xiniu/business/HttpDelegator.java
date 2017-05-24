package com.smona.app.xiniu.business;

import android.app.Application;

import com.smona.app.xiniu.business.param.CarbillParam;
import com.smona.app.xiniu.business.param.UserParam;
import com.smona.app.xiniu.data.bean.CarBillBean;
import com.smona.app.xiniu.data.bean.CarImageBean;
import com.smona.app.xiniu.framework.IProxy;
import com.smona.app.xiniu.util.UrlConstants;

import org.xutils.x;

import java.io.File;

/**
 * Created by Moth on 2016/12/18.
 */

public class HttpDelegator implements IProxy {
    private static final String TAG = HttpDelegator.class.getSimpleName();

    private volatile static HttpDelegator sInstance;

    private HttpDelegator() {
    }

    public static HttpDelegator getInstance() {
        if (sInstance == null) {
            sInstance = new HttpDelegator();
        }
        return sInstance;
    }

    public void init(Application app) {
        x.Ext.init(app);
    }

    public String getCacheKey(int urlCode) {
        return getCacheKey(urlCode, "");
    }

    public String getCacheKey(int urlCode, String suffix) {
        return UrlConstants.getInterface(urlCode) + suffix;
    }

    private ReqParams createParams(int type) {
        String url = UrlConstants.getInterface(type);
        ReqParams params = new ReqParams(url);
        return params;
    }

    public void checkUser(UserParam userParam, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.CHECK_USER);
        params.addParameter(UserParam.USERNAME, userParam.userName);
        params.addParameter(UserParam.PASSWORD, userParam.password);
        x.http().get(params, callback);
    }

    public void createCarBillId(ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.CREATE_CARBILLID);
        x.http().get(params, callback);
    }

    public void queryCarbillList(CarbillParam param, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBILL_LIST);
        params.addParameter(CarbillParam.USERNAME, param.userName);
        params.addParameter(CarbillParam.STATUS, param.status);
        params.addParameter(CarbillParam.CURPAGE, param.curPage);
        params.addParameter(CarbillParam.PAGESIZE, param.pageSize);
        x.http().get(params, callback);
    }

    public void uploadImage(String createUser, CarImageBean bean, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.UPLOAD_IMAGE);
        params.addParameter("createUser", createUser);
        params.addParameter("clientName", "android");
        params.addParameter("carBillId", bean.carBillId);
        params.addParameter("imageSeqNum", bean.imageSeqNum);
        params.addParameter("imageClass", bean.imageClass);
        params.addBodyParameter("image", new File(bean.imageLocalUrl));
        x.http().post(params, callback);
    }

    public void submitCarBill(String userName, CarBillBean carBill, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.SUBMIT_CARBILL);
        params.addParameter("userName", userName);
        params.addParameter("carBillId", carBill.carBillId);
        params.addParameter("clientName", "android");
        params.addParameter("preSalePrice", carBill.preSalePrice);
        params.addParameter("mark", carBill.mark);
        x.http().get(params, callback);
    }


    public void getCarbillImages(String userName, String carBillId, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBILL_IMAGE);
        params.addParameter("userName", userName);
        params.addParameter("carBillId", carBillId);
        x.http().get(params, callback);
    }

    public void queryOperationDesc(ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_OPERATION_DESC);
        x.http().get(params, callback);
    }

    //Notice

    public void requestUpgradeInfo(ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_APP_UPGRADE);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }


    //预评估
    public void queryCarBrand(ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBRAND);
        x.http().get(params, callback);
    }

    public void queryCarSet(String carBrandId, ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARSET);
        params.addParameter("carBrandId", carBrandId);
        x.http().get(params, callback);
    }

    public void queryCarType(String carBrandId, String carSetId, ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBTYPE);
        params.addParameter("carBrandId", carBrandId);
        params.addParameter("carSetId", carSetId);
        x.http().get(params, callback);
    }

    public void queryCity(ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CITY);
        x.http().get(params, callback);
    }

    public void queryPreCarbillList(CarbillParam param, ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_PREEVALUATION);
        params.addParameter("userName", param.userName);
        params.addParameter("curPage", param.curPage);
        params.addParameter("pageSize", param.pageSize);
        x.http().get(params, callback);
    }
}
