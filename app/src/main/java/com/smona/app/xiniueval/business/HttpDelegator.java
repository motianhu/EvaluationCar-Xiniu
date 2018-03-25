package com.smona.app.xiniueval.business;

import android.app.Application;
import android.os.Build;

import com.smona.app.xiniueval.business.param.CarbillParam;
import com.smona.app.xiniueval.business.param.UserParam;
import com.smona.app.xiniueval.data.bean.CarBillBean;
import com.smona.app.xiniueval.data.bean.CarImageBean;
import com.smona.app.xiniueval.framework.IProxy;
import com.smona.app.xiniueval.util.UrlConstants;

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
        params.addParameter("brand", Build.BRAND);
        params.addParameter("model", Build.MODEL);
        return params;
    }

    public void checkUser(UserParam userParam, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.CHECK_USER);
        params.addParameter(UserParam.USERNAME, userParam.userName);
        params.addParameter(UserParam.PASSWORD, userParam.password);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void createCarBillId(ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.CREATE_CARBILLID);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void queryCarbillList(CarbillParam param, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBILL_LIST);
        params.addParameter(CarbillParam.USERNAME, param.userName);
        params.addParameter(CarbillParam.STATUS, param.status);
        params.addParameter(CarbillParam.CURPAGE, param.curPage);
        params.addParameter(CarbillParam.PAGESIZE, param.pageSize);
        params.addParameter("clientName", "android");
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
        params.addParameter("leaseTerm", carBill.leaseTerm);
        x.http().get(params, callback);
    }


    public void getCarbillImages(String userName, String carBillId, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBILL_IMAGE);
        params.addParameter("userName", userName);
        params.addParameter("carBillId", carBillId);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void queryOperationDesc(ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_OPERATION_DESC);
        x.http().get(params, callback);
    }

    public void queryCarbillCount(String userName, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBILL_COUNT);
        params.addParameter("userName", userName);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void getEvaluationNotPassAttach(String userName, String carBillId, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_EVALUATION_NOTPASS_ATTACH);
        params.addParameter("userName", userName);
        params.addParameter("carBillId", carBillId);
        params.addParameter("status", "23,33,43,53");
        params.addParameter("clientName", "android");
        params.addParameter("curPage", "1");
        params.addParameter("pageSize", "50");
        x.http().get(params, callback);
    }

    public void requestUpgradeInfo(ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_APP_UPGRADE);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void queryPageElementDetail(int pageId, ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_PAGEELEMENT_DETAIL);
        params.addParameter("id", pageId);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public String getAutoLogos(String name) {
        String url = UrlConstants.getInterface(UrlConstants.GET_AUTO_LOGOS);
        return url + name;
    }
}
