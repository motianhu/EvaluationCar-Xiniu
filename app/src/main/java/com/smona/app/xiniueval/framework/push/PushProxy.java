package com.smona.app.xiniueval.framework.push;

import android.content.Context;

import com.smona.app.xiniueval.framework.IProxy;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by motianhu on 3/14/17.
 */

public class PushProxy implements IProxy {
    public static void init(Context context) {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
    }
}
