package com.smona.app.xiniueval.data.event;

import com.smona.app.xiniueval.data.event.common.MainEvent;

/**
 * Created by motianhu on 4/24/17.
 */

public class PageElementEvent extends MainEvent {
    private Object mContent;
    private String mMessage;
    private String mTag;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }


    public Object getContent() {
        return mContent;
    }

    public void setContent(Object content) {
        this.mContent = content;
    }
}
