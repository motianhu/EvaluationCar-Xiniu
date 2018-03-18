package com.smona.app.xiniueval.data.event;

import com.smona.app.xiniueval.data.event.common.BaseEvent;

/**
 * Created by Moth on 2017/3/15.
 */

public class BillTotalEvent implements BaseEvent {
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
