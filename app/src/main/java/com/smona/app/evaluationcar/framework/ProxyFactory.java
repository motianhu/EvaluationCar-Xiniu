package com.smona.app.evaluationcar.framework;

import com.smona.app.evaluationcar.framework.event.EventProxy;
import com.smona.app.evaluationcar.framework.imageloader.ImageLoaderProxy;

/**
 * Created by Moth on 2016/12/18.
 */

public class ProxyFactory {
    private volatile static ProxyFactory sProxyFactory;
    private IProxy mImageLoaderProxy;
    private IProxy mEventProxy;
    private IProxy mHttpProxy;

    private ProxyFactory() {
        mImageLoaderProxy = new ImageLoaderProxy();
        mEventProxy = new EventProxy();
    }

    public static ProxyFactory getInstance() {
        synchronized (ProxyFactory.class) {
            if (sProxyFactory == null) {
                sProxyFactory = new ProxyFactory();
            }
            return sProxyFactory;
        }
    }

    public ImageLoaderProxy getImageLoaderProxy() {
        return (ImageLoaderProxy) mImageLoaderProxy;
    }

    public EventProxy getEventProxy() {
        return (EventProxy) mEventProxy;
    }
}
