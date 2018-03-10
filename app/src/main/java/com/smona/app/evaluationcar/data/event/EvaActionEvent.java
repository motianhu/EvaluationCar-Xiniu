package com.smona.app.evaluationcar.data.event;

import com.smona.app.evaluationcar.data.event.common.MainEvent;

/**
 * Created by Moth on 2017/4/8.
 */

public class EvaActionEvent extends MainEvent {
    public static final int REFRESH = 1;
    public static final int FINISH = 2;
    public int action = REFRESH;
}
