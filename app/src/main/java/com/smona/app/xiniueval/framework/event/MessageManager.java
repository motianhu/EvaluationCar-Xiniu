package com.smona.app.xiniueval.framework.event;

import com.smona.app.xiniueval.data.event.LocalStatusRefreshEvent;
import com.smona.app.xiniueval.data.event.SubmitStatusEvent;

/**
 * Created by motianhu on 3/27/18.
 */

public class MessageManager {
    public static void refreshNoSubmitStatus() {
        LocalStatusRefreshEvent event = new LocalStatusRefreshEvent();
        EventProxy.post(event);
    }

    public static void refreshSubmited(){
        //刷新已提交
        EventProxy.post(new SubmitStatusEvent());
    }

}
