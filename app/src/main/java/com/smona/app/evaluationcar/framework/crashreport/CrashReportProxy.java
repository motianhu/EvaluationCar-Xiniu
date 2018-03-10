package com.smona.app.evaluationcar.framework.crashreport;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Moth on 2017/3/29.
 */

public class CrashReportProxy {

    public static void init(Context appContext) {
        CrashReport.initCrashReport(appContext, "18e9624730", false);
    }

}
