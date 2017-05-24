package com.smona.app.xiniu.framework;

import android.app.Application;

import com.smona.app.xiniu.business.HttpDelegator;
import com.smona.app.xiniu.data.event.ToastEvent;
import com.smona.app.xiniu.framework.cache.DataDelegator;
import com.smona.app.xiniu.framework.event.EventProxy;
import com.smona.app.xiniu.framework.imageloader.ImageLoaderProxy;
import com.smona.app.xiniu.framework.provider.DBDelegator;
import com.smona.app.xiniu.framework.provider.EvaluationProvider;
import com.smona.app.xiniu.framework.provider.GenerateMaxId;
import com.smona.app.xiniu.framework.storage.DeviceStorageManager;
import com.smona.app.xiniu.ui.evaluation.ImageModelDelegator;
import com.smona.app.xiniu.util.ScreenInfo;
import com.smona.app.xiniu.util.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * Created by Moth on 2016/12/18.
 */

public class EvaluationApp extends Application {
    private WeakReference<EvaluationProvider> mProvider;

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
        DeviceStorageManager.getInstance().setContext(this);
        DeviceStorageManager.getInstance().initPath();
        EventProxy.register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionMainEvent(ToastEvent event) {
        ToastUtils.show(this, event.message);
    }
}
