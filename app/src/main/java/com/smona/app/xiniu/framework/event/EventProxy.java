package com.smona.app.xiniu.framework.event;

import com.smona.app.xiniu.data.event.common.BaseEvent;
import com.smona.app.xiniu.framework.IProxy;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Moth on 2016/12/18.
 */

public class EventProxy implements IProxy {
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void post(BaseEvent event) {
        EventBus.getDefault().post(event);
    }
}
