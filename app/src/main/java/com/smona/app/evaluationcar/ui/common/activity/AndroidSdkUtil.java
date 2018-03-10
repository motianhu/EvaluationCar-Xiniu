package com.smona.app.evaluationcar.ui.common.activity;

import android.os.Build;

public class AndroidSdkUtil {

    public static boolean sdkLessThan24() {
        return Build.VERSION.SDK_INT < 24;
    }

    public static boolean sdkLessThan23() {
        return Build.VERSION.SDK_INT < 23;
    }

    public static boolean sdkLessThan22() {
        return Build.VERSION.SDK_INT < 22;
    }

    public static boolean sdkLessThan19() {
        return Build.VERSION.SDK_INT < 19;
    }

    public static boolean sdkLessThan18() {
        return Build.VERSION.SDK_INT < 18;
    }

    public static boolean sdkLessThan17() {
        return Build.VERSION.SDK_INT < 17;
    }

    public static boolean sdkMoreThan23() {
        return Build.VERSION.SDK_INT >= 23;
    }
}
