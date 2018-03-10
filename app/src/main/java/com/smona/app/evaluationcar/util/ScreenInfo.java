package com.smona.app.evaluationcar.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by motianhu on 3/17/17.
 */

public class ScreenInfo {
    private static volatile ScreenInfo sInstance;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDpi;

    private ScreenInfo() {
    }

    public static ScreenInfo getInstance() {
        if (sInstance == null) {
            sInstance = new ScreenInfo();
        }
        return sInstance;
    }

    public void init(Context context) {
        Resources resource = context.getResources();
        DisplayMetrics display = resource.getDisplayMetrics();
        mScreenWidth = display.widthPixels;
        mScreenHeight = display.heightPixels;
        mScreenDpi = display.densityDpi;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }
}
