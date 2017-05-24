package com.smona.app.xiniu.data.event.background;

import com.smona.app.xiniu.data.event.common.SubEvent;

/**
 * Created by Moth on 2017/3/29.
 */

public class StatisticsStatusSubEvent extends SubEvent {
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
