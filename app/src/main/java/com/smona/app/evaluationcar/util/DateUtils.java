package com.smona.app.evaluationcar.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by motianhu on 4/9/17.
 */

public class DateUtils {
    public static String getCurrDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
}
