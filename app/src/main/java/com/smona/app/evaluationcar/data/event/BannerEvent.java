package com.smona.app.evaluationcar.data.event;

import com.smona.app.evaluationcar.data.event.common.BaseEvent;

/**
 * Created by Moth on 2017/2/25.
 */

public class BannerEvent implements BaseEvent {
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
