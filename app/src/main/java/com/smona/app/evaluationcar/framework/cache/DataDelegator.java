package com.smona.app.evaluationcar.framework.cache;

import android.content.Context;
import android.text.TextUtils;

import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.business.param.BannerParam;
import com.smona.app.evaluationcar.business.param.CarbillParam;
import com.smona.app.evaluationcar.business.param.PageParam;
import com.smona.app.evaluationcar.business.param.UserParam;
import com.smona.app.evaluationcar.data.bean.CarBillBean;
import com.smona.app.evaluationcar.data.bean.CarImageBean;
import com.smona.app.evaluationcar.data.bean.ImageMetaBean;
import com.smona.app.evaluationcar.data.bean.QuickPreCarBillBean;
import com.smona.app.evaluationcar.data.bean.QuickPreCarImageBean;
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

    public void requestCarbillCount(String userName, ResponseCallback<String> callback) {
        String url = UrlConstants.getInterface(UrlConstants.QUERY_CARBILL_COUNT);
        boolean cache = CacheDelegator.getInstance().checkCacheExit(url);
        if (cache) {
            CacheDelegator.getInstance().queryCarbillCount(callback);
        } else {
            HttpDelegator.getInstance().queryCarbillCount(userName, callback);
        }
    }


    public ImageMetaBean requestImageMeta(String imageClass, int imageSeqNum) {
        return DBDelegator.getInstance().queryImageMeta(imageClass, imageSeqNum);
    }

    public void requestUpgradeInfo(ResponseCallback<String> callback) {
        HttpDelegator.getInstance().requestUpgradeInfo(callback);
    }

    public void queryPreCarbillList(CarbillParam params, ResponseCallback<String> callback) {
        HttpDelegator.getInstance().queryPreCarbillList(params, callback);
    }


    //有缓存文件的处理方式
    public void requestLatestNews(BannerParam params) {
        String url = HttpDelegator.getInstance().getCacheKey(UrlConstants.QUERY_NEWS_LATEST,
                BannerParam.CLASSTYPE + "=" + params.classType);
        //需要重新请求
        if (CacheDelegator.getInstance().needReload(url)) {
            //删除原来的缓存文件
            CacheDelegator.getInstance().deleteCache(url);
            //重新加载服务器数据
            HttpDelegator.getInstance().requestLatestNews(params);
            return;
        }

        //是否有缓存文件
        boolean hasCache = CacheDelegator.getInstance().checkCacheExit(url);
        if (hasCache) {
            CacheDelegator.getInstance().requestLatestNews(params, url);
        } else {
            HttpDelegator.getInstance().requestLatestNews(params);
        }
    }

    public void requestNotice() {
        String url = HttpDelegator.getInstance().getCacheKey(UrlConstants.QUERY_NEWS_LATEST,
                RequestParamConstants.CLASS_TYPE + "=" + RequestParamConstants.CLASS_TYPE_NOTICE);
        //需要重新请求
        if (CacheDelegator.getInstance().needReload(url)) {
            //删除原来的缓存文件
            CacheDelegator.getInstance().deleteCache(url);
            //重新加载服务器数据
            HttpDelegator.getInstance().requestNotice();
            return;
        }

        //是否有缓存文件
        boolean hasCache = CacheDelegator.getInstance().checkCacheExit(url);
        if (hasCache) {
            CacheDelegator.getInstance().requestNotice(url);
        } else {
            HttpDelegator.getInstance().requestNotice();
        }
    }


    public void queryPageElementLatest() {
        String url = HttpDelegator.getInstance().getCacheKey(UrlConstants.QUERY_PAGEELEMENT_LATEST);
        //需要重新请求
        if (CacheDelegator.getInstance().needReload(url)) {
            //删除原来的缓存文件
            CacheDelegator.getInstance().deleteCache(url);
            //重新加载服务器数据
            HttpDelegator.getInstance().queryPageElementLatest();
            return;
        }

        //是否有缓存文件
        boolean hasCache = CacheDelegator.getInstance().checkCacheExit(url);
        if (hasCache) {
            CacheDelegator.getInstance().queryPageElementLatest(url);
        } else {
            HttpDelegator.getInstance().queryPageElementLatest();
        }
    }

    public void queryPageElementDetail(int pageId, ResponseCallback<String> callback) {
        HttpDelegator.getInstance().queryPageElementDetail(pageId, callback);
    }

    public void queryNewsDetail(int newsId, ResponseCallback callback) {
        HttpDelegator.getInstance().queryNewsDetail(newsId, callback);
    }

    public void queryMoreNews(String classType, PageParam page, ResponseCallback callback) {
        HttpDelegator.getInstance().queryMoreNews(classType, page, callback);
    }

    //quick preevaluation
    public void uploadQuickPreImage(String userName, QuickPreCarImageBean bean, ResponseCallback callback) {
        HttpDelegator.getInstance().uploadQuickPreImage(userName, bean, callback);
    }

    public void submitQuickPreCallBill(String userName, QuickPreCarBillBean bean, ResponseCallback<String> callback) {
        HttpDelegator.getInstance().submitQuickPreCallBill(userName, bean, callback);
    }

    public void getPreCarBillDetail(String userName, String carBillId, ResponseCallback callback) {
        HttpDelegator.getInstance().getPreCarBillDetail(userName, carBillId, callback);
    }

    public List<QuickPreCarBillBean> queryLocalQuickPreCarbill(int curPage, int pageSize) {
        List<QuickPreCarBillBean> dataList = DBDelegator.getInstance().queryLocalQuickPreCarbill(curPage, pageSize);
        return dataList;
    }
}
