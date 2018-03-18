package com.smona.app.xiniueval.data.event;

import com.smona.app.xiniueval.data.event.common.MainEvent;
import com.smona.app.xiniueval.data.model.ResUpgradeApi;

/**
 * Created by motianhu on 4/11/17.
 */

public class UpgradeEvent extends MainEvent {
    public static final int DIALOG = 1;
    public static final int TOAST = 2;

    public int action;
    public ResUpgradeApi mResBaseApi;
}
