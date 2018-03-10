package com.smona.app.evaluationcar.business;

import org.xutils.common.Callback;

/**
 * Created by motianhu on 4/5/17.
 */

public abstract class ResponseCallback<T> implements Callback.CommonCallback<String> {
    @Override
    public void onCancelled(Callback.CancelledException cex) {
        onFailed("onCanceled " + cex);
    }

    @Override
    public void onFinished() {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        ex.printStackTrace();
        onFailed("onError " + ex);
    }

    public abstract void onFailed(String error);
}
