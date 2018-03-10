package com.smona.app.evaluationcar.business;

import android.app.Application;
import android.text.TextUtils;

import com.smona.app.evaluationcar.business.param.BannerParam;
import com.smona.app.evaluationcar.business.param.CarbillParam;
import com.smona.app.evaluationcar.business.param.PageParam;
import com.smona.app.evaluationcar.business.param.UserParam;
import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.data.bean.CarImageBean;
import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.data.bean.QuickPreCarImageBean;
import com.smona.app.evaluationcar.data.model.ResNewsPage;
import com.smona.app.evaluationcar.data.model.ResPageElementPage;
import com.smona.app.evaluationcar.framework.IProxy;
import com.smona.app.evaluationcar.framework.cache.CacheDelegator;
import com.smona.app.evaluationcar.framework.cache.PostEventDelegator;
import com.smona.app.evaluationcar.framework.cache.RequestParamConstants;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.util.CarLog;
import com.smona.app.evaluationcar.util.UrlConstants;

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
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void requestLatestNews(final BannerParam bannerParam) {
        ReqParams params = createParams(UrlConstants.QUERY_NEWS_LATEST);
        params.addParameter(BannerParam.CLASSTYPE, bannerParam.classType);
        params.addParameter("curPage", 1);
        params.addParameter("clientName", "android");
        params.addParameter("pageSize", 4);
        x.http().get(params, new ResponseCallback<String>() {
            @Override
            public void onFailed(String error) {
                CarLog.d(TAG, "requestLatestNews onFailed error= " + error);
                PostEventDelegator.getInstance().postNewsEvent(null);
            }

            @Override
            public void onSuccess(String content) {
                ResNewsPage newsPage = JsonParse.parseJson(content, ResNewsPage.class);
                CarLog.d(TAG, "requestLatestNews onSuccess has content " + TextUtils.isEmpty(content));
                if (newsPage != null && newsPage.total > 0) {
                    PostEventDelegator.getInstance().postNewsEvent(newsPage.data);

                    String url = getCacheKey(UrlConstants.QUERY_NEWS_MORE, BannerParam.CLASSTYPE + "=" + bannerParam.classType);
                    CacheDelegator.getInstance().saveLastSuccessRequestTime(url);
                    CacheDelegator.getInstance().saveNewCacheByUrl(url, content);
                } else {
                    PostEventDelegator.getInstance().postNewsEvent(null);
                }
            }
        });
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

    public void queryCarBillForId(String userName, String carBillId, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBILL);
        params.addParameter("userName", userName);
        params.addParameter("carBillId", carBillId);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    //Notice
    public void requestNotice() {
        ReqParams params = createParams(UrlConstants.QUERY_NEWS_MORE);
        params.addParameter(RequestParamConstants.CLASS_TYPE, RequestParamConstants.CLASS_TYPE_NOTICE);
        params.addParameter("curPage", 1);
        params.addParameter("clientName", "android");
        params.addParameter("pageSize", 2);
        x.http().get(params, new ResponseCallback<String>() {
            @Override
            public void onFailed(String error) {
                CarLog.d(TAG, "requestNotice onFailed error= " + error);
                PostEventDelegator.getInstance().postNoticeEvent(null);
            }

            @Override
            public void onSuccess(String content) {
                ResNewsPage newsPage = JsonParse.parseJson(content, ResNewsPage.class);
                CarLog.d(TAG, "requestNotice onSuccess has content " + TextUtils.isEmpty(content));
                if (newsPage != null && newsPage.total > 0) {
                    PostEventDelegator.getInstance().postNoticeEvent(newsPage.data);
                    String url = getCacheKey(UrlConstants.QUERY_NEWS_MORE,
                            RequestParamConstants.CLASS_TYPE + "=" + RequestParamConstants.CLASS_TYPE_NOTICE);
                    CacheDelegator.getInstance().saveLastSuccessRequestTime(url);
                    CacheDelegator.getInstance().saveNewCacheByUrl(url, content);
                } else {
                    PostEventDelegator.getInstance().postNoticeEvent(null);
                }
            }
        });
    }


    public void queryMoreNews(String classType, PageParam page, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_NEWS_MORE);
        params.addParameter("classType", classType);
        params.addParameter("curPage", page.curPage);
        params.addParameter("clientName", "android");
        params.addParameter("pageSize", page.pageSize);
        x.http().get(params, callback);
    }

    public void queryNewsDetail(int newsId, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_NEWS_DETAIL);
        params.addParameter("newsId", newsId);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void requestUpgradeInfo(ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_APP_UPGRADE);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }


    //预评估
    public void queryCarBrand(ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBRAND);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void queryCarSet(String carBrandId, ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARSET);
        params.addParameter("carBrandId", carBrandId);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void queryCarType(String carBrandId, String carSetId, ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CARBTYPE);
        params.addParameter("carBrandId", carBrandId);
        params.addParameter("clientName", "android");
        params.addParameter("carSetId", carSetId);
        x.http().get(params, callback);
    }

    public void queryCity(ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_CITY);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void queryPageElementLatest() {
        ReqParams params = createParams(UrlConstants.QUERY_PAGEELEMENT_LATEST);
        params.addParameter("classType", "轮播图");
        params.addParameter("clientName", "android");
        x.http().get(params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String content) {
                CarLog.d(TAG, "queryPageElementLatest onSuccess has content " + TextUtils.isEmpty(content));
                ResPageElementPage pages = JsonParse.parseJson(content, ResPageElementPage.class);
                if (pages != null && pages.total > 0) {
                    PostEventDelegator.getInstance().postBannerEvent(pages.data);
                    String url = getCacheKey(UrlConstants.QUERY_PAGEELEMENT_LATEST);
                    CacheDelegator.getInstance().saveLastSuccessRequestTime(url);
                    CacheDelegator.getInstance().saveNewCacheByUrl(url, content);
                } else {
                    PostEventDelegator.getInstance().postBannerEvent(null);
                }
            }

            @Override
            public void onFailed(String error) {
                CarLog.d(TAG, "queryPageElementLatest onFailed error=" + error);
                PostEventDelegator.getInstance().postBannerEvent(null);
            }

        });
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

    //quick pre evaluation
    public void queryPreCarbillList(CarbillParam param, ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_QUICKPREEVALUATION_LIST);
        params.addParameter("userName", param.userName);
        params.addParameter("curPage", param.curPage);
        params.addParameter("pageSize", param.pageSize);
        params.addParameter("status", param.status);
        params.addParameter("carBillType", param.type);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void submitQuickPreCallBill(String userName, QuickPreCarBillBean bean, ResponseCallback<String> callback) {
        ReqParams params = createParams(UrlConstants.QUERY_PREEVALUATION_SUBMIT);
        params.addParameter("createUser", userName);
        params.addParameter("clientName", "android");
        params.addParameter("createTime", bean.createTime);
        params.addParameter("mark", bean.mark);
        params.addParameter("carBillType", "routine");
        x.http().get(params, callback);
    }

    public void getPreCarBillDetail(String userName, String carBillId, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_QUICKPREEVALUATION_DETAIL);
        params.addParameter("userName", userName);
        params.addParameter("clientName", "android");
        params.addParameter("carBillId", carBillId);
        x.http().get(params, callback);
    }

    public void uploadQuickPreImage(String createUser, QuickPreCarImageBean bean, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.SUBMIT_QUICKPREEVALUATION_IMAGE);
        params.addParameter("createUser", createUser);
        params.addParameter("clientName", "android");
        params.addParameter("carBillId", bean.carBillId);
        params.addParameter("imageSeqNum", bean.imageSeqNum);
        params.addParameter("imageClass", bean.imageClass);
        params.addBodyParameter("image", new File(bean.imageLocalUrl));
        x.http().post(params, callback);
    }

    public void getQuickPreImage(String userName, String carBillId, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_QUICKPREEVALUATION_IMAGE);
        params.addParameter("userName", userName);
        params.addParameter("carBillId", carBillId);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void reUploadQuickPreImage(String userName, String carBillId,String id, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.RESUBMIT_QUICKPREEVALUATION_IMAGE);
        params.addParameter("userName", userName);
        params.addParameter("carBillId", carBillId);
        params.addParameter("id", id);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }

    public void submitChangeCarBill(String userName, String carBillId, ResponseCallback callback) {
        ReqParams params = createParams(UrlConstants.QUERY_QUICKPREEVALUATION_POST);
        params.addParameter("userName", userName);
        params.addParameter("carBillId", carBillId);
        params.addParameter("clientName", "android");
        x.http().get(params, callback);
    }
}
