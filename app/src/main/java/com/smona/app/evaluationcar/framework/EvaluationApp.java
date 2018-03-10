package com.smona.app.evaluationcar.framework;

import android.app.Application;

import com.smona.app.evaluationcar.business.HttpDelegator;
import com.smona.app.evaluationcar.data.event.ToastEvent;
import com.smona.app.evaluationcar.framework.cache.DataDelegator;
import com.smona.app.evaluationcar.framework.chatclient.ChatClientProxy;
import com.smona.app.evaluationcar.framework.crashreport.CrashReportProxy;
import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.imageloader.ImageLoaderProxy;
import com.smona.app.evaluationcar.framework.provider.DBDelegator;
import com.smona.app.evaluationcar.framework.provider.EvaluationProvider;
import com.smona.app.evaluationcar.framework.provider.GenerateMaxId;
import com.smona.app.evaluationcar.framework.push.PushProxy;
import com.smona.app.evaluationcar.framework.storage.DeviceStorageManager;
import com.smona.app.evaluationcar.ui.evaluation.ImageModelDelegator;
import com.smona.app.evaluationcar.ui.evaluation.preevaluation.quick.QuickImageModelDelegator;
import com.smona.app.evaluationcar.util.ScreenInfo;
import com.smona.app.evaluationcar.util.ToastUtils;

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
        PushProxy.init(this);
        ScreenInfo.getInstance().init(this);
        DBDelegator.getInstance().init(this);
        DataDelegator.getInstance().init(this);
        HttpDelegator.getInstance().init(this);
        GenerateMaxId.getInstance().initMaxId();
        ImageModelDelegator.getInstance().init(this);
        QuickImageModelDelegator.getInstance().init(this);
        CrashReportProxy.init(this);
        DeviceStorageManager.getInstance().setContext(this);
        DeviceStorageManager.getInstance().initPath();
        ChatClientProxy.getInstance().init(this);
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
        ToastUtils.show(this, event.message);
    }
}
