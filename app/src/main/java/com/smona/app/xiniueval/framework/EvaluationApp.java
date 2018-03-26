package com.smona.app.xiniueval.framework;

import android.app.Application;

import com.smona.app.xiniueval.business.HttpDelegator;
import com.smona.app.xiniueval.data.event.ToastEvent;
import com.smona.app.xiniueval.framework.cache.DataDelegator;
import com.smona.app.xiniueval.framework.crashreport.CrashReportProxy;
import com.smona.app.xiniueval.framework.event.EventProxy;
import com.smona.app.xiniueval.framework.imageloader.ImageLoaderProxy;
import com.smona.app.xiniueval.framework.provider.DBDelegator;
import com.smona.app.xiniueval.framework.provider.EvaluationProvider;
import com.smona.app.xiniueval.framework.provider.GenerateMaxId;
import com.smona.app.xiniueval.framework.storage.DeviceStorageManager;
import com.smona.app.xiniueval.ui.evaluation.ImageModelDelegator;
import com.smona.app.xiniueval.util.ScreenInfo;
import com.smona.app.xiniueval.util.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * Created by Moth on 2016/12/18.
 */

public class EvaluationApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderProxy.init(this);
        ScreenInfo.getInstance().init(this);
        DBDelegator.getInstance().init(this);
        DataDelegator.getInstance().init(this);
        HttpDelegator.getInstance().init(this);
        GenerateMaxId.getInstance().initMaxId();
        ImageModelDelegator.getInstance().init(this);
        CrashReportProxy.init(this);
        DeviceStorageManager.getInstance().setContext(this);
        DeviceStorageManager.getInstance().initPath();
        EventProxy.register(this);
    }

    private WeakReference<EvaluationProvider> mProviderRefs = null;
    public void setProvider(EvaluationProvider provider) {
        mProviderRefs = new WeakReference<EvaluationProvider>(provider);
    }

    public void clearAllTableData() {
        EvaluationProvider provider = mProviderRefs.get();
        if(provider != null) {
            provider.clearAllTableData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionMainEvent(ToastEvent event) {
        ToastUtils.longShow(this, event.message);
    }
}
