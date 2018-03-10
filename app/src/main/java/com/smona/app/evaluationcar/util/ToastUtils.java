package com.smona.app.evaluationcar.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by motianhu on 3/21/17.
 */

public class ToastUtils {
    public static void show(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
