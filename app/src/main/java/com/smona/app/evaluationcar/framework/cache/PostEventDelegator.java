package com.smona.app.evaluationcar.framework.cache;

import com.smona.app.evaluationcar.data.event.BannerEvent;
import com.smona.app.evaluationcar.data.event.NewsEvent;
import com.smona.app.evaluationcar.data.event.NoticeEvent;
import com.smona.app.evaluationcar.data.item.BannerItem;
import com.smona.app.evaluationcar.data.item.NewsItem;
import com.smona.app.evaluationcar.framework.event.EventProxy;

import java.util.List;

/**
 * Created by Moth on 2017/5/4.
 */

public class PostEventDelegator {
    private volatile static PostEventDelegator sInstance = null;

    private PostEventDelegator() {
    }

    public static PostEventDelegator getInstance() {
        if (sInstance == null) {
            sInstance = new PostEventDelegator();
        }
        return sInstance;
    }

    public void postBannerEvent(List<BannerItem> items) {
        BannerEvent event = new BannerEvent();
        event.setContent(items);
        EventProxy.post(event);
    }

    public void postNoticeEvent(List<NewsItem> items) {
        NoticeEvent event = new NoticeEvent();
        event.setContent(items);
        EventProxy.post(event);
    }

    public void postNewsEvent(List<NewsItem> item) {
        NewsEvent event = new NewsEvent();
        event.setContent(item);
        EventProxy.post(event);
    }
}
