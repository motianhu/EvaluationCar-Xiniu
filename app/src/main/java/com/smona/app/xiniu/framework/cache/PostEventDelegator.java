package com.smona.app.xiniu.framework.cache;

/**
 * Created by Moth on 2017/5/4.
 */

public class PostEventDelegator {
    private volatile static PostEventDelegator sInstance = null;

    private PostEventDelegator(){}

    public static PostEventDelegator getInstance() {
        if (sInstance == null) {
            sInstance = new PostEventDelegator();
        }
        return sInstance;
    }
}
