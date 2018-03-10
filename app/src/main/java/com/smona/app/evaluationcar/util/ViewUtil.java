package com.smona.app.evaluationcar.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewUtil {

    public static View inflater(Context context, int resId) {
        return LayoutInflater.from(context).inflate(resId, null);
    }

    public static View inflater(Context context, int resId, ViewGroup group) {
        return LayoutInflater.from(context).inflate(resId, group);
    }

    public static void setViewVisible(View view, boolean show) {
        if (view == null) {
            return;
        }
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
