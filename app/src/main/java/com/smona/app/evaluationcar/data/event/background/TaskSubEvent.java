package com.smona.app.evaluationcar.data.event.background;

import com.smona.app.evaluationcar.data.event.common.SubEvent;

/**
 * Created by motianhu on 4/6/17.
 */

public class TaskSubEvent extends SubEvent {
    public static final int ACTION_TASK = 1;
    public static final int ACTION_RELOAD = 2;

    public Object obj;
    public int action;
}
