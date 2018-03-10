package com.smona.app.evaluationcar.framework.cache;

import android.content.Context;
import android.text.TextUtils;

import com.smona.app.evaluationcar.business.ResponseCallback;
import com.smona.app.evaluationcar.business.param.BannerParam;
import com.smona.app.evaluationcar.data.model.ResNewsPage;
import com.smona.app.evaluationcar.data.model.ResPageElementPage;
import com.smona.app.evaluationcar.framework.IProxy;
import com.smona.app.evaluationcar.framework.json.JsonParse;
import com.smona.app.evaluationcar.framework.storage.DeviceStorageManager;
import com.smona.app.evaluationcar.util.FileUtils;
import com.smona.app.evaluationcar.util.MD5;
import com.smona.app.evaluationcar.util.SPUtil;

/**
 * Created by Moth on 2017/3/15.
 */

public class CacheDelegator implements IProxy {
    private static final String TAG = CacheDelegator.class.getSimpleName();
    private static final String CHARSET = "UTF-8";

    private static final long LAST_SERVER_DEFAULT_VALUE = 0l;
    private static final long INTERVAL = 60 * 60 * 1000; // one hour

    private static volatile CacheDelegator sInstance;
    private Context mAppContext;

    private CacheDelegator() {
    }

    public static CacheDelegator getInstance() {
        if (sInstance == null) {
            sInstance = new CacheDelegator();
        }
        return sInstance;
    }

    public void init(Context appContext) {
        mAppContext = appContext;
    }

    //是否需要重新加载
    boolean needReload(String key) {
        return needReload(key, INTERVAL);
    }

    private boolean needReload(String key, long limitTime) {
        long lastTime = getLastSuccessRequestTime(key);
        long now = System.currentTimeMillis();
        long diff = now - lastTime;
        return Math.abs(diff) >= limitTime;
    }

    //获取和保存上一次成功加载的时间
    private long getLastSuccessRequestTime(String key) {
        return (long) SPUtil.get(mAppContext, key, LAST_SERVER_DEFAULT_VALUE);
    }

    public void saveLastSuccessRequestTime(String key) {
        SPUtil.put(mAppContext, key, LAST_SERVER_DEFAULT_VALUE);
    }

    //缓存文件管理
    boolean checkCacheExit(String url) {
        return FileUtils.isFileExist(getFilePathByUrl(url));
    }

    private String getFilePathByUrl(String url) {
        String md5 = MD5.getMD5(url);
        return DeviceStorageManager.getInstance().getMd5Path() + md5;
    }

    public boolean deleteCache(String url) {
        return FileUtils.deleteFile(getFilePathByUrl(url));
    }

    public void saveNewCacheByUrl(String url, String content) {
        FileUtils.writeFile(getFilePathByUrl(url), content, false);
    }


    public String loadCacheByUrl(String url) {
        if (checkCacheExit(url)) {
            StringBuilder result = FileUtils.readFile(getFilePathByUrl(url), CHARSET);
            return TextUtils.isEmpty(result) ? null : result.toString();
        } else {
            return null;
        }
    }

    public void checkUser(String cacheData, ResponseCallback callback) {
        callback.onSuccess(cacheData);
    }

    public void queryOperationDesc(ResponseCallback<String> callback) {

    }

    public void queryCarbillCount(ResponseCallback<String> callback) {

    }

    public void requestLatestNews(BannerParam params, String url) {
        String cacheData = loadCacheByUrl(url);
        ResNewsPage pages = JsonParse.parseJson(cacheData, ResNewsPage.class);
        if (pages != null && pages.total > 0) {
            PostEventDelegator.getInstance().postNewsEvent(pages.data);
        } else {
            PostEventDelegator.getInstance().postNewsEvent(null);
        }
    }

    public void requestNotice(String url) {
        String cacheData = loadCacheByUrl(url);
        ResNewsPage pages = JsonParse.parseJson(cacheData, ResNewsPage.class);
        if (pages != null && pages.total > 0) {
            PostEventDelegator.getInstance().postNoticeEvent(pages.data);
        } else {
            PostEventDelegator.getInstance().postNoticeEvent(null);
        }
    }

    public void requestUpgradeInfo(ResponseCallback<String> callback) {

    }

    public void queryPageElementLatest(String url) {
        String cacheData = loadCacheByUrl(url);
        ResPageElementPage pages = JsonParse.parseJson(cacheData, ResPageElementPage.class);
        if (pages != null && pages.total > 0) {
            PostEventDelegator.getInstance().postBannerEvent(pages.data);
        } else {
            PostEventDelegator.getInstance().postBannerEvent(null);
        }
    }
}
